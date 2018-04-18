package obj;

public class RedisConfig {
    private String host;
    private Integer port;
    private String password;
    private ProxyConfig proxyConfig;

    public String getHost() {
        return host;
    }

    public RedisConfig setHost(String host) {
        this.host = host;
        return this;
    }

    public Integer getPort() {
        return port;
    }

    public RedisConfig setPort(Integer port) {
        this.port = port;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public RedisConfig setPassword(String password) {
        this.password = password;
        return this;
    }

    public ProxyConfig getProxyConfig() {
        return proxyConfig;
    }
}
