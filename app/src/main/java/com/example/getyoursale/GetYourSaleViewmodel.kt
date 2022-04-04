package com.example.getyoursale

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel

class GetYourSaleViewModel: ViewModel() {
    private var _nextEnabled =  mutableStateOf(false)
    val nextEnabled: State<Boolean> = _nextEnabled
    private var _selectedCards = mutableStateOf(listOf<String>())
    val selectedCards: State<List<String>> = _selectedCards
    private var _brandList = mutableStateOf(listOf<Brand>())
    val brandList: State<List<Brand>> = _brandList

    private fun postNextEnabled() {
        _nextEnabled.value = _selectedCards.value.isNotEmpty()
    }

    fun postBrandsToList(brand: Brand) {
        _brandList.value = _brandList.value.toMutableList().apply {
            add(brand)
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
}