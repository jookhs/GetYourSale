package com.example.getyoursale.repo

import retrofit2.Retrofit

class RetrofitApi(private val retrofit: Retrofit) {
    fun createNetworkApi(): ApiRepo {
        return retrofit.create(ApiRepo::class.java)
    }
}