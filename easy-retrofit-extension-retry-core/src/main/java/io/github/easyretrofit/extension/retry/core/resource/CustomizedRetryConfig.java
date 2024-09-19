package io.github.easyretrofit.extension.retry.core.resource;

import java.util.Optional;

public class CustomizedRetryConfig {

    protected Optional<Integer> maxRetries;
    protected Optional<String> waitDuration;
    protected Optional<Double> backoffExponentialMultiplier;

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
