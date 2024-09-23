package io.github.easyretrofit.extension.retry.core.resource;

public class RetryConfigBean extends CustomizedRetryConfig {
    private String resourceName;
    private String fallBackMethodName;
    private Class<?> configClazz;


    public String getFallBackMethodName() {
        return fallBackMethodName;
    }

    public void setFallBackMethodName(String fallBackMethodName) {
        this.fallBackMethodName = fallBackMethodName;
    }

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
