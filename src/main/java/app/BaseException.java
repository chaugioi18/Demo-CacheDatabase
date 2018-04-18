package app;

public class BaseException extends RuntimeException {
    private static final long serialVersionUID = -1282169087175899678L;

    public BaseException(String msg){
        super(msg);
    }

    public BaseException(String msg, Throwable cause){
        super(msg, cause);
    }

}
