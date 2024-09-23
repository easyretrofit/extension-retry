package io.github.easyretrofit.extension.retry.core.resource;

import okhttp3.Response;

import java.util.Optional;
import java.util.function.Predicate;

public class CustomizedRetryConfig {

    protected Optional<Integer> maxRetries;
    protected Optional<String> waitDuration;
    protected Optional<Double> backoffExponentialMultiplier;

    protected Class<? extends Throwable>[] retryExceptions;
    protected Class<? extends Throwable>[] ignoreExceptions;
    protected Class<? extends Predicate<Response>> retryOnResultPredicate;
    protected Class<? extends Predicate<Throwable>> exceptionPredicate;

    public Class<? extends Throwable>[] getRetryExceptions() {
        return retryExceptions;
    }

    public void setRetryExceptions(Class<? extends Throwable>[] retryExceptions) {
        this.retryExceptions = retryExceptions;
    }

    public Class<? extends Throwable>[] getIgnoreExceptions() {
        return ignoreExceptions;
    }

    public void setIgnoreExceptions(Class<? extends Throwable>[] ignoreExceptions) {
        this.ignoreExceptions = ignoreExceptions;
    }

    public Class<? extends Predicate<Response>> getRetryOnResultPredicate() {
        return retryOnResultPredicate;
    }

    public void setRetryOnResultPredicate(Class<? extends Predicate<Response>> retryOnResultPredicate) {
        this.retryOnResultPredicate = retryOnResultPredicate;
    }

    public Class<? extends Predicate<Throwable>> getExceptionPredicate() {
        return exceptionPredicate;
    }

    public void setExceptionPredicate(Class<? extends Predicate<Throwable>> exceptionPredicate) {
        this.exceptionPredicate = exceptionPredicate;
    }

    public Optional<Integer> getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(Optional<Integer> maxRetries) {
        this.maxRetries = maxRetries;
    }

    public Optional<String> getWaitDuration() {
        return waitDuration;
    }

    public void setWaitDuration(Optional<String> waitDuration) {
        this.waitDuration = waitDuration;
    }

    public Optional<Double> getBackoffExponentialMultiplier() {
        return backoffExponentialMultiplier;
    }

    public void setBackoffExponentialMultiplier(Optional<Double> backoffExponentialMultiplier) {
        this.backoffExponentialMultiplier = backoffExponentialMultiplier;
    }
}
