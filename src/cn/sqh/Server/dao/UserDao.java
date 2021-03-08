package cn.sqh.Server.dao;

import cn.sqh.Server.domain.User;

public interface UserDao {

    User findUserByUserNameAndPassword(String username,String password);

    void addUser(User user);

    User findUserByUserName(String spUserName);

    void updateCurrentContain(User userByUserName);

    User findUserByCode(String code);

    void updateStatus(User user);

    User findUserById(int userId);

    void updateCode(String code, int userId);

    void updatePassword(String newPassword, int id);
}