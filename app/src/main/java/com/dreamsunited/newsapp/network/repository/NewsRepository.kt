package com.dreamsunited.newsapp.network.repository

import com.dreamsunited.newsapp.model.NewsApiResponse
import com.dreamsunited.newsapp.model.Source
import io.reactivex.Single

interface NewsRepository {

    fun getSources() : Single<List<Source>>
    fun getAllNews(source: String) : Single<NewsApiResponse>
    fun getTopHeadlines() : Single<NewsApiResponse>
}