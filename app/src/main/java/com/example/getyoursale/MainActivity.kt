package com.example.getyoursale

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.getyoursale.ui.theme.GetYourSaleTheme
import com.google.firebase.storage.FirebaseStorage


class MainActivity : ComponentActivity() {
    private lateinit var viewModel: GetYourSaleViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(GetYourSaleViewModel::class.java)
        val storage = FirebaseStorage.getInstance().reference
        storage.child("Logos").listAll().addOnSuccessListener {
            for (i in it.items) {
                i.downloadUrl.addOnSuccessListener { item ->
                    viewModel.postBrandsToList(Brand(item.toString(), i.name.split(".").first()))
                }
            }
        }.addOnFailureListener {
            throw RuntimeException("Logo was not found")
        }
        storage.child("Sales").listAll().addOnSuccessListener {
            for (i in it.items) {
                i.downloadUrl.addOnSuccessListener { item ->
                    viewModel.postOffersToList(Offer(item.toString(), i.name.split(".").first()))
                }.addOnFailureListener {
                    throw RuntimeException("Sale was not found")
                }
            }
        }.addOnFailureListener {
            throw RuntimeException("Logo was not found")
        }
        val orientation = resources.configuration.orientation
        val screenLayoutSize = resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK
        if (screenLayoutSize != Configuration.SCREENLAYOUT_SIZE_SMALL && screenLayoutSize != Configuration.SCREENLAYOUT_SIZE_NORMAL) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER
        } else  {
            if (orientation != Configuration.ORIENTATION_PORTRAIT) {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        }
       viewModel.setOrientation(orientation, requestedOrientation)

        installSplashScreen()

        setContent {
            GetYourSaleTheme {
                val navHostController = rememberNavController()
                viewModel.navHostController = navHostController
                NavHost(
                    navController = navHostController,
                    startDestination = Screen.BrandSelection.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colors.primary)
                ) {
                    composable(Screen.BrandSelection.name) {
                        BrandSelectionScreen(viewModel, Screen.BrandSelection.name)
                    }
                    composable(Screen.BrandEdition.name) {
                        BrandSelectionScreen(viewModel, Screen.BrandEdition.name)
                    }
                    composable(Screen.BrandScreen.name) {
                        BrandScreen(viewModel)
                    }
                    composable(Screen.HomePage.name) {
                        HomeScreen(viewModel)
                    }
                    composable(Screen.SetUp.name) {
                        SetUpScreen(viewModel)
                    }
                    composable(Screen.SaleScreen.name) {

                    }
                }
            }
        }
    }
}
