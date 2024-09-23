package io.github.easyretrofit.extension.retry.core.resource;

import okhttp3.Response;

import java.util.Optional;
import java.util.function.Predicate;

public class CustomizedRetryConfig {

    protected Optional<Integer> maxRetries;
    protected Optional<String> waitDuration;
    protected Optional<Double> backoffExponentialMultiplier;
    protected Optional<Class<? extends Throwable>[]> retryExceptions;
    protected Optional<Class<? extends Throwable>[]> ignoreExceptions;
    protected Optional<Class<? extends Predicate<Response>>> retryOnResultPredicate;
    protected Optional<Class<? extends Predicate<Throwable>>> exceptionPredicate;

    public Optional<Class<? extends Predicate<Response>>> getRetryOnResultPredicate() {
        return retryOnResultPredicate;
    }

    public void setRetryOnResultPredicate(Optional<Class<? extends Predicate<Response>>> retryOnResultPredicate) {
        this.retryOnResultPredicate = retryOnResultPredicate;
    }

    public Optional<Class<? extends Throwable>[]> getRetryExceptions() {
        return retryExceptions;
    }

    public void setRetryExceptions(Optional<Class<? extends Throwable>[]> retryExceptions) {
        this.retryExceptions = retryExceptions;
    }

    public Optional<Class<? extends Throwable>[]> getIgnoreExceptions() {
        return ignoreExceptions;
    }

    public void setIgnoreExceptions(Optional<Class<? extends Throwable>[]> ignoreExceptions) {
        this.ignoreExceptions = ignoreExceptions;
    }

    public Optional<Class<? extends Predicate<Throwable>>> getExceptionPredicate() {
        return exceptionPredicate;
    }

    public void setExceptionPredicate(Optional<Class<? extends Predicate<Throwable>>> exceptionPredicate) {
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
