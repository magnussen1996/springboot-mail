package com.bpf.springbootmail.service.impl;

import com.bpf.springbootmail.service.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Arrays;

@Service
public class MailServiceImpl implements MailService {

    private final Logger logger = LoggerFactory.getLogger(MailServiceImpl.class);

    @Value("${spring.mail.username}")
    private String from;

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendSimpleMail(String to, String subject, String content) {

        SimpleMailMessage message = new SimpleMailMessage();
        //收信人
        message.setTo(to);
        //主题
        message.setSubject(subject);
        //内容
        message.setText(content);
        //发信人
        message.setFrom(from);

        mailSender.send(message);
    }

    @Override
    public void sendHtmlMail(String to, String subject, String content) {

        logger.info("发送HTML邮件开始：{},{},{}", to, subject, content);
        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper;
        try {
            helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            //true代表支持html
            helper.setText(content, true);
            mailSender.send(message);
            logger.info("发送HTML邮件成功");
        } catch (MessagingException e) {
            logger.error("发送HTML邮件失败：", e);
        }
    }

    @Override
    public void sendAttachmentMails(String to, String subject, String content, String[] filePaths) {
        logger.info("发送邮件开始，发送至：{}；主题：{}；内容：{}；附件路径：{}", to, subject, content, filePaths);
        if (isBlank(to)) {
            logger.warn("无收件人，发送邮件失败");
            return;
        }

        if (isBlank(subject)) {
            subject = "未命名主题";
        }

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper;
        try {
            helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
        } catch (MessagingException e) {
            logger.error("设置邮件辅助器异常", e);
            return;
        }

        // 添加附件
        if (filePaths != null) {
            Arrays.stream(filePaths)
                    .filter(path -> !isBlank(path))
                    .map(path -> new FileSystemResource(new File(path)))
                    .forEach(file -> {
                        String fileName = file.getFilename();
                        if (isBlank(fileName)) {
                            fileName = "unknown";
                        }
                        try {
                            helper.addAttachment(fileName, file);
                        } catch (MessagingException e) {
                            logger.error("添加附件异常", e);
                        }
                    });
        }

        mailSender.send(message);
        logger.info("发送邮件成功");
    }

    @Override
    public void sendAttachmentMail(String to, String subject, String content, String filePath) {
        sendAttachmentMails(to, subject, content, new String[]{filePath});
    }

    @Override
    public void sendInlineResourceMail(String to, String subject, String content, String rscPath, String rscId) {

        logger.info("发送带图片邮件开始：{},{},{},{},{}", to, subject, content, rscPath, rscId);
        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper;
        try {
            helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            FileSystemResource res = new FileSystemResource(new File(rscPath));
            //重复使用添加多个图片
            helper.addInline(rscId, res);
            mailSender.send(message);
            logger.info("发送带图片邮件成功");
        } catch (MessagingException e) {
            logger.error("发送带图片邮件失败", e);
        }
    }

    private boolean isBlank(String string) {
        return string == null || "".equals(string.trim());
    }

}
