package io.github.easyretrofit.extension.retry.core;

import io.github.easyretrofit.core.exception.RetrofitExtensionException;
import io.github.easyretrofit.core.resource.RetrofitApiInterfaceBean;
import okhttp3.Request;

public class RetryException extends RetrofitExtensionException {

    protected String retryResourceName;

    public RetryException(String message, Throwable cause, RetrofitApiInterfaceBean retrofitApiInterfaceBean, Request request, String retryResourceName) {
        super(message, cause, retrofitApiInterfaceBean, request);
        this.retryResourceName = retryResourceName;
    }

    public RetryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, RetrofitApiInterfaceBean retrofitApiInterfaceBean, Request request, String retryResourceName) {
        super(message, null, enableSuppression, writableStackTrace, retrofitApiInterfaceBean, request);
        this.retryResourceName = retryResourceName;
    }

    public String getRetryResourceName() {
        return retryResourceName;
    }

}
