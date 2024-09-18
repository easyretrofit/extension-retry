package io.github.easyretrofit.extension.retry.core;

import okhttp3.OkHttpClient;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeoutException;

public class OkHttpClientBuilder {

    public static OkHttpClient builder(RetryConfig config){

        return new OkHttpClient.Builder()
                .addInterceptor(new Retry(config))
                .build();
    }
}
