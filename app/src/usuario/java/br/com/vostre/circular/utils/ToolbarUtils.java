package br.com.vostre.circular.utils;

import android.accounts.Account;
import android.app.Activity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import br.com.vostre.circular.BuildConfig;
import br.com.vostre.circular.R;
import br.com.vostre.circular.model.Mensagem;
import br.com.vostre.circular.view.BaseActivity;
import br.com.vostre.circular.view.FavoritosActivity;
import br.com.vostre.circular.view.MensagensActivity;
import br.com.vostre.circular.viewModel.BaseViewModel;
import br.com.vostre.circular.viewModel.ToolbarViewModel;

/**
 * Created by Almir on 16/12/2015.
 */
public class ToolbarUtils {

    static TextView textViewBadgeMsg;
//    static ImageButton imageButtonMsg;
    static ImageButton imageButtonAjuda;
//    static ImageButton imageButtonIncidente;
    static ImageButton imageButtonFavoritos;
//    static ImageButton imageButtonSync;
    static View.OnClickListener mListener;

    public static final Integer PICK_FILE = 310;

    static MenuItem itemMsg;

    public static void preparaMenu(Menu menu, Activity activity, View.OnClickListener listener){

        activity.getMenuInflater().inflate(R.menu.main, menu);

//        itemMsg = menu.findItem(R.id.icon_msg);
//        MenuItemCompat.getActionView(itemMsg).setOnClickListener(listener);

        MenuItem itemAjuda = menu.findItem(R.id.icon_ajuda);
        MenuItemCompat.getActionView(itemAjuda).setOnClickListener(listener);

//        MenuItem itemIncidente = menu.findItem(R.id.icon_incidente);
//        MenuItemCompat.getActionView(itemIncidente).setOnClickListener(listener);

        MenuItem itemFavoritos = menu.findItem(R.id.icon_favoritos);
        MenuItemCompat.getActionView(itemFavoritos).setOnClickListener(listener);

//        MenuItem itemSync = menu.findItem(R.id.icon_sync);
//        MenuItemCompat.getActionView(itemSync).setOnClickListener(listener);

        mListener = listener;

//        imageButtonMsg = MenuItemCompat.getActionView(itemMsg).findViewById(R.id.imageButtonMsg);
//        imageButtonMsg.setOnClickListener(mListener);

        imageButtonAjuda = MenuItemCompat.getActionView(itemAjuda).findViewById(R.id.imageButtonAjuda);
        imageButtonAjuda.setOnClickListener(mListener);

//        imageButtonIncidente = MenuItemCompat.getActionView(itemIncidente).findViewById(R.id.imageButtonIncidente);
//        imageButtonIncidente.setOnClickListener(mListener);

        imageButtonFavoritos = MenuItemCompat.getActionView(itemFavoritos).findViewById(R.id.imageButtonFavoritos);
        imageButtonFavoritos.setOnClickListener(mListener);

//        imageButtonSync = MenuItemCompat.getActionView(itemSync).findViewById(R.id.imageButtonSync);
//        imageButtonSync.setOnClickListener(mListener);

//        if(mensagensNaoLidas < 1){
//            textViewBadgeMsg = MenuItemCompat.getActionView(itemMsg).findViewById(R.id.textViewBadgeMsg);
//            textViewBadgeMsg.setVisibility(View.INVISIBLE);
//        }

        if(BuildConfig.DEBUG_APP == 1){
            activity.findViewById(R.id.toolbar).setBackgroundColor(Color.RED);
        } else{
            activity.findViewById(R.id.toolbar).setBackgroundColor(Color.TRANSPARENT);
        }

    }

    public static void onMenuItemClick(View v, BaseActivity activity){
        switch(v.getId()){
            case android.R.id.home:
                activity.onBackPressed();
                break;
//            case R.id.imageButtonMsg:
//            case R.id.msg:
//            case R.id.icon_msg:
//                Intent intent = new Intent(activity, MensagensActivity.class);
//                activity.startActivity(intent);
//                break;
            case R.id.imageButtonAjuda:
            case R.id.ajuda:
            case R.id.icon_ajuda:
                activity.onToolbarItemSelected(v);
                break;
//            case R.id.imageButtonIncidente:
//            case R.id.incidente:
//            case R.id.icon_incidente:
//                Toast.makeText(activity, "Incidente", Toast.LENGTH_SHORT).show();
//                break;
            case R.id.imageButtonFavoritos:
            case R.id.favoritos:
            case R.id.icon_favoritos:
                Intent i = new Intent(activity, FavoritosActivity.class);
                activity.startActivity(i);
                break;
//            case R.id.imageButtonSync:
//            case R.id.icon_sync:
//            case R.id.sync:
//
//                // Pass the settings flags by inserting them in a bundle
//                Bundle settingsBundle = new Bundle();
//                settingsBundle.putBoolean(
//                        ContentResolver.SYNC_EXTRAS_MANUAL, true);
//                settingsBundle.putBoolean(
//                        ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
//                /*
//                 * Request the sync for the default account, authority, and
//                 * manual sync settings
//                 */
//                ContentResolver.requestSync(new Account(ACCOUNT, ACCOUNT_TYPE), AUTHORITY, settingsBundle);
//
//                PreferenceUtils.gravaMostraToast(activity.getApplicationContext(),true);
//
//                Toast.makeText(activity.getApplicationContext(), "Iniciando sincronização", Toast.LENGTH_SHORT).show();
//
//                break;
        }
    }

}
