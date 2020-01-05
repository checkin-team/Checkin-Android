package com.checkin.app.checkin

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.checkin.app.checkin.menu.fragments.menuCartModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class CheckinApplication : Application() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
        startKoin {
            androidContext(this@CheckinApplication)
            androidLogger()
            modules(menuCartModule)
        }
    }
}