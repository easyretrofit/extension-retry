package io.github.easyretrofit.extension.retry.core;

public class RetryException extends RuntimeException {

    public RetryException(String message, Throwable cause) {
        super(message, cause);
    }

    public RetryException(String message, Throwable cause, boolean b, boolean writeableStackTrace) {
        super(message, null, b, writeableStackTrace);
    }
}
