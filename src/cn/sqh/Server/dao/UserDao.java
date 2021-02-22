package cn.sqh.Server.dao;

import cn.sqh.Server.domain.User;

public interface UserDao {

    User findUserByUserNameAndPassword(String username,String password);

    void addUser(User user);

    User findUserByUserName(String spUserName);

    void updateCurrentContain(User userByUserName);
}