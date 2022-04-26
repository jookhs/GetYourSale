package com.example.getyoursale.repo

import kotlinx.coroutines.flow.Flow


interface NetworkRepository {
    fun getNetwork(): Boolean
    fun onNetworkChange(): Flow<Boolean>
}