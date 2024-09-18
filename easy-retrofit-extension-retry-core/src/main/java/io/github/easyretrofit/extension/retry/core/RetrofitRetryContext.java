package io.github.easyretrofit.extension.retry.core;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeoutException;

public class RetrofitRetryContext {


    public RetryConfig getRetryConfig(String resourceName) {
        RetryConfig.Builder builder = RetryConfig.custom()
                .maxAttempts(3)
                .waitDuration(Duration.ofMillis(2000))
                .retryOnResult(response -> response.code() == 500)
                .retryExceptions(IOException.class, TimeoutException.class)
                .retryOnException(throwable -> throwable instanceof IOException)
                .ignoreExceptions(IllegalArgumentException.class)
                .backoffMultiplier(2.0);
        return builder.build();
    }
}
