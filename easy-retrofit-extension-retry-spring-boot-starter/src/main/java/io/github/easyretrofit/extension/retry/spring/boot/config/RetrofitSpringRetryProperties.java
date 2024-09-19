package io.github.easyretrofit.extension.retry.spring.boot.config;

import io.github.easyretrofit.extension.retry.core.properties.RetrofitRetryProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "retrofit.retry")
public class RetrofitSpringRetryProperties extends RetrofitRetryProperties {



}
