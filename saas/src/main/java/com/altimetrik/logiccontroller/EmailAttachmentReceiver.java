package com.altimetrik.logiccontroller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeBodyPart;

public class EmailAttachmentReceiver {
	public List<InputStream> downloadEmailAttachments()
			throws MessagingException, IOException {

		Properties properties = new Properties();

		try (InputStream input = new FileInputStream(
				"src\\main\\resources\\properties\\config.properties")) {

			properties.load(input);

		} catch (IOException ex) {
			System.out.println("Unable to load property file");
		}

		// server setting

		Session session = Session.getDefaultInstance(properties);
		Folder folderInbox = null;
		Store store = null;
		List<InputStream> allPdfStream = new ArrayList<InputStream>();

		try {
			// connects to the message store
			store = session.getStore("pop3");
			store.connect(properties.getProperty("userName"), properties.getProperty("password"));

			// opens the inbox folder
			folderInbox = store.getFolder("INBOX");
			folderInbox.open(Folder.READ_ONLY);

			// fetches new messages from server
			Message[] arrayMessages = folderInbox.getMessages();

			for (int i = 0; i < arrayMessages.length; i++) {
				Message message = arrayMessages[i];
				String contentType = message.getContentType();
				String attachFiles = "";

				if (contentType.contains("multipart")) {
					// content may contain attachments
					Multipart multiPart = (Multipart) message.getContent();
					int numberOfParts = multiPart.getCount();
					for (int partCount = 0; partCount < numberOfParts; partCount++) {
						MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
						if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
							// this part is attachment

							InputStream pdfStream = null;
							try {
								// new input stream created

								pdfStream = part.getInputStream();

								allPdfStream.add(pdfStream);
							} finally {
								// releases system resources associated with
								if (pdfStream != null)
									pdfStream.close();
							}

						} else {
						}
					}

					if (attachFiles.length() > 1) {
						attachFiles = attachFiles.substring(0, attachFiles.length() - 2);
					}
				} else if (contentType.contains("text/plain") || contentType.contains("text/html")) {
					Object content = message.getContent();
					if (content != null) {
					}
				}

			}

		} finally {

			folderInbox.close(false);
			store.close();
		}
		return allPdfStream;
	}

}