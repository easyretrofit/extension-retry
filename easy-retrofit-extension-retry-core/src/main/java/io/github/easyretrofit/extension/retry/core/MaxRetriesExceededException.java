package io.github.easyretrofit.extension.retry.core;

import io.github.easyretrofit.extension.retry.core.interceptor.RetryHandler;

public class MaxRetriesExceededException extends RetryException {
    private final transient String causingRetryName;

    private MaxRetriesExceededException(String causingRetryName, String message, boolean writeableStackTrace) {
        super(message, null, false, writeableStackTrace);
        this.causingRetryName = causingRetryName;
    }

    /**
     * Static method to construct a {@link MaxRetriesExceededException}
     *
     * @param retryHandler the Retry which failed
     */
    public static MaxRetriesExceededException createMaxRetriesExceededException(RetryHandler retryHandler) {
        boolean writeStackTrace = retryHandler.getConfig().isWriteableStackTrace();
        String resourceName = retryHandler.getConfig().getResourceName();
        String message = String.format(
                "Retry '%s' has exhausted all attempts (%d)",
                retryHandler.getConfig().getResourceName(),
                retryHandler.getConfig().getMaxRetries()
        );
        throw new MaxRetriesExceededException(resourceName, message, writeStackTrace);
    }

    /**
     * @return the name of the {@link RetryHandler} that caused this exception
     */
    public String getCausingRetryName() {
        return causingRetryName;
    }
}
