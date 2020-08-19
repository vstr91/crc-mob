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
import retrofit2.http.Query;

public interface CircularAPI {

    @GET("api/{id}/token/{tipo}/{versao}")
    Call<String> requisitaToken(@Path("id") String id, @Path("tipo") int tipo, @Path("versao") String versao);

    @Multipart
    @POST("api/envia-imagem/{token}")
    Call<String> enviaImagem(@Part MultipartBody.Part image, @Part("name") RequestBody name, @Path("token") String id);

    // API site Circular
//    @GET("api/recebe-imagem/{imagem}")
//    Call<ResponseBody> recebeImagem(@Path("imagem") String imagem);

    // API Cloudinary - CDN
    @GET("vostre/image/upload/v1568376445/circular/{imagem}")
    Call<ResponseBody> recebeImagem(@Path("imagem") String imagem);

    // API Cloudinary - CDN
    @GET("vostre/raw/upload/v1568376445/circular_viagem/{arquivo}")
    Call<ResponseBody> recebeArquivo(@Path("arquivo") String arquivo);

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

    @GET("route/v1/driving/{geopoints}?overview=simplified&geometries=polyline")
    Call<String> carregaCaminhoItinerario(@Path("geopoints") String geopoints);

    @GET("reverse")
    Call<String> carregaRua(@Query("format") String format, @Query("lat") Double latitude, @Query("lon") Double longitude, @Query("zoom") Integer zoom, @Query("addressdetails") Integer details);

    @GET("maps/api/geocode/json")
    Call<String> carregaEnderecoGoogle(@Query("latlng") String latLng, @Query("key") String key);

    @GET(".")
    Call<String> consultaFeriados(@Query("ano") String ano, @Query("estado") String estado, @Query("cidade") String cidade, @Query("token") String token, @Query("json") String json);

    @Headers({
            "Content-Type: application/json",
            "Authorization: Basic Mjk4Mzc5MjAtNzE0Ny00ODRmLWI5MGUtYTFkOTM4NTAwNTc1"
    })
    @POST("api/v1/notifications")
    Call<String> enviaNotificacaoPush(@Body String json);

}
