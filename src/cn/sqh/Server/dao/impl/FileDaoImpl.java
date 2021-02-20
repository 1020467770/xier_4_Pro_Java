package cn.sqh.Server.dao.impl;

import cn.sqh.Server.dao.FileDao;
import cn.sqh.Server.domain.BasicFile;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import java.util.List;

public class FileDaoImpl extends Dao implements FileDao {

    @Override
    public void addFile(BasicFile file) {
        String sql = "insert into files values(null,?,?,null,?,?,?,?)";
        System.out.println("执行到添加File了");
        template.update(sql, file.getFileName(), file.getCreatorId(), file.getFileRealPath(), file.getCapacity(), file.getParentId(), file.getFileType());
    }

    @Override
    public List<BasicFile> findAllFilesByParentId(int parentId) {
        String sql = "select * from files where parentId = ?";
        try {
            List<BasicFile> files = template.query(sql, new BeanPropertyRowMapper<BasicFile>(BasicFile.class), parentId);
            return files;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public List<BasicFile> findAllFilesByUserIdAndParentId(int userId, int parentFolderId) {
        String sql = "select * from files where creator_id = ? and parentId = ?";
        try {
            List<BasicFile> files = template.query(sql, new BeanPropertyRowMapper<BasicFile>(BasicFile.class), userId, parentFolderId);
            return files;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
