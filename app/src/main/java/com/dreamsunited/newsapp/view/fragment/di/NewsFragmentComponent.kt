package com.dreamsunited.newsapp.view.fragment.di

import com.dreamsunited.newsapp.di.component.AppLevelDependencies
import com.dreamsunited.newsapp.di.scope.ViewScope
import com.dreamsunited.newsapp.network.repository.NewsRepository
import com.dreamsunited.newsapp.view.fragment.NewsFragment
import com.dreamsunited.newsapp.view.fragment.NewsViewModel
import dagger.Component
import dagger.Module
import dagger.Provides

@Component(
    modules = [NewsFragmentModule::class],
    dependencies = [AppLevelDependencies::class]
)
@ViewScope
interface NewsFragmentComponent {

    @Component.Factory
    interface Factory {
        fun create(
            dependencies: AppLevelDependencies
        ): NewsFragmentComponent
    }
    fun inject(fragment: NewsFragment)
}

@Module
object NewsFragmentModule {

    @Provides
    @JvmStatic
    fun provideViewModel(repository: NewsRepository): NewsViewModel = NewsViewModel(repository)
}