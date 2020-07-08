package com.dreamsunited.newsapp.network.di

import com.dreamsunited.newsapp.di.scope.AppScope
import com.dreamsunited.newsapp.network.api.NewsApi
import com.dreamsunited.newsapp.network.repository.NewsRepository
import com.dreamsunited.newsapp.network.repository.NewsRepositoryImpl
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module(includes = [RetrofitModule::class])
object NetworkModule  {

    @Provides
    @AppScope
    @JvmStatic
    fun provideSdkRepository(api: NewsApi): NewsRepository =
        NewsRepositoryImpl(api)


    @JvmStatic
    @AppScope
    @Provides
    fun provideKycSdkApi(retrofit: Retrofit): NewsApi =
        retrofit.create(NewsApi::class.java)
}