package com.dreamsunited.newsapp.network.di

import com.dreamsunited.newsapp.common.Constants.base_url
import com.dreamsunited.newsapp.di.scope.AppScope
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@Module(includes = [GsonModule::class, HttpModule::class])
object RetrofitModule  {

    @JvmStatic
    @Provides
    @AppScope
    fun provideRxJava2CallAdapterFactory(): RxJava2CallAdapterFactory =
        RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io())

    @JvmStatic
    @Provides
    @AppScope
    fun provideRetrofit(
        gson: Gson,
        okHttpClient: OkHttpClient,
        rxJava2CallAdapterFactory: RxJava2CallAdapterFactory
    ): Retrofit = createRetrofit(
        gson = gson,
        okHttpClient = okHttpClient,
        rxJava2CallAdapterFactory = rxJava2CallAdapterFactory
    )

    private fun createRetrofit(
        gson: Gson,
        okHttpClient: OkHttpClient,
        rxJava2CallAdapterFactory: RxJava2CallAdapterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(base_url)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(rxJava2CallAdapterFactory)
            .client(okHttpClient)
            .build()
    }

}