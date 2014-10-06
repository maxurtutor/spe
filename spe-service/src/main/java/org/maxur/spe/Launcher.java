package org.maxur.spe;

import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.glassfish.grizzly.nio.transport.TCPNIOTransport;
import org.glassfish.grizzly.nio.transport.TCPNIOTransportBuilder;
import org.glassfish.grizzly.threadpool.ThreadPoolConfig;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.maxur.spe.service.SendMailService;
import org.slf4j.Logger;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Maxim Yunusov
 * @version 1.0 14.09.2014
 */
public class Launcher {

    private static Logger LOGGER = getLogger(Launcher.class);

    private static final URI BASE_URI = URI.create("http://localhost:9090/service/");

    private HttpServer httpServer;

    private SendMailService sendMailService = new SendMailService();

    public static void main(String[] args) throws Exception {
        final Launcher client = new Launcher();
        client.init();
        client.run();
        client.done();
    }

    private void init() {
        try {
            sendMailService.init();
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

        httpServer.getServerConfiguration().addHttpHandler(
                new HttpHandler() {
                    public void service(Request request, Response response) throws Exception {
                        final SimpleDateFormat format =
                                new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
                        final String date = format.format(new Date(System.currentTimeMillis()));
                        response.setContentType("text/plain");
                        response.setContentLength(date.length());
                        response.getWriter().write(date);
                    }
                },
                "/time");

        final TCPNIOTransportBuilder builder = TCPNIOTransportBuilder.newInstance();
        final ThreadPoolConfig config = builder.getWorkerThreadPoolConfig();
        final ThreadPoolConfig threadPoolConfig = config.setCorePoolSize(5).setMaxPoolSize(5).setQueueLimit(-1);
        final TCPNIOTransport transport = builder.build();



    }

    public ResourceConfig createApp() {
        return new ResourceConfig() {
            {
                packages("org.maxur.spe.mail");
                register(new AbstractBinder() {
                    @Override
                    protected void configure() {
                        bind(sendMailService).to(SendMailService.class);
                    }
                });
            }
        };
    }

    private void done() {
        httpServer.shutdownNow();
    }


}
