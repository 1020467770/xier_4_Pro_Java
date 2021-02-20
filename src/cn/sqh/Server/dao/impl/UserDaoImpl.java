package cn.sqh.Server.dao.impl;


import cn.sqh.Server.dao.UserDao;
import cn.sqh.Server.domain.User;
import cn.sqh.Server.util.MD5;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

public class UserDaoImpl extends Dao implements UserDao {

//    private JdbcTemplate template = new JdbcTemplate(JDBCUtils.getDataSource());

    @Override
    public User findUserByUserNameAndPassword(String username, String password) {
        try {
            String sql = "select * from user where username = ? and password = ?";
            String abstractPassword = MD5.MD5Encode(password, "utf-8");
            User user = template.queryForObject(sql, new BeanPropertyRowMapper<User>(User.class), username, abstractPassword);
            System.out.println("执行到找到User了");
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("没有找到User，返回null");
            return null;
        }
    }

    @Override
    public void addUser(User user) {
        String sql = "insert into user values(null,?,?,?)";
        System.out.println("执行到添加User了");
        String abstractPassword = MD5.MD5Encode(user.getPassword(), "utf-8");
        template.update(sql, user.getUsername(), abstractPassword, user.getContainer());
    }

    @Override
    public User findUserByUserName(String username) {
        try {
            String sql = "select * from user where username = ?";
            User user = template.queryForObject(sql, new BeanPropertyRowMapper<User>(User.class), username);
            System.out.println("执行到找到User了");
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("没有找到User，返回null");
            return null;
        }
    }
}