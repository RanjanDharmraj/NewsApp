package com.dreamsunited.newsapp

import androidx.multidex.MultiDexApplication
import com.dreamsunited.newsapp.di.component.AppComponent
import com.dreamsunited.newsapp.di.component.DaggerAppComponent

open class NewsApplication : MultiDexApplication() {

    private val component: AppComponent by lazy(LazyThreadSafetyMode.NONE) {
        DaggerAppComponent
            .factory()
            .create(this)
    }

    fun appComponent(): AppComponent = component
}