package io.github.easyretrofit.extension.retry.spring.boot;

import io.github.easyretrofit.core.RetrofitInterceptorExtension;
import io.github.easyretrofit.core.exception.RetrofitExtensionException;
import io.github.easyretrofit.core.extension.BaseInterceptor;
import io.github.easyretrofit.core.proxy.BaseExceptionDelegate;
import io.github.easyretrofit.extension.retry.core.annotation.EnableRetry;
import io.github.easyretrofit.extension.retry.core.interceptor.RetryExceptionFallBackHandler;
import io.github.easyretrofit.extension.retry.core.interceptor.RetryInterceptor;

import java.lang.annotation.Annotation;

public class RetrofitRetryExtension implements RetrofitInterceptorExtension {
    @Override
    public Class<? extends Annotation> createAnnotation() {
        return EnableRetry.class;
    }

    @Override
    public Class<? extends BaseInterceptor> createInterceptor() {
        return RetryInterceptor.class;
    }

    @Override
    public Class<? extends BaseExceptionDelegate<? extends RetrofitExtensionException>> createExceptionDelegate() {
        return RetryExceptionFallBackHandler.class;
    }
}
