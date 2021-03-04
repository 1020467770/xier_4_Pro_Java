package cn.sqh.Server.filter;

import cn.sqh.Server.util.Logging;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;

@WebFilter("/*")
public class IsLoginFilter implements Filter {
    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        String requestURI = request.getRequestURI();
        if (requestURI.contains("/loginServlet")) {
            chain.doFilter(req, resp);
        } else {
            HttpSession session = request.getSession(true);
            Object user = session.getAttribute("user");

            System.out.println("拦截时的session=" + session);
            System.out.println("拦截时的cookie=" + Arrays.toString(request.getCookies()));
            if (user != null) {
                System.out.println("filter拦截到的用户信息：" + user);
                chain.doFilter(req, resp);
            } else {
                System.out.println("没有用户");
            }
        }

    }

    @Override
    public void init(FilterConfig config) throws ServletException {

    }

}
