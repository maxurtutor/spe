package org.maxur.spe.infrastructure;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.maxur.spe.domain.MailIdService;

import java.sql.Connection;

import static org.junit.Assert.assertEquals;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>2/28/14</pre>
 */
public class MailIdServiceJDBCImplTest extends AbstractDAOJDBCTest {

    private MailIdService service;

    @Before
    public void initTest() throws Exception {
        service = new MailIdServiceJDBCImpl(this);

        IDataSet dataSet = new FlatXmlDataSetBuilder().build(
                MailIdServiceJDBCImplTest.class.getResourceAsStream("/sql/dataset.xml"));

        try(final Connection connection = get()) {
            IDatabaseConnection databaseConnection = new DatabaseConnection(connection);
            DatabaseOperation.CLEAN_INSERT.execute(databaseConnection, dataSet);
        }
    }

    @Test
    public void testGetId() {
        final Long id = service.getId();
        assertEquals(2L, id.longValue());
    }


    @After
    public void after() {
    }

}