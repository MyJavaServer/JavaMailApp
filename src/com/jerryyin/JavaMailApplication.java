package com.jerryyin;

import com.jerryyin.config.JMConfig;
import com.jerryyin.manager.JMailManager;
import com.sun.tools.javac.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class JavaMailApplication {


    // 发件人的 邮箱 和 密码（替换为自己的邮箱和密码）
    public static String myEmailAccount = "your account @xxx.com";
    // PS: 某些邮箱服务器为了增加邮箱本身密码的安全性，给 SMTP 客户端设置了独立密码（有的邮箱称为“授权码”）,
    //对于开启了独立密码的邮箱, 这里的邮箱密码必需使用这个独立密码（授权码）。
    public static String myEmailPassword = "your password";

    // 发件人邮箱的 SMTP 服务器地址, 必须准确, 不同邮件服务器地址不同, 一般(只是一般, 绝非绝对)格式为: smtp.xxx.com
    public static String myEmailSMTPHost = JMConfig.SERVER_SMTP;


    public static JMailManager mJMailManager;
    public static MimeMessage mMimeMessage;


    public static void main(String[] args) {
        // write your code here
        System.out.print("---------JavaMailApp is runninng...-----------");


        mJMailManager = JMailManager.getInstance(null);

        sendNormalMail();


    }



    /**
     * 发送一封普通邮件
     */
    private static void sendNormalMail() {
        ArrayList<JMailManager.MailUser> toUsers = new ArrayList<>();
        toUsers.add(new JMailManager.MailUser("xxxx@qq.com", "xxx"));

//        ArrayList<JMailManager.MailUser> ccUsers = new ArrayList<>();
//        ccUsers.add(new JMailManager.MailUser("xxx@qq.com", "xxx"));

//        ArrayList<JMailManager.MailUser> bccUsers = new ArrayList<>();
//        bccUsers.add(new JMailManager.MailUser("xxx@trini-cloud.com", "xxx"));
        try {
            mMimeMessage = mJMailManager.createEmailMessage(
                    new JMailManager.MailUser(myEmailAccount, "Developer Yin"),
                    "这是邮件主题！",
                    "这是一封测试邮件的正文内容，阿哈哈哈哈哈， 啥都没有。。。",
                    toUsers,
                    null,
                    null
            );

            mJMailManager.sendMail(mMimeMessage, myEmailAccount, myEmailPassword, myEmailSMTPHost);

        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.print(e.getMessage());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            System.out.print(e.getMessage());
        }
    }


}
