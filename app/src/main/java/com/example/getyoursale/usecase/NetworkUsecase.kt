package com.example.getyoursale.usecase

import com.example.getyoursale.repo.NetworkRepository
import kotlinx.coroutines.flow.Flow

class NetworkUsecase(private val networkRepository: NetworkRepository) {
    fun getNetwork(): Boolean {
        return  networkRepository.getNetwork()
    }
    fun onNetworkChange(): Flow<Boolean> {
        return networkRepository.onNetworkChange()
    }
}