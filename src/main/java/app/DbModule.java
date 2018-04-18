package app;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Named;
import database.mysql.handler.TransactionalMethodInterceptor;
import obj.ISqlConnection;
import org.springframework.transaction.annotation.Transactional;

public class DbModule extends AbstractModule {

    private ISqlConnection sqlConnection;

    public DbModule(@Named("Read") ISqlConnection sqlConnection) {
        this.sqlConnection = sqlConnection;
    }

    @Override
    protected void configure() {

    }
}
