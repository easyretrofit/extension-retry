package io.github.easyretrofit.extension.retry.core;

import io.github.easyretrofit.core.CDIBeanManager;
import io.github.easyretrofit.core.RetrofitResourceContext;
import io.github.easyretrofit.core.resource.RetrofitApiInterfaceBean;
import io.github.easyretrofit.core.resource.RetrofitClientBean;
import io.github.easyretrofit.extension.retry.core.annotation.Retry;
import io.github.easyretrofit.extension.retry.core.properties.RetrofitRetryProperties;
import io.github.easyretrofit.extension.retry.core.resource.*;
import io.github.easyretrofit.extension.retry.core.util.ResourceNameUtil;
import io.github.easyretrofit.extension.retry.core.util.WaitDurationUtils;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.*;

public class RetrofitRetryResourceContextProcessor {

    private final CDIBeanManager cdiBeanManager;

    private RetrofitRetryResourceContext retryResourceContext;
    private CustomizedRetryConfig customizeRetryBean;


    public RetrofitRetryResourceContextProcessor(RetrofitResourceContext retrofitResourceContext,
                                                 CDIBeanManager cdiBeanManager,
                                                 RetrofitRetryProperties retryProperties) {
        this.cdiBeanManager = cdiBeanManager;
        this.retryResourceContext = new RetrofitRetryResourceContext();
    }

    public RetrofitRetryResourceContext generateRetryResourceContext() {


        return retryResourceContext;
    }

    private Class<?> hasRetryAnnotationClass(Set<Class<?>> parentClasses) {
        for (Class<?> clazz : parentClasses) {
            if (clazz.isAnnotationPresent(Retry.class)) {
                return clazz;
            }
        }
        return null;
    }

    private void setRetryRules(List<RetrofitClientBean> retrofitClients, RetrofitRetryProperties retryProperties) throws InvocationTargetException, IllegalAccessException {
        for (RetrofitClientBean retrofitClient : retrofitClients) {
            for (RetrofitApiInterfaceBean retrofitApiServiceBean : retrofitClient.getRetrofitApiInterfaceBeans()) {
                Class<?> apiClazz = retrofitApiServiceBean.getSelfClazz();
                Class<?> parentClazz = hasRetryAnnotationClass(retrofitApiServiceBean.getSelf2ParentClasses());
                for (Method declaredMethod : apiClazz.getDeclaredMethods()) {
                    Set<RetryConfigBean> retryConfigBeans = new HashSet<>();
                    Annotation[] annotations = declaredMethod.getAnnotations();
                    RetryConfigBean retryConfigBean = getRetryBean(annotations, declaredMethod, true, retryProperties);
                    if (retryConfigBean == null) {
                        RetryConfigBean selfClazzRetryBean = getRetryBean(apiClazz.getDeclaredAnnotations(), declaredMethod, false, retryProperties);
                        if (selfClazzRetryBean == null && parentClazz != null) {
                            RetryConfigBean parentClazzRetryBean = getRetryBean(parentClazz.getDeclaredAnnotations(), declaredMethod, false, retryProperties);
                            if (parentClazzRetryBean != null) {
                                retryConfigBeans.add(parentClazzRetryBean);
                            }
                        } else {
                            retryConfigBeans.add(selfClazzRetryBean);
                        }
                    } else {
                        retryConfigBeans.add(retryConfigBean);
                    }

                    setToRetryResourceContext(retryConfigBeans);
                }
            }
        }
    }

    private void setToRetryResourceContext(Set<RetryConfigBean> retryConfigBeans) {
        for (RetryConfigBean retryConfigBean : retryConfigBeans) {
            retryResourceContext.addFallBackBean(retryConfigBean.getDefaultResourceName(), new FallBackBean(retryConfigBean.getResourceName(), retryConfigBean.getFallBackMethodName(), retryConfigBean));
            RetryConfig.Builder builder = RetryConfig.custom();
            builder.resourceName(retryConfigBean.getResourceName());
            if (retryConfigBean.getMaxRetries().isPresent()) {
                builder.maxAttempts(retryConfigBean.getMaxRetries().get());
            }
            if (retryConfigBean.getWaitDuration().isPresent()) {
                long waitDuration = WaitDurationUtils.getWaitDuration(retryConfigBean.getWaitDuration().get());
                builder.waitDuration(Duration.ofMillis(waitDuration));
            }
            if (retryConfigBean.getBackoffExponentialMultiplier().isPresent()) {
                builder.backoffMultiplier(retryConfigBean.getBackoffExponentialMultiplier().get());
            }
            RetryConfig retryConfig = builder.build();
            retryResourceContext.addRetryConfig(retryConfig);

        }
    }

    private RetryConfigBean getRetryBean(Annotation[] annotations, Method declaredMethod, boolean isMethod, RetrofitRetryProperties retryProperties) throws InvocationTargetException, IllegalAccessException {
        for (Annotation annotation : annotations) {
            if (annotation instanceof Retry) {
                CustomizedRetryConfig properties = getCustomizeRetryBean((Retry) annotation, isMethod, retryProperties);
                RetryConfigBean degradeRuleBean = getRetryRuleBean((Retry) annotation, properties);
                if (degradeRuleBean != null) {
                    degradeRuleBean.setDefaultResourceName(ResourceNameUtil.getConventionResourceName(declaredMethod));
                    return degradeRuleBean;
                }
            }
        }
        return null;
    }

    private RetryConfigBean getRetryRuleBean(Retry annotation, CustomizedRetryConfig properties) {
        Class<? extends BaseRetryConfig> configClazz = annotation.config();
        RetryConfigBean degradeRuleBean = new RetryConfigBean();
        if (properties == null) {
            if (configClazz == BaseRetryConfig.class) {
                return null;
            }
            BaseRetryConfig bean = cdiBeanManager.getBean(configClazz);

            if (bean != null) {
                customizeRetryBean = bean.build();
                try {
                    BeanUtils.copyProperties(degradeRuleBean, customizeRetryBean);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            } else {
                try {
                    bean = configClazz.newInstance();
                    customizeRetryBean = bean.build();
                    BeanUtils.copyProperties(degradeRuleBean, customizeRetryBean);
                } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            try {
                BeanUtils.copyProperties(degradeRuleBean, properties);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        degradeRuleBean.setResourceName(annotation.resourceName());
        degradeRuleBean.setFallBackMethodName(annotation.fallbackMethod());
        degradeRuleBean.setConfigClazz(configClazz);
        return degradeRuleBean;
    }

    private CustomizedRetryConfig getCustomizeRetryBean(Retry annotation, boolean isMethod, RetrofitRetryProperties properties) throws InvocationTargetException, IllegalAccessException {
        CustomizedRetryConfig customizeDegradeRuleBean = new CustomizedRetryConfig();
        if (!isMethod) {
            Map<String, RetrofitRetryProperties.ConfigProperties> configs = properties.getConfigs();
            RetrofitRetryProperties.ConfigProperties configProperties = configs.get(annotation.resourceName());
            if (configProperties == null) {
                return null;
            }
            BeanUtils.copyProperties(customizeDegradeRuleBean, configProperties);
        } else {
            Map<String, RetrofitRetryProperties.InstanceProperties> instances = properties.getInstances();
            RetrofitRetryProperties.InstanceProperties instanceProperties = instances.get(annotation.resourceName());
            if (instanceProperties == null) {
                return null;
            }
            BeanUtils.copyProperties(customizeDegradeRuleBean, instanceProperties);
        }
        return customizeDegradeRuleBean;
    }
}
