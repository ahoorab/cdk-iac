package io.haskins.cdkiac.utils;

/**
 * Exception thrown when a property is missing or invalid
 */
public class MissingPropertyException extends Exception {

    public MissingPropertyException(String m) { super(m); }
}
