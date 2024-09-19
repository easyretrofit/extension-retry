package io.github.easyretrofit.extension.retry.core.annotation;

import io.github.easyretrofit.extension.retry.core.resource.BaseRetryConfig;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Retry {

    String resourceName() default "";

    Class<? extends BaseRetryConfig> config() default BaseRetryConfig.class;

    String fallbackMethod();
}
