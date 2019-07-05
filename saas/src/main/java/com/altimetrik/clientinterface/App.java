package com.altimetrik.clientinterface;

import java.util.List;
import java.util.ListIterator;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Scanner;
import javax.mail.MessagingException;
import com.altimetrik.databaseinterface.DatabaseManipulator;
import com.altimetrik.logiccontroller.EmailAttachmentReceiver;
import com.altimetrik.logiccontroller.EmailSender;
import com.altimetrik.logiccontroller.ExtractText;

public class App {

	public static void main(String[] args) throws IOException {

		EmailAttachmentReceiver receiver = new EmailAttachmentReceiver();
		EmailSender sender = new EmailSender();

		ExtractText pdfText = new ExtractText();
		DatabaseManipulator data = new DatabaseManipulator();

		Scanner sc = new Scanner(System.in);
		boolean askAgain = true;

		while (askAgain) {
			System.out.print(
					" 1-> Download and Parse Pdf\n 2->Show All Parsed Invoice\n 3-> Approve Individual Record\n 4-> Approve All\n 5-> Exit\n Enter Your Option:");
			int option = sc.nextInt();

			switch (option) {
			case 1:
				List<InputStream> allPdfStream = null;
				try {
					System.out.println("Downloading Email Please Wait...");

					allPdfStream = receiver.downloadEmailAttachments();
					System.out.println("Done!");
				} catch (MessagingException | IOException e1) {
					System.out.println("Unable to Download Attachment from Mail");
				}

				List<String> extractedData = null;

				try {

					extractedData = (List<String>) pdfText.pdfExtractor(allPdfStream);
					if (extractedData.size() > 0) {
						System.out.println("Extracting PDF and Inserting Data into Database...");

						for (int i = 0; i < extractedData.size(); i = i + 5) {
							data.insert(extractedData.subList(i, i + 5));
						}
					} else {
						System.out.println("No New Attachment Found!");
					}

				} catch (IOException | InterruptedException e) {
					System.out.println("Error in extracting PDF");
				} catch (SQLException e) {
					System.out.println("Error in Inserting Data into Database or Data Already Exist in Database!");
				}

				finally {
					extractedData = null;
				}

				break;
			case 2:
				try {
					List<String> retrivedData = data.retrive();
					ListIterator<String> iterator = retrivedData.listIterator();
					while (iterator.hasNext()) {
						System.out.println("Id:" + iterator.next());
						System.out.print("Invoice No:" + iterator.next());
						System.out.print("Invoice Date:" + iterator.next());
						System.out.print("Invoice Po:" + iterator.next());

						System.out.print("Address:" + iterator.next());
						System.out.print("Payable Amount:" + iterator.next());
						System.out.println("Remittance Status:" + iterator.next());
						System.out.println("-----------------------------------------------------------");
					}
				} catch (SQLException e) {
					System.out.println("Error in retriving Data from Database!");
				}
				break;
			case 3:
				System.out.println("Enter The Id:");
				try {
					List<String> values = data.modify(sc.nextInt());
					if (values.size() > 0) {
						System.out.println("All Invoice Approved");

						sender.sender(values);
					} else {
						System.out.println("All Invoice Already Approved. No New Invoice Found!");

					}
				} catch (MessagingException e) {
					System.out.println("Unable to Send Mail!");

				} catch (SQLException e) {
					System.out.println("Unable to Approve Invoice!");

				}

				break;

			case 4:

				try {
					List<String> allValues = data.modify(0);
					if (allValues.size() > 0) {
						System.out.println("All Invoice Approved");
						sender.sender(allValues);
					} else {
						System.out.println("All Invoice Already Approved. No New Invoice Found!");

					}

				} catch (MessagingException e) {
					System.out.println("Unable to Send Mail!");
				} catch (SQLException e) {
					System.out.println("Unable to Approve Invoice!");

				}

				askAgain = false;

				break;

			case 5:
				askAgain = false;
				break;
			default:
				break;

			}

		}
		sc.close();

	}

}
