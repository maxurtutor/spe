package org.maxur.spe.mail;

import org.maxur.spe.service.SendMailService;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * @author Maxim
 * @version 1.0
 * @since <pre>04.10.2014</pre>
 */
@Path("/mail")
@Singleton
public class MailResource {

    @Inject
    private SendMailService service;

    @POST
    @Produces("text/plain")
    public String sendMessage(
            @FormParam("message") String message
    ) throws Exception {
        return service.send(message);
    }

}