package br.com.vostre.circular.model.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface CircularAPI {

    @Headers("Content-Type: application/json")
    @POST("api/envia-dados/-/-")
    Call<String> enviaDados(@Body String json);

    @POST("api/recebe-dados/-/-")
    Call<String> recebeDados();

}
