package io.github.easyretrofit.extension.retry.core.annotation;

import io.github.easyretrofit.core.annotation.RetrofitInterceptor;
import io.github.easyretrofit.core.annotation.RetrofitInterceptorParam;
import io.github.easyretrofit.core.proxy.BaseFallBack;
import io.github.easyretrofit.extension.retry.core.interceptor.RetryInterceptor;
import io.github.easyretrofit.extension.retry.core.resource.BaseRetryConfig;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@RetrofitInterceptor(handler = RetryInterceptor.class)
public @interface EnableRetry {

    Class<? extends BaseFallBack> fallback() default BaseFallBack.class;

    RetrofitInterceptorParam extensions() default @RetrofitInterceptorParam();
}
