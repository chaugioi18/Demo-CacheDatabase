package database.redis;

import com.google.inject.Inject;
import io.vertx.core.Vertx;
import io.vertx.core.net.ProxyOptions;
import io.vertx.core.net.ProxyType;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;
import io.vertx.redis.op.SetOptions;
import obj.RedisConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class RedisAdapter implements IRedisAdapter {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private Vertx vertx;
    private RedisClient redisClient;

    @Inject
    RedisAdapter(Vertx vertx) {
        this.vertx = vertx;
    }


    @Override
    public IRedisAdapter installRedis(RedisConfig redisConfig) {
        LOGGER.info("Install redis...");
        RedisOptions redisOptions = new RedisOptions()
                .setHost(redisConfig.getHost())
                .setPort(redisConfig.getPort())
                .setEncoding("utf8")
                .setAuth(redisConfig.getPassword());
        if (redisConfig.getProxyConfig().getProxy()) {
            Integer type = redisConfig.getProxyConfig().getProxyType();
            redisOptions
                    .setProxyOptions(new ProxyOptions()
                            .setHost(redisConfig.getProxyConfig().getHost())
                            .setPort(redisConfig.getProxyConfig().getPort())
                            .setPassword(redisConfig.getProxyConfig().getPassword())
                            .setType(type == 1 ? ProxyType.SOCKS4 : type == 2 ? ProxyType.SOCKS5 : ProxyType.HTTP));
        }
        this.redisClient = RedisClient.create(vertx, redisOptions);
        redisClient.ping(event -> {
            if (event.succeeded()) {
                LOGGER.info("Connected to redis");
            } else {
                LOGGER.error("Connected failed");
            }
        });
        return this;
    }

    @Override
    public void setKey(String key, String value) {
        redisClient.set(key, value, result -> {
            if (result.succeeded()) {
                LOGGER.info("Set key success");
                LOGGER.debug("Set key {} to {}", key, value);
            }
        });
    }

    @Override
    public String getValue(String key) throws InterruptedException {
        final BlockingQueue<String> response = new ArrayBlockingQueue<>(1);
        synchronized (this) {
            LOGGER.info("Get value of key {}", key);
            redisClient.get(key, event -> {
                if (event.result() == null) {
                    LOGGER.warn("Not found value with key {}", key);
                } else {
                    response.offer(event.result());
                }
            });
            return response.poll(500, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void getValue(String key, IHandlerSingleValue handler) {
        redisClient.get(key, result -> {
            handler.handle(result.result());
        });
    }

    @Override
    public void delKey(String key) {
        LOGGER.info("Ready to delete key {}", key);
        redisClient.del(key, event -> {
            if (event.succeeded()) {
                LOGGER.info("Delete key {} success", key);
            } else {
                LOGGER.info("Delete key {} failed", key);
            }
        });
    }

    @Override
    public void delKey(String key, IHandlerBooleanValue value) {
        LOGGER.info("Ready to delete key {}", key);
        redisClient.del(key, event -> {
            if (event.succeeded()) {
                value.handle(true);
                LOGGER.info("Delete key {} success", key);
            } else {
                value.handle(false);
                LOGGER.info("Delete key {} failed", key);
            }
        });
    }

    @Override
    public void setExpires(String key, String value, Long second) {
        SetOptions setOptions = new SetOptions().setEX(5);
        redisClient.setWithOptions(key, value, setOptions, result -> {
            if (result.succeeded()) {
                LOGGER.info("Set expires success");
                LOGGER.debug("Set key {} expires after {}", key, second);
            }
        });
    }

    @Override
    public void getKeys(String pattern, IHandler handler) {
        redisClient.keys(pattern, listKey -> {
            handler.handle(listKey.result().getList());
        });
    }

    @Override
    public void getValues(String pattern, IHandler handler) {
        redisClient.keys(pattern, resultKeys -> {
            redisClient.mgetMany(resultKeys.result().getList(), resultValues -> {
                handler.handle(resultValues.result().getList());
            });
        });
    }

    @Override
    public void delKeys(String pattern) {
        redisClient.keys(pattern, keys -> {
            try {
                if (keys.failed()) {
                    LOGGER.error("Not found keys in redis");
                } else {
                    redisClient.delMany(keys.result().getList(), result -> {
                        try {
                            if (result.failed()) {
                                LOGGER.error("Delete keys in redis failed");
                            } else {
                                LOGGER.info("Delete keys success");
                            }
                        } catch (Exception e) {
                            LOGGER.error("Unknown error on delete keys in redis to delete");
                        }
                    });
                }
            } catch (Exception e) {
                LOGGER.error("Error on get keys in redis to delete");
            }
        });
    }

    @FunctionalInterface
    public interface IHandler {
        void handle(List<String> result);
    }

    @FunctionalInterface
    public interface IHandlerSingleValue {
        void handle(String result);
    }

    @FunctionalInterface
    public interface IHandlerBooleanValue {
        void handle(Boolean result);
    }
}
