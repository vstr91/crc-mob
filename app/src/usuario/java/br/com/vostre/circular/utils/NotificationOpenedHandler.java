package br.com.vostre.circular.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;

import br.com.vostre.circular.view.DetalheItinerarioActivity;
import br.com.vostre.circular.view.DetalheParadaActivity;
import br.com.vostre.circular.view.DetalhePontoInteresseActivity;
import br.com.vostre.circular.view.ItinerariosActivity;
import br.com.vostre.circular.view.MapaActivity;
import br.com.vostre.circular.view.MensagensActivity;
import br.com.vostre.circular.view.MenuActivity;
import br.com.vostre.circular.view.ParadasActivity;

public class NotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {

    Context ctx;

    public NotificationOpenedHandler(Context ctx){
        this.ctx = ctx;
    }

    // This fires when a notification is opened by tapping on it.
    @Override
    public void notificationOpened(OSNotificationOpenResult result) {
        OSNotificationAction.ActionType actionType = result.action.type;
        JSONObject data = result.notification.payload.additionalData;
        String tela;
        Intent intent = null;

//        Log.i("OSNotificationPayload", "result.notification.payload.toJSONObject().toString(): "
//                + result.notification.payload.toJSONObject().toString());

        if (data != null) {
            tela = data.optString("tela", null);

            if (tela != null){
                Log.i("OneSignalExample", "customkey set with value: " + tela);

                switch(tela){
                    case "itinerario":
                        intent = new Intent(ctx, ItinerariosActivity.class);
                        break;
                    case "parada":
                        intent = new Intent(ctx, ParadasActivity.class);
                        break;
                    case "mapa":
                        intent = new Intent(ctx, MapaActivity.class);
                        break;
                    case "mensagem":
                        intent = new Intent(ctx, MensagensActivity.class);
                        break;
                    case "detalhe_itinerario":

                        String itinerario = data.optString("itinerario", null);

                        if(itinerario != null){
                            intent = new Intent(ctx, DetalheItinerarioActivity.class);
                            intent.putExtra("itinerario", itinerario);
                        } else{
                            intent = new Intent(ctx, MenuActivity.class);
                        }

                        break;
                    case "detalhe_parada":

                        String parada = data.optString("parada", null);

                        if(parada != null){
                            intent = new Intent(ctx, DetalheParadaActivity.class);
                            intent.putExtra("parada", parada);
                        } else{
                            intent = new Intent(ctx, MenuActivity.class);
                        }

                        break;
                    case "detalhe_poi":

                        String poi = data.optString("poi", null);

                        if(poi != null){
                            intent = new Intent(ctx, DetalhePontoInteresseActivity.class);
                            intent.putExtra("poi", poi);
                        } else{
                            intent = new Intent(ctx, MenuActivity.class);
                        }

                        break;
                    default:
                        intent = new Intent(ctx, MenuActivity.class);
                        break;
                }

            } else{
                intent = new Intent(ctx, MenuActivity.class);
            }

        } else{
            intent = new Intent(ctx, MenuActivity.class);
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);

        if (actionType == OSNotificationAction.ActionType.ActionTaken)
            Log.i("OneSignalExample", "Button pressed with id: " + result.action.actionID);

        // The following can be used to open an Activity of your choice.
        // Replace - getApplicationContext() - with any Android Context.
        // Intent intent = new Intent(getApplicationContext(), YourActivity.class);
        // intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
        // startActivity(intent);

        // Add the following to your AndroidManifest.xml to prevent the launching of your main Activity
        //   if you are calling startActivity above.
     /*
        <application ...>
          <meta-data android:name="com.onesignal.NotificationOpened.DEFAULT" android:value="DISABLE" />
        </application>
     */
    }
}