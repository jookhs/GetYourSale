package com.example.getyoursale

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.media.Image

object SingleTon {
    var brands = mutableListOf<Brand>()
    var offers = mutableListOf<Offer>()
    var cachedLogoUrls = mutableListOf<String>()
    var cachedOfferUrls = mutableListOf<String>()
    var cachedBrandNames = mutableListOf<String>()
    var cachedOfferNames = mutableListOf<String>()
    var firstInstall = true
    var preferences: SharedPreferences? = null
    var notifications = mutableListOf<Notification>()
    var connected = false
}