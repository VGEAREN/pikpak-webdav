package com.vgearen.pikpakwebdav.store;

import com.vgearen.pikpakwebdav.model.FileType;
import com.vgearen.pikpakwebdav.model.PFile;
import com.vgearen.pikpakwebdav.model.PathInfo;
import net.sf.webdav.ITransaction;
import net.sf.webdav.IWebdavStore;
import net.sf.webdav.StoredObject;
import net.sf.webdav.Transaction;
import net.sf.webdav.exceptions.WebdavException;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.InputStream;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Set;

public class PikpakDriverFileSystemStore implements IWebdavStore {
    private static final Logger LOGGER = LoggerFactory.getLogger(PikpakDriverFileSystemStore.class);

    private static PikpakDriverClientService pikpakDriverClientService;

    public PikpakDriverFileSystemStore(File file) {
    }

    public static void setBean(PikpakDriverClientService pikpakDriverClientService) {
        PikpakDriverFileSystemStore.pikpakDriverClientService = pikpakDriverClientService;
    }

    @Override
    public void destroy() {
        LOGGER.info("destroy");

    }

    @Override
    public ITransaction begin(Principal principal, HttpServletRequest req, HttpServletResponse resp) {
        LOGGER.debug("begin");
        return new Transaction(principal, req, resp);
    }

    @Override
    public void checkAuthentication(ITransaction transaction) {
        LOGGER.debug("checkAuthentication");
    }

    @Override
    public void commit(ITransaction transaction) {
        LOGGER.debug("commit");
    }

    @Override
    public void rollback(ITransaction transaction) {
        LOGGER.debug("rollback");

    }

    @Override
    public void createFolder(ITransaction transaction, String folderUri) {
        LOGGER.info("createFolder: {}", folderUri);
        pikpakDriverClientService.createFolder(folderUri);
    }

    @Override
    public void createResource(ITransaction transaction, String resourceUri) {
        LOGGER.info("createResource: {}", resourceUri);
    }

    @Override
    public InputStream getResourceContent(ITransaction transaction, String resourceUri) {
        LOGGER.info("getResourceContent: {}", resourceUri);
        Enumeration<String> headerNames = transaction.getRequest().getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String s = headerNames.nextElement();
            LOGGER.debug("{} request: {} = {}",resourceUri,  s, transaction.getRequest().getHeader(s));
        }
        HttpServletResponse response = transaction.getResponse();
        long size = getResourceLength(transaction, resourceUri);
        Response downResponse = pikpakDriverClientService.download(resourceUri, transaction.getRequest(), size);
        response.setContentLengthLong(downResponse.body().contentLength());
        LOGGER.debug("{} code = {}", resourceUri, downResponse.code());
        for (String name : downResponse.headers().names()) {
            LOGGER.debug("{} downResponse: {} = {}", resourceUri, name, downResponse.header(name));
            response.addHeader(name, downResponse.header(name));
        }
        response.setStatus(downResponse.code());
        return downResponse.body().byteStream();
    }

    @Override
    public long setResourceContent(ITransaction transaction, String resourceUri, InputStream content, String contentType, String characterEncoding) {
        LOGGER.info("setResourceContent {}", resourceUri);
        return 0;
    }

    @Override
    public String[] getChildrenNames(ITransaction transaction, String folderUri) {
//        LOGGER.info("getChildrenNames: {}", folderUri);
        PFile pFile = pikpakDriverClientService.getPFileByPath(folderUri);
        if (pFile.getFileType().equals(FileType.file.name())) {
            return new String[0];
        }
        Set<PFile> pFiles = pikpakDriverClientService.getPFiles(pFile.getFileId());
        return pFiles.stream().map(PFile::getName).toArray(String[]::new);
    }

    @Override
    public long getResourceLength(ITransaction transaction, String path) {
//        LOGGER.info("getResourceLength: {}", path);
        PFile pFile = pikpakDriverClientService.getPFileByPath(path);
        if (pFile == null || pFile.getSize() == null) {
            return 384;
        }

        return pFile.getSize();
    }

    @Override
    public void removeObject(ITransaction transaction, String uri) {
        LOGGER.info("removeObject: {}", uri);
        pikpakDriverClientService.remove(uri);
    }

    @Override
    public boolean moveObject(ITransaction transaction, String destinationPath, String sourcePath) {
        LOGGER.info("moveObject: {} -> {}", sourcePath,destinationPath);

        PathInfo destinationPathInfo = pikpakDriverClientService.getPathInfo(destinationPath);
        PathInfo sourcePathInfo = pikpakDriverClientService.getPathInfo(sourcePath);
        // 名字相同，说明是移动目录
        if (sourcePathInfo.getName().equals(destinationPathInfo.getName())) {
            pikpakDriverClientService.move(sourcePath, destinationPathInfo.getParentPath());
        } else {
            if (!destinationPathInfo.getParentPath().equals(sourcePathInfo.getParentPath())) {
                throw new WebdavException("不支持目录和名字同时修改");
            }
            // 名字不同，说明是修改名字。不考虑目录和名字同时修改的情况
            pikpakDriverClientService.rename(sourcePath, destinationPathInfo.getName());
        }
        return true;
    }

    @Override
    public StoredObject getStoredObject(ITransaction transaction, String uri) {
        LOGGER.info("getStoredObject: {}", uri);
        PFile pFile = pikpakDriverClientService.getPFileByPath(uri);
        if (pFile != null) {
            StoredObject so = new StoredObject();
            so.setFolder(pFile.getFileType().equalsIgnoreCase(FileType.folder.name()));
            so.setResourceLength(getResourceLength(transaction, uri));
            so.setCreationDate(pFile.getCreateTime());
            so.setLastModified(pFile.getUpdateTime());
            return so;
        }
        return null;
    }
}
