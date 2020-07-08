package com.dreamsunited.newsapp.view.activity

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dreamsunited.newsapp.model.ApiError
import com.dreamsunited.newsapp.model.Source
import com.dreamsunited.newsapp.network.repository.NewsRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


const val APP_TITLE = "NEWS"

class NewsSourceViewModel(private val repository: NewsRepository) {

    val title = MutableLiveData<String>()

    private val _loaderVisibility = MutableLiveData<Int>()
    val loaderVisibility : LiveData<Int>
        get() = _loaderVisibility

    private val _sources = MutableLiveData<List<Source>>()
    val sources : LiveData<List<Source>>
    get() = _sources


    private var compositeDisposable : CompositeDisposable ?= null

    init {
        compositeDisposable = CompositeDisposable()
        title.value = APP_TITLE
        getSources()
    }

    private fun getSources() {
        val source = repository.getSources()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                _loaderVisibility.value = View.VISIBLE
            }
            .subscribe(
                { success ->
                    _loaderVisibility.value = View.GONE
                    _sources.value = success
                }
                , { error ->
                    _loaderVisibility.value = View.GONE
                    if (error is ApiError) {
                        Log.e("error", error.msg)
                    } else {
                        Log.e("error", error.localizedMessage)
                    }
                }
            )

        compositeDisposable?.add(source)
    }

    fun onDestroy() {
        compositeDisposable?.clear()
    }


}
