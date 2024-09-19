package io.github.easyretrofit.extension.retry.core.properties;

import io.github.easyretrofit.extension.retry.core.resource.CustomizedRetryConfig;

import java.util.HashMap;
import java.util.Map;

public class RetrofitRetryProperties {

    protected Map<String, InstanceProperties> instances = new HashMap<>();
    protected Map<String, ConfigProperties> configs = new HashMap<>();


    public void checkAndMerge() {
        instances.forEach((resourceName, bean) -> {
            if (bean.getBaseConfig() != null) {
                ConfigProperties configProperties = configs.get(bean.getBaseConfig());
                if (configProperties == null) {
                    throw new IllegalArgumentException("retry instances '" + resourceName + "' : baseConfig " + bean.getBaseConfig() + " not found");
                } else {
                    merge(bean, configProperties);
                }
            }
        });
    }

    private void merge(InstanceProperties instanceProperties, ConfigProperties configProperties) {
        if (instanceProperties.getMaxRetries().isPresent()) {
            instanceProperties.setMaxRetries(configProperties.getMaxRetries());
        }
        if (instanceProperties.getWaitDuration().isPresent()) {
            instanceProperties.setWaitDuration(configProperties.getWaitDuration());
        }
        if (instanceProperties.getBackoffExponentialMultiplier().isPresent()) {
            instanceProperties.setBackoffExponentialMultiplier(configProperties.getBackoffExponentialMultiplier());
        }
    }

    public static class InstanceProperties extends CustomizedRetryConfig {
        private String baseConfig;

        public String getBaseConfig() {
            return baseConfig;
        }

        public void setBaseConfig(String baseConfig) {
            this.baseConfig = baseConfig;
        }
    }

    public static class ConfigProperties extends CustomizedRetryConfig {
    }

    public Map<String, InstanceProperties> getInstances() {
        return instances;
    }

    public void setInstances(Map<String, InstanceProperties> instances) {
        this.instances = instances;
    }

    public Map<String, ConfigProperties> getConfigs() {
        return configs;
    }

    public void setConfigs(Map<String, ConfigProperties> configs) {
        this.configs = configs;
    }
}
