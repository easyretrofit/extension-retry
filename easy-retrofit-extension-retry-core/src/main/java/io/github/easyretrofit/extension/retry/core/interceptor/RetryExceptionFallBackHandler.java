package io.github.easyretrofit.extension.retry.core.interceptor;

import io.github.easyretrofit.core.CDIBeanManager;
import io.github.easyretrofit.core.exception.RetrofitExtensionException;
import io.github.easyretrofit.core.extension.InterceptorUtils;
import io.github.easyretrofit.core.delegate.BaseExceptionDelegate;
import io.github.easyretrofit.core.delegate.BaseFallBack;
import io.github.easyretrofit.core.resource.RetrofitApiInterfaceBean;
import io.github.easyretrofit.extension.retry.core.RetrofitRetryResourceContext;
import io.github.easyretrofit.extension.retry.core.RetryException;
import io.github.easyretrofit.extension.retry.core.annotation.EnableRetry;
import io.github.easyretrofit.extension.retry.core.resource.FallBackBean;
import okhttp3.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Invocation;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

public class RetryExceptionFallBackHandler extends BaseExceptionDelegate<RetryException> {
    private static final Logger log = LoggerFactory.getLogger(RetryExceptionFallBackHandler.class);
    private final CDIBeanManager cdiBeanManager;

    public RetryExceptionFallBackHandler(Class<RetryException> exceptionClassName, CDIBeanManager cdiBeanManager) {
        super(exceptionClassName);
        this.cdiBeanManager = cdiBeanManager;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args, RetrofitExtensionException throwable) {
        if (throwable instanceof RetryException) {
            RetrofitApiInterfaceBean apiInterfaceBean = throwable.getRetrofitApiServiceBean();
            List<Annotation> annotations = InterceptorUtils.getAnnotationFromCurrentClass(EnableRetry.class, apiInterfaceBean.getSelfClazz());
            if (annotations.isEmpty()) {
                annotations = InterceptorUtils.getAnnotationFromParentClass(EnableRetry.class, apiInterfaceBean);
            }
            Request request = throwable.getRequest();
            Method blockMethod = Objects.requireNonNull(request.tag(Invocation.class)).method();
            assert !annotations.isEmpty();
            EnableRetry enableRetry = (EnableRetry) annotations.get(0);
            Class<? extends BaseFallBack> fallbackClazz = enableRetry.fallback();
            if (!fallbackClazz.getName().equals(BaseFallBack.class.getName())) {
                BaseFallBack<RetrofitExtensionException> fallBack = cdiBeanManager.getBean(fallbackClazz);
                try {
                    fallBack.setException(throwable);
                    RetrofitRetryResourceContext context = cdiBeanManager.getBean(RetrofitRetryResourceContext.class);

                    FallBackBean fallBackBean = context.getFallBackBean(((RetryException) throwable).getRetryResourceName());
                    if (fallBackBean != null) {
                        String fallBackMethodName = fallBackBean.getFallBackMethodName();
                        Method fallbackMethod = fallbackClazz.getDeclaredMethod(fallBackMethodName, blockMethod.getParameterTypes());
                        return fallbackMethod.invoke(fallBack, args);
                    }
                    return null;
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return null;
    }

}
