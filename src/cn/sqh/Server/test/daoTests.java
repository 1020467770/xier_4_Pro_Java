package cn.sqh.Server.test;


import cn.sqh.Server.dao.FileDao;
import cn.sqh.Server.dao.impl.FileDaoImpl;
import cn.sqh.Server.domain.BasicFile;
import org.junit.Test;

import java.util.List;

public class daoTests {


    @Test
    public void test1(){
        FileDao dao = new FileDaoImpl();
        List<BasicFile> allFilesByParentId = dao.findAllFilesByParentId(0);
        for (BasicFile basicFile : allFilesByParentId) {
            System.out.println(basicFile);
        }
    }
}
