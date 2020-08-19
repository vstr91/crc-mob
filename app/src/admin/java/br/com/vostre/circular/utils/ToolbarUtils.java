package br.com.vostre.circular.utils;

import android.accounts.Account;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import androidx.core.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.com.vostre.circular.BuildConfig;
import br.com.vostre.circular.R;
import br.com.vostre.circular.model.EntidadeBase;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.view.MensagensActivity;

/**
 * Created by Almir on 16/12/2015.
 */
public class ToolbarUtils {

    static TextView textViewBadgeMsg;
    static ImageButton imageButtonMsg;
    static ImageButton imageButtonExportar;
    static ImageButton imageButtonSync;
//    static ImageButton imageButtonProblemas;
    //    static ImageButton imageButtonImportar;
    static View.OnClickListener mListener;
    public static int NOVAS_MENSAGENS = 0;

    public static final Integer PICK_FILE = 310;

    // The authority for the sync adapter's content provider
    public static final String AUTHORITY = "br.com.vostre.circular.admin.datasync.provider";
    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "br.com.vostre.circular.admin";
    // The account name
    public static final String ACCOUNT = "dummyaccount";

    public static void preparaMenu(Menu menu, Activity activity, View.OnClickListener listener) {

        activity.getMenuInflater().inflate(R.menu.main, menu);

        MenuItem itemMsg = menu.findItem(R.id.icon_msg);
        MenuItemCompat.getActionView(itemMsg).setOnClickListener(listener);

        MenuItem itemHorarios = menu.findItem(R.id.icon_exportar);
        MenuItemCompat.getActionView(itemHorarios).setOnClickListener(listener);

        MenuItem itemSync = menu.findItem(R.id.icon_sync);
        MenuItemCompat.getActionView(itemSync).setOnClickListener(listener);

//        MenuItem itemProblemas = menu.findItem(R.id.icon_problemas);
//        MenuItemCompat.getActionView(itemProblemas).setOnClickListener(listener);

//        MenuItem itemImport = menu.findItem(R.id.icon_import);
//        MenuItemCompat.getActionView(itemImport).setOnClickListener(listener);

        mListener = listener;

        NOVAS_MENSAGENS = 0;

        imageButtonMsg = MenuItemCompat.getActionView(itemMsg).findViewById(R.id.imageButtonMsg);
        imageButtonMsg.setOnClickListener(mListener);

        imageButtonExportar = MenuItemCompat.getActionView(itemHorarios).findViewById(R.id.imageButtonExportar);
        imageButtonExportar.setOnClickListener(mListener);

        imageButtonSync = MenuItemCompat.getActionView(itemSync).findViewById(R.id.imageButtonSync);
        imageButtonSync.setOnClickListener(mListener);

//        imageButtonProblemas = MenuItemCompat.getActionView(itemProblemas).findViewById(R.id.imageButtonProblemas);
//        imageButtonProblemas.setOnClickListener(mListener);

//        imageButtonImportar = MenuItemCompat.getActionView(itemImport).findViewById(R.id.imageButtonImport);
//        imageButtonImportar.setOnClickListener(mListener);

        if (NOVAS_MENSAGENS < 1) {
            textViewBadgeMsg = MenuItemCompat.getActionView(itemMsg).findViewById(R.id.textViewBadgeMsg);
            textViewBadgeMsg.setVisibility(View.INVISIBLE);
        }
//
//        int qtdMensagensNaoLidas = MessageUtils.getQuantidadeMensagensNaoLidas(activity);
//

//
//        atualizaBadge(qtdMensagensNaoLidas);

        if(BuildConfig.DEBUG_APP == 1){
            activity.findViewById(R.id.toolbar).setBackgroundColor(Color.RED);
        } else{
            activity.findViewById(R.id.toolbar).setBackgroundColor(Color.TRANSPARENT);
        }

    }

    public static void onMenuItemClick(View v, final Activity activity) {
        switch (v.getId()) {
            case android.R.id.home:
                activity.onBackPressed();
                break;
//            case R.id.imageButtonImport:
//            case R.id.icon_import:
//            case R.id.importar:
//
//                Intent intentFile = new Intent();
//                intentFile.setType("text/*");
//                intentFile.setAction(Intent.ACTION_GET_CONTENT);
//                activity.startActivityForResult(Intent.createChooser(intentFile, "Escolha o arquivo de dados"), PICK_FILE);
//
//                break;
            case R.id.imageButtonSync:
            case R.id.icon_sync:
            case R.id.sync:

                // Pass the settings flags by inserting them in a bundle
                Bundle settingsBundle = new Bundle();
                settingsBundle.putBoolean(
                        ContentResolver.SYNC_EXTRAS_MANUAL, true);
                settingsBundle.putBoolean(
                        ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
                /*
                 * Request the sync for the default account, authority, and
                 * manual sync settings
                 */
                ContentResolver.requestSync(new Account(ACCOUNT, ACCOUNT_TYPE), AUTHORITY, settingsBundle);
                Toast.makeText(activity.getApplicationContext(), "Iniciando sincronização", Toast.LENGTH_SHORT).show();

                break;
            case R.id.imageButtonExportar:
            case R.id.icon_exportar:
            case R.id.exportar:

                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        AppDatabase appDatabase = AppDatabase.getAppDatabase(activity.getApplicationContext());

                        List<? extends EntidadeBase> paises = appDatabase.paisDAO().listarTodosSync();
                        List<? extends EntidadeBase> empresas = appDatabase.empresaDAO().listarTodosSync();
                        List<? extends EntidadeBase> onibus = new ArrayList<>();
                        List<? extends EntidadeBase> estados = appDatabase.estadoDAO().listarTodosSync();
                        List<? extends EntidadeBase> cidades = appDatabase.cidadeDAO().listarTodosSync();
                        List<? extends EntidadeBase> bairros = appDatabase.bairroDAO().listarTodosSync();
                        List<? extends EntidadeBase> paradas = appDatabase.paradaDAO().listarTodosSync();
                        List<? extends EntidadeBase> itinerarios = appDatabase.itinerarioDAO().listarTodosSync();
                        List<? extends EntidadeBase> horarios = appDatabase.horarioDAO().listarTodosSync();
                        List<? extends EntidadeBase> paradasItinerario = appDatabase.paradaItinerarioDAO().listarTodosSync();
                        List<? extends EntidadeBase> secoesItinerarios = appDatabase.secaoItinerarioDAO().listarTodosSync();
                        List<? extends EntidadeBase> horariosItinerarios = appDatabase.horarioItinerarioDAO().listarTodosSync();
                        List<? extends EntidadeBase> mensagens = new ArrayList<>();
                        List<? extends EntidadeBase> parametros = appDatabase.parametroDAO().listarTodosSync();
                        List<? extends EntidadeBase> pontosInteresse = appDatabase.pontoInteresseDAO().listarTodosSync();
                        List<? extends EntidadeBase> usuarios = new ArrayList<>();

                        List<? extends EntidadeBase> paradaSugestoes = new ArrayList<>();

                        // v2.3.x
                        List<? extends EntidadeBase> historicosParadas = appDatabase.historicoParadaDAO().listarTodosSync();
                        List<? extends EntidadeBase> historicosItinerarios = appDatabase.historicoItinerarioDAO().listarTodosSync();
                        List<? extends EntidadeBase> pontoInteresseSugestoes = new ArrayList<>();
                        List<? extends EntidadeBase> tiposProblemas = appDatabase.tipoProblemaDAO().listarTodosSync();
                        List<? extends EntidadeBase> problemas = appDatabase.problemaDAO().listarTodosSync();
                        List<? extends EntidadeBase> servicos = appDatabase.servicoDAO().listarTodosSync();
                        List<? extends EntidadeBase> feriados = appDatabase.feriadoDAO().listarTodosSync();
                        List<? extends EntidadeBase> historicosSecoes = appDatabase.historicoSecaoDAO().listarTodosSync();

                        String strPaises = "\"paises\": " + JsonUtils.toJson((List<EntidadeBase>) paises);
                        String strEmpresas = "\"empresas\": " + JsonUtils.toJson((List<EntidadeBase>) empresas);
                        String strOnibus = "\"onibus\": " + JsonUtils.toJson((List<EntidadeBase>) onibus);
                        String strEstados = "\"estados\": " + JsonUtils.toJson((List<EntidadeBase>) estados);
                        String strCidades = "\"cidades\": " + JsonUtils.toJson((List<EntidadeBase>) cidades);
                        String strBairros = "\"bairros\": " + JsonUtils.toJson((List<EntidadeBase>) bairros);
                        String strParadas = "\"paradas\": " + JsonUtils.toJson((List<EntidadeBase>) paradas);
                        String strItinerarios = "\"itinerarios\": " + JsonUtils.toJson((List<EntidadeBase>) itinerarios);
                        String strHorarios = "\"horarios\": " + JsonUtils.toJson((List<EntidadeBase>) horarios);
                        String strParadasItinerarios = "\"paradas_itinerarios\": " + JsonUtils.toJson((List<EntidadeBase>) paradasItinerario);
                        String strSecoesItinerarios = "\"secoes_itinerarios\": " + JsonUtils.toJson((List<EntidadeBase>) secoesItinerarios);
                        String strHorariosItinerarios = "\"horarios_itinerarios\": " + JsonUtils.toJson((List<EntidadeBase>) horariosItinerarios);
                        String strMensagens = "\"mensagens\": " + JsonUtils.toJson((List<EntidadeBase>) mensagens);
                        String strParametros = "\"parametros\": " + JsonUtils.toJson((List<EntidadeBase>) parametros);
                        String strPontosInteresse = "\"pontos_interesse\": " + JsonUtils.toJson((List<EntidadeBase>) pontosInteresse);
                        String strUsuarios = "\"usuarios\": " + JsonUtils.toJson((List<EntidadeBase>) usuarios);

                        String strParadasSugestoes = "\"paradas_sugestoes\": " + JsonUtils.toJson((List<EntidadeBase>) paradaSugestoes);

                        //v2.3.x
                        String strHistoricosParadas = "\"historicos_paradas\": " + JsonUtils.toJson((List<EntidadeBase>) historicosParadas);
                        String strHistoricosItinerarios = "\"historicos_itinerarios\": " + JsonUtils.toJson((List<EntidadeBase>) historicosItinerarios);
                        String strPontoInteresseSugestoes = "\"pontos_interesse_sugestoes\": " + JsonUtils.toJson((List<EntidadeBase>) pontoInteresseSugestoes);
                        String strTiposProblemas = "\"tipos_problemas\": " + JsonUtils.toJson((List<EntidadeBase>) tiposProblemas);
                        String strProblemas = "\"problemas\": " + JsonUtils.toJson((List<EntidadeBase>) problemas);
                        String strServicos = "\"servicos\": " + JsonUtils.toJson((List<EntidadeBase>) servicos);
                        String strFeriados = "\"feriados\": " + JsonUtils.toJson((List<EntidadeBase>) feriados);
                        String strHistoricosSecoes = "\"historicos_secoes\": " + JsonUtils.toJson((List<EntidadeBase>) historicosSecoes);

                        String json = "{" + strPaises + "," + strEmpresas + "," + strOnibus + "," + strEstados + "," + strCidades + ","
                                + strBairros + "," + strParadas + "," + strItinerarios + "," + strHorarios + "," + strParadasItinerarios + ","
                                + strSecoesItinerarios + "," + strHorariosItinerarios + "," + strMensagens + "," + strParametros + ","
                                + strPontosInteresse + "," + strUsuarios + "," + strParadasSugestoes + ","
                                + strHistoricosParadas + "," + strHistoricosItinerarios + "," + strPontoInteresseSugestoes
                                + "," + strTiposProblemas + "," + strProblemas + "," + strServicos + "," + strFeriados + "," + strHistoricosSecoes
                                + "}";

                        // EXPORTA ARQUIVO DE DADOS

                        File caminho = Environment.getExternalStorageDirectory();

                        File arquivo = new File(caminho, "dados_circular_"+DateTimeFormat.forPattern("dd-MM-yyyy-HH-mm-ss").print(DateTime.now())+".txt");

                        FileOutputStream stream = null;

                        try {
                            stream = new FileOutputStream(arquivo);
                            stream.write(json.getBytes());
                            stream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }
                });

                Toast.makeText(activity, "Dados exportados!", Toast.LENGTH_SHORT).show();

                break;
            case R.id.imageButtonMsg:
            case R.id.msg:
            case R.id.icon_msg:
                Intent intent = new Intent(activity, MensagensActivity.class);
                activity.startActivity(intent);
                break;
//            case R.id.imageButtonProblemas:
//            case R.id.icon_problemas:
//            case R.id.problemas:
//                Intent i = new Intent(activity, ProblemasActivity.class);
//                activity.startActivity(i);
//                break;
        }
    }

    public static void atualizaBadge(int qtdMensagensNaoLidas) {

        if (textViewBadgeMsg != null) {

            if (qtdMensagensNaoLidas > 0) {
                textViewBadgeMsg.setText(String.valueOf(qtdMensagensNaoLidas));

                textViewBadgeMsg.setOnClickListener(mListener);
                textViewBadgeMsg.invalidate();
            } else {
                textViewBadgeMsg.setVisibility(View.GONE);
            }


        }

    }

}
