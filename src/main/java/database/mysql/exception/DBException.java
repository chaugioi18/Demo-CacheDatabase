package database.mysql.exception;


import app.BaseException;

public class DBException {

    public static class InvalidConfig extends BaseException {
        public InvalidConfig(String msg) {
            super(msg);
        }

        public InvalidConfig(String msg, Throwable cause) {
            super(msg, cause);
        }
    }

    public static class ConnectFailed extends BaseException {
        public ConnectFailed(String msg) {
            super(msg);
        }

        public ConnectFailed(String msg, Throwable cause) {
            super(msg, cause);
        }
    }

    public static class SQLError extends BaseException {
        public SQLError(String msg) {
            super(msg);
        }

        public SQLError(String msg, Throwable cause) {
            super(msg, cause);
        }
    }

    public static class SQLExecuteError extends BaseException {
        public SQLExecuteError(String msg) {
            super(msg);
        }

        public SQLExecuteError(String msg, Throwable cause) {
            super(msg, cause);
        }
    }

    public static class RecordNotFound extends BaseException {
        public RecordNotFound(String msg) {
            super(msg);
        }

        public RecordNotFound(String msg, Throwable cause) {
            super(msg, cause);
        }
    }

    public static class NotFoundFilterValuesException extends BaseException {
        public NotFoundFilterValuesException(String msg) {
            super(msg);
        }

        public NotFoundFilterValuesException(String msg, Throwable cause) {
            super(msg, cause);
        }
    }
}
