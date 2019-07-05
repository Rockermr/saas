package com.altimetrik.logiccontroller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailSender {

	public void sendEmail(String mail) throws AddressException, MessagingException, IOException {
		InputStream input = new FileInputStream(
				"src\\main\\resources\\properties\\SenderConfig.properties");
		Properties properties = new Properties();
		Session mailSession;
		MimeMessage emailMessage;

		properties.load(input);

		String emailBody = mail;

		mailSession = Session.getInstance(properties, null);
		emailMessage = new MimeMessage(mailSession);

		emailMessage.addRecipient(Message.RecipientType.TO,
				new InternetAddress(properties.getProperty("toEmail").toString()));

		emailMessage.setSubject(properties.getProperty("subject"));
		emailMessage.setContent(emailBody, "text/plain");

		Transport transport = mailSession.getTransport("smtp");

		transport.connect(properties.getProperty("host"), properties.getProperty("fromEmail"),
				properties.getProperty("password"));
		transport.sendMessage(emailMessage, emailMessage.getAllRecipients());
		transport.close();
		System.out.println("Email sent successfully.");
	}

	public void sender(List<String> values) throws AddressException, MessagingException, IOException {
		StringBuilder str = new StringBuilder();

		str.append(
				"Dear Finance Department,\n\n The Following Invoices Have been Approved By The Purchase Department\n Please Make Timely Settlement To The Respective Vendor\n\n");
		Iterator<String> itr = values.iterator();
		while (itr.hasNext()) {
			str.append("* Invoice No: " + itr.next() + "  Amount: " + itr.next() + "\n");
		}
		str.append("\n With Regards\n Head\n Purchase Department\n");
		sendEmail(str.toString());

	}

}
