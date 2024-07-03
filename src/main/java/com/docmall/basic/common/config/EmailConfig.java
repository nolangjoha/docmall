package com.docmall.basic.common.config;

import java.util.Properties;

//import org.eclipse.angus.mail.util.MailSSLSocketFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j // 로그출력 목적
@Configuration //빈 등록작업
@PropertySource("classpath:mail/email.properties")  
public class EmailConfig {

	public EmailConfig() throws Exception{
		log.info("EmailConfig.java constructor called...");
	}
	
	//현재 사용 않지만 아래 영향을 주는것 같으므로 살려두겠다.
	//email.properties 파일의 설정정보를 참조
	@Value("${spring.mail.transport.protocol}") //값을 참조: 업로드에서 사용한적 있는 어노테이션
	private String protocol; //smtp
	//현재 사용 않지만 아래 영향을 주는것 같으므로 살려두겠다.
	@Value("${spring.mail.debug}")
	private boolean debug;
	
	
	
	@Value("${spring.mail.properties.mail.smtp.auth}")
	private boolean auth;
	
	@Value("${spring.mail.properties.mail.smtp.starttls.enable}")
	private boolean starttls;

	@Value("${spring.mail.host}")
	private String host;
	
	@Value("${spring.mail.port}")
	private int port;
	
	@Value("${spring.mail.username}")
	private String username;
	
	@Value("${spring.mail.password}")
	private String password;
	
	@Value("${spring.mail.default-encoding}")
	private String encoding;
	
	@Bean  //JavaMailSender : 스프링에서 메일발송하는 객체 /JavaMailSender : bean생성 및 스프링 컨테이너에 등록
	//빈의 목적은 주입(DI)
	public JavaMailSender javaMailSender() {
		
		//JavaMailSenderImpl클래스가 어떤 메일서버를 이용하여 메일발송을 할지 서버에 대한 정보를 구성하는 작업
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		
		Properties properties = new Properties();
	
//		properties.put("mail.transport.protocol", protocol);
		properties.put("mail.smtp.auth", auth);
		properties.put("mail.smtp.starttls.enable", starttls);
//		properties.put("mail.smtp.debug", debug);
		
//		properties.put("mail.smtp.ssl.protocol", "TLSv1.2");
		
//		properties.put("mail.smtp.socketFactory.port", "25");
//		properties.put("mail.socketFactory.class", "javax.net.ssl.SSLSocketFactory" );
//		properties.put("mail.socketFactory.fallback", "true" );

		
		/*************운영체제별 벌어지는 문제를 잡기위해 추가한 구문***********/
		/*import 패키지 나중에 확인 할 것.*/
		/*
		MailSSLSocketFactory sf = null;
		
		try {
			sf = new MailSSLSocketFactory();
		}catch(GeneralSecurityException e) {
			e.printStackTrace();
		}
		sf.setTrustAllHosts(true);
		properties.put("mail.smtp.ssl.socketFactory", sf);
		*/
		/*********************************************************/
	
		mailSender.setHost(host);
		mailSender.setUsername(username);
		mailSender.setPassword(password);
		mailSender.setPort(port);
		mailSender.setJavaMailProperties(properties);
		mailSender.setDefaultEncoding(encoding);
		
		System.out.println("메일서버:" + host);
		log.info("메일서버로그:" + host);
		
		return mailSender;
		
	}
}
