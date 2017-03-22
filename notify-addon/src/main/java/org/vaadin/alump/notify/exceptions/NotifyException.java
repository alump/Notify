package org.vaadin.alump.notify.exceptions;

/**
 * Exception
 */
public abstract class NotifyException extends RuntimeException {

    protected NotifyException() {
        super();
    }

    protected NotifyException(String message) {
        super(message);
    }
}
