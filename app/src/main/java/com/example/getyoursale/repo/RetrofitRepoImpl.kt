package com.example.getyoursale.repo

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitRepoImpl: RetrofitRepo {
    override fun createRetrofit(): Retrofit {
        val baseUrl = "someURL"
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(baseUrl)
            .build()
    }
}