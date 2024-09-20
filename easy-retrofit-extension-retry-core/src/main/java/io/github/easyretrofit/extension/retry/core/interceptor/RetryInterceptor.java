package io.github.easyretrofit.extension.retry.core.interceptor;

import io.github.easyretrofit.core.RetrofitResourceContext;
import io.github.easyretrofit.core.extension.BaseInterceptor;
import io.github.easyretrofit.extension.retry.core.RetrofitRetryResourceContext;
import io.github.easyretrofit.extension.retry.core.annotation.Retry;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;

public class RetryInterceptor extends BaseInterceptor {

    private final RetrofitResourceContext context;
    private final RetrofitRetryResourceContext retryContext;

    public RetryInterceptor(RetrofitResourceContext context, RetrofitRetryResourceContext retryContext) {
        this.retryContext = retryContext;
        this.context = context;
    }

    @Override
    protected Response executeIntercept(Chain chain) throws IOException {
        String resourceName = null;
        List<Annotation> annotationFromMethod = super.getAnnotationFromMethod(Retry.class);
        if (annotationFromMethod.isEmpty()) {
            List<Annotation> annotationFromCurrentClass = super.getAnnotationFromCurrentClass(Retry.class);
            if (annotationFromCurrentClass.isEmpty()) {
                List<Annotation> annotationFromParentClass = super.getAnnotationFromParentClass(Retry.class);
                if (!annotationFromParentClass.isEmpty()) {
                    resourceName = getResourceName(annotationFromParentClass);
                }
            } else {
                resourceName = getResourceName(annotationFromMethod);
            }
        } else {
            resourceName = getResourceName(annotationFromMethod);
        }
        if (resourceName == null) {
            return chain.proceed(chain.request());
        }
        return new RetryHandler(retryContext.getRetryConfig(resourceName))
                .intercept(chain);
    }

    @Override
    protected RetrofitResourceContext getInjectedRetrofitResourceContext() {
        return context;
    }

    private String getResourceName(List<Annotation> annotations) {
        Retry retry = (Retry) (annotations.get(0));
        return retry.resourceName();
    }
}
