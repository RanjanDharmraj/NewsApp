package com.dreamsunited.newsapp.view.activity.di

import com.dreamsunited.newsapp.di.component.AppLevelDependencies
import com.dreamsunited.newsapp.di.scope.ActivityScope
import com.dreamsunited.newsapp.network.repository.NewsRepository
import com.dreamsunited.newsapp.view.activity.NewsActivity
import com.dreamsunited.newsapp.view.activity.NewsSourceViewModel
import dagger.Component
import dagger.Module
import dagger.Provides

@ActivityScope
@Component(
    modules = [NewsActivityModule::class],
    dependencies = [AppLevelDependencies::class]
)
interface NewsActivityComponent {

    @Component.Factory
    interface Factory {
        fun create(
            dependencies: AppLevelDependencies
        ): NewsActivityComponent
    }
    fun inject(newsActivity: NewsActivity)
}

@Module
object NewsActivityModule {

    @Provides
    @JvmStatic
    fun provideViewModel(repository: NewsRepository) : NewsSourceViewModel = NewsSourceViewModel(repository)
}