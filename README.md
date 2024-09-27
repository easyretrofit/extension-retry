[![Version](https://img.shields.io/maven-central/v/io.github.easyretrofit/extension-retry-spring-boot-starter?logo=apache-maven&style=flat-square)](https://central.sonatype.com/artifact/io.github.easyretrofit/extension-retry-spring-boot-starter)
[![Build](https://github.com/easyretrofit/extension-retry/actions/workflows/build.yml/badge.svg)](https://github.com/easyretrofit/extension-retry/actions/workflows/build.yml/badge.svg)
[![License](https://img.shields.io/github/license/easyretrofit/extension-retry.svg)](http://www.apache.org/licenses/LICENSE-2.0)
[![License](https://img.shields.io/badge/JDK-8+-4EB1BA.svg)](https://docs.oracle.com/javase/8/)
[![License](https://img.shields.io/badge/spring--boot-2.0.0+-green.svg)]()

# extension-retry

## How to use in Spring Boot

please look at retry spring boot [example](https://github.com/liuziyuan/easy-retrofit-demo/tree/main/retrofit-spring-boot-retry-sample)

```java
@EnableRetry(fallback = RetryApiFallBack.class, extensions = @RetrofitInterceptorParam(sort = 10))
@RetrofitBuilder(baseUrl = "${app.hello.url}",
        addCallAdapterFactory = {SimpleBodyCallAdapterFactoryBuilder.class, ReactorCallAdapterFactoryBuilder.class},
        addConverterFactory = GsonConvertFactoryBuilder.class
)
public interface HelloApi {

    @Retry(resourceName = "error400", fallbackMethod = "error400")
    @GET("backend/v1/error/400")
    HelloBean error400();

    @Retry(resourceName = "error404")
    @GET("backend/v1/error/404")
    HelloBean error404();

    @Retry(resourceName = "helloMono400", fallbackMethod = "helloMono400")
    @GET("backend/v1/error/400")
    Mono<HelloBean> helloMono400();
}
```

```java
@RestController
public class Hello2Controller {

    @Autowired
    private HelloApi helloApi;
    @Autowired
    private View error;


    @GetMapping("/v2/hello/{message}")
    public Mono<HelloBean> hello(@PathVariable String message) throws IOException {
        return Mono.fromCallable(helloApi::error400);
    }

    @GetMapping("/v3/hello/{message}")
    public Mono<HelloBean> helloMono400(@PathVariable String message) throws IOException {
        return helloApi.helloMono400().onErrorResume(error -> {
            log.info("error400 fallback {}", error.getMessage());
            HelloBean helloBean = new HelloBean();
            helloBean.setMessage("mono error400 fallback");
            return Mono.just(helloBean);
        });
    }
}

```

when you use async call like Rxjava or Reactor, you can not use fallback function.

1. use `Mono.fromCallable(helloApi::error400)` , the `helloApi.error400()` is sync call, then you can use fallback function.
2. use `helloApi.helloMono400().onErrorResume(error -> {})` , the `helloApi.helloMono400()` is async call, then you can not use fallback function. the fallback function will be executed on onErrorResume function.

