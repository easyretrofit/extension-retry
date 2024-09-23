package io.github.easyretrofit.extension.retry.core.properties;

import io.github.easyretrofit.extension.retry.core.resource.CustomizedRetryConfig;

import java.util.HashMap;
import java.util.Map;

public class RetrofitRetryProperties {

    protected Map<String, InstanceProperties> instances = new HashMap<>();
    protected Map<String, ConfigProperties> configs = new HashMap<>();

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
