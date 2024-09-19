package io.github.easyretrofit.extension.retry.core;

import io.github.easyretrofit.extension.retry.core.annotation.Retry;
import io.github.easyretrofit.extension.retry.core.properties.RetrofitRetryProperties;
import io.github.easyretrofit.extension.retry.core.resource.CustomizedRetryConfig;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Optional;

/**
 * 从配置文件中获取的Retry Properties对象处理
 * 处理配置文件中的config和instance关系,不论@Retry存在于方法还是类上,都需要有自己的instance, config只是提供一个通用配置.
 */
public class RetryConfigPropertiesProcessor {

    public static CustomizedRetryConfig getCustomizedRetryConfig(Retry annotation, RetrofitRetryProperties properties) {
        CustomizedRetryConfig customizeDegradeRuleBean = null;
        Map<String, RetrofitRetryProperties.InstanceProperties> instances = properties.getInstances();
        RetrofitRetryProperties.InstanceProperties instanceProperties = instances.get(annotation.resourceName());
        if (instanceProperties != null) {
            customizeDegradeRuleBean = new CustomizedRetryConfig();
            Map<String, RetrofitRetryProperties.ConfigProperties> configs = properties.getConfigs();
            RetrofitRetryProperties.ConfigProperties configProperties = configs.get(instanceProperties.getBaseConfig());
            if (configProperties != null) {
                try {
                    BeanUtils.copyProperties(customizeDegradeRuleBean, configProperties);
                    copyPropertiesIfNotNull(instanceProperties, customizeDegradeRuleBean);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            } else {
                try {
                    BeanUtils.copyProperties(customizeDegradeRuleBean, instanceProperties);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
            return customizeDegradeRuleBean;
        }

        return customizeDegradeRuleBean;
    }

    public static void copyPropertiesIfNotNull(Object source, Object destination) {
        Class<?> destinationClass = destination.getClass();
        Field[] fields = destinationClass.getDeclaredFields();
        while (destinationClass != null) {
            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    Object value = field.get(source);
                    if (value instanceof Optional<?>) {
                        Optional<?> optionalValue = (Optional<?>) value;
                        if (optionalValue.isPresent()) {
                            Field destinationField = destinationClass.getDeclaredField(field.getName());
                            destinationField.setAccessible(true);
                            destinationField.set(destination, optionalValue);
                        }
                    }
                } catch (IllegalAccessException | NoSuchFieldException ignored) {
                }
            }

            destinationClass = destinationClass.getSuperclass();
        }

    }
}
