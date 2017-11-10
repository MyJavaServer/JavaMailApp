package com.jerryyin.manager;

import com.jerryyin.config.JMConfig;
import com.sun.org.apache.regexp.internal.RE;
import com.sun.tools.javac.util.List;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

public class JMailManager {


    private static final String TAG = "JMailManager";
    private static final String RESP_SEND_ERR = "邮件发送异常!";


    public static JMailManager mInstance;

    //    private Properties mProperties;
    private Session mSession;
    private MimeMessage mMimeMessage;


    public JMailManager(Session session) {
        init(session);
    }

    private void init(Session session) {
        Properties properties = new Properties();           // 用于连接邮件服务器的参数配置（发送邮件时才需要用到）
        if (session == null)
            mSession = Session.getInstance(properties);     // 根据参数配置，创建会话对象（为了发送邮件准备的）
        else
            mSession = session;
        mMimeMessage = new MimeMessage(mSession);           // 创建邮件对象
    }

    public static JMailManager getInstance(Session session) {
        if (mInstance == null)
            mInstance = new JMailManager(session);
        return mInstance;
    }



    /**
     * 邮件收发人
     */
    public static class MailUser {
        private String emailAddress;
        private String nickName;

        public MailUser(String addr, String name) {
            this.emailAddress = addr;
            this.nickName = name;
        }

        public String getEmailAddress() {
            return emailAddress;
        }

        public void setEmailAddress(String emailAddress) {
            this.emailAddress = emailAddress;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }
    }




//    public void samples() throws IOException, MessagingException {
//        Properties properties = new Properties();               // 用于连接邮件服务器的参数配置（发送邮件时才需要用到）
//        Session session = Session.getInstance(properties);      // 根据参数配置，创建会话对象（为了发送邮件准备的）
//
//        MimeMessage message = new MimeMessage(session);     // 创建邮件对象
//
//        /*
//         * 也可以根据已有的eml邮件文件创建 MimeMessage 对象
//         * MimeMessage message = new MimeMessage(session, new FileInputStream("MyEmail.eml"));
//         */
//
//        // 2. From: 发件人
//        //    其中 InternetAddress 的三个参数分别为: 邮箱, 显示的昵称(只用于显示, 没有特别的要求), 昵称的字符集编码
//        //    真正要发送时, 邮箱必须是真实有效的邮箱。
//        message.setFrom(new InternetAddress("aa@send.com", "USER_AA", "UTF-8"));
//
//        // 3. To: 收件人
//        message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress("cc@receive.com", "USER_CC", "UTF-8"));
//        //    To: 增加收件人（可选）
//        message.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress("dd@receive.com", "USER_DD", "UTF-8"));
//        //    Cc: 抄送（可选）
//        message.setRecipient(MimeMessage.RecipientType.CC, new InternetAddress("ee@receive.com", "USER_EE", "UTF-8"));
//        //    Bcc: 密送（可选）
//        message.setRecipient(MimeMessage.RecipientType.BCC, new InternetAddress("ff@receive.com", "USER_FF", "UTF-8"));
//
//        // 4. Subject: 邮件主题
//        message.setSubject("TEST邮件主题", "UTF-8");
//
//        // 5. Content: 邮件正文（可以使用html标签）
//        message.setContent("TEST这是邮件正文。。。", "text/html;charset=UTF-8");
//
//        // 6. 设置显示的发件时间
//        message.setSentDate(new Date());
//
//        // 7. 保存前面的设置
//        message.saveChanges();
//
//        // 8. 将该邮件保存到本地
//        OutputStream out = new FileOutputStream("MyEmail.eml");
//        message.writeTo(out);
//        out.flush();
//        out.close();
//    }


    /**
     * 发送邮件
     *
     * @param fromUser 发送人              ("aa@send.com", "USER_AA")
     * @param subject  主题                "TEST邮件主题"
     * @param content  内容正文             "TEST这是邮件正文。。。"
     * @param toUsers  接收人列表（可选）    ("cc@receive.com", "USER_CC")..
     * @param ccUsers  抄送人列表（可选）    ("dd@receive.com", "USER_DD")..
     * @param bccUsers 密送人列表（可选）    ("ee@receive.com", "USER_EE")..
     * @return MimeMessage 邮件对象
     * @throws MessagingException
     * @throws UnsupportedEncodingException
     */
    public MimeMessage createEmailMessage(MailUser fromUser, String subject, String content, ArrayList<MailUser> toUsers, ArrayList<MailUser> ccUsers, ArrayList<MailUser> bccUsers) throws MessagingException, UnsupportedEncodingException {
        /*
         * 也可以根据已有的eml邮件文件创建 MimeMessage 对象
         * MimeMessage message = new MimeMessage(session, new FileInputStream("MyEmail.eml"));
         */

        // 2. From: 发件人
        //    其中 InternetAddress 的三个参数分别为: 邮箱, 显示的昵称(只用于显示, 没有特别的要求), 昵称的字符集编码
        //    真正要发送时, 邮箱必须是真实有效的邮箱。
        InternetAddress internetAddress = new InternetAddress(fromUser.getEmailAddress(), fromUser.getNickName(), "UTF-8");
        mMimeMessage.setFrom(internetAddress);

        // 3. To: 收件人
        if (toUsers != null && toUsers.size() > 0)
            for (MailUser user : toUsers)
                mMimeMessage.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(user.getEmailAddress(), user.getNickName(), "UTF-8"));

        //    Cc: 抄送（可选）
        if (ccUsers != null && ccUsers.size()>0)
            for (MailUser user: ccUsers)
                mMimeMessage.setRecipient(MimeMessage.RecipientType.CC, new InternetAddress(user.getEmailAddress(), user.getNickName(), "UTF-8"));

        //    Bcc: 密送（可选）
        if (bccUsers != null && bccUsers.size()>0)
            for (MailUser user: bccUsers)
        mMimeMessage.setRecipient(MimeMessage.RecipientType.BCC, new InternetAddress(user.getEmailAddress(), user.getNickName(),"UTF-8"));

        // 4. Subject: 邮件主题
        mMimeMessage.setSubject(subject, "UTF-8");

        // 5. Content: 邮件正文（可以使用html标签）
        mMimeMessage.setContent(content, "text/html;charset=UTF-8");

        // 6. 设置显示的发件时间
        mMimeMessage.setSentDate(new Date());

        // 7. 保存前面的设置
        mMimeMessage.saveChanges();


//        保存到本地
//        try {
//            // 8. 将该邮件保存到本地
//            OutputStream out = new FileOutputStream("MyEmail.eml");
//            mMimeMessage.writeTo(out);
//            out.flush();
//            out.close();
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//            System.out.print(TAG + RESP_SEND_ERR + e.getMessage());
//            return null;
//        } catch (IOException e) {
//            e.printStackTrace();
//            System.out.print(TAG + RESP_SEND_ERR + e.getMessage());
//            return null;

        return mMimeMessage;
    }


    public boolean sendMail(MimeMessage mimeMessage, String fromAccount, String fromPwd, String smtpServer) throws MessagingException {
        // 1. 创建参数配置, 用于连接邮件服务器的参数配置
        Properties props = new Properties();                    // 参数配置
        props.setProperty("mail.transport.protocol", "smtp");   // 使用的协议（JavaMail规范要求）
        props.setProperty("mail.smtp.host", smtpServer);   // 发件人的邮箱的 SMTP 服务器地址
        props.setProperty("mail.smtp.auth", "true");            // 需要请求认证

        // PS: 某些邮箱服务器要求 SMTP 连接需要使用 SSL 安全认证 (为了提高安全性, 邮箱支持SSL连接, 也可以自己开启),
        //     如果无法连接邮件服务器, 仔细查看控制台打印的 log, 如果有有类似 “连接失败, 要求 SSL 安全连接” 等错误,
        //     打开下面 /* ... */ 之间的注释代码, 开启 SSL 安全连接。
        /*
        // SMTP 服务器的端口 (非 SSL 连接的端口一般默认为 25, 可以不添加, 如果开启了 SSL 连接,
        //                  需要改为对应邮箱的 SMTP 服务器的端口, 具体可查看对应邮箱服务的帮助,
        //                  QQ邮箱的SMTP(SLL)端口为465或587, 其他邮箱自行去查看)
        final String smtpPort = "465";
        props.setProperty("mail.smtp.port", smtpPort);
        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.socketFactory.port", smtpPort);
        */

        // 2. 根据配置创建会话对象, 用于和邮件服务器交互
        Session session = Session.getInstance(props);
        session.setDebug(true);                                 // 设置为debug模式, 可以查看详细的发送 log


        // 4. 根据 Session 获取邮件传输对象
        Transport transport = session.getTransport();

        // 5. 使用 邮箱账号 和 密码 连接邮件服务器, 这里认证的邮箱必须与 message 中的发件人邮箱一致, 否则报错
        //
        //    PS_01: 成败的判断关键在此一句, 如果连接服务器失败, 都会在控制台输出相应失败原因的 log,
        //           仔细查看失败原因, 有些邮箱服务器会返回错误码或查看错误类型的链接, 根据给出的错误
        //           类型到对应邮件服务器的帮助网站上查看具体失败原因。
        //
        //    PS_02: 连接失败的原因通常为以下几点, 仔细检查代码:
        //           (1) 邮箱没有开启 SMTP 服务;
        //           (2) 邮箱密码错误, 例如某些邮箱开启了独立密码;
        //           (3) 邮箱服务器要求必须要使用 SSL 安全连接;
        //           (4) 请求过于频繁或其他原因, 被邮件服务器拒绝服务;
        //           (5) 如果以上几点都确定无误, 到邮件服务器网站查找帮助。
        //
        //    PS_03: 仔细看log, 认真看log, 看懂log, 错误原因都在log已说明。
        transport.connect(fromAccount, fromPwd);

        // 6. 发送邮件, 发到所有的收件地址, message.getAllRecipients() 获取到的是在创建邮件对象时添加的所有收件人, 抄送人, 密送人
        transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());

        // 7. 关闭连接
        transport.close();

        return true;
    }




    /**
     * 创建一封复杂邮件（文本+图片+附件）
     */
    public static MimeMessage createMimeMessage(Session session, String sendMail, String receiveMail) throws Exception {
        // 1. 创建邮件对象
        MimeMessage message = new MimeMessage(session);

        // 2. From: 发件人
        message.setFrom(new InternetAddress(sendMail, "我的测试邮件_发件人昵称", "UTF-8"));

        // 3. To: 收件人（可以增加多个收件人、抄送、密送）
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(receiveMail, "我的测试邮件_收件人昵称", "UTF-8"));

        // 4. Subject: 邮件主题
        message.setSubject("TEST邮件主题（文本+图片+附件）", "UTF-8");

        /*
         * 下面是邮件内容的创建:
         */

        // 5. 创建图片“节点”
        MimeBodyPart image = new MimeBodyPart();
        DataHandler dh = new DataHandler(new FileDataSource("FairyTail.jpg")); // 读取本地文件
        image.setDataHandler(dh);                   // 将图片数据添加到“节点”
        image.setContentID("image_fairy_tail");     // 为“节点”设置一个唯一编号（在文本“节点”将引用该ID）

        // 6. 创建文本“节点”
        MimeBodyPart text = new MimeBodyPart();
        //    这里添加图片的方式是将整个图片包含到邮件内容中, 实际上也可以以 http 链接的形式添加网络图片
        text.setContent("这是一张图片<br/><img src='cid:image_fairy_tail'/>", "text/html;charset=UTF-8");

        // 7. （文本+图片）设置 文本 和 图片 “节点”的关系（将 文本 和 图片 “节点”合成一个混合“节点”）
        MimeMultipart mm_text_image = new MimeMultipart();
        mm_text_image.addBodyPart(text);
        mm_text_image.addBodyPart(image);
        mm_text_image.setSubType("related");    // 关联关系

        // 8. 将 文本+图片 的混合“节点”封装成一个普通“节点”
        //    最终添加到邮件的 Content 是由多个 BodyPart 组成的 Multipart, 所以我们需要的是 BodyPart,
        //    上面的 mm_text_image 并非 BodyPart, 所有要把 mm_text_image 封装成一个 BodyPart
        MimeBodyPart text_image = new MimeBodyPart();
        text_image.setContent(mm_text_image);

        // 9. 创建附件“节点”
        MimeBodyPart attachment = new MimeBodyPart();
        DataHandler dh2 = new DataHandler(new FileDataSource("附件文档.doc"));  // 读取本地文件
        attachment.setDataHandler(dh2);                                             // 将附件数据添加到“节点”
        attachment.setFileName(MimeUtility.encodeText(dh2.getName()));              // 设置附件的文件名（需要编码）

        // 10. 设置（文本+图片）和 附件 的关系（合成一个大的混合“节点” / Multipart ）
        MimeMultipart mm = new MimeMultipart();
        mm.addBodyPart(text_image);
        mm.addBodyPart(attachment);     // 如果有多个附件，可以创建多个多次添加
        mm.setSubType("mixed");         // 混合关系

        // 11. 设置整个邮件的关系（将最终的混合“节点”作为邮件的内容添加到邮件对象）
        message.setContent(mm);

        // 12. 设置发件时间
        message.setSentDate(new Date());

        // 13. 保存上面的所有设置
        message.saveChanges();

        return message;
    }


}









