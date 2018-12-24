package io.haskins.cdkiac.exception;

public class MissingPropertyException extends Exception {

    public MissingPropertyException(Throwable e) { super(e); }

    public MissingPropertyException(String message) {
        super(message);
    }

}
