package br.com.vostre.circular.model.api;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CircularAPI {

    @GET("api/{id}/token")
    Call<String> requisitaToken(@Path("id") String id);

    @Headers("Content-Type: application/json")
    @POST("api/envia-dados/{token}")
    Call<String> enviaDados(@Path("token") String token, @Body String json);

    @GET("api/recebe-dados/{token}/{ultimoAcesso}")
    Call<String> recebeDados(@Path("token") String token, @Path("ultimoAcesso") String ultimoAcesso);

}
