package database.mysql.context;

import org.jooq.Configuration;
import org.jooq.ConnectionProvider;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.DefaultDSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;


public class AutoCloseableDSLContext extends DefaultDSLContext implements AutoCloseable{
    private Connection connection;
    private SQLDialect dialect;
    private DataSource dataSource;
    private ConnectionProvider connectionProvider;
    private Configuration configuration;
    private Settings settings;

    private static final Logger LOGGER = LoggerFactory.getLogger(AutoCloseableDSLContext.class);

    public AutoCloseableDSLContext(SQLDialect dialect) {
        super(dialect);
        this.dialect = dialect;
    }

    public AutoCloseableDSLContext(Connection connection, SQLDialect dialect) {
        super(connection, dialect);
        this.connection = connection;
        this.dialect = dialect;
    }

    public AutoCloseableDSLContext(Connection connection, SQLDialect dialect, Settings settings){
        super(connection, dialect, settings);
        this.settings = settings;
        this.connection = connection;
        this.dialect = dialect;
    }

    public AutoCloseableDSLContext(DataSource datasource, SQLDialect dialect) {
        super(datasource, dialect);
        this.dataSource = datasource;
        this.dialect = dialect;
    }

    public AutoCloseableDSLContext(ConnectionProvider connectionProvider, SQLDialect dialect) {
        super(connectionProvider, dialect);
        this.connectionProvider = connectionProvider;
        this.dialect = dialect;
    }

    public AutoCloseableDSLContext(Configuration configuration) {
        super(configuration);
        this.configuration = configuration;
    }

    @Override
    public void close() {
        if(connection != null){
            try {
                connection.close();
            } catch (SQLException e) {
                LOGGER.debug(e.getMessage());
            }
        }

        //TODO handle other resource
    }
}
