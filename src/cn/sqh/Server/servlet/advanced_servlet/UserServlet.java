package cn.sqh.Server.servlet.advanced_servlet;

import cn.sqh.Server.domain.BasicFile;
import cn.sqh.Server.domain.User;
import cn.sqh.Server.service.UserService;
import cn.sqh.Server.service.impl.UserServiceImpl;
import cn.sqh.Server.util.Logging;
import cn.sqh.Server.util.MD5;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.UUID;

/**
 * 抽取所有跟用户操作有关的Servlet到这个Servlet里并整合成方法
 * 原理是通过父类BaseServlet的service方法使用反射执行该类的方法
 * 这样就不用通过访问/loginServlet这样的uri而是通过访问/user/login进行访问
 * 而且抽取出了UserService，不用在每个方法中分别创建实例
 */
@WebServlet("/user/*")
public class UserServlet extends BaseServlet {

    private UserService service = new UserServiceImpl();

    /**
     * 功能；用户注册
     * 调用路径：/user/register
     * @param request
     * @param response
     */
    public void register(HttpServletRequest request, HttpServletResponse response){
        try {
            HttpSession session = request.getSession(true);
            String sessionId = session.getId();
            request.setCharacterEncoding("utf-8");
            String spUserName = request.getParameter("username");
            String spUserPassword = request.getParameter("password");
            if (spUserName == null || spUserPassword == null) {
                response.setStatus(401);
                return;
            }
            User signUpedUser = service.signUp(spUserName, spUserPassword);
            if (signUpedUser != null) {
                session.setAttribute("user", signUpedUser); //将用户存到session
                String respondUser = JSONObject.toJSONString(signUpedUser);
                response.getWriter().write(respondUser);
                response.setHeader("Set-Cookie", sessionId);
            }
        } catch (Exception e) {
            Logging.logger.error(e);
        }
    }

    /**
     * 功能：用户登录
     * 调用路径：/user/login
     * @param request
     * @param response
     */
    public void login(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = request.getSession(true);
            String sessionId = session.getId();
            request.setCharacterEncoding("utf-8");

            BufferedReader br = request.getReader();
            String line = null;
            User user = null;
            while ((line = br.readLine()) != null) {
                user = JSONObject.parseObject(line, User.class);
            }
            if (user == null) {
                response.setStatus(401);
                return;
            }
            User loginUser = service.login(user);
            if (loginUser != null) {
                session.setAttribute("user", loginUser); //将用户存到session
                String respondUser = JSONObject.toJSONString(loginUser);
                response.getWriter().write(respondUser);
                response.setHeader("Set-Cookie", sessionId);
            }

        } catch (Exception e) {
            Logging.logger.error(e);
        }

    }

    /**
     * 功能：用户创建新文件夹
     * 调用路径：/user/createNewFolder
     * @param request
     * @param response
     */
    public void createNewFolder(HttpServletRequest request, HttpServletResponse response) {
        try {
            request.setCharacterEncoding("utf-8");

            String folderName = request.getParameter("folderName");
            String creatorUsername = request.getParameter("creatorUsername");
            String parentFolderId = request.getParameter("parentFolderId");
            if (folderName == null || creatorUsername == null || parentFolderId == null) {
                response.setStatus(403);
                return;
            }

            BasicFile newFolder = new BasicFile(folderName, "-", 0, Integer.parseInt(parentFolderId), BasicFile.FILETYPE_FOLDER);

            service.createNewFolder(creatorUsername, newFolder);
        } catch (Exception e) {
            Logging.logger.error(e);
        }
    }

    /**
     * 功能：从网盘下载文件
     * 调用路径：/user/downloadFile
     * @param request
     * @param response
     */
    public void downloadFile(HttpServletRequest request, HttpServletResponse response) {

        FileInputStream fis = null;

        try {
            String fileName = request.getParameter("fileName");
            if (fileName == null) {
                response.setStatus(403);
                return;
            }
            ServletContext servletContext = this.getServletContext();
            String filePath = servletContext.getRealPath("/files/" + fileName);

            fis = new FileInputStream(filePath);
            ServletOutputStream sos = response.getOutputStream();
            byte[] bytes = new byte[1024 * 8];
            int len = 0;
            while ((len = fis.read(bytes)) != -1) {
                sos.write(bytes, 0, len);
            }

        } catch (Exception e) {
            Logging.logger.error(e);
        } finally {
            try {
                fis.close();
            } catch (IOException e) {
                Logging.logger.error(e);
            }
        }

    }

    /**
     * 功能：向网盘上传文件
     * 调用路径：/user/uploadFile
     * @param request
     * @param response
     */
    public void uploadFile(HttpServletRequest request, HttpServletResponse response) {
        try {
            request.setCharacterEncoding("utf-8");

            String username = request.getParameter("uploader");
            String date = request.getParameter("uploadDate");
            String parentFolderId = request.getParameter("parentFolderId");
            if (!service.isHavaEnoughCapicity(username)) {
                response.setStatus(403);
                return;
            }

            Part picture = request.getPart("file");

            String header = picture.getHeader("Content-Disposition");

            int start = header.lastIndexOf("=") + 1;
            String name = header.substring(start).replace("\"", "");//把引号去掉

            /*//限制文件格式
            //1.方法一，通过mime限制上传文件类型
            String mimeType = request.getServletContext().getMimeType(name);//获取文件的mime类型
            //2.方法二，通过后缀名限制上传文件类型
            if (name.endsWith(".jpg")) {

            }*/

            String fileName = MD5.MD5Encode(UUID.randomUUID().toString(), "utf-8") + "@" + name;

            String savePath = request.getServletContext()
                    .getRealPath("files");
            if (fileName != null && !"".equals(fileName)) {
                picture.write(savePath + File.separator + fileName);
            }

            BasicFile file = new BasicFile(fileName, savePath, picture.getSize(), Integer.parseInt(parentFolderId), BasicFile.FILETYPE_PICTURE);

            service.uploadPicture(username, file);

        } catch (Exception e) {
            Logging.logger.error(e);
        }
    }

    /**
     * 功能：获取本页所有文件以及文件夹
     * 调用路径：/user/getFilesByParentId
     * @param request
     * @param response
     */
    public void getFilesByParentId(HttpServletRequest request, HttpServletResponse response) {
        try {
            request.setCharacterEncoding("utf-8");

            int userId = Integer.parseInt(request.getParameter("userId"));
            int parentFolderId = Integer.parseInt(request.getParameter("parentFolderId"));
            ArrayList<BasicFile> fileList = service.getAllFilesByUserIdAndParentFolderId(userId, parentFolderId);
            String s = "";
            if (fileList != null) {
                s = JSONObject.toJSONString(fileList);
            }
            response.setCharacterEncoding("utf-8");
            response.getWriter().write(s);
        } catch (Exception e) {
            Logging.logger.error(e);
        }
    }
}
