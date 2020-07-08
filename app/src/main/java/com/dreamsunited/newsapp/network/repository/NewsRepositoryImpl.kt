package com.dreamsunited.newsapp.network.repository

import com.dreamsunited.newsapp.common.Constants
import com.dreamsunited.newsapp.model.*
import com.dreamsunited.newsapp.network.api.NewsApi
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import io.reactivex.Single
import retrofit2.HttpException

inline fun <reified T : Any> typeToken() = object : TypeToken<T>() {}

class NewsRepositoryImpl(private val api: NewsApi) : NewsRepository {

    private val simpleGson = GsonBuilder().create()

    override fun getSources(): Single<List<Source>> {
        return api.getSources(Constants.apiKey)
            .mapThrowing()
            .map {
                it.sources?.take(10)
            }
    }

    override fun getAllNews(source: String): Single<NewsApiResponse> {
        return api.getEveryNews(Constants.apiKey, sources = source)
            .mapThrowing()
    }

    override fun getTopHeadlines(): Single<NewsApiResponse> {
        return api.getHeadlines(Constants.apiKey)
            .mapThrowing()
    }

    private fun Single<NewsApiResponse>.mapThrowing(): Single<NewsApiResponse> {
        return map { it.mapThrowing() }
            .onErrorResumeNext { error ->
                val errorResponse = getError(error)
                val typeToken = typeToken<NewsApiResponse>()
                val responseError: NewsApiResponse? =
                    simpleGson.fromJson(errorResponse, typeToken.type)
                if (responseError?.code != null && responseError.message != null) {
                    Single.error(ApiError(responseError.code, responseError.message))
                } else {
                    Single.error(error)
                }
            }
    }

    private fun getError(throwable: Throwable?): String? {
        if (throwable is HttpException) {
            return try {
                throwable.response()?.errorBody()?.string()
            } catch (exception: Exception) {
                null
            }
        }
        return null
    }
}