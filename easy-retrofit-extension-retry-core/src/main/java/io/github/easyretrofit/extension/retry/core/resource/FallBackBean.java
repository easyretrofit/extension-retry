package io.github.easyretrofit.extension.retry.core.resource;

public class FallBackBean {

    private long id;
    private String type;
    private String defaultResourceName;
    private String resourceName;
    private String fallBackMethodName;

    public FallBackBean(String resourceName, String fallBackMethodName, RetryConfigBean retryConfigBean) {
        this.resourceName = resourceName;
        this.fallBackMethodName = fallBackMethodName;

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDefaultResourceName() {
        return defaultResourceName;
    }

    public void setDefaultResourceName(String defaultResourceName) {
        this.defaultResourceName = defaultResourceName;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getFallBackMethodName() {
        return fallBackMethodName;
    }

    public void setFallBackMethodName(String fallBackMethodName) {
        this.fallBackMethodName = fallBackMethodName;
    }
}
