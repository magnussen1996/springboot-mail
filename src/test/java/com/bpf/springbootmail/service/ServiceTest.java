package com.bpf.springbootmail.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ServiceTest {

    @Autowired
    private MailService mailService;
    @Autowired
    private TemplateEngine templateEngine;

    private static final String EMAIL_ADDRESS = "15131260825@163.com";

    /**
     * 发送简单纯文本邮件
     */
    @Test
    public void sendSimpleMail() {
        mailService.sendSimpleMail(EMAIL_ADDRESS, "发送邮件测试", "大家好，这是我用springboot进行发送邮件测试");
    }

    /**
     * 发送HTML邮件
     */
    @Test
    public void sendHtmlMail() {
        String content = "<html><body><h3><font color=\"red\">" + "大家好，这是springboot发送的HTML邮件" + "</font></h3></body></html>";
        mailService.sendHtmlMail(EMAIL_ADDRESS, "发送邮件测试", content);
    }

    /**
     * 发送带附件的邮件
     */
    @Test
    public void sendAttachmentMail() {
        String content = "<html><body><h3><font color=\"red\">" + "大家好，这是springboot发送的HTML邮件，有附件哦" + "</font></h3></body></html>";
        String filePath = "./attach.txt";
        mailService.sendAttachmentMail(EMAIL_ADDRESS, "发送邮件测试", content, filePath);
    }

    /**
     * 发送带多个附件的邮件
     */
    @Test
    public void sendAttachmentMails() {
        String content = "<html><body><h3><font color=\"red\">" + "大家好，这是springboot发送的HTML邮件，有附件哦" + "</font></h3></body></html>";
        String[] filePaths = {"./attach.txt", "./attach1.txt"};
        mailService.sendAttachmentMails(EMAIL_ADDRESS, "发送邮件测试", content, filePaths);
    }

    /**
     * 发送带图片的邮件
     */
    @Test
    public void sendInlineResourceMail() {
        String rscPath = "./photo.png";
        String rscId = "skill001";
        String content = "<html><body><h3><font color=\"red\">" + "大家好，这是springboot发送的HTML邮件，有图片哦" + "</font></h3>"
                + "<img src=\'cid:" + rscId + "\'></body></html>";
        mailService.sendInlineResourceMail(EMAIL_ADDRESS, "发送邮件测试", content, rscPath, rscId);
    }

    /**
     * 指定模板发送邮件
     */
    @Test
    public void testTemplateMail() {
        //向Thymeleaf模板传值，并解析成字符串
        Context context = new Context();
        context.setVariable("id", "001");
        String emailContent = templateEngine.process("emailTemplate", context);

        mailService.sendHtmlMail(EMAIL_ADDRESS, "这是一个模板文件", emailContent);
    }
}
