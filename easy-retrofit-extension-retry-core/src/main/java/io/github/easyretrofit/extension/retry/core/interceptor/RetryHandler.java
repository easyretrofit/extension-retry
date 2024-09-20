package io.github.easyretrofit.extension.retry.core.interceptor;

import io.github.easyretrofit.extension.retry.core.resource.RetryConfig;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.util.Arrays;
import java.util.Objects;

import static io.github.easyretrofit.extension.retry.core.MaxRetriesExceededException.createMaxRetriesExceededException;

public class RetryHandler implements Interceptor {
    private int retryCount;
    private final RetryConfig config;

    public RetryHandler(RetryConfig config) {
        this.config = config;
        this.retryCount = config.getMaxRetries();
    }

    @Override
    public Response intercept(Chain chain) {
        Request request = chain.request();
        while (true) {
            Response response;
            try {
                response = chain.proceed(request);
                if (config.getRetryOnResultPredicate() != null && config.getRetryOnResultPredicate().test(response)) {
                    if (this.shouldRetry()) {
                        createMaxRetriesExceededException(this);
                    }
                    retry();
                    continue;
                }
                return response;
            } catch (Exception e) {
                if (this.shouldRetry()) {
                    createMaxRetriesExceededException(this);
                } else if (Objects.requireNonNull(config.getExceptionPredicate()).test(e)) {
                    retry();
                } else if (Arrays.stream(config.getIgnoreExceptions()).anyMatch(x -> x.isInstance(e))) {
                    throw new RuntimeException(e);
                } else {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public RetryConfig getConfig() {
        return config;
    }

    private boolean shouldRetry() {
        return this.retryCount <= 0;
    }

    private void retry() {
        this.retryCount--;
        waitDuration();
    }

    private void waitDuration() {
        long backoffMs;
        if (config.getBackoffExponentialMultiplier() > RetryConfig.DEFAULT_EXPONENTIAL_BACKOFF_MULTIPLIER) {
            backoffMs = (long) (config.getWaitDuration() * Math.pow(config.getBackoffExponentialMultiplier(), config.getMaxRetries() - retryCount));
        } else {
            backoffMs = config.getWaitDuration();
        }

        try {
            Thread.sleep(backoffMs);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }
}
