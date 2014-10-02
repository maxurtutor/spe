package org.maxur.spe.infrastructure;

import org.junit.BeforeClass;
import org.maxur.spe.domain.Factory;

import java.sql.Connection;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>2/28/14</pre>
 */
public class AbstractDAOJDBCTest implements Factory<Connection> {

    private static Factory<Connection> factory;

    @BeforeClass
    public static void initTestFixture() throws Exception {
        factory = new ConnectionFactoryJDBCImpl();
    }

    public Connection get() {
        return factory.get();
    }


}