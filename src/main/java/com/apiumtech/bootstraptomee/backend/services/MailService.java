package com.apiumtech.bootstraptomee.backend.services;

import com.apiumtech.bootstraptomee.backend.model.ApplicationSettings;
import com.apiumtech.bootstraptomee.backend.model.User;

import javax.inject.Inject;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.Serializable;
import java.util.Properties;

/**
 * Mailing service
 * 
 */
@SuppressWarnings("serial")
public class MailService implements Serializable {

	@Inject
	ApplicationSettingsService applicationSettingsService;

	public void sendMail(User user, String subject, String content) throws Exception {

		ApplicationSettings settings = applicationSettingsService.get();

		final String username = settings.getSmtpUserName();
		final String password = settings.getSmtpPassword();

		String dest = user.getEmail();

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "" + settings.isSmtpTls());
		props.put("mail.smtp.host", settings.getSmtpHost());
		props.put("mail.smtp.port", "" + settings.getSmtpPort());

		Session session = Session.getInstance(props, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(username, "CommaFeed"));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(dest));
		message.setSubject("CommaFeed - " + subject);
		message.setContent(content, "text/html; charset=utf-8");

		Transport.send(message);

	}
}
