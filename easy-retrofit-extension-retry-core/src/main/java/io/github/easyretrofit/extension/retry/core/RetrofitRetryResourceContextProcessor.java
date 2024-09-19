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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.*;

public class RetrofitRetryResourceContextProcessor {

    private final CDIBeanManager cdiBeanManager;
    private RetrofitRetryResourceContext retryResourceContext;


    public RetrofitRetryResourceContextProcessor(RetrofitResourceContext retrofitResourceContext,
                                                 CDIBeanManager cdiBeanManager,
                                                 RetrofitRetryProperties retryProperties) {
        this.cdiBeanManager = cdiBeanManager;
        this.retryResourceContext = new RetrofitRetryResourceContext();
        setRetryRules(retrofitResourceContext.getRetrofitClients(), retryProperties);
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

    private void setRetryRules(Set<RetrofitClientBean> retrofitClients, RetrofitRetryProperties retryProperties) {
        for (RetrofitClientBean retrofitClient : retrofitClients) {
            for (RetrofitApiInterfaceBean retrofitApiServiceBean : retrofitClient.getRetrofitApiInterfaceBeans()) {
                Class<?> apiClazz = retrofitApiServiceBean.getSelfClazz();
                Class<?> parentClazz = hasRetryAnnotationClass(retrofitApiServiceBean.getSelf2ParentClasses());
                for (Method declaredMethod : apiClazz.getDeclaredMethods()) {
                    Set<RetryConfigBean> retryConfigBeans = new HashSet<>();
                    Annotation[] annotations = declaredMethod.getAnnotations();
                    RetryConfigBean retryConfigBean = getRetryConfigBean(annotations, declaredMethod, retryProperties);
                    if (retryConfigBean == null) {
                        RetryConfigBean selfClazzRetryBean = getRetryConfigBean(apiClazz.getDeclaredAnnotations(), declaredMethod, retryProperties);
                        if (selfClazzRetryBean == null && parentClazz != null) {
                            RetryConfigBean parentClazzRetryBean = getRetryConfigBean(parentClazz.getDeclaredAnnotations(), declaredMethod, retryProperties);
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
            if (retryConfigBean != null) {
                retryResourceContext.addFallBackBean(retryConfigBean.getDefaultResourceName(), new FallBackBean(retryConfigBean.getResourceName(), retryConfigBean.getFallBackMethodName(), retryConfigBean));
                RetryConfig.Builder builder = RetryConfig.custom();
                builder.resourceName(retryConfigBean.getResourceName());
                if (retryConfigBean.getMaxRetries() != null && retryConfigBean.getMaxRetries().isPresent()) {
                    builder.maxAttempts(retryConfigBean.getMaxRetries().get());
                }
                if (retryConfigBean.getWaitDuration() != null && retryConfigBean.getWaitDuration().isPresent()) {
                    long waitDuration = WaitDurationUtils.getWaitDuration(retryConfigBean.getWaitDuration().get());
                    builder.waitDuration(Duration.ofMillis(waitDuration));
                }
                if (retryConfigBean.getBackoffExponentialMultiplier() != null && retryConfigBean.getBackoffExponentialMultiplier().isPresent()) {
                    builder.backoffMultiplier(retryConfigBean.getBackoffExponentialMultiplier().get());
                }
                RetryConfig retryConfig = builder.build();
                retryResourceContext.addRetryConfig(retryConfig);
            }
        }
    }

    private RetryConfigBean getRetryConfigBean(Annotation[] annotations, Method declaredMethod, RetrofitRetryProperties retryProperties) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof Retry) {
                CustomizedRetryConfig properties = RetryConfigPropertiesProcessor.getCustomizedRetryConfig((Retry) annotation, retryProperties);
                RetryConfigBean retryConfigBean = RetryConfigCustomizeProcessor.getCustomizedRetryConfig((Retry) annotation, properties, cdiBeanManager);
                if (retryConfigBean != null) {
                    retryConfigBean.setDefaultResourceName(ResourceNameUtil.getConventionResourceName(declaredMethod));
                    return retryConfigBean;
                }
            }
        }
        return null;
    }


}
