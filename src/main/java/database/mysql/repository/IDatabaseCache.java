package database.mysql.repository;

import obj.DatabaseMethod;
import org.jooq.Record;

public interface IDatabaseCache {

    DatabaseCache save(Record record, DatabaseMethod method);

    IDatabaseCache remove(Record record);

    IDatabaseCache reSave();

    IDatabaseCache reUpdate();
}
