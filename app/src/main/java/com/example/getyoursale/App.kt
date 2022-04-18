package com.example.getyoursale

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.preference.PreferenceManager
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await


const val NOT_FIRST_RUN = "not first run"
const val LOGO_URLS = "logo urls"
const val OFFER_URLS = "offer urls"
const val BRAND_NAMES = "brand names"
const val OFFER_NAMES = "offer names"
const val NOTIFICATIONS = "notifications"
const val NOTIFICATIONS_SIZE = "notification size"


class App : Application() {
    @SuppressLint("CommitPrefEdits")
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        val storage = FirebaseStorage.getInstance().reference
        val database = Firebase.database.reference
        val coroutineContext = GlobalScope
        val context = this
        val cm =
            applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nInfo = cm.activeNetworkInfo
        val connected = nInfo != null && nInfo.isAvailable && nInfo.isConnected
        val job = coroutineContext.launch {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            if (connected) {
                val brandsListResult: ListResult = storage.child("Logos").listAll().await()
                for (i in brandsListResult.items) {
                    val url = i.downloadUrl.await()
                    if (!SingleTon.brands.contains(
                            Brand(
                                url.toString(),
                                i.name.split(".").first()
                            )
                        )
                    ) {
                        SingleTon.brands.add(Brand(url.toString(), i.name.split(".").first()))
                        SingleTon.cachedLogoUrls.add(url.toString())
                        SingleTon.cachedBrandNames.add(i.name.split(".").first())
                    }
                }
                preferences.edit().putStringSet(LOGO_URLS, SingleTon.cachedLogoUrls.toSet()).apply()
                preferences.edit().putStringSet(BRAND_NAMES, SingleTon.cachedBrandNames.toSet())
                    .apply()
                val offersListResult: ListResult = storage.child("Sales").listAll().await()
                for (i in offersListResult.items) {
                    val url = i.downloadUrl.await()
                    if (!SingleTon.offers.contains(
                            Offer(
                                url.toString(),
                                i.name.split(".").first()
                            )
                        )
                    ) {
                        SingleTon.offers.add(Offer(url.toString(), i.name.split(".").first()))
                        SingleTon.cachedOfferUrls.add(url.toString())
                        SingleTon.cachedOfferNames.add(i.name.split(".").first())
                    }
                    preferences.edit().putStringSet(OFFER_URLS, SingleTon.cachedOfferUrls.toSet())
                        .apply()
                    preferences.edit().putStringSet(OFFER_NAMES, SingleTon.cachedOfferNames.toSet())
                        .apply()
                }
                //will be modified
                if (!preferences.getBoolean(NOT_FIRST_RUN, false)) {
                    SingleTon.firstInstall = true
                    preferences.edit().putBoolean(NOT_FIRST_RUN, true).apply()
                } else {
                    val notificationResult: DataSnapshot = database.child("offers").get().await()
                    if (notificationResult.exists()) {
                        (notificationResult.value as HashMap<*, *>).forEach {
                            val notification = (it.value as HashMap<*, *>)
                            SingleTon.notifications.add(
                                Notification(
                                    notification["image"].toString(),
                                    notification["name"].toString(),
                                    notification["description"].toString(),
                                    notification["stateRead"] as Boolean,
                                    notification["start"] as Long
                                )
                            )
                        }
                    }
                    SingleTon.firstInstall = false
                }
            } else {
                if (!preferences.getBoolean(NOT_FIRST_RUN, false)) {
                    SingleTon.firstInstall = true
                    preferences.edit().putBoolean(NOT_FIRST_RUN, true).apply()
                } else {
                    SingleTon.firstInstall = false
                    val logos = preferences.getStringSet(LOGO_URLS, null)
                    val offers = preferences.getStringSet(OFFER_URLS, null)
                    val brandNames = preferences.getStringSet(BRAND_NAMES, null)
                    val offerNames = preferences.getStringSet(OFFER_NAMES, null)

                    logos?.forEach { logo ->
                        val name = brandNames?.find {
                            logo.contains(it)
                        }
                        if (name != null) {
                            SingleTon.brands.add(Brand(logo.toString(), name.split(".").first()))
                        }
                    }
                    // should be changed
                    offers?.forEach { offer ->
                        val name = offerNames?.find {
                            offer.contains(it)
                        }
                        if (name != null) {
                            SingleTon.offers.add(Offer(offer.toString(), name.split(".").first()))
                        }
                    }
                }
            }
            SingleTon.preferences = preferences
        }
        runBlocking { job.join() }
    }
}