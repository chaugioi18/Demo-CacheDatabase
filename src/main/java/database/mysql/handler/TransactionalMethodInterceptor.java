package database.mysql.handler;


import com.google.inject.Inject;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class TransactionalMethodInterceptor implements MethodInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionalMethodInterceptor.class);

    private DataSourceTransactionManager transactionManager;

    @Inject
    public TransactionalMethodInterceptor(DataSourceTransactionManager transactionManager){
        super();
        this.transactionManager = transactionManager;
    }

    @Override
    public Object invoke(final MethodInvocation invocation) throws Throwable {
        Propagation propagation = invocation.getMethod().getAnnotation(Transactional.class).propagation();
        DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        transactionDefinition.setPropagationBehavior(propagation.value());
        transactionDefinition.setPropagationBehaviorName(DefaultTransactionDefinition.PREFIX_PROPAGATION + propagation.name());

        TransactionStatus transaction = transactionManager.getTransaction(transactionDefinition);

        try {
            Object result = invocation.proceed();

            try {
                if (transaction.isNewTransaction()) {
                    transactionManager.commit(transaction);
                    LOGGER.debug("transaction committed after method: " + invocation.getMethod().getName());
                }
            }
            catch (UnexpectedRollbackException ignore) {}

            return result;
        }
        catch (Exception e) {
            if (transaction.isNewTransaction()) {
                transactionManager.rollback(transaction);
                LOGGER.info("transaction does rollback: " + e.getMessage());
            }

            throw e;
        }
    }
}