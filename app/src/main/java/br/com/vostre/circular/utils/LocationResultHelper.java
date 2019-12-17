package br.com.vostre.circular.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.v4.app.TaskStackBuilder;
import android.app.NotificationChannel;


import org.joda.time.DateTime;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.view.MenuActivity;

/**
 * Class to process location results.
 */
public class LocationResultHelper {

    public final static String KEY_LOCATION_UPDATES_RESULT = "location-update-result";

    final private static String PRIMARY_CHANNEL = "default";


    private Context mContext;
    private List<Location> mLocations;
    private NotificationManager mNotificationManager;

    public final static String KEY_LISTA = "location-route";

    public LocationResultHelper(Context context, List<Location> locations) {
        mContext = context;
        mLocations = locations;

//        NotificationChannel channel = new NotificationChannel(PRIMARY_CHANNEL,
//                context.getString("ch_circular_1"), NotificationManager.IMPORTANCE_DEFAULT);
//        channel.setLightColor(Color.GREEN);
//        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
//        getNotificationManager().createNotificationChannel(channel);
    }

    private String getLocationResultText() {
        if (mLocations.isEmpty()) {
            return "0;0;0;0;0";
        }
        StringBuilder sb = new StringBuilder();

        sb.append(mLocations.get(mLocations.size()-1).getLatitude());
        sb.append(";");
        sb.append(mLocations.get(mLocations.size()-1).getLongitude());
        sb.append(";");
        sb.append(mLocations.get(mLocations.size()-1).getAccuracy());
        sb.append(";");
        sb.append(mLocations.get(mLocations.size()-1).getSpeed());
        sb.append(";");
        sb.append(mLocations.get(mLocations.size()-1).getTime());

        return sb.toString();
    }

    private String getLocationResultText(Location location) {
        if (location == null) {
            return "0;0;0;0;0";
        }
        StringBuilder sb = new StringBuilder();

        sb.append(location.getLatitude());
        sb.append(";");
        sb.append(location.getLongitude());
        sb.append(";");
        sb.append(location.getAccuracy());
        sb.append(";");
        sb.append(location.getSpeed());
        sb.append(";");
        sb.append(location.getTime());

        return sb.toString();
    }

    /**
     * Saves location result as a string to {@link android.content.SharedPreferences}.
     */
    public void saveResults() {
        PreferenceManager.getDefaultSharedPreferences(mContext)
                .edit()
                .putString(KEY_LOCATION_UPDATES_RESULT, getLocationResultText())
                .apply();
    }

    /**
     * Fetches location results from {@link android.content.SharedPreferences}.
     */
    public static String getSavedLocationResult(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(KEY_LOCATION_UPDATES_RESULT, "");
    }

    /**
     * Get the notification mNotificationManager.
     * <p>
     * Utility method as this helper works with it a lot.
     *
     * @return The system service NotificationManager
     */
    private NotificationManager getNotificationManager() {
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) mContext.getSystemService(
                    Context.NOTIFICATION_SERVICE);
        }
        return mNotificationManager;
    }

    /**
     * Displays a notification with the location results.
     */
    void showNotification() {
        Intent notificationIntent = new Intent(mContext, MenuActivity.class);

        // Construct a task stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(MenuActivity.class);

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

//        Notification.Builder notificationBuilder = new Notification.Builder(mContext,
//                PRIMARY_CHANNEL)
//                .setContentTitle(getLocationResultTitle())
//                .setContentText(getLocationResultText())
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setAutoCancel(true)
//                .setContentIntent(notificationPendingIntent);

//        getNotificationManager().notify(0, notificationBuilder.build());
    }

    public void updateRoute(Context context, Location location) {

        String route = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(KEY_LISTA, "");

        route = route.concat(getLocationResultText(location)+"|");

        PreferenceManager.getDefaultSharedPreferences(mContext)
                .edit()
                .putString(KEY_LISTA, route)
                .apply();
    }

    public static void clearRoute(Context context) {

        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(KEY_LISTA, "")
                .apply();
    }

    public static void marcaGravando(Context context, boolean gravando) {

        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(LocationUpdatesBroadcastReceiver.KEY_GRAVANDO, gravando)
                .apply();
    }

    public static void marcaHoraInicial(Context context, DateTime horaInicial) {

        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putLong(LocationUpdatesBroadcastReceiver.KEY_HORA_INICIAL, horaInicial.getMillis())
                .apply();
    }

    public static Long recuperaHoraInicial(Context context) {

        return PreferenceManager.getDefaultSharedPreferences(context)
                .getLong(LocationUpdatesBroadcastReceiver.KEY_HORA_INICIAL, 0L);
    }

}