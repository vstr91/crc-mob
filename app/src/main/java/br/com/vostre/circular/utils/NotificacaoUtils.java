package br.com.vostre.circular.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import br.com.vostre.circular.R;

/**
 * Created by Almir on 24/03/2016.
 */
public class NotificacaoUtils {

    public static void criaNotificacao(Class anterior, Class destino, Context context, String titulo, String mensagem, String id){
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
//        builder.setSmallIcon(R.drawable.icon);
//        builder.setContentTitle(titulo);
//        builder.setContentText(mensagem);
//        builder.setAutoCancel(true);
//        //Notification notification = builder.build();
//
//        System.out.println("NOT::: Entrou na notificacao");
//
//        Intent backIntent = new Intent(context, anterior);
//        backIntent.putExtra("flagVerifica", false);
//        Intent intent = new Intent(context, destino);
//
//        android.support.v4.app.TaskStackBuilder stackBuilder = android.support.v4.app.TaskStackBuilder.create(context);
//        stackBuilder.addParentStack(anterior);
//        stackBuilder.addNextIntent(intent);
//
//        //PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//        PendingIntent pendingIntent =
//                PendingIntent.getActivities(context, 200, new Intent[]{backIntent, intent}, PendingIntent.FLAG_ONE_SHOT);
//
//        builder.setContentIntent(pendingIntent);
//
//        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//
//        manager.notify(id, builder.build());

        Intent backIntent = new Intent(context, anterior);

        Intent intent = new Intent(context, destino);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        androidx.core.app.TaskStackBuilder stackBuilder = androidx.core.app.TaskStackBuilder.create(context);
        stackBuilder.addParentStack(anterior);
        stackBuilder.addNextIntent(intent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//                PendingIntent.getActivities(context, 200, new Intent[]{backIntent, intent}, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, id)
                .setSmallIcon(R.drawable.ic_notificacao)
                .setContentTitle(titulo)
                .setContentText(mensagem)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(Constants.ID_NOTIFICACAO_MSG, mBuilder.build());

    }

    public static void removeNotificacao(Context ctx, int id) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancel(id);
    }

    public static void createNotificationChannel(Context context, String id) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "circular";
            String description = "notificacao_circular";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(id, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}