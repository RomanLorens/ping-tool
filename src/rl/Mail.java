package rl;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSenderImpl;

public class Mail {

	private final JavaMailSenderImpl sender = new JavaMailSenderImpl();
	private final Properties props;

	public Mail(Properties props) {
		this.props = props != null ? props : new Properties();
		sender.setJavaMailProperties(props);
		sender.setHost(props.getProperty("host", "localhost"));
		sender.setPort(Integer.valueOf(props.getProperty("port", "25")));
	}

	public void send(String msg) {
		try {
			MimeMessage message = sender.createMimeMessage();
			message.setText(msg);
			message.setFrom(props.getProperty("from", "donoreply.ping.report@com.pl"));
			message.setRecipients(Message.RecipientType.TO, props.getProperty("recipients", "dummy@com.pl"));
			message.setSubject(props.getProperty("subject"));
			sender.send(message);
			System.out.println("send email seccussfully");
		} catch (Exception e) {
			System.out.println("Could not send email!");
			e.printStackTrace();
		}
	}
}
