package com.example.trendingapp.utils.common

import com.example.trendingapp.api.Status
import androidx.lifecycle.MutableLiveData
import com.example.trendingapp.api.Resource
import com.example.trendingapp.network.BaseResponse

fun <T> MutableLiveData<Resource<T>>.setSuccess(data: T) {
    (data as BaseResponse).let {
        if (it.responseStatus?.status?.equals("SUCCESS") == true) {
            postValue(
                Resource(
                    Status.SUCCESS,
                    data
                )
            )
        } else {
            // for now commenting this because there is no success or error response' status
       //     setError(data = data)

            // adding this for above issue
            postValue(
                Resource(
                    Status.SUCCESS,
                    data
                )
            )

        }
    }
}

fun <T> MutableLiveData<Resource<T>>.setLoading(message: String? = null) =
    postValue(Resource(Status.LOADING, message = message))

fun <T> MutableLiveData<Resource<T>>.setError(data: T? = null) {
    postValue(
        Resource(
            Status.ERROR,
            data
        )
    )
}

fun <T> MutableLiveData<Resource<T>>.setThrowable(throwable: Throwable?) {
    postValue(Resource(Status.THROWABLE, throwable = throwable))
}