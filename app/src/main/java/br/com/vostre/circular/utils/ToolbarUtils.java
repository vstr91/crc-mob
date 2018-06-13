package br.com.vostre.circular.utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.util.UUID;

import br.com.vostre.circular.R;
import br.com.vostre.circular.model.Pais;
import br.com.vostre.circular.view.HorariosActivity;
import br.com.vostre.circular.view.MensagensActivity;
import br.com.vostre.circular.view.MenuActivity;

import static android.content.Context.ACCOUNT_SERVICE;

/**
 * Created by Almir on 16/12/2015.
 */
public class ToolbarUtils {

    static TextView textViewBadgeMsg;
    static ImageButton imageButtonMsg;
    static ImageButton imageButtonHorarios;
    static ImageButton imageButtonSync;
    static ImageButton imageButtonImportar;
    static View.OnClickListener mListener;
    public static int NOVAS_MENSAGENS = 0;

    public static final Integer PICK_FILE = 310;

    // The authority for the sync adapter's content provider
    public static final String AUTHORITY = "br.com.vostre.circular.datasync.provider";
    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "br.com.vostre.circular";
    // The account name
    public static final String ACCOUNT = "dummyaccount";

    public static void preparaMenu(Menu menu, Activity activity, View.OnClickListener listener){

        activity.getMenuInflater().inflate(R.menu.main, menu);

        MenuItem itemMsg = menu.findItem(R.id.icon_msg);
        MenuItemCompat.getActionView(itemMsg).setOnClickListener(listener);

        MenuItem itemHorarios = menu.findItem(R.id.icon_horarios);
        MenuItemCompat.getActionView(itemHorarios).setOnClickListener(listener);

        MenuItem itemSync = menu.findItem(R.id.icon_sync);
        MenuItemCompat.getActionView(itemSync).setOnClickListener(listener);

        MenuItem itemImport = menu.findItem(R.id.icon_import);
        MenuItemCompat.getActionView(itemImport).setOnClickListener(listener);

        mListener = listener;

        NOVAS_MENSAGENS = 0;

        imageButtonMsg = MenuItemCompat.getActionView(itemMsg).findViewById(R.id.imageButtonMsg);
        imageButtonMsg.setOnClickListener(mListener);

        imageButtonHorarios = MenuItemCompat.getActionView(itemHorarios).findViewById(R.id.imageButtonHorarios);
        imageButtonHorarios.setOnClickListener(mListener);

        imageButtonSync = MenuItemCompat.getActionView(itemSync).findViewById(R.id.imageButtonSync);
        imageButtonSync.setOnClickListener(mListener);

        imageButtonImportar = MenuItemCompat.getActionView(itemImport).findViewById(R.id.imageButtonImport);
        imageButtonImportar.setOnClickListener(mListener);

        if(NOVAS_MENSAGENS < 1){
            textViewBadgeMsg = MenuItemCompat.getActionView(itemMsg).findViewById(R.id.textViewBadgeMsg);
            textViewBadgeMsg.setVisibility(View.INVISIBLE);
        }
//
//        int qtdMensagensNaoLidas = MessageUtils.getQuantidadeMensagensNaoLidas(activity);
//

//
//        atualizaBadge(qtdMensagensNaoLidas);

    }

    public static void onMenuItemClick(View v, Activity activity){
        switch(v.getId()){
            case android.R.id.home:
                activity.onBackPressed();
                break;
            case R.id.imageButtonImport:
            case R.id.icon_import:
            case R.id.importar:

                Intent intentFile = new Intent();
                intentFile.setType("text/*");
                intentFile.setAction(Intent.ACTION_GET_CONTENT);
                activity.startActivityForResult(Intent.createChooser(intentFile, "Escolha o arquivo de dados"), PICK_FILE);

                break;
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

                Pais pais = new Pais();
                pais.setId(UUID.randomUUID().toString());
                pais.setNome("Brasil");
                pais.setSigla("BRA");
                pais.setAtivo(true);
                pais.setDataCadastro(new DateTime());
                pais.setUltimaAlteracao(new DateTime());
                pais.setEnviado(false);
                pais.setSlug(StringUtils.toSlug(pais.getNome()));

                //System.out.println("PAIS: "+pais.toJson());

                break;
            case R.id.imageButtonHorarios:
            case R.id.icon_horarios:
            case R.id.horarios:
                Intent i = new Intent(activity, HorariosActivity.class);
                activity.startActivity(i);
                break;
            case R.id.imageButtonMsg:
            case R.id.msg:
            case R.id.icon_msg:
                Intent intent = new Intent(activity, MensagensActivity.class);
                activity.startActivity(intent);
                break;
        }
    }

    public static void atualizaBadge(int qtdMensagensNaoLidas){

            if(textViewBadgeMsg != null){

                if(qtdMensagensNaoLidas > 0){
                textViewBadgeMsg.setText(String.valueOf(qtdMensagensNaoLidas));

                textViewBadgeMsg.setOnClickListener(mListener);
                textViewBadgeMsg.invalidate();
            } else{
                textViewBadgeMsg.setVisibility(View.GONE);
            }


        }

    }

}
