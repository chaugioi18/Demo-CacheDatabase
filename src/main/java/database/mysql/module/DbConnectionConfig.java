package database.mysql.module;

import obj.RedisConfig;

public class DbConnectionConfig {

    private DbConfig readDbConfig;
    private DbConfig writeDbConfig;
    private RedisConfig redisConfig;

    public DbConfig getReadDbConfig() {
        return readDbConfig;
    }

    public DbConfig getWriteDbConfig() {
        return writeDbConfig;
    }

    public RedisConfig getRedisConfig() {
        return redisConfig;
    }
}
