package app;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

public class Start extends AbstractVerticle {

    @Override
    public void start() {
        //load config
        Vertx vertx = Vertx.vertx(new VertxOptions().setWorkerPoolSize(16).setMaxWorkerExecuteTime(10000));
        Injector injector = Guice.createInjector(new ModuleService(vertx, context));
        IService service = injector.getInstance(ServiceImpl.class);
        service
                .connectDb()
                .executeData()
                .getData();
    }
}
