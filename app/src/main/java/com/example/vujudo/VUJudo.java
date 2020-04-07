package com.example.vujudo;

import android.app.Application;
import io.realm.Realm;

public class VUJudo extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize Realm
        Realm.init(this);

        // No set configuration. By default RealmConfiguration.Builder().build() is used
    }
}
