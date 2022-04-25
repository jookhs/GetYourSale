package com.example.getyoursale.repo

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.annotation.RequiresApi

class NetworkRepoImpl(val context: Context): NetworkRepository {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun getNetwork(): Boolean {
        val connectivityManager =
            context.getSystemService(ConnectivityManager::class.java) as ConnectivityManager
        val nInfo = connectivityManager.activeNetworkInfo
        return nInfo != null && nInfo.isAvailable && nInfo.isConnected
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onNetworkChange(isConnected: (Boolean) -> Unit) {
        val connectivityManager =
            context.getSystemService(ConnectivityManager::class.java) as ConnectivityManager
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                isConnected.invoke(true)
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                super.onCapabilitiesChanged(network, networkCapabilities)
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                isConnected.invoke(true)
            }
        }
        connectivityManager.requestNetwork(networkRequest, networkCallback)
    }
}