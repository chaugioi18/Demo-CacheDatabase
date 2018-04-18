package obj;

import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import database.mysql.context.ExceptionTranslator;
import database.mysql.context.SpringConnectionProvider;
import database.mysql.module.DbConfig;
import org.jooq.Configuration;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultExecuteListenerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import java.nio.charset.StandardCharsets;

public class SqlReadConnection implements ISqlConnection{

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private HikariDataSource dataSource;

    @Inject
    SqlReadConnection(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Provides
    @Singleton
    public Configuration getConfiguration() {
        Settings settings = new Settings();
        settings.setExecuteLogging(false);

        return new DefaultConfiguration()
                .set(new SpringConnectionProvider(dataSource))
                .set(SQLDialect.MYSQL)
                .set(new DefaultExecuteListenerProvider(new ExceptionTranslator(dataSource)))
                .set(settings);
    }

    @Provides
    @Singleton
    public DataSourceTransactionManager getDataSourceTransactionManager() {
        return new DataSourceTransactionManager(new TransactionAwareDataSourceProxy(dataSource));
    }

    @Override
    public void initPool(DbConfig config) {
        LOGGER.info("initializing connection");
        //deserialize the specified configJson into JsonObject

        //config hikariCp by above attributes
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDataSourceClassName(config.getDataSourceClassName());
        hikariConfig.setUsername(config.getUsername());
        hikariConfig.setPassword(config.getPassword());
        hikariConfig.addDataSourceProperty("serverName", config.getHost());
        hikariConfig.addDataSourceProperty("port", config.getPort());
        hikariConfig.addDataSourceProperty("databaseName", config.getDatabase());
        hikariConfig.addDataSourceProperty("characterEncoding", StandardCharsets.UTF_8);
        hikariConfig.addDataSourceProperty("useUnicode", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", 250);
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        hikariConfig.addDataSourceProperty("cachePrepStmts", true);
        hikariConfig.addDataSourceProperty("useServerPrepStmts", true);
        hikariConfig.addDataSourceProperty("useLocalSessionState", true);
        hikariConfig.addDataSourceProperty("useLocalTransactionState", true);
        hikariConfig.addDataSourceProperty("cacheResultSetMetadata", true);
        hikariConfig.addDataSourceProperty("cacheServerConfiguration", true);
        hikariConfig.addDataSourceProperty("autoReconnectForPools", true);

//        hikariConfig.addDataSourceProperty("autoReconnect", true);
//        hikariConfig.addDataSourceProperty("failOverReadOnly", false);
//        hikariConfig.addDataSourceProperty("maxReconnects", 10);
//        hikariConfig.addDataSourceProperty("connectTimeout", 10000);
        hikariConfig.setConnectionTimeout(10000);
//        hikariConfig.setIdleTimeout(15000);
        //hikariConfig.addDataSourceProperty("useTimezone", "true");
        hikariConfig.addDataSourceProperty("serverTimezone", "Asia/Ho_Chi_Minh");
        hikariConfig.addDataSourceProperty("useLegacyDatetimeCode", "false");
        LOGGER.trace("Set character encoding utf8");
        hikariConfig.setAutoCommit(true); //disable this one because we will use our own TransactionalManager

        /*if(!Strings.isNullOrEmpty(poolName)) {
            hikariConfig.setPoolName(poolName);
        }*/

        if (config.getMaxPoolSize() != null) {
            hikariConfig.setMaximumPoolSize(config.getMaxPoolSize());
            hikariConfig.setMinimumIdle(10);
        } else {
            //using default pool size of hikariCP
            hikariConfig.setMaximumPoolSize(16);
            hikariConfig.setMinimumIdle(10);
        }
        dataSource = new HikariDataSource(hikariConfig);
        LOGGER.trace("connection pool initialized");
    }

}
