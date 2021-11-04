package com.example.wordstest

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

class CustomApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        //初期化
        Realm.init(this)
        //設定
        val config = RealmConfiguration.Builder()
            .deleteRealmIfMigrationNeeded()
            .allowWritesOnUiThread(true)
            // .readOnly()
            .build()
        Realm.setDefaultConfiguration(config)
    }
}