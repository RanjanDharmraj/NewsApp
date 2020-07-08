package com.dreamsunited.newsapp.view.fragment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dreamsunited.newsapp.model.ApiError
import com.dreamsunited.newsapp.model.Articles
import com.dreamsunited.newsapp.model.Source
import com.dreamsunited.newsapp.network.repository.NewsRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class NewsViewModel(private val repository: NewsRepository) {

    private val _isError = MutableLiveData<Boolean>()
    val isError : LiveData<Boolean>
        get() = _isError

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage : LiveData<String>
        get() = _errorMessage

    private val _isLoading =  MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean>
        get() = _isLoading

    private val _articles = MutableLiveData<List<Articles>>()
    val articles : LiveData<List<Articles>>
        get() = _articles

    private var compositeDisposable: CompositeDisposable? = null

    init {
        compositeDisposable = CompositeDisposable()
    }

    fun getAllNews(source: String) {
        val allNews = repository.getAllNews(source = source)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                _isLoading.value = true
            }.subscribe(
                { success ->
                    _isLoading.value = false
                    _isError.value = false
                    _articles.value = success.articles
                }
                , { error ->
                    _isLoading.value = false
                    _isError.value = true
                    if (error is ApiError) {
                        _errorMessage.value = error.msg
                        Log.e("error", error.msg)
                    } else {
                        _errorMessage.value = error.localizedMessage
                        Log.e("error", error.localizedMessage)
                    }
                }
            )

        compositeDisposable?.add(allNews)
    }

    fun onDestroy() {
        compositeDisposable?.clear()
    }

}