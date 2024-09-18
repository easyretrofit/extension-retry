package io.github.easyretrofit.extension.retry.core.resource;

import io.github.easyretrofit.extension.retry.core.PredicateCreator;
import okhttp3.Response;

import javax.annotation.Nullable;
import java.time.Duration;
import java.util.function.Predicate;

public class RetryConfig {

    public static final long DEFAULT_WAIT_DURATION = 500;
    public static final int DEFAULT_MAX_ATTEMPTS = 3;
    public static final double DEFAULT_EXPONENTIAL_BACKOFF_MULTIPLIER = 0.0;
    public static final Predicate<Throwable> DEFAULT_RECORD_FAILURE_PREDICATE = throwable -> true;

    private int maxRetries;
    private long waitDuration;
    private double backoffExponentialMultiplier;
    @Nullable
    private transient Predicate<Response> retryOnResultPredicate;

    @SuppressWarnings("unchecked")
    private Class<? extends Throwable>[] retryExceptions = new Class[0];
    @SuppressWarnings("unchecked")
    private Class<? extends Throwable>[] ignoreExceptions = new Class[0];
    private Predicate<Throwable> exceptionPredicate;
    private boolean writeableStackTrace = true;
    private String resourceName;

    public RetryConfig() {

    }

    public static RetryConfig ofDefaults() {
        return new Builder().build();
    }

    public static Builder custom() {
        return new Builder();
    }

    public static Builder from(RetryConfig baseConfig) {
        return new Builder(baseConfig);
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public double getBackoffExponentialMultiplier() {
        return backoffExponentialMultiplier;
    }

    public long getWaitDuration() {
        return waitDuration;
    }

    @Nullable
    public Predicate<Response> getRetryOnResultPredicate() {
        return retryOnResultPredicate;
    }

    public Class<? extends Throwable>[] getRetryExceptions() {
        return retryExceptions;
    }

    public Predicate<Throwable> getExceptionPredicate() {
        return exceptionPredicate;
    }

    public boolean isWriteableStackTrace() {
        return writeableStackTrace;
    }

    public Class<? extends Throwable>[] getIgnoreExceptions() {
        return ignoreExceptions;
    }

    public String getResourceName() {
        return resourceName;
    }


    public static class Builder {
        private int maxAttempts = DEFAULT_MAX_ATTEMPTS;
        private Duration waitDuration = Duration.ofMillis(DEFAULT_WAIT_DURATION);
        private double backoffMultiplier = 0.0;
        private Predicate<Response> retryOnResultPredicate;
        private Predicate<Throwable> retryOnExceptionPredicate;
        @SuppressWarnings("unchecked")
        private Class<? extends Throwable>[] retryExceptions = new Class[0];
        @SuppressWarnings("unchecked")
        private Class<? extends Throwable>[] ignoreExceptions = new Class[0];
        private boolean writeableStackTrace = false;
        private String resourceName;

        public Builder() {
        }

        public Builder(RetryConfig baseConfig) {

        }

        public RetryConfig build() {
            RetryConfig config = new RetryConfig();
            config.maxRetries = maxAttempts;
            config.waitDuration = waitDuration.toMillis();
            config.backoffExponentialMultiplier = backoffMultiplier;
            config.retryOnResultPredicate = retryOnResultPredicate;
            config.retryExceptions = retryExceptions;
            config.ignoreExceptions = ignoreExceptions;
            config.writeableStackTrace = writeableStackTrace;
            config.exceptionPredicate = createExceptionPredicate();
            config.resourceName = resourceName;
            return config;
        }

        public Builder maxAttempts(int maxAttempts) {
            if (maxAttempts < 1) {
                throw new IllegalArgumentException(
                        "maxAttempts must be greater than or equal to 1");
            }
            this.maxAttempts = maxAttempts;
            return this;
        }

        public Builder waitDuration(Duration waitDuration) {
            if (waitDuration.toMillis() >= 0) {
                this.waitDuration = waitDuration;
            } else {
                throw new IllegalArgumentException(
                        "waitDuration must be a positive value");
            }
            return this;
        }

        public Builder backoffMultiplier(double backoffMultiplier) {
            this.backoffMultiplier = backoffMultiplier;
            return this;
        }

        public Builder retryOnResult(Predicate<Response> predicate) {
            this.retryOnResultPredicate = predicate;
            return this;
        }

        public Builder retryOnException(Predicate<Throwable> predicate) {
            this.retryOnExceptionPredicate = predicate;
            return this;
        }

        @SuppressWarnings("unchecked")
        @SafeVarargs
        public final Builder retryExceptions(
                @Nullable Class<? extends Throwable>... errorClasses) {
            this.retryExceptions = errorClasses != null ? errorClasses : new Class[0];
            return this;
        }

        @SuppressWarnings("unchecked")
        @SafeVarargs
        public final Builder ignoreExceptions(
                @Nullable Class<? extends Throwable>... errorClasses) {
            this.ignoreExceptions = errorClasses != null ? errorClasses : new Class[0];
            return this;
        }


        private Predicate<Throwable> createExceptionPredicate() {
            return createRetryOnExceptionPredicate()
                    .and(PredicateCreator.createNegatedExceptionsPredicate(ignoreExceptions)
                            .orElse(DEFAULT_RECORD_FAILURE_PREDICATE));
        }

        private Predicate<Throwable> createRetryOnExceptionPredicate() {
            return PredicateCreator.createExceptionsPredicate(retryOnExceptionPredicate, retryExceptions)
                    .orElse(DEFAULT_RECORD_FAILURE_PREDICATE);
        }

        private Builder writeableStackTrace(boolean writeableStackTrace) {
            this.writeableStackTrace = writeableStackTrace;
            return this;
        }


        public Builder resourceName(String resourceName) {
            this.resourceName = resourceName;
            return this;
        }
    }


}
