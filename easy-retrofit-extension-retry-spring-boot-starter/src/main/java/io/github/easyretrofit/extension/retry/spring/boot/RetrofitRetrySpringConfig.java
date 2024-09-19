package io.github.easyretrofit.extension.retry.spring.boot;

import io.github.easyretrofit.core.RetrofitResourceContext;
import io.github.easyretrofit.extension.retry.core.RetrofitRetryResourceContext;
import io.github.easyretrofit.extension.retry.core.RetrofitRetryResourceContextProcessor;
import io.github.easyretrofit.extension.retry.spring.boot.config.RetrofitSpringRetryProperties;
import io.github.easyretrofit.spring.boot.SpringCDIBeanManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(RetrofitSpringRetryProperties.class)
public class RetrofitRetrySpringConfig implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Bean
    @ConditionalOnMissingBean
    public RetrofitRetryResourceContext retrofitRetryResourceContext(@Autowired RetrofitSpringRetryProperties properties) {
        RetrofitRetryResourceContextProcessor processor = new RetrofitRetryResourceContextProcessor(
                applicationContext.getBean(RetrofitResourceContext.class),
                new SpringCDIBeanManager(applicationContext),
                properties);
        RetrofitRetryResourceContext context = processor.generateRetryResourceContext();
        return context;
    }
}
