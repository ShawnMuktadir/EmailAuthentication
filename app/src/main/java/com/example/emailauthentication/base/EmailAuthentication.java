package com.example.emailauthentication.base;

import android.app.Application;

import com.example.emailauthentication.BuildConfig;

import timber.log.Timber;

public class EmailAuthentication extends Application {

    public static final String TAG = EmailAuthentication.class.getSimpleName();
    private static EmailAuthentication mInstance;

    public static synchronized EmailAuthentication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        initTimber();
    }

    private void initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree() {
                @Override
                protected String createStackElementTag(StackTraceElement element) {
                    return super.createStackElementTag(element) + ": " + element.getLineNumber();
                }
            });
        }
    }
}
