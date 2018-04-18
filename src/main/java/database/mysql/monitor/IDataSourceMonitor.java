package database.mysql.monitor;

public interface IDataSourceMonitor {

    void shutdownDataSource();

    void closeDataSource();
}
