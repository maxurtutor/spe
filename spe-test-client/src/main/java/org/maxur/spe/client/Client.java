package org.maxur.spe.client;

import org.maxur.spe.Worker;

/**
 * @author Maxim Yunusov
 * @version 1.0 14.09.2014
 */
public class Client {

    private Worker worker;

    public static void main(String[] args) throws Exception {
        final Client client = new Client();
        client.init();
        client.run();
        client.done();
    }

    private void init() {
        worker = new Worker();
    }

    private void run() throws Exception {
        for (int i = 0; i < 20000; i++) {
            String response = worker.run(makeRequest());
            System.out.println(response);
        }
    }

    private String makeRequest() {
        return "Hello";
    }

    private void done() {
        worker.done();
    }


}
