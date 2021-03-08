package cn.sqh.Server.service.impl;

import cn.sqh.Server.dao.FileDao;
import cn.sqh.Server.dao.UserDao;
import cn.sqh.Server.dao.impl.FileDaoImpl;
import cn.sqh.Server.dao.impl.UserDaoImpl;
import cn.sqh.Server.domain.BasicFile;
import cn.sqh.Server.domain.User;
import cn.sqh.Server.service.UserService;
import cn.sqh.Server.util.JedisPoolUtils;
import cn.sqh.Server.util.MD5;
import cn.sqh.Server.util.MailUtils;
import redis.clients.jedis.Jedis;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserServiceImpl implements UserService {

    private UserDao userDao = new UserDaoImpl();
    private FileDao fileDao = new FileDaoImpl();

    private Jedis jedis = JedisPoolUtils.getJedis();

    @Override
    public User signUp(String spUserName, String spUserPassword) {
        User userfindInDB = userDao.findUserByUserName(spUserName);
        if (userfindInDB == null) {//只有数据库里没有该用户时才会创建新用户
            User spUser = new User(spUserName, spUserPassword, 0);
            try {

                //这里要给User加Code字段和Status激活字段和Email后才能用
                /*spUser.setCode(UUID.randomUUID().toString());
                spUser.setStatus("N");
                String content = "<a href='http://localhost:8080/TESTS/user/activeByEmail?code=" + user.getCode() + "'>点击激活【sqh网】</a>";
                MailUtils.sendMail(spUser.getEmail(), content, "激活邮件");*/

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

    @Override
    public boolean isHavaEnoughCapicity(String username) {
        User userfindInDB = userDao.findUserByUserName(username);
        if (userfindInDB == null || userfindInDB.getCurrentContain() >= userfindInDB.getContainer()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 这个其实不应该写的，用户删除文件没有必要去数据层删除数据，数据库操作数据耗时操作
     * 正常只要删除本地文件就行了
     *
     * @param fileId
     * @return
     */
    @Override
    public Boolean deleteFile(int fileId) {
        BasicFile fileFound = fileDao.findOneFileById(fileId);
        if (fileFound != null) {
            if (fileFound.getFileType() == BasicFile.FILETYPE_FOLDER) {
                List<BasicFile> allFilesByParentId = fileDao.findAllFilesByParentId(fileId);
                if (allFilesByParentId != null) {
                    for (BasicFile basicFile : allFilesByParentId) {
                        deleteFile(basicFile.getIdFile());
                    }
                }
                fileDao.deleteFile(fileId);
            } else if (fileFound.getFileType() == BasicFile.FILETYPE_PICTURE) {
                fileDao.deleteFile(fileId);
            }
            deleteLocalFile(fileId);
            return true;
        }
        return false;
    }

    @Override
    public Boolean deleteLocalFile(int fileId) {
        BasicFile fileFound = fileDao.findOneFileById(fileId);
        if (fileFound != null) {
            String path = fileFound.getFileRealPath();
            File file = new File(path);
            if (file.exists()) {
                file.delete();
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean activeUser(String code) {
        User user = userDao.findUserByCode(code);
        if (user != null) {
            userDao.updateStatus(user);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Boolean tryChangePsw(int userId, String newPassword, int tryType) {
        switch (tryType) {
            case 0:
                String code = MD5.MD5Encode(UUID.randomUUID().toString() + newPassword, "utf-8");
                User userById = userDao.findUserById(userId);
                if (userById != null) {
                    userDao.updateCode(code, userId);

                    //在jedis数据库设置激活码-新密码的键值对，激活码有效时间为24小时
                    jedis.setex(code, 60 * 60 * 24, newPassword);

                    //和注册同理，要给User加上email字段才能用
                    /*String content = "<a href='http://localhost:8080/TESTS/user/changePasswordByEmail?code=" + userById.getCode() + "'>点击此处验证邮箱并更换密码</a>";
                    MailUtils.sendMail(userById.getEmail(), content, "激活邮件");*/

                    return true;
                }
                return false;
            default:
                return false;
        }
    }

    @Override
    public boolean changePassword(String code) {
        String newPassword = jedis.get(code);
        if (newPassword == null) {
            //激活码过期或者激活码是错的
            return false;
        } else {
            User userByCode = userDao.findUserByCode(code);
            if (userByCode == null) {
                return false;
            }
            userDao.updatePassword(newPassword, userByCode.getId());
            return true;
        }
    }
}

