package database.mysql.repository;

import app.Data;
import database.mysql.model.tables.records.TestdataRecord;

import java.util.List;

public interface ITestDataRepo extends IBaseRepo<Integer, TestdataRecord> {

    List<Data> getAllData();

}
