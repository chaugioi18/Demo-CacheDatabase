package obj;

import org.jooq.Record;

public class CacheData {

    private String id;
    private Record record;
    private Integer status;
    private Integer method;

    public String getId() {
        return id;
    }

    public CacheData setId(String id) {
        this.id = id;
        return this;
    }

    public Record getRecord() {
        return record;
    }

    public CacheData setRecord(Record record) {
        this.record = record;
        return this;
    }

    public Integer getStatus() {
        return status;
    }

    public CacheData setStatus(Integer status) {
        this.status = status;
        return this;
    }

    public Integer getMethod() {
        return method;
    }

    public CacheData setMethod(Integer method) {
        this.method = method;
        return this;
    }
}
