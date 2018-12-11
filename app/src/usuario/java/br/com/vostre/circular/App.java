package br.com.vostre.circular;

import android.app.Application;
import android.support.multidex.MultiDexApplication;

import net.danlew.android.joda.JodaTimeAndroid;

public class App extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        JodaTimeAndroid.init(this);
    }
}
