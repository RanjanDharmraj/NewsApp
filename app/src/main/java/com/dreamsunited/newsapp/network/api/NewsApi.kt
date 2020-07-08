package com.dreamsunited.newsapp.network.api

import com.dreamsunited.newsapp.model.NewsApiResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {

    @GET("v2/sources")
    fun getSources(
        @Query("apiKey") apiKey: String,
        @Query("category") category: String? = null,
        @Query("language") language: String? = null,
        @Query("country") country: String? = null
    ): Single<NewsApiResponse>

    @GET("v2/top-headlines")
    fun getHeadlines(
        @Query("apiKey") apiKey: String,
        @Query("q") q: String? = null,
        @Query("category") category: String? = null,
        @Query("language") language: String? = null,
        @Query("country") country: String? = null,
        @Query("sources") sources: String? = null,
        @Query("pageSize") pageSize: Int = 20,
        @Query("page") page: Int = 1
    ): Single<NewsApiResponse>

    @GET("v2/everything")
    fun getEveryNews(
        @Query("apiKey") apiKey: String,
        @Query("q") q: String? = null,
        @Query("sources") sources: String? = null,
        @Query("qInTitle") qInTitle: String? = null,
        @Query("domains") domains: String? = null,
        @Query("excludeDomains") excludeDomains: String? = null,
        @Query("from") from: String? = null,
        @Query("to") to: String? = null,
        @Query("sortBy") sortBy: String? = null,
        @Query("language") language: String? = null,
        @Query("pageSize") pageSize: Int = 20,
        @Query("page") page: Int = 1

    ): Single<NewsApiResponse>
}