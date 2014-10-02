package org.maxur.spe.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author Maxim Yunusov
 * @version 1.0 14.09.2014
 */
public class TestClient {

    private WebTarget target;

    public static void main(String[] args) throws Exception {
        final TestClient client = new TestClient();
        client.init();
        client.run();
        client.done();
    }

    private void init() {
        Client client = ClientBuilder.newClient();
        target = client.target("http://localhost:9090").path("service/mail");
    }

    private void run() throws Exception {
        for (int i = 0; i < 20000; i++) {
            Response response =
                    target.request(MediaType.TEXT_PLAIN_TYPE)
                            .post(Entity.entity(makeRequest(), MediaType.APPLICATION_FORM_URLENCODED_TYPE));
            System.out.println(response.readEntity(String.class));
        }
    }

    private Form makeRequest() {
        Form form = new Form();
        form.param("message", "Hello");
        return form;
    }

    private void done() {
    }


}
