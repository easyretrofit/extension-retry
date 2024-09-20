package io.github.easyretrofit.extension.retry.core;

import io.github.easyretrofit.extension.retry.core.interceptor.RetryHandler;
import io.github.easyretrofit.extension.retry.core.resource.RetryConfig;
import okhttp3.OkHttpClient;

public class OkHttpClientBuilder {

    public static OkHttpClient builder(RetryConfig config){

        return new OkHttpClient.Builder()
                .addInterceptor(new RetryHandler(config))
                .build();
    }
}
