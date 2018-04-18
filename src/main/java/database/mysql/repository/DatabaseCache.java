package database.mysql.repository;

import com.google.inject.Inject;
import database.mysql.model.tables.records.TestdataRecord;
import obj.DatabaseMethod;
import org.jooq.Record;

import java.util.LinkedList;
import java.util.Queue;

public class DatabaseCache implements IDatabaseCache {

    private Queue<Record> cacheMap;
    private ITestDataRepo testDataRepo;

    @Inject
    DatabaseCache(ITestDataRepo testDataRepo) {
        this.cacheMap = new LinkedList<>();
        this.testDataRepo = testDataRepo;
    }

    //Save cache and set status query is waiting
    @Override
    public DatabaseCache save(Record record, DatabaseMethod method) {
        cacheMap.add(record);
        return this;
    }

    @Override
    public IDatabaseCache remove(Record record) {
        cacheMap.remove(record);
        return this;
    }

    //Get fail cache
    @Override
    public IDatabaseCache reSave() {
        for (Record record : cacheMap) {
            Record temp = record;
            remove(record);
            testDataRepo.save((TestdataRecord) temp);
        }
        return this;
    }

    @Override
    public IDatabaseCache reUpdate() {
        return this;
    }

}
