package com.drbrosdev.extractor

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.util.DebugLogger
import com.drbrosdev.extractor.framework.koin.allKoinModules
import com.drbrosdev.extractor.framework.logger.DatabaseEventLogTree
import com.drbrosdev.extractor.framework.requireDebug
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin
import timber.log.Timber

class ExtractorApplication : Application(), ImageLoaderFactory {

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
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.20)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizeBytes(5 * 1024 * 1024)
                    .build()
            }
            .logger(DebugLogger())
            .respectCacheHeaders(false)
            .build()
    }
}