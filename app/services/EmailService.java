package services;

import java.util.Properties;
import java.util.Set;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import play.Logger;

import controllers.Emails;

import models.Invitation;
import models.PasswordReset;

//IAM User Name,Smtp Username,Smtp Password
//"support",AKIAJQ72M6Z7B4MEJIOQ,AgpIxZ2ns1fJI09fUrXgVoEDWDMjKukXYPBsZ8ybqSsB
public class EmailService {

	private static EmailService instance;


	// Supply your SMTP credentials below. Note that your SMTP credentials are different from your AWS credentials.
	static final String SMTP_USERNAME = "AKIAJQ72M6Z7B4MEJIOQ";  // Replace with your SMTP username.
	static final String SMTP_PASSWORD = "AgpIxZ2ns1fJI09fUrXgVoEDWDMjKukXYPBsZ8ybqSsB";  // Replace with your SMTP password.

	static final String FROM = "misael@socialfunnel.net";

	// Amazon SES SMTP host name.
	static final String HOST = "email-smtp.us-east-1.amazonaws.com";    

	// Port we will connect to on the Amazon SES SMTP endpoint. We are choosing port 25 because we will use
	// STARTTLS to encrypt the connection.
	static final int PORT = 465;

	private EmailService() {}

	public static EmailService getInstance(){
		if(instance == null){
			instance = new EmailService();
		}

		return instance;
	}

	//	public void sendEmailInvite(Invitation invitation) {
	//		// TODO Auto-generated method stub
	//		try {
	//
	//			HtmlEmail email = new HtmlEmail();
	//			email.setSmtpPort(465);
	//			email.setSSLOnConnect(true);
	//			email.setAuthenticator(new DefaultAuthenticator("misael@socialfunnel.net", "Misa99zz!"));
	//			email.setDebug(false);
	//			email.setHostName("smtp.gmail.com");
	//			email.setFrom("noreply@socialfunnel.net");
	//			email.setSubject("[Social Funnel] Você foi adicionado como analista para a marca " + invitation.getBrand().getName());
	//			email.setHtmlMsg(Emails.invitationEmail(invitation).toString());
	//			email.setTextMsg("Isso é um email do tipo texto. Email html foi rejeitado.");
	//			email.addTo(invitation.getEmail());
	//			email.send();
	//
	//		} catch (EmailException e) {
	//			// TODO: handle exception
	//		}
	//	}

	public void batchAmazonInvitationEmails(Set<Invitation> invitations){
		try {

			// Create a Properties object to contain connection configuration information.
			Properties props = System.getProperties();
			props.put("mail.transport.protocol", "smtp");
			props.put("mail.smtp.port", PORT); 

			// Set properties indicating that we want to use STARTTLS to encrypt the connection.
			// The SMTP session will begin on an unencrypted connection, and then the client
			// will issue a STARTTLS command to upgrade to an encrypted connection.
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.starttls.required", "true");
			
			props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

			// Create a Session object to represent a mail session with the specified properties. 
			Session session = Session.getDefaultInstance(props);

			for(Invitation invitation: invitations){
				// Create a message with the specified information. 
				MimeMessage message = new MimeMessage(session);
				message.setFrom(new InternetAddress(FROM));
				message.setRecipient(Message.RecipientType.TO, new InternetAddress(invitation.getEmail()));
				message.setSubject("[Social Funnel] Você foi adicionado como analista para a marca " + invitation.getBrand().getName());
				message.setText(Emails.invitationEmail(invitation).toString(), "utf-8", "html");
				// Create a transport.        
				Transport transport = session.getTransport();
				// Send the message.
				try
				{
					// Connect to Amazon SES using the SMTP username and password you specified above.
					transport.connect(HOST, SMTP_USERNAME, SMTP_PASSWORD);
					// Send the email.
					transport.sendMessage(message, message.getAllRecipients());
					System.out.println("Email sent!");
				}
				catch (Exception ex) {
					System.out.println("The email was not sent.");
					System.out.println("Error message: " + ex.getMessage());
				}
				finally
				{
					// Close and terminate the connection.
					transport.close();        	
				}
			}
		} catch (AddressException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NoSuchProviderException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (MessagingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public void batchInvitationEmails(Set<Invitation> invitations){
		if(invitations != null && invitations.size() > 0){
			Properties props = new Properties();
			props.put("mail.smtp.host", "smtp.gmail.com");
			props.put("mail.smtp.socketFactory.port", "465");
			props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.port", "465");

			Session session = Session.getInstance(props, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication("misael@socialfunnel.net","Misa99zz!");
				}
			});
			try {
				Transport transport = session.getTransport("smtp");

				InternetAddress fromAddress;
				fromAddress = new InternetAddress("noreply@socialfunnel.net");

				transport.connect();

				for(Invitation invitation: invitations){
					InternetAddress toAddress = new InternetAddress(invitation.getEmail());

					try {
						MimeMessage message = new MimeMessage(session);
						message.setFrom(fromAddress);

						InternetAddress[] addresses = {toAddress};//addresses

						message.setRecipients(Message.RecipientType.TO, addresses);
						message.setSubject("[Social Funnel] Você foi adicionado como analista para a marca " + invitation.getBrand().getName());
						message.setText(Emails.invitationEmail(invitation).toString(), "utf-8", "html");

						transport.sendMessage(message, addresses);

					} catch (MessagingException e) {
						throw new RuntimeException(e);
					}
				}

				transport.close();

			} catch (AddressException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (NoSuchProviderException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	public void resetEmail(PasswordReset passwordReset) {
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("misael@socialfunnel.net","Misa99zz!");
			}
		});
		try {
			Transport transport = session.getTransport("smtp");

			InternetAddress fromAddress;
			fromAddress = new InternetAddress("noreply@socialfunnel.net");

			transport.connect();

			InternetAddress toAddress = new InternetAddress(passwordReset.getEmail());

			try {
				MimeMessage message = new MimeMessage(session);
				message.setFrom(fromAddress);

				InternetAddress[] addresses = {toAddress};//addresses

				message.setRecipients(Message.RecipientType.TO, addresses);
				message.setSubject("[Social Funnel] Redefina sua senha");
				message.setText(Emails.passwordResetEmail(passwordReset).toString(), "utf-8", "html");

				transport.sendMessage(message, addresses);

			} catch (MessagingException e) {
				throw new RuntimeException(e);
			}

			transport.close();

		} catch (AddressException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NoSuchProviderException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (MessagingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}
