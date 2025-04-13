package com.drbrosdev.extractor

import android.app.Application
import android.content.Context
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.memory.MemoryCache
import coil3.util.DebugLogger
import com.drbrosdev.extractor.framework.koin.allKoinModules
import com.drbrosdev.extractor.framework.logger.DatabaseEventLogTree
import com.drbrosdev.extractor.framework.requireDebug
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin
import timber.log.Timber

class ExtractorApplication : Application(), SingletonImageLoader.Factory {

    val databaseLogger by lazy {
        DatabaseEventLogTree()
    }

    override fun onCreate() {
        super.onCreate()
        // start koin for DI
        startKoin {
            requireDebug {
                androidLogger()
            }
            androidContext(this@ExtractorApplication)
            workManagerFactory()
            modules(allKoinModules)
        }
        // Plant database backed logger tree
        requireDebug(
            fallback = { Timber.plant(databaseLogger) }
        ) {
            Timber.plant(
                Timber.DebugTree(),
                databaseLogger
            )
        }
    }

    // configure Coil image loader
    override fun newImageLoader(context: Context): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(context, 0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizePercent(0.02)
                    .build()
            }
            .logger(DebugLogger())
            .build()
    }
}