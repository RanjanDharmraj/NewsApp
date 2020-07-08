package com.dreamsunited.newsapp.network.di

import android.content.Context
import com.dreamsunited.newsapp.di.scope.AppScope
import dagger.Module
import dagger.Provides
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import java.util.concurrent.TimeUnit

internal const val CONNECTION_TIMEOUT = 60_000L
internal const val READ_TIMEOUT = 60_000L
internal const val WRITE_TIMEOUT = 60_000L
internal const val CACHE_DIR = "default"

@Module
internal object HttpModule {

    @JvmStatic
    @AppScope
    @Provides
    fun provideCache(context: Context): Cache {
        val cacheSize = (10 * 1024 * 1024).toLong()
        return Cache(File(context.cacheDir, CACHE_DIR), cacheSize)
    }

    @Provides
    @AppScope
    @JvmStatic
    fun provideConnectionPool(): ConnectionPool = ConnectionPool()

    @JvmStatic
    @AppScope
    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return interceptor
    }


    @JvmStatic
    @AppScope
    @Provides
    internal fun provideOkHttpClient(
        cache: Cache,
        loggingInterceptor: HttpLoggingInterceptor,
        connectionPool: ConnectionPool
    ): OkHttpClient {
        return getCommonOkHttpBuilder(
            loggingInterceptor,
            connectionPool
        )
            .cache(cache)
            .build()
    }

    private fun getCommonOkHttpBuilder(
        loggingInterceptor: HttpLoggingInterceptor,
        connectionPool: ConnectionPool
    ): OkHttpClient.Builder {
        return OkHttpClient.Builder()
            .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
            .addInterceptor(loggingInterceptor)
            .connectionPool(connectionPool)
    }
}