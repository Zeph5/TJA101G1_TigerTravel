package com.member.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.member.model.memVO;

@Service
public class MailService {
	
	@Autowired
	private JavaMailSender mailSender;

	public void sendEmail(String to , String subject, String content) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setSubject(subject);
		message.setText(content);
		mailSender.send(message);
	}
	
	public void sendVerificationEmail(String to,String code) {
		String subject = "【TigerTravel】註冊驗證碼通知";
		String text = "親愛的用戶您好，\n\n您的驗證碼為：" + code + "\n" +
						"請於 5 分鐘內輸入驗證碼完成註冊。\n\n" +
						"若您未進行註冊，請忽略此信件。";
		
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setSubject(subject);
		message.setText(text);
		mailSender.send(message);
		
	}
	
	public void sendVerificationEmail(memVO member, String url) {
	    String subject = "請驗證您的信箱";
	    String content = "親愛的 " + member.getMemberName() + ",\n請點擊以下連結完成信箱驗證:\n" + url;
	    sendEmail(member.getMemberEmail(), subject, content);
	    System.out.println("[DEBUG] Verification email sent to: " + member.getMemberEmail());
	}

	public void sendResetPasswordEmail(memVO member, String url) {
	    String subject = "重設密碼連結";
	    String content = "您可以透過以下連結重新設定密碼:\n" + url + "\n如非本人操作請忽略此信件。";
	    sendEmail(member.getMemberEmail(), subject, content);
	    System.out.println("[DEBUG] Reset password email sent to: " + member.getMemberEmail());
	}
	
	//新增：寄送連結驗證信 (給正式Email 驗證用)
//	public void sendVerificationLink(String to, String link) {
//		String subject = "【TigerTravle】帳號啟用驗證信";
//		String text = "親愛的用戶您好，\n\n" +
//						"請點擊下方連結完成帳號驗證：\n" +
//						link + "\n\n" +
//						"若您未註冊此帳號，請忽略此封信件";
//		
//		SimpleMailMessage message = new SimpleMailMessage();
//		message.setTo(to);
//		message.setSubject(subject);
//		message.setText(text);
//		mailSender.send(message);
//	}
	
	//寄送重設密碼信功能
	public void sendResetPasswordEmail(String to, String resetLink) {
	    String subject = "【TigerTravel】重設密碼通知";
	    String text = "親愛的用戶您好，\n\n" +
	                  "請點擊下方連結來重設您的密碼：\n" +
	                  resetLink + "\n\n" +
	                  "若您未申請此操作，請忽略此封信件";

	    SimpleMailMessage message = new SimpleMailMessage();
	    message.setTo(to);
	    message.setSubject(subject);
	    message.setText(text);
	    mailSender.send(message);
	}
	
}
