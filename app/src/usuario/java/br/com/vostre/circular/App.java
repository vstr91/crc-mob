package br.com.vostre.circular;

import android.app.Application;

import androidx.multidex.BuildConfig;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;
import androidx.appcompat.app.AppCompatDelegate;

import com.cloudinary.android.MediaManager;
import com.onesignal.OneSignal;

import net.danlew.android.joda.JodaTimeAndroid;

import org.osmdroid.config.Configuration;

public class App extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        JodaTimeAndroid.init(this);
        MultiDex.install(this);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        MediaManager.init(this);

        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);

        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .setNotificationReceivedHandler(new br.com.vostre.circular.utils.NotificationReceivedHandler())
                .setNotificationOpenedHandler(new br.com.vostre.circular.utils.NotificationOpenedHandler(this))
                .init();

        OneSignal.sendTag("tipo", "usuario");


    }
}
