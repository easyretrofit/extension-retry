package io.github.easyretrofit.extension.retry.core;

import io.github.easyretrofit.extension.retry.core.resource.FallBackBean;
import io.github.easyretrofit.extension.retry.core.resource.RetryConfig;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RetrofitRetryResourceContext {

    private final HashMap<String, List<FallBackBean>> fallBackBeanMap = new HashMap<>();

    private final HashMap<String, RetryConfig> retryConfigHashMap;

    public RetrofitRetryResourceContext() {
        retryConfigHashMap = new HashMap<>();
    }


    public RetryConfig getRetryConfig(String resourceName) {
        return retryConfigHashMap.get(resourceName);
    }

    public HashMap<String, RetryConfig> getRetryConfigHashMap() {
        return retryConfigHashMap;
    }

    public void addFallBackBean(String resourceName, FallBackBean fallBackBean) {
        List<FallBackBean> fallBackBeans = getFallBackBeans(resourceName);
        fallBackBeans.add(fallBackBean);
        fallBackBeanMap.put(resourceName, fallBackBeans);
    }

    public List<FallBackBean> getFallBackBeans(String resourceName) {
        List<FallBackBean> fallBackBeans = fallBackBeanMap.get(resourceName);
        if (fallBackBeans == null) {
            fallBackBeans = new ArrayList<>();
        }
        return fallBackBeans;
    }

    public void addRetryConfig(RetryConfig retryConfig) {
        retryConfigHashMap.put(retryConfig.getResourceName(), retryConfig);
    }

    /**
     * check retry resource context when running time
     */
    public void check() {
        fallBackBeanMap.forEach((resourceName, fallBackBeans) -> {
            long fallBackMethodCount = fallBackBeans.stream().filter(fallBackBean -> StringUtils.isNotEmpty(fallBackBean.getFallBackMethodName())).count();
            if (fallBackMethodCount > 1) {
                throw new RuntimeException("resourceName:" + resourceName + " has more than one fallBackMethodName");
            }
        });
    }
}
