package com.example.getyoursale.di

import com.example.getyoursale.GetYourSaleViewModel
import com.example.getyoursale.repo.*
import com.example.getyoursale.usecase.NetworkUsecase
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModules = module {

    single<NetworkRepository> { NetworkRepoImpl(androidContext()) }

    single { NetworkUsecase(get()) }

    single<RetrofitRepo> { RetrofitRepoImpl() }

    single { RetrofitApi(get()) }

    viewModel { GetYourSaleViewModel(get()) }
}


