package io.github.easyretrofit.extension.retry.core;

import junit.framework.TestCase;
import okhttp3.*;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import retrofit2.Retrofit;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;

public class RetryTest extends TestCase {

    OkHttpClient client;
    Request request;

    @Before
    public void setUp() {
        client = OkHttpClientBuilder.builder();
        request = new Request.Builder()
                .url("http://localhost:8080/hello")
                .get()
                .build();

    }

    @Test
    public void testInterceptSync() throws IOException {
        okhttp3.Call call = client.newCall(request);
        okhttp3.Response execute = call.execute();

    }

    @Test
    public void testInterceptAsync() throws IOException {
        // 创建一个CountDownLatch对象，初始计数为1
        CountDownLatch latch = new CountDownLatch(1);

        okhttp3.Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                // 请求失败，释放CountDownLatch
                latch.countDown();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // 释放CountDownLatch
                latch.countDown();
            }
        });
        // 主线程等待异步请求完成
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Interrupted while waiting for the request to complete.");
        }

    }


}