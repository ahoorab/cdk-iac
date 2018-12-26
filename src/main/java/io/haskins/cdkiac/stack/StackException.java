package io.haskins.cdkiac.stack;

/**
 * This exception should be thrown is there is a problem creating the stack
 */
public class StackException extends Exception {

    public StackException(String m) { super(m); }
}