package com.example.getyoursale.repo

import retrofit2.Retrofit

interface RetrofitRepo {
    fun createRetrofit(): Retrofit
}