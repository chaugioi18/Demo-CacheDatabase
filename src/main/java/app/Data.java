package app;

public class Data {

    private String name;
    private Long time;

    public String getName() {
        return name;
    }

    public Data setName(String name) {
        this.name = name;
        return this;
    }

    public Long getTime() {
        return time;
    }

    public Data setTime(Long time) {
        this.time = time;
        return this;
    }
}
