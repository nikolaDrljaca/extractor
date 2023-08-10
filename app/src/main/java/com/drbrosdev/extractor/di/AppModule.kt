package com.drbrosdev.extractor.di

import com.drbrosdev.extractor.data.ExtractorDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module


val dataModule = module {
    single { ExtractorDatabase.createExtractorDatabase(androidContext()) }
}