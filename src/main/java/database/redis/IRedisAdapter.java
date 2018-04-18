package database.redis;


import obj.RedisConfig;

public interface IRedisAdapter {

    IRedisAdapter installRedis(RedisConfig redisConfig);

    void setKey(String s, String token);

    String getValue(String key) throws InterruptedException;

    void getValue(String key, RedisAdapter.IHandlerSingleValue handler);

    void delKey(String key);

    void delKey(String key, RedisAdapter.IHandlerBooleanValue value);

    void setExpires(String key, String value, Long second);

    void getKeys(String pattern, RedisAdapter.IHandler handler);

    void getValues(String pattern, RedisAdapter.IHandler handler);

    void delKeys(String pattern);
}
