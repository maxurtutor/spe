package org.maxur.spe.infrastructure;

import org.maxur.spe.domain.Factory;
import org.maxur.spe.domain.MailIdService;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>10/2/2014</pre>
 */
public class MailIdServiceJDBCImpl implements MailIdService {

    private final DataSource dataSource;

    public MailIdServiceJDBCImpl(Factory<DataSource> factory) {
        this.dataSource = factory.get();
    }

    @Override
    public Long getId() {
        JdbcTemplate select = new JdbcTemplate(dataSource);
        final Long result = select.queryForObject("select max(ID) from MAIL", Long.class);
        if (result == null) {
            return 0l;
        }
        return result + 1L;
    }
}
