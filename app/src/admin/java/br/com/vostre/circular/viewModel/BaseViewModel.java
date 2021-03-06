package br.com.vostre.circular.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableField;
import android.os.AsyncTask;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.List;

import br.com.vostre.circular.model.Bairro;
import br.com.vostre.circular.model.Cidade;
import br.com.vostre.circular.model.Empresa;
import br.com.vostre.circular.model.EntidadeBase;
import br.com.vostre.circular.model.Estado;
import br.com.vostre.circular.model.Horario;
import br.com.vostre.circular.model.HorarioItinerario;
import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.Mensagem;
import br.com.vostre.circular.model.Onibus;
import br.com.vostre.circular.model.Pais;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.ParadaItinerario;
import br.com.vostre.circular.model.Parametro;
import br.com.vostre.circular.model.ParametroInterno;
import br.com.vostre.circular.model.PontoInteresse;
import br.com.vostre.circular.model.SecaoItinerario;
import br.com.vostre.circular.model.Usuario;
import br.com.vostre.circular.model.api.CircularAPI;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.utils.Crypt;
import br.com.vostre.circular.utils.PreferenceUtils;
import br.com.vostre.circular.utils.Unique;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class BaseViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;
    ObservableField<String> id;
    public LiveData<List<Mensagem>> mensagensNaoLidas;

    public MutableLiveData usuarioValidado;
    String baseUrl;

    public LiveData<List<ParametroInterno>> parametrosInternos;

    public ObservableField<String> getId() {
        return id;
    }

    public void setId(ObservableField<String> id) {
        this.id = id;
    }

    public BaseViewModel(Application app) {
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
        id = new ObservableField<>();
        new paramAsyncTask(appDatabase).execute();
        mensagensNaoLidas = appDatabase.mensagemDAO().listarTodosNaoLidosServidor();
        parametrosInternos = appDatabase.parametroInternoDAO().listarTodos();

        usuarioValidado = new MutableLiveData<>();
        usuarioValidado.postValue(false);
    }

    public void atualizarMensagens() {
        mensagensNaoLidas = appDatabase.mensagemDAO().listarTodosNaoLidosServidor();
    }

    public void salvar(List<? extends EntidadeBase> dados, String entidade) {
        add(dados, entidade);
    }

    // adicionar

    public void add(final List<? extends EntidadeBase> entidadeBase, String entidade) {

        new addAsyncTask(appDatabase, entidade).execute(entidadeBase);
    }

    private static class addAsyncTask extends AsyncTask<List<? extends EntidadeBase>, Void, Void> {

        private AppDatabase db;
        private String entidade;

        addAsyncTask(AppDatabase appDatabase, String entidade) {
            db = appDatabase;
            this.entidade = entidade;
        }

        @Override
        protected Void doInBackground(final List<? extends EntidadeBase>... params) {

            switch (entidade) {
                case "pais":
                    db.paisDAO().deletarTodos();
                    db.paisDAO().inserirTodos((List<Pais>) params[0]);
                    break;
                case "empresa":
                    db.empresaDAO().deletarTodos();
                    db.empresaDAO().inserirTodos((List<Empresa>) params[0]);
                    break;
                case "onibus":
                    db.onibusDAO().deletarTodos();
                    db.onibusDAO().inserirTodos((List<Onibus>) params[0]);
                    break;
                case "estado":
                    db.estadoDAO().deletarTodos();
                    db.estadoDAO().inserirTodos((List<Estado>) params[0]);
                    break;
                case "cidade":
                    db.cidadeDAO().deletarTodos();
                    db.cidadeDAO().inserirTodos((List<Cidade>) params[0]);
                    break;
                case "bairro":
                    db.bairroDAO().deletarTodos();
                    db.bairroDAO().inserirTodos((List<Bairro>) params[0]);
                    break;
                case "parada":
                    db.paradaDAO().deletarTodos();
                    db.paradaDAO().inserirTodos((List<Parada>) params[0]);
                    break;
                case "itinerario":
                    db.itinerarioDAO().deletarTodos();
                    db.itinerarioDAO().inserirTodos((List<Itinerario>) params[0]);
                    break;
                case "horario":
                    db.horarioDAO().deletarTodos();
                    db.horarioDAO().inserirTodos((List<Horario>) params[0]);
                    break;
                case "parada_itinerario":
                    db.paradaItinerarioDAO().deletarTodos();
                    db.paradaItinerarioDAO().inserirTodos((List<ParadaItinerario>) params[0]);
                    break;
                case "secao_itinerario":
                    db.secaoItinerarioDAO().deletarTodos();
                    db.secaoItinerarioDAO().inserirTodos((List<SecaoItinerario>) params[0]);
                    break;
                case "horario_itinerario":
                    db.horarioItinerarioDAO().deletarTodos();
                    db.horarioItinerarioDAO().inserirTodos((List<HorarioItinerario>) params[0]);
                    break;
                case "mensagem":
                    db.mensagemDAO().deletarTodos();
                    db.mensagemDAO().inserirTodos((List<Mensagem>) params[0]);
                    break;
                case "parametro":
                    db.parametroDAO().deletarTodos();
                    db.parametroDAO().inserirTodos((List<Parametro>) params[0]);
                    break;
                case "ponto_interesse":
                    db.pontoInteresseDAO().deletarTodos();
                    db.pontoInteresseDAO().inserirTodos((List<PontoInteresse>) params[0]);
                    break;
                case "usuario":
                    db.usuarioDAO().deletarTodos();
                    db.usuarioDAO().inserirTodos((List<Usuario>) params[0]);
                    break;
                case "parametro_interno":
                    db.parametroInternoDAO().inserir((ParametroInterno) params[0].get(0));
                    break;
            }

            return null;
        }

    }

    // fim adicionar

    private class paramAsyncTask extends AsyncTask<ParametroInterno, Void, Void> {

        private AppDatabase db;
        private ParametroInterno parametro;

        paramAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final ParametroInterno... params) {

            parametro = appDatabase.parametroInternoDAO().carregar();

            if (parametro == null) {
                parametro = new ParametroInterno();
                parametro.setId("1");
                parametro.setDataCadastro(DateTime.now());
                parametro.setAtivo(true);
                parametro.setEnviado(true);

                String identificadorUnico = Unique.geraIdentificadorUnico();

                parametro.setIdentificadorUnico(identificadorUnico);

                parametro.setDataUltimoAcesso(DateTimeFormat.forPattern("dd/mm/yyyy").parseDateTime("01/01/2000"));
                parametro.setUltimaAlteracao(DateTime.now());

                db.parametroInternoDAO().inserir(parametro);

            }

            id.set(parametro.getIdentificadorUnico());

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }


    public void validaUsuario(String idToken, String id) {
        final Crypt crypt = new Crypt();
        try {

            if (baseUrl != null) {

                //baseUrl = "http://192.168.42.113/crc-web/web/app_dev.php/";

                idToken = crypt.bytesToHex(crypt.encrypt(idToken));
                id = crypt.bytesToHex(crypt.encrypt(id));

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .build();

                CircularAPI api = retrofit.create(CircularAPI.class);
                Call<String> call = api.validaUsuario(idToken, id);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        if (response.code() == 200) {
                            usuarioValidado.postValue(true);

                            String id = response.body();
                            try {
                                id = new String(crypt.decrypt(id), "UTF-8");

                                PreferenceUtils.salvarUsuarioLogado(getApplication().getApplicationContext(), id);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {
                            usuarioValidado.postValue(false);
                            Toast.makeText(getApplication().getApplicationContext(), "Erro ao processar a validação do usuário. " +
                                    "Falha ao se comunicar com os servidores do Google", Toast.LENGTH_SHORT).show();
                        }


                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        usuarioValidado.postValue(false);
                        System.out.println("ERR1: " + t.getMessage());
                        Toast.makeText(getApplication().getApplicationContext(), "Erro ao validar usuário.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ERR: " + e.getMessage());
        }

    }

}
