package cn.sqh.Server.servlet;

import cn.sqh.Server.util.Logging;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;

@WebServlet("/downloadServlet")
public class DownloadServlet extends HttpServlet {//可以下载任意类型的文件，不只是图片

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        FileInputStream fis = null;

        try {
            String fileName = request.getParameter("fileName");
            System.out.println(fileName);
            if (fileName == null) {
                response.setStatus(403);
                return;
            }
            ServletContext servletContext = this.getServletContext();
            String filePath = servletContext.getRealPath("/files/" + fileName);
            System.out.println(filePath);

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
            fis.close();
        }

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }
}
