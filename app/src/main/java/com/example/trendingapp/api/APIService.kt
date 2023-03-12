package com.example.trendingapp.api

import com.example.trendingapp.network.response.GetRepositoriesResponse
import com.example.trendingapp.utils.AppConstant.REPOSITORIES
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query


interface APIService {
    @GET(REPOSITORIES)
    fun getRepositories(@Query("q") q: String, @Query("per_page") perPage: String): Single<GetRepositoriesResponse>

}