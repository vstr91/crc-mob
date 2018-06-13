package br.com.vostre.circular.model.adapter;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
import br.com.vostre.circular.model.PontoInteresse;
import br.com.vostre.circular.model.SecaoItinerario;
import br.com.vostre.circular.model.Usuario;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.utils.JsonUtils;

/**
 * Handle the transfer of data between a server and an
 * app, using the Android sync adapter framework.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

    // Global variables
    // Define a variable to contain a content resolver instance
    ContentResolver mContentResolver;

    /**
     * Set up the sync adapter
     */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();
    }

    /**
     * Set up the sync adapter. This form of the
     * constructor maintains compatibility with Android 3.0
     * and later platform versions
     */
    public SyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();

    }

    /*
     * Specify the code you want to run in the sync adapter. The entire
     * sync adapter runs in a background thread, so you don't have to set
     * up your own background processing.
     */
    @Override
    public void onPerformSync(
            Account account,
            Bundle extras,
            String authority,
            ContentProviderClient provider,
            SyncResult syncResult) {

        AppDatabase appDatabase = AppDatabase.getAppDatabase(this.getContext());

        List<? extends EntidadeBase> paises = appDatabase.paisDAO().listarTodosAEnviar();
        List<? extends EntidadeBase> empresas = appDatabase.empresaDAO().listarTodosAEnviar();
        List<? extends EntidadeBase> onibus = appDatabase.onibusDAO().listarTodosAEnviar();
        List<? extends EntidadeBase> estados = appDatabase.estadoDAO().listarTodosAEnviar();
        List<? extends EntidadeBase> cidades = appDatabase.cidadeDAO().listarTodosAEnviar();
        List<? extends EntidadeBase> bairros = appDatabase.bairroDAO().listarTodosAEnviar();
        List<? extends EntidadeBase> paradas = appDatabase.paradaDAO().listarTodosAEnviar();
        List<? extends EntidadeBase> itinerarios = appDatabase.itinerarioDAO().listarTodosAEnviar();
        List<? extends EntidadeBase> horarios = appDatabase.horarioDAO().listarTodosAEnviar();
        List<? extends EntidadeBase> paradasItinerario = appDatabase.paradaItinerarioDAO().listarTodosAEnviar();
        List<? extends EntidadeBase> secoesItinerarios = appDatabase.secaoItinerarioDAO().listarTodosAEnviar();
        List<? extends EntidadeBase> horariosItinerarios = appDatabase.horarioItinerarioDAO().listarTodosAEnviar();
        List<? extends EntidadeBase> mensagens = appDatabase.mensagemDAO().listarTodosAEnviar();
        List<? extends EntidadeBase> parametros = appDatabase.parametroDAO().listarTodosAEnviar();
        List<? extends EntidadeBase> pontosInteresse = appDatabase.pontoInteresseDAO().listarTodosAEnviar();
        List<? extends EntidadeBase> usuarios = appDatabase.usuarioDAO().listarTodosAEnviar();

        String strPaises = "\"paises\": "+JsonUtils.toJson((List<EntidadeBase>) paises);
        String strEmpresas = "\"empresas\": "+JsonUtils.toJson((List<EntidadeBase>) empresas);
        String strOnibus = "\"onibus\": "+JsonUtils.toJson((List<EntidadeBase>) onibus);
        String strEstados = "\"estados\": "+JsonUtils.toJson((List<EntidadeBase>) estados);
        String strCidades = "\"cidades\": "+JsonUtils.toJson((List<EntidadeBase>) cidades);
        String strBairros = "\"bairros\": "+JsonUtils.toJson((List<EntidadeBase>) bairros);
        String strParadas = "\"paradas\": "+JsonUtils.toJson((List<EntidadeBase>) paradas);
        String strItinerarios = "\"itinerarios\": "+JsonUtils.toJson((List<EntidadeBase>) itinerarios);
        String strHorarios = "\"horarios\": "+JsonUtils.toJson((List<EntidadeBase>) horarios);
        String strParadasItinerarios = "\"paradas_itinerarios\": "+JsonUtils.toJson((List<EntidadeBase>) paradasItinerario);
        String strSecoesItinerarios = "\"secoes_itinerarios\": "+JsonUtils.toJson((List<EntidadeBase>) secoesItinerarios);
        String strHorariosItinerarios = "\"horarios_itinerarios\": "+JsonUtils.toJson((List<EntidadeBase>) horariosItinerarios);
        String strMensagens = "\"mensagens\": "+JsonUtils.toJson((List<EntidadeBase>) mensagens);
        String strParametros = "\"parametros\": "+JsonUtils.toJson((List<EntidadeBase>) parametros);
        String strPontosInteresse = "\"pontos_interesse\": "+JsonUtils.toJson((List<EntidadeBase>) pontosInteresse);
        String strUsuarios = "\"usuarios\": "+JsonUtils.toJson((List<EntidadeBase>) usuarios);

        String json = "{"+strPaises+","+strEmpresas+","+strOnibus+","+strEstados+","+strCidades+","
                +strBairros+","+strParadas+","+strItinerarios+","+strHorarios+","+strParadasItinerarios+","
                +strSecoesItinerarios+","+strHorariosItinerarios+","+strMensagens+","+strParametros+","
                +strPontosInteresse+","+strUsuarios+"}";

//        String json = "{"+strHorarios+"}";

        System.out.println(json);

        File caminho = Environment.getExternalStorageDirectory();

        System.out.println("caminho: "+caminho);

        File arquivo = new File(caminho, "data.txt");

        FileOutputStream stream = null;

        try {
            stream = new FileOutputStream(arquivo);
            stream.write(json.getBytes());
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}