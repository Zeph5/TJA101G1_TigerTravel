package com.member.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {
	
	@Autowired
	private JavaMailSender mailSender;
	
	public void sendVerificationEmail(String to,String code) {
		String subject = "【TigerTravel】註冊驗證碼通知";
		String text = "親愛的用戶您好，\n\n您的驗證碼為：" + code + "**\n" +
						"請於 5 分鐘內輸入驗證碼完成註冊。\n\n" +
						"若您未進行註冊，請忽略此信件。";
		
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setSubject(subject);
		message.setText(text);
		mailSender.send(message);
		
	}
}
