package app;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.vertx.core.AbstractVerticle;

public class Start extends AbstractVerticle {

    @Override
    public void start() {
        //load config
        Injector injector = Guice.createInjector(new ModuleService(context));
        IService service = injector.getInstance(ServiceImpl.class);
        service
                .connectDb()
                .executeData()
                .getData();
    }
}
