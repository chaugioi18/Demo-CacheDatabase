package database.mysql.module;

public class FilterRequest {
    private String orderBy;
    private String orderType;
    private Integer start;
    private Integer end;

    public String getOrderBy() {
        return orderBy;
    }

    public FilterRequest setOrderBy(String orderBy) {
        this.orderBy = orderBy;
        return this;
    }

    public String getOrderType() {
        return orderType;
    }

    public FilterRequest setOrderType(String orderType) {
        this.orderType = orderType;
        return this;
    }

    public Integer getStart() {
        return start;
    }

    public FilterRequest setStart(Integer start) {
        this.start = start;
        return this;
    }

    public Integer getEnd() {
        return end;
    }

    public FilterRequest setEnd(Integer end) {
        this.end = end;
        return this;
    }
}
