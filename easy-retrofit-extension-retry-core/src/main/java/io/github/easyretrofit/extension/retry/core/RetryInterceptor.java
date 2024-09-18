package io.github.easyretrofit.extension.retry.core;

import io.github.easyretrofit.core.RetrofitResourceContext;
import io.github.easyretrofit.core.extension.BaseInterceptor;
import okhttp3.Response;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeoutException;

public class RetryInterceptor extends BaseInterceptor {

    @Override
    protected Response executeIntercept(Chain chain) throws IOException {
        RetryConfig.Builder builder = RetryConfig.custom()
                .maxAttempts(3)
                .waitDuration(Duration.ofMillis(2000))
                .retryOnResult(response -> response.code() == 500)
                .retryExceptions(IOException.class, TimeoutException.class)
                .retryOnException(throwable -> throwable instanceof IOException)
                .ignoreExceptions(IllegalArgumentException.class)
                .backoffMultiplier(2.0);
        RetryConfig retryConfig = builder.build();
        return new Retry(retryConfig, "")
                .intercept(chain);
    }

    @Override
    protected RetrofitResourceContext getInjectedRetrofitResourceContext() {
        return null;
    }
}
