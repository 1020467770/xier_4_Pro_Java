package cn.sqh.Server.servlet.advanced_servlet;

import cn.sqh.Server.domain.BasicFile;
import cn.sqh.Server.domain.User;
import cn.sqh.Server.service.UserService;
import cn.sqh.Server.service.impl.UserServiceImpl;
import cn.sqh.Server.util.Logging;
import cn.sqh.Server.util.MD5;
import com.alibaba.fastjson.JSONObject;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Random;
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
     *
     * @param request
     * @param response
     */
    public void register(HttpServletRequest request, HttpServletResponse response) {
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
     *
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
     *
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
     *
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
     *
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
     *
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

    /**
     * 功能：删除单个文件或文件夹
     * 调用路径：/user/deleteFile
     *
     * @param request
     * @param response
     */
    public void deleteFile(HttpServletRequest request, HttpServletResponse response) {
        try {
            int fileId = Integer.parseInt(request.getParameter("fileId"));
//            service.deleteFile(fileId);这个会删除数据库的数据
            service.deleteLocalFile(fileId);
        } catch (Exception e) {
            Logging.logger.error(e);
        }
    }

    /**
     * 功能：一次删除多个文件或文件夹
     * 调用路径：/user/deleteFile
     *
     * @param request
     * @param response
     */
    public void deleteFiles(HttpServletRequest request, HttpServletResponse response) {
        try {
            //id分别为1,2,3,4,50,13123
            String fileIds = request.getParameter("fileIds");
            String[] strings = fileIds.split(",");
//            service.deleteFile(fileId);
            if (strings != null) {
                for (String string : strings) {
                    service.deleteLocalFile(Integer.parseInt(string));//仅删除本地图片
                }
            }
        } catch (Exception e) {
            Logging.logger.error(e);
        }
    }

    /**
     * 功能：获取验证码图片
     * 调用路径：/user/getVerifyPic
     *
     * @param request
     * @param response
     */
    public void getVerifyPic(HttpServletRequest request, HttpServletResponse response) {
        try {
            int width = 80;
            int height = 30;
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics g = image.getGraphics();
            g.setColor(Color.GRAY);
            g.fillRect(0, 0, width, height);
            String base = "0123456789ABCDEFGabcdefg";
            int size = base.length();
            Random r = new Random();
            StringBuffer sb = new StringBuffer();
            for (int i = 1; i <= 4; i++) {
                int index = r.nextInt(size);
                char c = base.charAt(index);
                sb.append(c);
            }
            String checkCode = sb.toString();
            request.getSession().setAttribute("CHECKCODE_SERVER", checkCode);
            g.setColor(Color.YELLOW);
            g.setFont(new Font("黑体", Font.BOLD, 24));
            g.drawString(checkCode, 15, 25);
            ImageIO.write(image, "PNG", response.getOutputStream());
        } catch (Exception e) {
            Logging.logger.error(e);
        }
    }

    /**
     * 功能：客户端申请找回密码
     * 请求url：/user/tryChangePassword
     *
     * @param request
     * @param response
     */
    public void tryChangePassword(HttpServletRequest request, HttpServletResponse response) {
        try {
            int userId = Integer.parseInt(request.getParameter("userId"));
            String newPassword = request.getParameter("newPassword");
            int tryType = Integer.parseInt(request.getParameter("tryType"));
            service.tryChangePsw(userId, newPassword, tryType);
        } catch (Exception e) {
            Logging.logger.error(e);
        }
    }

    /**
     * 功能：邮箱验证超链接跳转找回密码
     * 请求url：/user/changePasswordByEmail
     *
     * @param request
     * @param response
     */
    public void changePasswordByEmail(HttpServletRequest request, HttpServletResponse response) {
        try {
            String code = request.getParameter("code");//更改密码所需的激活码
            if (code != null) {
                boolean flag = service.changePassword(code);
                String msg = null;
                if (flag) {
                    msg = "激活成功";
                } else {
                    msg = "激活失败，请联系管理员";
                }
                response.setContentType("text/html;charset=utf-8");
                response.getWriter().write(msg);
            }
        } catch (Exception e) {
            Logging.logger.error(e);
        }
    }

    /**
     * 通过邮箱激活用户功能
     * 请求url：/user/activeByEmail
     *
     * @param request
     * @param response
     */
    public void activeByEmail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String code = request.getParameter("code");
            if (code != null) {
                boolean flag = service.activeUser(code);
                String msg = null;
                if (flag) {
                    msg = "激活成功";
                } else {
                    msg = "激活失败，请联系管理员";
                }
                response.setContentType("text/html;charset=utf-8");
                response.getWriter().write(msg);
            }
        } catch (Exception e) {
            Logging.logger.error(e);
        }
    }
}
