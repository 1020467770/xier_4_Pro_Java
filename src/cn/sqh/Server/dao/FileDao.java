package cn.sqh.Server.dao;


import cn.sqh.Server.domain.BasicFile;

import java.util.List;

public interface FileDao {

    void addFile(BasicFile file);

    List<BasicFile> findAllFilesByParentId(int parentId);

    List<BasicFile> findAllFilesByUserIdAndParentId(int userId, int parentFolderId);
}
