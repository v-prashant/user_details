package com.example.trendingapp.base

import androidx.lifecycle.ViewModel
import com.example.trendingapp.ui.Application
import io.reactivex.disposables.CompositeDisposable

/**
 *
 * Created by Prashant Verma
 *
 */
abstract class BaseViewModel : ViewModel() {
    val applicationContext: Application = Application.getInstance()
    val compositeDisposable: CompositeDisposable = CompositeDisposable()

    fun clear() {
        compositeDisposable.clear()
    }

}