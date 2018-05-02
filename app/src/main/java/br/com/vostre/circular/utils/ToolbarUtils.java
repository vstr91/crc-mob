package br.com.vostre.circular.utils;

import android.app.Activity;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import br.com.vostre.circular.R;

/**
 * Created by Almir on 16/12/2015.
 */
public class ToolbarUtils {

    static TextView textViewBadgeMsg;
    static ImageButton imageButtonMsg;
    static View.OnClickListener mListener;
    public static int NOVAS_MENSAGENS = 0;

    public static void preparaMenu(Menu menu, Activity activity, View.OnClickListener listener){

        activity.getMenuInflater().inflate(R.menu.main, menu);

        MenuItem itemMsg = menu.findItem(R.id.icon_msg);
        MenuItemCompat.getActionView(itemMsg).setOnClickListener(listener);

        mListener = listener;

        NOVAS_MENSAGENS = 0;

        imageButtonMsg = MenuItemCompat.getActionView(itemMsg).findViewById(R.id.imageButtonMsg);
        imageButtonMsg.setOnClickListener(mListener);

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
            case R.id.imageButtonMsg:
            case R.id.msg:
            case R.id.icon_msg:
//                Intent intent = new Intent(activity, Mensagens.class);
//                activity.startActivity(intent);
                System.out.println("CLICOU MENSAGEM");
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
