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
import android.widget.Toast;

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
    static ImageButton imageButtonFavoritos;
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

        MenuItem itemFavoritos = menu.findItem(R.id.icon_favoritos);
        MenuItemCompat.getActionView(itemFavoritos).setOnClickListener(listener);

        mListener = listener;

        NOVAS_MENSAGENS = 0;

        imageButtonMsg = MenuItemCompat.getActionView(itemMsg).findViewById(R.id.imageButtonMsg);
        imageButtonMsg.setOnClickListener(mListener);

        imageButtonFavoritos = MenuItemCompat.getActionView(itemFavoritos).findViewById(R.id.imageButtonFavoritos);
        imageButtonFavoritos.setOnClickListener(mListener);

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
            case R.id.imageButtonMsg:
            case R.id.msg:
            case R.id.icon_msg:
                Intent intent = new Intent(activity, MensagensActivity.class);
                activity.startActivity(intent);
                break;
            case R.id.imageButtonFavoritos:
            case R.id.favoritos:
            case R.id.icon_favoritos:
                Intent i = new Intent(activity, MensagensActivity.class);
                activity.startActivity(i);
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
