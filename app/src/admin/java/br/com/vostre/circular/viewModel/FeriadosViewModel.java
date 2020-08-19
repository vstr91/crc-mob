package br.com.vostre.circular.viewModel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import android.os.AsyncTask;
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
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.utils.StringUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class FeriadosViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;

    public LiveData<List<Feriado>> feriados;
    public Feriado feriado;

    public static MutableLiveData<Integer> retorno;

    public FeriadosViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
        feriado = new Feriado();
        feriados = appDatabase.feriadoDAO().listarTodosAtivos();

        retorno = new MutableLiveData<>();
        retorno.setValue(-1);
    }

    public void carregaFeriados(){
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .baseUrl("https://api.calendario.com.br/")
                .build();

        CircularAPI api = retrofit.create(CircularAPI.class);

        Call<String> call = api.consultaFeriados(DateTimeFormat.forPattern("yyyy").print(DateTime.now()),
                "RJ", "BARRA_DO_PIRAI", "YWxtaXIuYW1qdW5pb3JAZ21haWwuY29tJmhhc2g9MTIxOTc1MDcz", "true");

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                try {
                    JSONArray array = new JSONArray(response.body());

                    int cont = array.length();
                    List<Feriado> feriados = new ArrayList<>();

                    for(int i = 0; i < cont; i++){
                        JSONObject obj = array.getJSONObject(i);

                        if(obj != null){

                            // considera so feriados nacionais
                            if(obj.getInt("type_code") == 1 || obj.getInt("type_code") == 2){
                                Feriado feriado = new Feriado();

                                feriado.setNome(obj.getString("name"));
                                feriado.setDescricao(obj.optString("description", ""));
                                feriado.setTipo(obj.getInt("type_code"));
                                feriado.setData(DateTimeFormat.forPattern("dd/MM/yyyy").parseLocalDateTime((obj.getString("date"))).toDateTime());

                                feriado.setSlug(StringUtils.toSlug(feriado.getNome()));

                                feriados.add(feriado);
                            }

                            //System.out.println("RUA: "+rua+", "+cep);

                            //parada.setRua(rua);
                            //parada.setCep(cep);

                            //edit(parada);

                        } else{
                            Toast.makeText(getApplication().getApplicationContext(), "Erro ao carregar feriados. "+response.message(), Toast.LENGTH_SHORT).show();
                        }

                    }

                    atualizarFeriados(feriados);

                } catch (JSONException e) {
                    Toast.makeText(getApplication().getApplicationContext(), "Erro ao processar retorno. "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getApplication().getApplicationContext(), "Erro ao carregar feriados. "+t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void atualizarFeriados(List<Feriado> feriados){

        new removeAsyncTask(appDatabase).execute(DateTimeFormat.forPattern("yyyy").print(DateTime.now()));

        for(Feriado f : feriados){
            add(f);
        }


    }

    // adicionar

    public void add(final Feriado feriado) {

        feriado.setDataCadastro(new DateTime());
        feriado.setUltimaAlteracao(new DateTime());
        feriado.setEnviado(false);

        new addAsyncTask(appDatabase).execute(feriado);
    }

    private static class addAsyncTask extends AsyncTask<Feriado, Void, Void> {

        private AppDatabase db;

        addAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Feriado... params) {
            db.feriadoDAO().inserir((params[0]));
            return null;
        }

    }

    // fim adicionar

    private static class removeAsyncTask extends AsyncTask<String, Void, Void> {

        private AppDatabase db;

        removeAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final String... params) {

            DateTime anoNovo = new DateTime(2020, 1, 1, 0, 0, 0);

            db.feriadoDAO().deletarTodosPorAno((params[0]));
            return null;
        }

    }

}
