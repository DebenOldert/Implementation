
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/*
 * Feel free to copy/use it for your own project.
 * Keep in mind that it took me several days/weeks, beers and asperines to make this.
 * So be nice, and give me some credit, I won't bite and it won't hurt you.
 */

/**
 *
 * @author Deben Oldert
 */
public class MAIL {
    public boolean send(String mail, String subject, String body){
        String to = mail;
        String from = "deben1997@gmail.com";
        Properties properties = System.getProperties();
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        
        Session session = Session.getDefaultInstance(properties, null);
        try{
         // Create a default MimeMessage object.
         MimeMessage message = new MimeMessage(session);

         // Set From: header field of the header.
         message.setFrom(new InternetAddress(from));

         // Set To: header field of the header.
         message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

         // Set Subject: header field
         message.setSubject(subject);

         // Send the actual HTML message, as big as you like
         message.setContent(body, "text/html" );

         // Send message
         Transport transport = session.getTransport("smtp");
         
         transport.connect("smtp.gmail.com", "deben1997@gmail.com", "Beer1997");
         transport.sendMessage(message, message.getAllRecipients());
	 transport.close();
         
         return true;
      }catch (MessagingException mex) {
         return false;
      }        
    }
   public boolean check(String mail) {
       boolean result = true;
       try {
        InternetAddress emailAddr = new InternetAddress(mail);
        emailAddr.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }
}