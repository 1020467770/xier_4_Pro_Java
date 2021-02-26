package cn.sqh.Server.servlet;


import cn.sqh.Server.domain.BasicFile;
import cn.sqh.Server.service.UserService;
import cn.sqh.Server.service.impl.UserServiceImpl;
import cn.sqh.Server.util.Logging;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@WebServlet("/getFilesByParentIdServlet")
public class GetFilesByParentIdServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            request.setCharacterEncoding("utf-8");

            int userId = Integer.parseInt(request.getParameter("userId"));
            int parentFolderId = Integer.parseInt(request.getParameter("parentFolderId"));
            System.out.println("进入获取文件Servlet，发起者的id是" + userId + ",查询的parentId是" + parentFolderId);
            UserService service = new UserServiceImpl();
            ArrayList<BasicFile> fileList = service.getAllFilesByUserIdAndParentFolderId(userId, parentFolderId);
            String s = "";
            if (fileList != null) {
                s = JSONObject.toJSONString(fileList);
            }
            System.out.println("返回的文件有这些：" + s);
            response.setCharacterEncoding("utf-8");
            response.getWriter().write(s);
        } catch (Exception e) {
            Logging.logger.error(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }
}