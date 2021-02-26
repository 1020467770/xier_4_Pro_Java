package cn.sqh.Server.servlet;


import cn.sqh.Server.domain.BasicFile;
import cn.sqh.Server.service.UserService;
import cn.sqh.Server.service.impl.UserServiceImpl;
import cn.sqh.Server.util.Logging;
import cn.sqh.Server.util.MD5;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.UUID;

@MultipartConfig
@WebServlet("/uploadFileServlet")
public class UploadFileServlet extends HttpServlet {//可以上传任意类型的文件，不只是图片

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            request.setCharacterEncoding("utf-8");

            String username = request.getParameter("uploader");
            String date = request.getParameter("uploadDate");
            String parentFolderId = request.getParameter("parentFolderId");
            Timestamp uploadDate = Timestamp.valueOf(date);
//        System.out.println(uploadDate);


            Part picture = request.getPart("file");

            String header = picture.getHeader("Content-Disposition");
//        System.out.println("head = " + header);//form-data; name="file"; filename="IMG_20210217_140028.jpg"

            int start = header.lastIndexOf("=") + 1;
            String name = header.substring(start).replace("\"", "");//把引号去掉

            /*//限制文件格式
            //1.方法一，通过mime限制上传文件类型
            String mimeType = request.getServletContext().getMimeType(name);//获取文件的mime类型
            //2.方法二，通过后缀名限制上传文件类型
            if (name.endsWith(".jpg")) {

            }*/

            // 生成随机文件名
            String fileName = MD5.MD5Encode(UUID.randomUUID().toString(), "utf-8") + "@" + name;
//        System.out.println(fileName);

            //文件存放
            String savePath = request.getServletContext()
                    .getRealPath("files");
            if (fileName != null && !"".equals(fileName)) {
                // 写入磁盘
                picture.write(savePath + File.separator + fileName);
            }

            UserService service = new UserServiceImpl();

            BasicFile file = new BasicFile(fileName, savePath, picture.getSize(), Integer.parseInt(parentFolderId), BasicFile.FILETYPE_PICTURE);

            service.uploadPicture(username, file);
        } catch (Exception e) {
            Logging.logger.error(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }
}
