package com.dreamsunited.newsapp.di.component

import android.content.Context
import com.dreamsunited.newsapp.di.module.AppModule
import com.dreamsunited.newsapp.di.scope.AppScope
import com.dreamsunited.newsapp.network.di.NetworkModule
import com.dreamsunited.newsapp.network.repository.NewsRepository
import com.dreamsunited.newsapp.view.activity.NewsActivity
import dagger.BindsInstance
import dagger.Component

@Component(modules = [AppModule::class])
@AppScope
interface AppComponent : AppLevelDependencies {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }
}

interface AppLevelDependencies {
    fun newsRepository(): NewsRepository
}