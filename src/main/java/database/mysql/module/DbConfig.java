package database.mysql.module;

public class DbConfig {

    private String dataSourceClassName;
    private String host;
    private Integer port;
    private Integer maxPoolSize;
    private String username;
    private String password;
    private String database;

    public String getDataSourceClassName() {
        return dataSourceClassName;
    }

    public String getHost() {
        return host;
    }

    public Integer getPort() {
        return port;
    }

    public Integer getMaxPoolSize() {
        return maxPoolSize;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDatabase() {
        return database;
    }
}
