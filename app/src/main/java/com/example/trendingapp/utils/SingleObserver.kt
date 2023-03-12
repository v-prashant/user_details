package com.example.trendingapp.utils

import com.example.trendingapp.api.Resource
import androidx.lifecycle.MutableLiveData
import com.example.trendingapp.utils.common.setLoading
import com.example.trendingapp.utils.common.setSuccess
import com.example.trendingapp.utils.common.setThrowable
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

fun <T> Single<T>.responseSubscribe(
    liveData: MutableLiveData<Resource<T>>,
    disposable: CompositeDisposable? = null, progressMessage: String? = null,
) {
    var currentDisposable: Disposable? = null
    subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(object : SingleObserver<T> {
            override fun onSubscribe(d: Disposable) {
                d.let {
                    disposable?.add(it)
                    currentDisposable = it
                }
                liveData.setLoading(progressMessage)
            }

            override fun onSuccess(t: T) {
                liveData.setSuccess(t)
                currentDisposable?.let {
                    disposable?.remove(it)
                }
            }

            override fun onError(e: Throwable) {
                liveData.setThrowable(e)
                currentDisposable?.let {
                    disposable?.remove(it)
                }
            }
        })
}