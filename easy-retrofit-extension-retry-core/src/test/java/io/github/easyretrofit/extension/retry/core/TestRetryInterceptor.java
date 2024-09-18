package io.github.easyretrofit.extension.retry.core;

import okhttp3.Interceptor;
import okhttp3.Response;

import java.io.IOException;
import java.time.Duration;

public class TestRetryInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        RetryConfig.Builder builder = RetryConfig.custom()
                .maxAttempts(3)
                .waitDuration(Duration.ofSeconds(2))
                .backoffMultiplier(2.0);
        RetryConfig build = builder.build();
        return new Retry(build)
                .intercept(chain);
    }
}
