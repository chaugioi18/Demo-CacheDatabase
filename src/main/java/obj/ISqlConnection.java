package obj;

import database.mysql.module.DbConfig;
import org.jooq.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

public interface ISqlConnection {

    void initPool(DbConfig config);

    DataSourceTransactionManager getDataSourceTransactionManager();

    Configuration getConfiguration();
}
