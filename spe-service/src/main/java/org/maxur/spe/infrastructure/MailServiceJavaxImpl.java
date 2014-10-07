package org.maxur.spe.infrastructure;

import com.ecyrd.speed4j.StopWatch;
import com.ecyrd.speed4j.StopWatchFactory;
import org.maxur.spe.domain.Mail;
import org.maxur.spe.domain.MailService;
import org.slf4j.Logger;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Maxim Yunusov
 * @version 1.0 14.09.2014
 */
public class MailServiceJavaxImpl implements MailService {

    private static Logger LOGGER = getLogger(MailServiceJavaxImpl.class);

    public static final int DEFAULT_SMTP_PORT = 25;

    public static final String DEFAULT_SMTP_HOST = "127.0.0.1";

    private final String fromAddress;

    private final Properties props;

    private final StopWatchFactory stopWatchFactory;
    private Session session;
    private Transport transport;
    private String host;


    public MailServiceJavaxImpl(final String fromAddress) {
        this(fromAddress, DEFAULT_SMTP_HOST, DEFAULT_SMTP_PORT);
    }

    public MailServiceJavaxImpl(final String fromAddress, final String host, final int port) {
        this.stopWatchFactory = StopWatchFactory.getInstance("loggingFactory");
        this.fromAddress = fromAddress;
        props = new Properties();
        this.host = host;
        props.put("mail.smtp.host", this.host);
        props.put("mail.smtp.port", port);
        initSession();
    }


    @Override
    public void send(final Mail mail) {
        StopWatch sw = stopWatchFactory.getStopWatch();
        try {
            checkConnection();
            transport.sendMessage(makeMessageBy(mail), makeAddressBy(mail));
            sw.stop("send");
        } catch (MessagingException e) {
            sw.stop("send:failure");
            LOGGER.error("Unable to send email", e);
            throw new IllegalStateException("Unable to send email", e);
        }
    }

    public synchronized void checkConnection() throws MessagingException {
        if (!transport.isConnected()) {
            transport.connect(this.host, "", "");
        }
    }

    @Override
    public void done() {
        try {
            transport.close();
        } catch (MessagingException e) {
            LOGGER.error("Unable to close smtp connection", e);
            throw new IllegalStateException("Unable to close smtp connection", e);
        }
    }

    private Address[] makeAddressBy(Mail mail) {
        try {
            return new Address[] {new InternetAddress(mail.getToAddress())};
        } catch (AddressException e) {
            LOGGER.error("Cannot get smtp address", e);
            throw new IllegalStateException("Cannot get smtp address", e);
        }
    }

    private Message makeMessageBy(final Mail mail) throws MessagingException {
        final Message message = prepareMessage(mail, getSession());
        final Multipart multipart = new MimeMultipart();
        addTextPart(mail, multipart);
        message.setContent(multipart);
        return message;
    }

    private void addTextPart(final Mail mail, final Multipart multipart) throws MessagingException {
        final BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setText(mail.getBody());
        multipart.addBodyPart(messageBodyPart);
    }

    private Message prepareMessage(final Mail mail, final Session session) throws MessagingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(fromAddress));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mail.getToAddress()));
        message.setSubject(mail.getSubject());
        return message;
    }

    private Session getSession() {
        if (session == null) {
            initSession();
        }
        return session;
    }

    private void initSession() {
        session = Session.getInstance(props);
        try {
            transport = session.getTransport("smtp");
        } catch (NoSuchProviderException e) {
            LOGGER.error("Cannot get smtp connection", e);
            throw new IllegalStateException("Cannot get smtp connection", e);
        }
    }

}
