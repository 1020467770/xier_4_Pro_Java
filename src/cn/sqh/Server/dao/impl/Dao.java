package cn.sqh.Server.dao.impl;

import cn.sqh.Server.util.JDBCUtils;
import org.springframework.jdbc.core.JdbcTemplate;

public class Dao {

    static protected JdbcTemplate template = new JdbcTemplate(JDBCUtils.getDataSource());

}