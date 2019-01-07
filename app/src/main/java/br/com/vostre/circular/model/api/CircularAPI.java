package br.com.vostre.circular.model.api;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface CircularAPI {

    @GET("api/{id}/token/{tipo}")
    Call<String> requisitaToken(@Path("id") String id, @Path("tipo") int tipo);

    @Multipart
    @POST("api/envia-imagem/{token}")
    Call<String> enviaImagem(@Part MultipartBody.Part image, @Part("name") RequestBody name, @Path("token") String id);

    @GET("api/recebe-imagem/{imagem}")
    Call<ResponseBody> recebeImagem(@Path("imagem") String imagem);

    @Headers("Content-Type: application/json")
    @POST("api/envia-dados/{token}")
    Call<String> enviaDados(@Body String json, @Path("token") String token);

    @GET("api/recebe-dados/{token}/{ultimoAcesso}/{id}")
    Call<String> recebeDados(@Path("token") String token, @Path("ultimoAcesso") String ultimoAcesso, @Path("id") String id);

    @FormUrlEncoded
    @POST("api/valida-usuario")
    Call<String> validaUsuario(@Field("idToken") String idToken, @Field("id") String id);

    @GET("api/recebe-preferencias/{id}")
    Call<ResponseBody> recebePreferencias(@Path("id") String id);

    @GET("route/v1/driving/{partida};{destino}?overview=false")
    Call<String> carregaDistancia(@Path("partida") String partida, @Path("destino") String destino);

}
