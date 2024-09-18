package io.github.easyretrofit.extension.retry.core;

import okhttp3.OkHttpClient;

public class OkHttpClientBuilder {

    public static OkHttpClient builder(){

        return new OkHttpClient.Builder()
                .addInterceptor(new TestRetryInterceptor())
                .build();
    }
}
