package io.github.easyretrofit.extension.retry.core;

import io.github.easyretrofit.core.RetrofitResourceContext;
import io.github.easyretrofit.core.extension.BaseInterceptor;
import okhttp3.Response;

import java.io.IOException;

public class RetryInterceptor extends BaseInterceptor {


    private final RetrofitRetryContext retryContext;

    public RetryInterceptor(RetrofitRetryContext retryContext) {
        this.retryContext = retryContext;
    }

    @Override
    protected Response executeIntercept(Chain chain) throws IOException {

        return new RetryHandler(retryContext.getRetryConfig("test"))
                .intercept(chain);
    }

    @Override
    protected RetrofitResourceContext getInjectedRetrofitResourceContext() {
        return null;
    }
}
