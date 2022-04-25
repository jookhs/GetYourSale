package com.example.getyoursale.repo


interface NetworkRepository {
    fun getNetwork(): Boolean
    fun onNetworkChange(isConnected: (Boolean) -> Unit)
}