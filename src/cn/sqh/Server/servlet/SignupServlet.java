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
import java.io.IOException;

@WebServlet("/signUpServlet")
public class SignupServlet extends javax.servlet.http.HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        try {
            HttpSession session = request.getSession(true);
            request.setCharacterEncoding("utf-8");
            String spUserName = request.getParameter("username");
            String spUserPassword = request.getParameter("password");
            System.out.println("进入注册Servlet了");
            System.out.println(spUserName);
            System.out.println(spUserPassword);
            UserService service = new UserServiceImpl();
            //System.out.println("执行到创建完service了");
            User signUpedUser = service.signUp(spUserName, spUserPassword);
            System.out.println("正常执行完注册用户");
            if (signUpedUser != null) {
                session.setAttribute("user", signUpedUser); //将用户存到session
                String respondUser = JSONObject.toJSONString(signUpedUser);
                response.getWriter().write(respondUser);
                System.out.println("新注册的用户信息如下" + respondUser);
            }
        /*if(signUpedUser == null){
            System.out.println("Servlet阶段存在该用户");
            //response.getWriter().write("已存在该用户，请重新注册。");
            String respondUser = JSONObject.toJSONString(signUpedUser);
            response.getWriter().write(respondUser);
        }
        else {
            System.out.println("Servlet阶段不存在该用户");
            String respondUser = JSONObject.toJSONString(signUpedUser);
            response.getWriter().write(respondUser);
        }*/
        } catch (Exception e) {
            Logging.logger.error(e);
        }

//        response.getWriter().write("hhh");

    }

    @Override
    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        this.doPost(request, response);
    }
}
