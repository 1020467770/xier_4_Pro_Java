package cn.sqh.Server.servlet;

import cn.sqh.Server.domain.User;
import cn.sqh.Server.service.UserService;
import cn.sqh.Server.service.impl.UserServiceImpl;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

@WebServlet("/loginServlet")
public class LoginServlet extends javax.servlet.http.HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        request.setCharacterEncoding("utf-8");

        BufferedReader br = request.getReader();
        String line = null;
        User user = null;
        while ((line = br.readLine()) != null) {
            user = JSONObject.parseObject(line, User.class);
        }
        System.out.println(user);
        UserService service = new UserServiceImpl();
        User loginUser = service.login(user);
        String respondUser = JSONObject.toJSONString(loginUser);
        response.getWriter().write(respondUser);
        System.out.println(respondUser);


    }

    @Override
    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        this.doPost(request, response);
    }
}
