package database.mysql.repository;

import database.mysql.exception.DBException;
import org.jooq.Record;

import java.io.Serializable;
import java.util.List;

public interface IBaseRepo<K extends Serializable, R extends Record> {

    /**
     * find a record by id
     *
     * @param id
     * @return
     */
    R findOne(K id);

    /**
     * find all records
     * @return
     */
    List<R> findAll() throws DBException.SQLError;

    /**
     * update a record
     * @param record
     */
    int update(R record) throws DBException.SQLError;

    /**
     * update a record if properties not null
     * @param record
     */
    int updateNotNull(R record) throws DBException.SQLError;

    /**
     * batch update a list of record
     * @param records
     * @return
     */
    int[] batchUpdate(List<R> records) throws DBException.SQLError;

    /**
     * save record to database
     * @param record
     * @return
     */
    int save(R record) throws DBException.SQLError;

    /**
     * batch save list of records to database;
     * @param records
     * @return
     */
    int[] batchSave(List<R> records) throws DBException.SQLError;

    /**
     * save record to database and fetch the record (with primary keys only)
     * @param record
     * @return
     */
    R saveAndFetch(R record) throws DBException.SQLError;

    /**
     * if record not available, save new one
     * @param record
     * @return
     */
    int saveOrUpdate(R record) throws DBException.SQLError;

//    /**
//     * save or update a collection of records
//     * @param records
//     * @return
//     */
//    int batchSaveOrUpdate(List<R> records);

    /**
     * remove a record by id
     * @param id
     * @return
     */
    int delete(K id) throws DBException.SQLError;

    /**
     * count number of records in table
     * @return
     */
    int count() throws DBException.SQLError;

}
