package io.github.easyretrofit.extension.retry.core;

import retrofit2.Call;
import retrofit2.http.GET;

public interface TestServiceApi {

    @GET("/hello")
    Call<String> getHellos();

}
