package io.github.easyretrofit.extension.retry.core;

import io.github.easyretrofit.core.CDIBeanManager;
import io.github.easyretrofit.core.RetrofitResourceContext;
import io.github.easyretrofit.core.resource.RetrofitApiInterfaceBean;
import io.github.easyretrofit.core.resource.RetrofitClientBean;
import io.github.easyretrofit.extension.retry.core.annotation.Retry;
import io.github.easyretrofit.extension.retry.core.interceptor.RetryHandler;
import io.github.easyretrofit.extension.retry.core.properties.RetrofitRetryProperties;
import io.github.easyretrofit.extension.retry.core.resource.*;
import io.github.easyretrofit.extension.retry.core.util.ResourceNameUtil;
import io.github.easyretrofit.extension.retry.core.util.WaitDurationUtils;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.*;
import java.util.function.Predicate;

public class RetrofitRetryResourceContextProcessor {
    private static final Logger log = LoggerFactory.getLogger(RetrofitRetryResourceContextProcessor.class);

    private final CDIBeanManager cdiBeanManager;
    private final RetrofitRetryResourceContext retryResourceContext;


    public RetrofitRetryResourceContextProcessor(RetrofitResourceContext retrofitResourceContext,
                                                 CDIBeanManager cdiBeanManager,
                                                 RetrofitRetryProperties retryProperties) {
        this.cdiBeanManager = cdiBeanManager;
        this.retryResourceContext = new RetrofitRetryResourceContext();
        setRetryRules(retrofitResourceContext.getRetrofitClients(), retryProperties);
    }

    public RetrofitRetryResourceContext generateRetryResourceContext() {
        log.debug("Generate Retry Resource Context");
        HashMap<String, RetryConfig> retryConfigHashMap = retryResourceContext.getRetryConfigHashMap();
        for (Map.Entry<String, RetryConfig> entry : retryConfigHashMap.entrySet()) {
            String key = entry.getKey();
            RetryConfig value = entry.getValue();
            log.debug("{} -> {}", key, value.toString());
        }
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
                retryResourceContext.addFallBackBean(retryConfigBean.getResourceName(), new FallBackBean(retryConfigBean.getResourceName(), retryConfigBean.getFallBackMethodName(), retryConfigBean));
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
                if (retryConfigBean.getRetryExceptions() != null) {
                    builder.retryExceptions(retryConfigBean.getRetryExceptions());
                }
                if (retryConfigBean.getIgnoreExceptions() != null) {
                    builder.ignoreExceptions(retryConfigBean.getIgnoreExceptions());
                }
                builder.writeableStackTrace(retryConfigBean.isWriteableStackTrace());
                if (retryConfigBean.getRetryOnResultPredicate() != null) {
                    Class<? extends Predicate<Response>> clazz = retryConfigBean.getRetryOnResultPredicate();
                    Predicate<Response> bean = cdiBeanManager.getBean(clazz);
                    if (bean == null) {
                        try {
                            bean = clazz.newInstance();
                        } catch (IllegalAccessException | InstantiationException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    builder.retryOnResult(bean);
                }
                if (retryConfigBean.getExceptionPredicate() != null) {
                    Class<? extends Predicate<Throwable>> clazz = retryConfigBean.getExceptionPredicate();
                    Predicate<Throwable> bean = cdiBeanManager.getBean(clazz);
                    if (bean == null) {
                        try {
                            bean = clazz.newInstance();
                        } catch (IllegalAccessException | InstantiationException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    builder.retryOnException(bean);
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
                return RetryConfigCustomizeProcessor.getCustomizedRetryConfig((Retry) annotation, properties, cdiBeanManager);
            }
        }
        return null;
    }


}
