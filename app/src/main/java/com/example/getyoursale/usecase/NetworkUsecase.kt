package com.example.getyoursale.usecase

import com.example.getyoursale.repo.NetworkRepository

class NetworkUsecase(private val networkRepository: NetworkRepository) {
    fun getNetwork(): Boolean {
        return  networkRepository.getNetwork()
    }
    fun onNetworkChange(isConnected: (Boolean) -> Unit) {
        return networkRepository.onNetworkChange(isConnected)
    }
}