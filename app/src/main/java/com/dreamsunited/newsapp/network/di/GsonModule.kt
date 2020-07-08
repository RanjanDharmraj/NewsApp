package com.dreamsunited.newsapp.network.di

import com.dreamsunited.newsapp.di.scope.AppScope
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides

@Module
object GsonModule {
    @JvmStatic
    @Provides
    @AppScope
    fun provideGson(): Gson {
        return GsonBuilder().serializeNulls().create()
    }

}