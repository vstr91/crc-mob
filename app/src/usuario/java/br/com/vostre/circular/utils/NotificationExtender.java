package br.com.vostre.circular.utils;

import android.accounts.Account;
import android.content.ContentResolver;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationReceivedResult;

import org.json.JSONException;
import org.json.JSONObject;

public class NotificationExtender extends NotificationExtenderService {
    @Override
    protected boolean onNotificationProcessing(OSNotificationReceivedResult receivedResult) {
        // Read properties from result.

        if(receivedResult != null && receivedResult.payload != null && receivedResult.payload.additionalData != null){

            JSONObject obj = receivedResult.payload.additionalData;

            try {

                if(obj.getString("atualizar").equals("1")){
                    Log.i("PUSH_ATT", "Atualizar Dados via Push");

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
                    ContentResolver.requestSync(new Account(Constants.ACCOUNT, Constants.ACCOUNT_TYPE), Constants.AUTHORITY, settingsBundle);

                }



                if(obj.optString("mostrar").equals("1")){
                    Log.d("NOT_PUSH", "Mostrar!");
                    return false;
                } else{
                    Log.d("NOT_PUSH", "Não mostrar!");
                    return true;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Return true to stop the notification from displaying.
        return true;
    }
}
