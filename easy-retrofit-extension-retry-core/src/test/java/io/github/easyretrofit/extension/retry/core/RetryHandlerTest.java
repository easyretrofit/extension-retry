package io.github.easyretrofit.extension.retry.core;

import junit.framework.TestCase;
import okhttp3.*;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeoutException;

public class RetryHandlerTest extends TestCase {
    @Rule
    public final MockWebServer server = new MockWebServer();
    OkHttpClient client;
    Request request;

    @Before
    public void setUp() throws IOException {

    }

    @After
    public void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    public void testInterceptSync() throws IOException {
        server.start();
        request = new Request.Builder()
                .url(server.url("/test"))
                .get()
                .build();
        RetryConfig config = RetryConfig.custom()
                .resourceName("test")
                .maxAttempts(2)
                .waitDuration(Duration.ofMillis(200))
                .retryOnResult(response -> response.code() == 500)
                .retryExceptions(IOException.class, TimeoutException.class)
                .retryOnException(throwable -> throwable instanceof IOException)
                .ignoreExceptions(IllegalArgumentException.class)
                .backoffMultiplier(2.0).build();
        server.enqueue(new MockResponse().setBody("hello").setResponseCode(200));
        client = OkHttpClientBuilder.builder(config);
        okhttp3.Call call = client.newCall(request);
        try {
            call.execute();
        } catch (MaxRetriesExceededException e) {

        }
    }

    @Test
    public void testInterceptSyncResponse500() throws IOException {
        request = new Request.Builder()
                .url("http://localhost:8080")
                .get()
                .build();
        RetryConfig config = RetryConfig.custom()
                .resourceName("test")
                .maxAttempts(3)
                .waitDuration(Duration.ofMillis(200))
                .retryExceptions(IOException.class, TimeoutException.class)
                .retryOnException(throwable -> throwable instanceof IOException)
                .ignoreExceptions(IllegalArgumentException.class)
                .backoffMultiplier(2.0).build();
        client = OkHttpClientBuilder.builder(config);
        okhttp3.Call call = client.newCall(request);
        try {
            call.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testInterceptSyncIgnoreIOException() throws IOException {
        request = new Request.Builder()
                .url("http://localhost:8080")
                .get()
                .build();
        RetryConfig config = RetryConfig.custom()
                .resourceName("test")
                .maxAttempts(3)
                .waitDuration(Duration.ofMillis(200))
                .ignoreExceptions(IOException.class)
                .backoffMultiplier(2.0).build();
        client = OkHttpClientBuilder.builder(config);
        okhttp3.Call call = client.newCall(request);
        try {
            call.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testInterceptAsync() throws IOException {
        request = new Request.Builder()
                .url("http://localhost:8080")
                .get()
                .build();
        RetryConfig config = RetryConfig.custom()
                .resourceName("test")
                .maxAttempts(3)
                .waitDuration(Duration.ofMillis(200))
                .retryOnResult(response -> response.code() == 500)
                .retryExceptions(IOException.class, TimeoutException.class)
                .retryOnException(throwable -> throwable instanceof IOException)
                .ignoreExceptions(IllegalArgumentException.class)
                .backoffMultiplier(2.0).build();
        client = OkHttpClientBuilder.builder(config);
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