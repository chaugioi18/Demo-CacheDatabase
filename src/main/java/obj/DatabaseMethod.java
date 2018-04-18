package obj;

public enum DatabaseMethod {

    QUERY_INSERT(0,"db_save_"),
    QUERY_UPDATE(1,"db_update_");

    private Integer code;
    private String description;

    DatabaseMethod(Integer code, String description) {

    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
