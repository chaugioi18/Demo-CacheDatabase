package app;

import com.google.gson.Gson;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import database.mysql.module.DbConnectionConfig;
import database.mysql.repository.DatabaseCache;
import database.mysql.repository.IDatabaseCache;
import database.mysql.repository.ITestDataRepo;
import database.mysql.repository.TestDataImpl;
import database.redis.IRedisAdapter;
import database.redis.RedisAdapter;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.impl.VertxImpl;
import obj.ISqlConnection;
import obj.SqlReadConnection;
import obj.SqlWriteConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;


public class ModuleService extends AbstractModule {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private Context context;
    private DbConnectionConfig config;
    private Vertx vertx;
//    private Map<String, CacheData> cacheMap;

    ModuleService(Vertx vertx, Context context) {
        this.vertx = vertx;
        this.context = context;
    }

    @Provides
    @Singleton
    public DbConnectionConfig getConfig() {
        return new Gson().fromJson(context.config().encode(), DbConnectionConfig.class);
    }

    @Provides
    @Singleton
    public Vertx getVertx() {
        return vertx;
    }

    @Provides
    @Singleton
    public ExecutorService getExecutorService() {
        VertxImpl vertxImpl = (VertxImpl) vertx;
        return vertxImpl.getWorkerPool();
    }

    @Override
    protected void configure() {
        bind(IService.class).to(ServiceImpl.class).in(Scopes.SINGLETON);
        bind(ITestDataRepo.class).to(TestDataImpl.class).in(Scopes.SINGLETON);
        bind(ISqlConnection.class).annotatedWith(Names.named("Read")).to(SqlReadConnection.class).in(Scopes.SINGLETON);
        bind(ISqlConnection.class).annotatedWith(Names.named("Write")).to(SqlWriteConnection.class).in(Scopes.SINGLETON);
        bind(IRedisAdapter.class).to(RedisAdapter.class).in(Scopes.SINGLETON);
        bind(IDatabaseCache.class).to(DatabaseCache.class).in(Scopes.SINGLETON);

//        bindInterceptor(Matchers.any(),
//                Matchers.annotatedWith(Transactional.class),
//                new TransactionalMethodInterceptor(getDataSourceTransactionManager()));
    }

}
