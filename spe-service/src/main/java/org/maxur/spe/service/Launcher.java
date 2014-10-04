package org.maxur.spe.service;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.maxur.spe.domain.Factory;
import org.maxur.spe.domain.Mail;
import org.maxur.spe.domain.MailIdService;
import org.maxur.spe.domain.MailService;
import org.maxur.spe.domain.Repository;
import org.maxur.spe.domain.Worker;
import org.maxur.spe.infrastructure.ConnectionFactoryJDBCImpl;
import org.maxur.spe.infrastructure.MailIdServiceJDBCImpl;
import org.maxur.spe.infrastructure.MailRepositoryJDBCImpl;
import org.maxur.spe.infrastructure.MailServiceJavaxImpl;
import org.slf4j.Logger;

import java.net.URI;
import java.sql.Connection;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Maxim Yunusov
 * @version 1.0 14.09.2014
 */
public class Launcher {

    private static Logger LOGGER = getLogger(Launcher.class);

    private static final URI BASE_URI = URI.create("http://localhost:9090/service/");

    public static final String FROM_ADDRESS = "sender@here.com";

    private Worker worker;

    private HttpServer httpServer;

    public static void main(String[] args) throws Exception {
        final Launcher client = new Launcher();
        client.init();
        client.run();
        client.done();
    }

    private void init() {
        try {
            MailService mailService = new MailServiceJavaxImpl(FROM_ADDRESS);
            Factory<Connection> factory = new ConnectionFactoryJDBCImpl();
            Repository<Mail> repository = new MailRepositoryJDBCImpl(factory);
            MailIdService idService = new MailIdServiceJDBCImpl(factory);
            worker = new Worker(mailService, repository, idService);

        } catch (RuntimeException e) {
            LOGGER.error("System don't initialising", e);
        }
    }

    private void run() throws Exception {
        startWebServer();
        System.in.read();
    }

    private void startWebServer() {
        LOGGER.info("Start Grizzly");
        httpServer = GrizzlyHttpServerFactory.createHttpServer(BASE_URI, createApp());
    }

    public ResourceConfig createApp() {
        return new ResourceConfig() {
            {
                packages("org.maxur.spe.mail");
                register(new AbstractBinder() {
                    @Override
                    protected void configure() {
                        bind(worker).to(Worker.class);
                    }
                });
            }
        };
    }

    private void done() {
        httpServer.shutdownNow();
        worker.done();
    }


}
