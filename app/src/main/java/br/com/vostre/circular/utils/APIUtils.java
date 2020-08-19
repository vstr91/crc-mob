package br.com.vostre.circular.utils;

import android.content.Context;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.com.vostre.circular.model.Feriado;
import br.com.vostre.circular.model.api.CircularAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class APIUtils {

    public static void criaNotificacaoPush(String titulo, String mensagem, String destino, String segmento,
                                           int atualizar, int mostrar, String id, Context ctx){

        String payload = "";

        switch(destino){
            case "itinerario":
                payload = "\"tela\": \"itinerario\"";
                break;
            case "parada":
                payload = "\"tela\": \"parada\"";
                break;
            case "mapa":
                payload = "\"tela\": \"mapa\"";
                break;
            case "mensagem":
                payload = "\"tela\": \"mensagem\"";
                break;
            case "detalhe_itinerario":
                payload = "\"tela\": \"detalhe_itinerario\", \"itinerario\": \""+id+"\"";
                break;
            case "detalhe_parada":
                payload = "\"tela\": \"detalhe_parada\", \"parada\": \""+id+"\"";
                break;
            case "detalhe_poi":
                payload = "\"tela\": \"detalhe_poi\", \"poi\": \""+id+"\"";
                break;
            default:
                payload = "\"tela\": \"menu\"";
                break;
        }

                String dados = "{" +
                "\"app_id\": \"02ec2fb2-4df1-41c4-828e-4db1a7247276\"," +
                "\"included_segments\": [\""+segmento+"\"],"+
                "\"data\": {\"atualizar\": \""+atualizar+"\", \"mostrar\": \""+mostrar+"\", "+payload+"}, "+
                "\"contents\": {\"en\": \""+mensagem+"\"}, " + "\"headings\": {\"en\": \""+titulo+"\"}"+
                "}";

        APIUtils.enviaNotificacaoPush(ctx, dados);
    }

    public static void forcaAtualizacaoPush(Context ctx){

        String dados = "{" +
                "\"app_id\": \"02ec2fb2-4df1-41c4-828e-4db1a7247276\"," +
                "\"included_segments\": [\"All\"],"+
                "\"data\": {\"atualizar\": \"1\", \"mostrar\": \"0\"}, "+
                "\"contents\": {\"en\": \"-\"}, " + "\"headings\": {\"en\": \"-\"}"+
                "}";

        APIUtils.enviaNotificacaoPush(ctx, dados);
    }

    public static void enviaNotificacaoPush(final Context ctx, String dados){

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .baseUrl("https://onesignal.com/")
                .build();

        CircularAPI api = retrofit.create(CircularAPI.class);

        Call<String> call = api.enviaNotificacaoPush(dados);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if(response.code() == 200){
                    Toast.makeText(ctx, "Push enviado!", Toast.LENGTH_SHORT).show();
                } else{
                    Toast.makeText(ctx, "Problema ao enviar Push! "+response.message(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(ctx.getApplicationContext(), "Erro ao enviar Push. "+t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

}
