package io.github.easyretrofit.extension.retry.core;

import io.github.easyretrofit.core.CDIBeanManager;
import io.github.easyretrofit.extension.retry.core.annotation.Retry;
import io.github.easyretrofit.extension.retry.core.resource.BaseRetryConfig;
import io.github.easyretrofit.extension.retry.core.resource.CustomizedRetryConfig;
import io.github.easyretrofit.extension.retry.core.resource.RetryConfigBean;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * 用户自定义的RetryConfig实现的处理
 */
public class RetryConfigCustomizeProcessor {

    public static RetryConfigBean getCustomizedRetryConfig(Retry annotation, CustomizedRetryConfig config, CDIBeanManager cdiBeanManager) {
        CustomizedRetryConfig customize = null;
        Class<? extends BaseRetryConfig> configClazz = annotation.config();
        RetryConfigBean retryConfigBean = new RetryConfigBean();
        if (config == null) {
            if (configClazz == BaseRetryConfig.class) {
                return null;
            }
            BaseRetryConfig bean = cdiBeanManager.getBean(configClazz);

            if (bean != null) {
                customize = bean.build();
                try {
                    BeanUtils.copyProperties(retryConfigBean, customize);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            } else {
                try {
                    bean = configClazz.newInstance();
                    customize = bean.build();
                    BeanUtils.copyProperties(retryConfigBean, customize);
                } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            try {
                BeanUtils.copyProperties(retryConfigBean, config);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        retryConfigBean.setResourceName(annotation.resourceName());
        retryConfigBean.setFallBackMethodName(annotation.fallbackMethod());
        retryConfigBean.setConfigClazz(configClazz);
        return retryConfigBean;
    }
}
