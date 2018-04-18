package database.mysql.monitor;

import com.google.inject.Inject;
import com.zaxxer.hikari.HikariDataSource;

public class DataSourceMonitorImpl implements IDataSourceMonitor {
    private HikariDataSource dataSource;

    @Inject
    public DataSourceMonitorImpl setDataSource(HikariDataSource dataSource) {
        this.dataSource = dataSource;
        return this;
    }

    @Override
    public void shutdownDataSource() {
        dataSource.shutdown();
    }

    @Override
    public void closeDataSource() {
        dataSource.close();
    }
}
