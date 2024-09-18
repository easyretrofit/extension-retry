package io.github.easyretrofit.extension.retry.core;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Objects;

import static io.github.easyretrofit.extension.retry.core.MaxRetriesExceededException.createMaxRetriesExceededException;

public class Retry implements Interceptor {

    private final String resourceName;
    private int retryCount;
    private final RetryConfig config;

    public Retry(RetryConfig config, String resourceName) {
        this.config = config;
        this.resourceName = resourceName;
        this.retryCount = config.getMaxRetries();
    }

    @Override
    public Response intercept(Chain chain) {
        Request request = chain.request();
        while (true) {
            Response response = null;
            try {
                response = chain.proceed(request);
                if (Objects.requireNonNull(config.getRetryOnResultPredicate()).test(response)) {
                    if (this.shouldRetry()) {
                        retry();
                        continue;
                    }
                }
                return response;
            } catch (IOException e) {
                if (!this.shouldRetry()) {
                    createMaxRetriesExceededException(this);
                }
                retry();
            }
        }
    }


    public RetryConfig getConfig() {
        return config;
    }

    public String getResourceName() {
        return resourceName;
    }

    private boolean shouldRetry() {
        return this.retryCount > 0;
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
