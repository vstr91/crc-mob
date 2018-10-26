package br.com.vostre.circular.utils;

import android.accounts.Account;
import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.model.Mensagem;
import br.com.vostre.circular.view.FavoritosActivity;
import br.com.vostre.circular.view.MensagensActivity;
import br.com.vostre.circular.viewModel.BaseViewModel;
import br.com.vostre.circular.viewModel.ToolbarViewModel;

/**
 * Created by Almir on 16/12/2015.
 */
public class ToolbarUtils {

    static TextView textViewBadgeMsg;
    static ImageButton imageButtonMsg;
    static ImageButton imageButtonAlerta;
    static ImageButton imageButtonFavoritos;
    static ImageButton imageButtonSync;
    static View.OnClickListener mListener;

    public static final Integer PICK_FILE = 310;

    static MenuItem itemMsg;

    // The authority for the sync adapter's content provider
    public static final String AUTHORITY = "br.com.vostre.circular.datasync.provider";
    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "br.com.vostre.circular.usuario";
    // The account name
    public static final String ACCOUNT = "dummyaccount";

    public static void preparaMenu(Menu menu, Activity activity, View.OnClickListener listener){

        activity.getMenuInflater().inflate(R.menu.main, menu);

        itemMsg = menu.findItem(R.id.icon_msg);
        MenuItemCompat.getActionView(itemMsg).setOnClickListener(listener);

        MenuItem itemAlerta = menu.findItem(R.id.icon_alerta);
        MenuItemCompat.getActionView(itemAlerta).setOnClickListener(listener);

        MenuItem itemFavoritos = menu.findItem(R.id.icon_favoritos);
        MenuItemCompat.getActionView(itemFavoritos).setOnClickListener(listener);

        MenuItem itemSync = menu.findItem(R.id.icon_sync);
        MenuItemCompat.getActionView(itemSync).setOnClickListener(listener);

        mListener = listener;

        imageButtonMsg = MenuItemCompat.getActionView(itemMsg).findViewById(R.id.imageButtonMsg);
        imageButtonMsg.setOnClickListener(mListener);

        imageButtonAlerta = MenuItemCompat.getActionView(itemAlerta).findViewById(R.id.imageButtonAlerta);
        imageButtonAlerta.setOnClickListener(mListener);

        imageButtonFavoritos = MenuItemCompat.getActionView(itemFavoritos).findViewById(R.id.imageButtonFavoritos);
        imageButtonFavoritos.setOnClickListener(mListener);

        imageButtonSync = MenuItemCompat.getActionView(itemSync).findViewById(R.id.imageButtonSync);
        imageButtonSync.setOnClickListener(mListener);

//        if(mensagensNaoLidas < 1){
//            textViewBadgeMsg = MenuItemCompat.getActionView(itemMsg).findViewById(R.id.textViewBadgeMsg);
//            textViewBadgeMsg.setVisibility(View.INVISIBLE);
//        }

    }

    public static void onMenuItemClick(View v, Activity activity){
        switch(v.getId()){
            case android.R.id.home:
                activity.onBackPressed();
                break;
            case R.id.imageButtonMsg:
            case R.id.msg:
            case R.id.icon_msg:
                Intent intent = new Intent(activity, MensagensActivity.class);
                activity.startActivity(intent);
                break;
            case R.id.imageButtonAlerta:
            case R.id.alerta:
            case R.id.icon_alerta:
                Toast.makeText(activity, "Alerta!!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.imageButtonFavoritos:
            case R.id.favoritos:
            case R.id.icon_favoritos:
                Intent i = new Intent(activity, FavoritosActivity.class);
                activity.startActivity(i);
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
                Toast.makeText(activity.getApplicationContext(), "Iniciando sincronização", Toast.LENGTH_SHORT).show();

                break;
        }
    }

}
