package database.mysql.repository;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.mysql.jdbc.CommunicationsException;
import database.mysql.context.AutoCloseableDSLContext;
import database.mysql.exception.DBException;
import obj.DatabaseMethod;
import obj.ISqlConnection;
import org.jooq.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

public class BaseRepoImpl<K extends Serializable, R extends Record, T extends Table> implements IBaseRepo<K, R> {
    protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());


    private ISqlConnection readSqlConnection;
    private ISqlConnection writeSqlConnection;
    private IDatabaseCache databaseCache;

    @Inject
    public BaseRepoImpl setReadSqlConnection(@Named("Read") ISqlConnection readSqlConnection) {
        this.readSqlConnection = readSqlConnection;
        return this;
    }

    @Inject
    public BaseRepoImpl setWriteSqlConnection(@Named("Write") ISqlConnection writeSqlConnection) {
        this.writeSqlConnection = writeSqlConnection;
        return this;
    }

    @Inject
    public BaseRepoImpl setDatabaseCache(IDatabaseCache databaseCache) {
        this.databaseCache = databaseCache;
        return this;
    }

    //generic table
    private final Class<T> genericTable = (Class<T>) ((ParameterizedType) getClass()
            .getGenericSuperclass())
            .getActualTypeArguments()[2];

    /**
     * compare primary key
     *
     * @param fields
     * @param id
     * @return
     */
    private Condition equalKeys(List<TableField<R, ?>> fields, K id) {
        if (fields.size() == 1) { //single primary key
            TableField<R, Object> field = (TableField<R, Object>) fields.get(0);
            return field.equal(field.getDataType().convert(id));
        } else { //composite primary key
            LOGGER.debug("unsupported composite keys. Using the first primary key to compare");
            //TODO handle composite keys. Just return a false condition at this moment
            TableField<R, Object> field = (TableField<R, Object>) fields.get(0);
            return field.equal(field.getDataType().convert(id));
        }
    }

    /**
     * get jooq table
     *
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    protected T getTable() throws IllegalAccessException, InstantiationException {
        return genericTable.newInstance();
    }

    /**
     * get primary key
     *
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    private UniqueKey getPrimaryKey() throws InstantiationException, IllegalAccessException {
        return getTable().getPrimaryKey();
    }

    /**
     * get primary key fields
     *
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private List<TableField<R, ?>> getPrimaryKeyFields() throws IllegalAccessException, InstantiationException {
        return getPrimaryKey().getFields();
    }

    /**
     * get custom AutoCloseableDSLContext
     *
     * @return
     * @throws Exception
     */

    protected AutoCloseableDSLContext getAutoCloseableDSLContextDb1() throws Exception {
//        return getDbContext().getAutoCloseableDSLContextDb1();
        return new AutoCloseableDSLContext(readSqlConnection.getConfiguration());
    }

    protected AutoCloseableDSLContext getAutoCloseableDSLContextDb2() throws Exception {
//        return getDbContext().getAutoCloseableDSLContextDb1();
        return new AutoCloseableDSLContext(writeSqlConnection.getConfiguration());
    }

    @Override
    public R findOne(K id) {

        try (AutoCloseableDSLContext context = getAutoCloseableDSLContextDb1()) {
            return (R) context
                    .selectFrom(getTable())
                    .where(equalKeys(getPrimaryKeyFields(), id))
                    .fetchOne();
        } catch (Exception e) {
            LOGGER.error("failed to find record: " + e.getMessage(), e);
            throw new DBException.SQLError("failed to find record: " + e.getMessage(), e);
        }

    }

    @Override
    public List<R> findAll() throws DBException.SQLError {
        List<R> result = new ArrayList<>();

        try (AutoCloseableDSLContext context = getAutoCloseableDSLContextDb1()) {
            result = context
                    .selectFrom(getTable())
                    .fetch();
        } catch (Exception e) {
            LOGGER.error("failed to find all records: " + e.getMessage(), e);
            throw new DBException.SQLError("failed to find all records: " + e.getMessage(), e);
        }
        return result;
    }

    @Override
    public int update(R record) throws DBException.SQLError {

        try (AutoCloseableDSLContext context = getAutoCloseableDSLContextDb1()) {
            UpdateSetMoreStep query = context.update(getTable())
                    .set(record);

            for (TableField<R, ?> keyField : getPrimaryKeyFields()) {
                query.where(equalKeys(getPrimaryKeyFields(), (K) record.getValue(keyField.getName())));
            }

            return query.execute();

        } catch (Exception e) {
            LOGGER.error("failed to update record: " + e.getMessage(), e);
            throw new DBException.SQLError("failed to update record: " + e.getMessage(), e);
        }
    }

    @Override
    public int updateNotNull(R record) throws DBException.SQLError {

        try (AutoCloseableDSLContext context = getAutoCloseableDSLContextDb1()) {

            for (Field f : record.fields()) {
                if (record.getValue(f) == null) {
                    record.changed(f, false);
                }
            }
            return update(record);

        } catch (Exception e) {
            LOGGER.error("failed to update record: " + e.getMessage(), e);
            throw new DBException.SQLError("failed to update record: " + e.getMessage(), e);
        }
    }

    @Override
    public int[] batchUpdate(List<R> records) throws DBException.SQLError {
        try (AutoCloseableDSLContext context = getAutoCloseableDSLContextDb1()) {

            List<UpdateSetMoreStep> queries = new ArrayList<UpdateSetMoreStep>(records.size());
            for (R record : records) {

                UpdateSetMoreStep query = context.update(getTable())
                        .set(record);

                for (TableField<R, ?> keyField : getPrimaryKeyFields()) {
                    query.where(equalKeys(getPrimaryKeyFields(), (K) record.getValue(keyField.getName())));
                }

                queries.add(query);
            }

            return context.batch(queries).execute();

        } catch (Exception e) {
            LOGGER.error("failed to batch update records: " + e.getMessage(), e);
            throw new DBException.SQLError("failed to batch update records: " + e.getMessage(), e);
        }
    }

    @Override
    public int save(R record) throws DBException.SQLError {
        try (AutoCloseableDSLContext context = getAutoCloseableDSLContextDb1()) {
            int i = context
                    .insertInto(getTable())
                    .set(record)
                    .execute();
            databaseCache.reSave();
            return i;
        } catch (CommunicationsException e) {
            LOGGER.error("Can't connect to server");
            databaseCache.save(record, DatabaseMethod.QUERY_INSERT);
            throw new DBException.SQLError("failed to save record: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.error("failed to save record: " + e.getMessage(), e);
            databaseCache.save(record, DatabaseMethod.QUERY_INSERT);
            throw new DBException.SQLError("failed to save record: " + e.getMessage(), e);
        }

    }

    @Override
    public int[] batchSave(List<R> records) throws DBException.SQLError {
        try (AutoCloseableDSLContext context = getAutoCloseableDSLContextDb1()) {

            List<InsertSetMoreStep> queries = new ArrayList<InsertSetMoreStep>(records.size());
            for (R record : records) {
                queries.add(context.insertInto(getTable())
                        .set(record));
            }

            return context.batch(queries).execute();
        } catch (Exception e) {
            LOGGER.error("failed to batch save records: " + e.getMessage(), e);
            throw new DBException.SQLError("failed to batch save records: " + e.getMessage(), e);
        }
    }

    @Override
    public R saveAndFetch(R record) throws DBException.SQLError {

        try (AutoCloseableDSLContext context = getAutoCloseableDSLContextDb1()) {
            return (R) context
                    .insertInto(getTable())
                    .set(record)
                    .returning()
                    .fetchOne();

        } catch (Exception e) {
            LOGGER.error("failed to saveAndFetch record: " + e.getMessage(), e);
            throw new DBException.SQLError("failed to saveAndFetch record: " + e.getMessage(), e);
        }
    }

    @Override
    public int saveOrUpdate(R record) throws DBException.SQLError {
        try (AutoCloseableDSLContext context = getAutoCloseableDSLContextDb1()) {
            return context
                    .insertInto(getTable())
                    .set(record)
                    .onDuplicateKeyUpdate()
                    .set(record)
                    .execute();

        } catch (Exception e) {
            LOGGER.error("failed to save record: " + e.getMessage(), e);
            throw new DBException.SQLError("failed to save record: " + e.getMessage(), e);
        }
    }

    @Override
    public int delete(K id) throws DBException.SQLError {

        try (AutoCloseableDSLContext context = getAutoCloseableDSLContextDb1()) {
            return context
                    .deleteFrom(getTable())
                    .where(equalKeys(getPrimaryKeyFields(), id))
                    .execute();
        } catch (Exception e) {
            LOGGER.error("failed to delete record: " + e.getMessage(), e);
            throw new DBException.SQLError("failed to delete record: " + e.getMessage(), e);
        }

    }

    @Override
    public int count() throws DBException.SQLError {

        try (AutoCloseableDSLContext context = getAutoCloseableDSLContextDb1()) {
            return context.fetchCount(getTable());
        } catch (Exception e) {
            LOGGER.error("failed to count number of records: " + e.getMessage(), e);
            throw new DBException.SQLError("failed to count number of records: " + e.getMessage(), e);
        }

    }
}