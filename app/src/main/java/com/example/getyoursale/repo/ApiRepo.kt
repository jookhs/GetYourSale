package com.example.getyoursale.repo

import com.example.getyoursale.Notification
import retrofit2.Call
import retrofit2.http.GET

interface ApiRepo {
    @GET("volley_array.json")
    fun getOffers() : Call<List<Notification>>
}