package cn.sqh.Server.util;


import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * 发送邮件工具类
 */
public final class MailUtils {

    private static final String USER = "1020467770@qq.com";
    private static final String PASSWORD = "dawwlhhocqqbbbig";


    public static boolean sendMail(String to, String text, String title){
        try {
            final Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.host", "smtp.qq.com");

            props.put("mail.user", USER);
            props.put("mail.password", PASSWORD);


            Authenticator authenticator = new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    String userName = props.getProperty("mail.user");
                    String password = props.getProperty("mail.password");
                    return new PasswordAuthentication(userName, password);
                }
            };

            Session mailSession = Session.getInstance(props, authenticator);

            MimeMessage message = new MimeMessage(mailSession);

            String username = props.getProperty("mail.user");
            InternetAddress form = new InternetAddress(username);
            message.setFrom(form);

            InternetAddress toAddress = new InternetAddress(to);
            message.setRecipient(Message.RecipientType.TO, toAddress);

            message.setSubject(title);

            message.setContent(text, "text/html;charset=UTF-8");

            Transport.send(message);
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) throws Exception { // 做测试用
        MailUtils.sendMail("1020467770@qq.com","你好，这是一封测试邮件，无需回复。","测试邮件");
        System.out.println("发送成功");
    }

}
