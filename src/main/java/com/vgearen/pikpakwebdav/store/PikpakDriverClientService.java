package com.vgearen.pikpakwebdav.store;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.vgearen.pikpakwebdav.client.PikpakDriverClient;
import com.vgearen.pikpakwebdav.config.PikpakProperties;
import com.vgearen.pikpakwebdav.model.FileType;
import com.vgearen.pikpakwebdav.model.PFile;
import com.vgearen.pikpakwebdav.model.PathInfo;
import com.vgearen.pikpakwebdav.model.filelist.result.File;
import com.vgearen.pikpakwebdav.model.filelist.result.FilelistRes;
import com.vgearen.pikpakwebdav.model.folder.CreateFolderRequest;
import com.vgearen.pikpakwebdav.model.operate_file.MoveRequest;
import com.vgearen.pikpakwebdav.model.operate_file.To;
import com.vgearen.pikpakwebdav.util.JsonUtil;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class PikpakDriverClientService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PikpakDriverClientService.class);
    private static ObjectMapper objectMapper = new ObjectMapper();
    private static String rootPath = "/";
    private static int chunkSize = 10485760; // 100MB
    private PFile rootPFile = null;

    private static Cache<String, Set<PFile>> pFilesCache = Caffeine.newBuilder()
            .initialCapacity(128)
            .maximumSize(1024)
            .expireAfterWrite(5, TimeUnit.SECONDS)
            .build();

    private final PikpakDriverClient client;

    @Autowired
    private PikpakProperties pikpakProperties;

    @Autowired
    private VirtualPFileService virtualPFileService;

    public PikpakDriverClientService(PikpakDriverClient pikpakDriverClient) {
        this.client = pikpakDriverClient;
        PikpakDriverFileSystemStore.setBean(this);
    }

    public PFile getPFileByPath(String path) {
        path = normalizingPath(path);

        return getFileIdByPath(path);
    }

    public Set<PFile> getPFiles(String fileId) {
        Set<PFile> pFiles = pFilesCache.get(fileId, key -> {
            // 获取真实的文件列表
            return getPFilesWithNoRepeat(fileId);
        });
        Set<PFile> all = new LinkedHashSet<>(pFiles);
        // 获取上传中的文件列表
        Collection<PFile> virtualPFiles = virtualPFileService.list(fileId);
        all.addAll(virtualPFiles);
        return all;
    }

    private Set<PFile> getPFilesWithNoRepeat(String catalogId) {
        List<PFile> pFiles = fileListFromApi(catalogId, "", new ArrayList<>());
        pFiles.sort(Comparator.comparing(PFile::getUpdateTime).reversed());
        Set<PFile> pFileSet  = new LinkedHashSet<>();
        for (PFile item : pFiles) {
            if (!pFileSet.add(item)) {
                LOGGER.info("当前目录下{} 存在同名文件：{}，文件大小：{}", catalogId, item.getName(), item.getSize());
            }
        }
        // 对文件名进行去重，只保留最新的一个
        return pFileSet;
    }

    private String normalizingPath(String path) {
        path = path.replaceAll("//", "/");
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    private PFile getFileIdByPath(String path) {
        if (!StringUtils.hasLength(path)) {
            path = rootPath;
        }
        if (path.equals(rootPath)) {
            return getRootPFile();
        }
        PathInfo pathInfo = getPathInfo(path);
        PFile pFile = getPFileByPath(pathInfo.getParentPath());
        if (pFile == null ) {
            return null;
        }
        return getPFileByParentId(pFile.getFileId(),pathInfo.getName());
    }

    private PFile getPFileByParentId(String parentId, String name) {
        Set<PFile> pFiles = getPFiles(parentId);
        for (PFile pFile : pFiles) {
            if (pFile.getName().equals(name)) {
                return pFile;
            }
        }
        return null;
    }

    public PathInfo getPathInfo(String path) {
        path = normalizingPath(path);
        if (path.equals(rootPath)) {
            PathInfo pathInfo = new PathInfo();
            pathInfo.setPath(path);
            pathInfo.setName(path);
            return pathInfo;
        }
        int index = path.lastIndexOf("/");
        String parentPath = path.substring(0, index + 1);
        String name = path.substring(index+1);
        PathInfo pathInfo = new PathInfo();
        pathInfo.setPath(path);
        pathInfo.setParentPath(parentPath);
        pathInfo.setName(name);
        return pathInfo;
    }

    private PFile getRootPFile() {
        if (rootPFile == null) {
            rootPFile = new PFile();
            rootPFile.setName("/");
            rootPFile.setFileId("");
            rootPFile.setCreateTime(new Date());
            rootPFile.setUpdateTime(new Date());
            rootPFile.setFileType(FileType.folder.name());
        }
        return rootPFile;
    }

    public List<PFile> fileListFromApi(String parentId,String pageToken, List<PFile> all) {
        Map<String,String> listQuery = new HashMap<>(8);
        listQuery.put("parent_id",parentId);
        listQuery.put("thumbnail_size","SIZE_LARGE");
        listQuery.put("with_audit","true");
        listQuery.put("page_token",pageToken);
        listQuery.put("limit","0");

        String json = client.get("/drive/v1/files", listQuery);
        FilelistRes pFileListResult = JsonUtil.readValue(json, new TypeReference<FilelistRes>() {});
        List<File> files = pFileListResult.getFiles();
        for (File file : files) {
            String fileType = file.getKind().equals("drive#file") ? FileType.file.name() : FileType.folder.name();
            if(!file.getTrashed()){
                PFile pFile = new PFile();
                pFile.setFileId(file.getId());
                pFile.setFileType(fileType);
                pFile.setParentFileId(file.getParent_id());
                pFile.setName(file.getName());
                pFile.setSize(file.getSize());
                pFile.setCreateTime(file.getCreated_time());
                pFile.setUpdateTime(file.getModified_time());
                all.add(pFile);
            }
        }
        if(!StringUtils.hasLength(pFileListResult.getNext_page_token())){
            return all;
        }
        return fileListFromApi(parentId,pFileListResult.getNext_page_token(), all);
    }

    public Response download(String path, HttpServletRequest request, long size ) {
        PFile pFile = getPFileByPath(path);
        String json = client.get("/drive/v1/files/"+pFile.getFileId(),new HashMap<>());
        String web_content_link = (String) JsonUtil.getJsonNodeValue(json, "web_content_link");
        LOGGER.debug("{} url = {}", path, web_content_link);
        return client.download(web_content_link, request, size);
    }


    public void remove(String path) {
        path = normalizingPath(path);
        PFile pFile = getPFileByPath(path);
        if (pFile == null) {
            return;
        }
        Map<String,Object> removeRequest = new HashMap<>();
        removeRequest.put("ids",Arrays.asList(pFile.getFileId()));
        client.post("/drive/v1/files:batchTrash", removeRequest);
        clearCache();
    }

    public void createFolder(String path) {
        path = normalizingPath(path);
        PathInfo pathInfo = getPathInfo(path);
        PFile parent =  getPFileByPath(pathInfo.getParentPath());
        if (parent == null) {
            LOGGER.warn("创建目录失败，未发现父级目录：{}", pathInfo.getParentPath());
            return;
        }

        CreateFolderRequest createFileRequest = new CreateFolderRequest();
        createFileRequest.setKind("drive#folder");
        createFileRequest.setParent_id(parent.getFileId());
        createFileRequest.setName(pathInfo.getName());
        String json = client.post("/drive/v1/files", createFileRequest);
        String upload_type = (String) JsonUtil.getJsonNodeValue(json, "upload_type");
        if (!upload_type.equals("UPLOAD_TYPE_UNKNOWN")) {
            LOGGER.error("创建目录{}失败: {}",path, json);
        }
        clearCache();
    }
    public void rename(String sourcePath, String newName) {
        sourcePath = normalizingPath(sourcePath);
        PFile pFile = getPFileByPath(sourcePath);
        Map<String,String> renameRequest = new HashMap<>();
        renameRequest.put("name",newName);
        client.patch("/drive/v1/files/"+pFile.getFileId(), renameRequest);
        clearCache();
    }

    public void move(String sourcePath, String targetPath) {
        sourcePath = normalizingPath(sourcePath);
        targetPath = normalizingPath(targetPath);

        PFile sourcePFile = getPFileByPath(sourcePath);
        PFile targetPFile = getPFileByPath(targetPath);

        To to = new To();
        to.setParent_id(targetPFile.getFileId());
        MoveRequest moveRequest = new MoveRequest();
        moveRequest.setTo(to);
        moveRequest.setIds(Arrays.asList(sourcePFile.getFileId()));
        client.post("/drive/v1/files:batchMove", moveRequest);
        clearCache();
    }
    private void clearCache() {
        pFilesCache.invalidateAll();
    }
}
