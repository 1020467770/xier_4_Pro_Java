package cn.sqh.Server.service.impl;

import cn.sqh.Server.dao.FileDao;
import cn.sqh.Server.dao.UserDao;
import cn.sqh.Server.dao.impl.FileDaoImpl;
import cn.sqh.Server.dao.impl.UserDaoImpl;
import cn.sqh.Server.domain.BasicFile;
import cn.sqh.Server.domain.User;
import cn.sqh.Server.service.UserService;
import cn.sqh.Server.util.MD5;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserServiceImpl implements UserService {

    private UserDao userDao = new UserDaoImpl();
    private FileDao fileDao = new FileDaoImpl();


    @Override
    public User signUp(String spUserName, String spUserPassword) {
        User userfindInDB = userDao.findUserByUserName(spUserName);
        if (userfindInDB == null) {//只有数据库里没有该用户时才会创建新用户
            User spUser = new User(spUserName, spUserPassword, 0);
            try {
                userDao.addUser(spUser);
                return userDao.findUserByUserName(spUserName);
            } catch (Exception e) {
                return null;
            }
        } else {
            return null;
        }

    }

    @Override
    public User login(User user) {
        return userDao.findUserByUserNameAndPassword(user.getUsername(), user.getPassword());
    }

    @Override
    public void uploadPicture(String username, BasicFile file) {
        User userByUserName = userDao.findUserByUserName(username);
        if (userByUserName != null) {
            file.setCreatorId(userByUserName.getId());
            userByUserName.setCurrentContain(userByUserName.getCurrentContain() + file.getCapacity());
            userDao.updateCurrentContain(userByUserName);
            fileDao.addFile(file);
        }

    }

    @Override
    public void createNewFolder(String creatorUsername, BasicFile newFolder) {
        User userByUserName = userDao.findUserByUserName(creatorUsername);
        if (userByUserName != null) {
            newFolder.setCreatorId(userByUserName.getId());
            String fileName = newFolder.getFileName();
            String abstractFileName = MD5.MD5Encode(UUID.randomUUID().toString(), "utf-8") + "@" + fileName;
            newFolder.setFileName(abstractFileName);
            fileDao.addFile(newFolder);
        }
    }

    @Override
    public ArrayList<BasicFile> getAllFilesByUserIdAndParentFolderId(int userId, int parentFolderId) {
        List<BasicFile> allFilesByParentId = fileDao.findAllFilesByUserIdAndParentId(userId, parentFolderId);

        return (ArrayList<BasicFile>) allFilesByParentId;
    }


}

