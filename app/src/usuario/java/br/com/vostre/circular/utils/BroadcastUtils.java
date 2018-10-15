package br.com.vostre.circular.utils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;

public class BroadcastUtils {

    public static void registraReceiver(Activity activity, BroadcastReceiver receiver){
        activity.getApplicationContext().registerReceiver(receiver, new IntentFilter("MensagensService"));
    }

    public static void removeRegistroReceiver(Activity activity, BroadcastReceiver receiver){
        activity.getApplicationContext().unregisterReceiver(receiver);
    }

}
