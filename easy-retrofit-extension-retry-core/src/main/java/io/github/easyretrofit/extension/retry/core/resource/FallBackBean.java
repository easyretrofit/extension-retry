package io.github.easyretrofit.extension.retry.core.resource;

public class FallBackBean {

    private long id;
    private String type;
    private String defaultResourceName;
    private String resourceName;

    public FallBackBean(String resourceName, RetryConfigBean retryConfigBean) {
        this.resourceName = resourceName;
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
}
