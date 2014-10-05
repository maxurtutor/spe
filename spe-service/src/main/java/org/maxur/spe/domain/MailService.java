package org.maxur.spe.domain;

import javax.mail.MessagingException;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>10/2/2014</pre>
 */
public interface MailService {

    void send(Mail mail);

}
