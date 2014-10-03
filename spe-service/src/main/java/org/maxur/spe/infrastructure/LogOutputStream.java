package org.maxur.spe.infrastructure;

import org.slf4j.Logger;

import java.io.IOException;
import java.io.OutputStream;

/**
* @author Maxim Yunusov
* @version 1.0
* @since <pre>10/3/2014</pre>
*/
public class LogOutputStream extends OutputStream {

    private final Logger logger;

    private StringBuilder string = new StringBuilder();

    public LogOutputStream(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void write(int b) throws IOException {
        this.string.append((char) b );
    }

    @Override
    public void close() {
        logger.info(string.toString());
    }
}
