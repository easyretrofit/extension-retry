package io.github.easyretrofit.extension.retry.core.annotation;

import io.github.easyretrofit.core.annotation.RetrofitInterceptor;
import io.github.easyretrofit.extension.retry.core.interceptor.RetryInterceptor;
import io.github.easyretrofit.extension.retry.core.resource.BaseRetryConfig;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@RetrofitInterceptor(handler = RetryInterceptor.class)
public @interface Retry {

    String resourceName() default "";

    Class<? extends BaseRetryConfig> config() default BaseRetryConfig.class;
}
