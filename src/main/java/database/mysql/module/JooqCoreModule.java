package database.mysql.module;

import com.google.gson.JsonElement;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.matcher.Matchers;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import database.mysql.handler.TransactionalMethodInterceptor;
import database.mysql.monitor.DataSourceMonitorImpl;
import database.mysql.monitor.IDataSourceMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

public class JooqCoreModule extends AbstractModule {
    private static final Logger LOGGER = LoggerFactory.getLogger(JooqCoreModule.class);
    private HikariDataSource dataSource;

    public JooqCoreModule(DbConfig config) {
        initPool(config);
    }

    @Override
    protected void configure() {
        bind(IDataSourceMonitor.class).to(DataSourceMonitorImpl.class);
        //bind annotation
        bindInterceptor(Matchers.any(),
                Matchers.annotatedWith(Transactional.class),
                new TransactionalMethodInterceptor(getDataSourceTransactionManager()));
    }

    @Provides
    public HikariDataSource getDataSource() {
        return dataSource;
    }

    @Provides
    @Singleton
    public DataSourceTransactionManager getDataSourceTransactionManager() {
        return new DataSourceTransactionManager(new TransactionAwareDataSourceProxy(dataSource));
    }

    /**
     * initialize connection pool
     *
     * @param config
     */
    private void initPool(DbConfig config) {
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
            hikariConfig.setConnectionTimeout(10000);
            //hikariConfig.addDataSourceProperty("useTimezone", "true");
            hikariConfig.addDataSourceProperty("serverTimezone", "Asia/Ho_Chi_Minh");
            hikariConfig.addDataSourceProperty("useLegacyDatetimeCode", "false");
            LOGGER.trace("Set character encoding utf8");
            hikariConfig.setAutoCommit(false); //disable this one because we will use our own TransactionalManager

        /*if(!Strings.isNullOrEmpty(poolName)) {
            hikariConfig.setPoolName(poolName);
        }*/

            if (config.getMaxPoolSize() != null) {
                hikariConfig.setMaximumPoolSize(config.getMaxPoolSize());
                hikariConfig.setMinimumIdle(4);
            } else {
                //using default pool size of hikariCP
                hikariConfig.setMaximumPoolSize(8);
                hikariConfig.setMinimumIdle(4);
            }
            dataSource = new HikariDataSource(hikariConfig);
            LOGGER.trace("connection pool initialized");
    }

    /**
     * a wrapper to get json node as int safety
     *
     * @param element
     * @return
     */
    private Integer getInteger(JsonElement element) {
        if (element == null) {
            return null;
        }

        try {
            return element.getAsInt();
        } catch (UnsupportedOperationException e) {
            LOGGER.error("failed to get element as int: " + element);
            return null;
        }
    }

    /**
     * * a wrapper to get json node as string safety
     *
     * @param element
     * @return
     */
    private String getString(JsonElement element) {
        if (element == null) {
            return null;
        }

        try {
            return element.getAsString();
        } catch (UnsupportedOperationException e) {
            LOGGER.error("failed to get element as string: " + element);
            return null;
        }
    }
}
