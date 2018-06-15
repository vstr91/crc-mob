package br.com.vostre.circular.model.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface CircularAPI {

    @POST("api/recebe-dados/-/-")
    Call<String> enviaDados(@Body String json);

    @POST("api/recebe-dados/")
    Call<String> recebeDados();

}
