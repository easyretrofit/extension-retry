package io.github.easyretrofit.extension.retry.core.resource;

public abstract class BaseRetryConfig {

    protected abstract CustomizedRetryConfig customizeRetryConfig();

    public CustomizedRetryConfig build() {
        return this.customizeRetryConfig();
    }
}
