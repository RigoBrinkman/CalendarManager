package com.brinkbros;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class MailHandler {
  
  private static final String USERNAME = "jaarplanning@pensioenfederatie.nl";
  private static final String PASSWORD = "WelkomC18090188";
  
  public static void main(String[] args) throws MessagingException {
    System.setProperty("https.protocols", "TLSv1.1,TLSv1.2");
    System.setProperty("http.protocols", "TLSv1.1,TLSv1.2");
    
    Properties mailProps = new Properties();
    mailProps.put("mail.smtp.auth", true);
    mailProps.put("mail.smtp.starttls.enable", true);
    mailProps.put("mail.smtp.host", "mail.spo.nl");
    mailProps.put("mail.smtp.port", "25");
    mailProps.put("mail.smtp.ssl.trust", "mail.spo.nl");

    Session mailSession = Session.getInstance(mailProps, new Authenticator() {
      @Override
      protected PasswordAuthentication getPasswordAuthentication(){
        return new PasswordAuthentication(USERNAME, PASSWORD);
      }
    });
    
    Message msg = new MimeMessage(mailSession);
    try {
      msg.setFrom(new InternetAddress("jaarplanning@pensioenfederatie.nl"));
      msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse("jaarplanning@pensioenfederatie.nl"));
      msg.setSubject("Test 1");
      
      BodyPart mbp = new MimeBodyPart();
      mbp.setContent("Test 1 tekst tekst tekst", "text/html");
      
      Multipart multiP = new MimeMultipart();
      multiP.addBodyPart(mbp);
      
      msg.setContent(multiP);
      
      Transport.send(msg);
      
    } catch (AddressException ex) {
      throw new RuntimeException("Something terrible has happened");
    }
  }
}
