package app;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import database.mysql.model.tables.records.TestdataRecord;
import database.mysql.module.DbConnectionConfig;
import database.mysql.repository.ITestDataRepo;
import database.redis.IRedisAdapter;
import io.vertx.core.AsyncResult;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import obj.ISqlConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

public class ServiceImpl implements IService {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private ITestDataRepo testDataWriteRepo;
    private ISqlConnection db1;
    private ISqlConnection db2;
    private DbConnectionConfig config;
    private IRedisAdapter redisAdapter;

    @Inject
    ServiceImpl(ITestDataRepo testDataWriteRepo,
                @Named("Read") ISqlConnection db1,
                @Named("Write") ISqlConnection db2,
                DbConnectionConfig config,
                IRedisAdapter redisAdapter) {
        this.testDataWriteRepo = testDataWriteRepo;
        this.db1 = db1;
        this.db2 = db2;
        this.config = config;
        this.redisAdapter = redisAdapter;
    }

    @Override
    public IService connectDb() {
        db1.initPool(config.getReadDbConfig());
        db2.initPool(config.getWriteDbConfig());
//        redisAdapter.installRedis(config.getRedisConfig());
        return this;
    }

    @Override
    public IService executeData() {
        Vertx vertx = Vertx.vertx();
        HttpServer server = vertx.createHttpServer(new HttpServerOptions()
                .setHost("localhost")
                .setPort(9990));
        Router router = Router.router(vertx);
        server.requestHandler(router::accept);
        router.post("/test").handler(event->{
            event.request().bodyHandler(buffer->{
                vertx.executeBlocking(handle->{
                    LOGGER.debug("Start with payload {}", String.valueOf(buffer));
                    Data data = new Gson().fromJson(String.valueOf(buffer), Data.class);
                    TestdataRecord record = new TestdataRecord();
                    record.setTime(data.getTime());
                    record.setName(data.getName());
                    saveToDb(record);
                    handle.complete();
                }, AsyncResult::result);
            });
            event.response().end();

        });
        router.get("/test").handler(request-> {
            LOGGER.info("Get from database");
            JsonElement json = new Gson().toJsonTree(testDataWriteRepo.getAllData());
            LOGGER.debug("Data: {}", json);
            request.response().end(json.toString());
        });
        server.listen();

        return this;
    }

    @Override
    public IService getData() {

        return this;
    }

    @Transactional
    private void saveToDb(TestdataRecord record) {
        LOGGER.info("Save data into database");
        testDataWriteRepo.save(record);
    }
}
