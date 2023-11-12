package com.drbrosdev.extractor

import android.app.Application
import com.drbrosdev.extractor.framework.koin.allKoinModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin

class ExtractorApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@ExtractorApplication)
            workManagerFactory()
            modules(allKoinModules)
        }
    }
}