package cn.sqh.Server.servlet;

import cn.sqh.Server.domain.User;
import cn.sqh.Server.service.UserService;
import cn.sqh.Server.service.impl.UserServiceImpl;
import cn.sqh.Server.util.Logging;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;

@WebServlet("/loginServlet")
public class LoginServlet extends javax.servlet.http.HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        try {
            HttpSession session = request.getSession(true);
            request.setCharacterEncoding("utf-8");

            BufferedReader br = request.getReader();
            String line = null;
            User user = null;
            while ((line = br.readLine()) != null) {
                user = JSONObject.parseObject(line, User.class);
            }
            System.out.println(user);
            if (user == null) {
                response.setStatus(401);
                return;
            }
            UserService service = new UserServiceImpl();
            User loginUser = service.login(user);
            if (loginUser != null) {
                session.setAttribute("user", loginUser); //将用户存到session
                System.out.println("登录时的sesion=" + session);
                String respondUser = JSONObject.toJSONString(loginUser);
                response.getWriter().write(respondUser);
                System.out.println(respondUser);
            }

        } catch (Exception e) {
            Logging.logger.error(e);
        }

    }

    @Override
    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        this.doPost(request, response);
    }
}
