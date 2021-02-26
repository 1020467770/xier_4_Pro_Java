package cn.sqh.Server.servlet;

import cn.sqh.Server.domain.BasicFile;
import cn.sqh.Server.service.UserService;
import cn.sqh.Server.service.impl.UserServiceImpl;
import cn.sqh.Server.util.Logging;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/createNewFolderServlet")
public class CreateNewFolderServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            request.setCharacterEncoding("utf-8");

            String folderName = request.getParameter("folderName");
            String creatorUsername = request.getParameter("creatorUsername");
            String parentFolderId = request.getParameter("parentFolderId");
            System.out.println(creatorUsername);
            System.out.println(parentFolderId);

            BasicFile newFolder = new BasicFile(folderName, "-", 0, Integer.parseInt(parentFolderId), BasicFile.FILETYPE_FOLDER);
            UserService service = new UserServiceImpl();

            service.createNewFolder(creatorUsername, newFolder);
        } catch (Exception e) {
            Logging.logger.error(e);
        }

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }
}
