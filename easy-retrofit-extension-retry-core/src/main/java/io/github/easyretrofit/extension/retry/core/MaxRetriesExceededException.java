package io.github.easyretrofit.extension.retry.core;

import io.github.easyretrofit.core.resource.RetrofitApiInterfaceBean;
import io.github.easyretrofit.extension.retry.core.interceptor.RetryHandler;
import okhttp3.Request;

public class MaxRetriesExceededException extends RetryException {
    private final transient String causingRetryName;

    private MaxRetriesExceededException(String causingRetryName, String message, boolean writeableStackTrace, RetrofitApiInterfaceBean retrofitApiInterfaceBean, Request request, String retryResourceName) {
        super(message, null, false, writeableStackTrace, retrofitApiInterfaceBean, request, retryResourceName);
        this.causingRetryName = causingRetryName;
    }

    /**
     * Static method to construct a {@link MaxRetriesExceededException}
     *
     * @param retryHandler the Retry which failed
     */
    public static MaxRetriesExceededException createMaxRetriesExceededException(RetryHandler retryHandler, RetrofitApiInterfaceBean retrofitApiInterfaceBean, Request request, String retryResourceName) {
        boolean writeStackTrace = retryHandler.getConfig().isWriteableStackTrace();
        String resourceName = retryHandler.getConfig().getResourceName();
        String message = String.format(
                "Retry '%s' has exhausted all attempts (%d)",
                retryHandler.getConfig().getResourceName(),
                retryHandler.getConfig().getMaxRetries()
        );
        throw new MaxRetriesExceededException(resourceName, message, writeStackTrace, retrofitApiInterfaceBean, request, retryResourceName);
    }

    /**
     * @return the name of the {@link RetryHandler} that caused this exception
     */
    public String getCausingRetryName() {
        return causingRetryName;
    }
}
