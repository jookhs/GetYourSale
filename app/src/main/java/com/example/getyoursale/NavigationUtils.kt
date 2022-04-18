package com.example.getyoursale

sealed class Screen(val name: String) {

    object BrandSelection: Screen(name = "BrandSelection")
    object BrandEdition: Screen(name = "BrandEdition")
    object SetUp: Screen(name = "SetUp")
    object HomePage: Screen(name = "Home")
    object BrandScreen: Screen(name = "Brand")
    object NoNetworkScreen: Screen(name = "NoNetWork")
    object OfferScreen: Screen(name = "Offer")
}