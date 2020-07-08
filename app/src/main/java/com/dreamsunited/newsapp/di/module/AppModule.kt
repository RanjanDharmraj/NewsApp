package com.dreamsunited.newsapp.di.module

import com.dreamsunited.newsapp.network.di.NetworkModule
import dagger.Module

@Module(includes = [NetworkModule::class])
object AppModule