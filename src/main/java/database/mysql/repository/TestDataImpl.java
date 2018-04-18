package database.mysql.repository;

import app.Data;
import database.mysql.context.AutoCloseableDSLContext;
import database.mysql.exception.DBException;
import database.mysql.model.Tables;
import database.mysql.model.tables.Testdata;
import database.mysql.model.tables.records.TestdataRecord;

import java.util.List;

public class TestDataImpl extends BaseRepoImpl<Integer, TestdataRecord, Testdata> implements ITestDataRepo {

    @Override
    public List<Data> getAllData() {
        try(AutoCloseableDSLContext context = getAutoCloseableDSLContextDb2()) {
            return context.select(Tables.TESTDATA.TIME, Tables.TESTDATA.NAME)
                    .from(Tables.TESTDATA)
                    .fetchInto(Data.class);
        } catch (Exception e) {
            LOGGER.error("Error on get all data cause by {}", e.getMessage());
            throw new DBException.SQLError("Error on get all data");
        }
    }

}
