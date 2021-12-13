package com.vgearen.pikpakwebdav.store;

import com.vgearen.pikpakwebdav.model.PFile;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 虚拟文件（用于上传时，列表展示）
 */
@Service
public class VirtualPFileService {
    private final Map<String, Map<String, PFile>> virtualPFileMap = new ConcurrentHashMap<>();

    /**
     * 创建文件
     */
//    public void createPFile(String parentId, PreUploadData preUploadData) {
//        Map<String, PFile> pFileMap = virtualPFileMap.computeIfAbsent(parentId, s -> new ConcurrentHashMap<>());
//        pFileMap.put(preUploadData.getUploadResult().getNewContentIDList().get(0).getContentID(), convert(preUploadData));
//    }

    public void updateLength(String parentId, String fileId, long length) {
        Map<String, PFile> pFileMap = virtualPFileMap.get(parentId);
        if (pFileMap == null) {
            return;
        }
        PFile pFile = pFileMap.get(fileId);
        if (pFile == null) {
            return;
        }
        pFile.setSize(pFile.getSize() + length);
        pFile.setUpdateTime(new Date());
    }

    public void remove(String parentId, String fileId) {
        Map<String, PFile> pFileMap = virtualPFileMap.get(parentId);
        if (pFileMap == null) {
            return;
        }
        pFileMap.remove(fileId);
    }

    public Collection<PFile> list(String parentId) {
        Map<String, PFile> pFileMap = virtualPFileMap.get(parentId);
        if (pFileMap == null) {
            return Collections.emptyList();
        }
        return pFileMap.values();
    }

//    private PFile convert(PreUploadData preUploadData) {
//        PFile pFile = new PFile();
//        pFile.setCreateTime(new Date());
//        pFile.setFileId(preUploadData.getUploadResult().getNewContentIDList().get(0).getContentID());
//        pFile.setName(preUploadData.getUploadResult().getNewContentIDList().get(0).getContentName());
//        pFile.setFileType(FileType.file.name());
//        pFile.setUpdateTime(new Date());
//        pFile.setSize(0L);
//        return pFile;
//    }
}
