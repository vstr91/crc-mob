package br.com.vostre.circular;

import android.app.Application;

import com.cloudinary.android.MediaManager;

import net.danlew.android.joda.JodaTimeAndroid;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        JodaTimeAndroid.init(this);
        MediaManager.init(this);
    }
}
