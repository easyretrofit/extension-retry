package io.github.easyretrofit.extension.retry.core.resource;

public class RetryConfigBean extends CustomizedRetryConfig {
    private String resourceName;
    private Class<?> configClazz;

    public Class<?> getConfigClazz() {
        return configClazz;
    }

    public void setConfigClazz(Class<?> configClazz) {
        this.configClazz = configClazz;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

}
