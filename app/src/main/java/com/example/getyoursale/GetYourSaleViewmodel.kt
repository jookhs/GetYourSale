package com.example.getyoursale

import android.graphics.drawable.GradientDrawable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.toLowerCase
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController

class GetYourSaleViewModel: ViewModel() {
    private var _nextEnabled =  mutableStateOf(false)
    val nextEnabled: State<Boolean> = _nextEnabled
    private var _selectedCards = mutableStateOf(listOf<String>())
    val selectedCards: State<List<String>> = _selectedCards
    private var _brandList = mutableStateOf(listOf<Brand>())
    val brandList: State<List<Brand>> = _brandList
    private var _offersList = mutableStateOf(listOf<Offer>())
    val offersList: State<List<Offer>> = _offersList
    var navHostController: NavHostController? = null
    private var _tabIndex = mutableStateOf(0)
    val tabIndex: State<Int> = _tabIndex
    private var _searchText = mutableStateOf("")
    val searchText: State<String> = _searchText
    private var _searchPredictions = mutableStateOf(listOf<String>())
    val searchPredictions: State<List<String>> = _searchPredictions
    private var _selectedBrandName = mutableStateOf("")
    val selectedBrandName: State<String> = _selectedBrandName
    private var _orientation = mutableStateOf(0)
    val orientation: State<Int> = _orientation
    private var _requestedOrientation = mutableStateOf(0)
    val requestedOrientation: State<Int> = _requestedOrientation

    fun getOffersForBrand(name: String): List<Offer> {
        val offers = mutableListOf<Offer>()
        offersList.value.forEach {
            if (it.name.lowercase().contains(name.lowercase())) {
                offers.add(it)
            }
        }
        return offers
    }

    fun setOrientation(orientation: Int, requestedOrientation: Int) {
        _orientation.value = orientation
        _requestedOrientation.value = requestedOrientation
    }

    fun setSelectedBrandName(name: String) {
        _selectedBrandName.value = name
    }

    fun getSelectedBrandUrl(): String {
        var selectedBrandUrl = ""
        brandList.value.forEach {
            if (_selectedBrandName.value == it.name) {
                selectedBrandUrl = it.image
            }
        }
        return selectedBrandUrl
    }

    fun setSearchText(text: String) {
        _searchText.value = text
        _searchPredictions.value = _searchPredictions.value.toMutableList().apply {
            this.clear()
            brandList.value.forEach {
                if (text != "" && it.name.lowercase().contains(text)) {
                    this.add(it.name)
                }
            }
        }
    }

    fun setTabIndex(value: Int) {
        _tabIndex.value = value
    }

    private fun postNextEnabled() {
        _nextEnabled.value = _selectedCards.value.isNotEmpty()
    }

    fun postBrandsToList(brand: Brand) {
        _brandList.value = _brandList.value.toMutableList().apply {
            if (!this.contains(brand)) {
                add(brand)
            }
        }
    }

    fun postOffersToList(offer: Offer) {
        _offersList.value = _offersList.value.toMutableList().apply {
            add(offer)
        }
    }

    fun addToSelectedCards(name: String) {
        if (_selectedCards.value.contains(name)) {
            _selectedCards.value = _selectedCards.value.toMutableList().apply {
                remove(name)
            }
        } else {
            _selectedCards.value = _selectedCards.value.toMutableList().apply {
                add(name)
            }
        }
        postNextEnabled()
    }

    fun getSelectedBrands(): List<Brand> {
        val selectedBrands = mutableListOf<Brand>()
        brandList.value.forEach {
            if (selectedCards.value.contains(it.name)) {
                selectedBrands.add(it)
            }
        }
        return selectedBrands
    }
}