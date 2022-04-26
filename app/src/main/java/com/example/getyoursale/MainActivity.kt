package com.example.getyoursale

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.getyoursale.ui.theme.GetYourSaleTheme
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.jakewharton.processphoenix.ProcessPhoenix
import org.koin.androidx.viewmodel.ext.android.getViewModel

const val SELECTED_CARD_NAME = "selectedCardName"

class MainActivity : ComponentActivity() {
    lateinit var viewModel: GetYourSaleViewModel
    @SuppressLint("SourceLockedOrientationActivity")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = getViewModel()
        onNewIntent(intent)
        viewModel.retryNetwork = {
            viewModel.preferences?.edit()?.putBoolean(NOT_FIRST_RUN, false)?.apply()
            ProcessPhoenix.triggerRebirth(this@MainActivity)
        }
        viewModel.setConnection()
        createNotificationChannel()
        viewModel.postFirstInstall(SingleTon.firstInstall)
        viewModel.postBrandsToList(SingleTon.brands)
        viewModel.postOffersToList(SingleTon.offers)
        if (viewModel.selectedCards.value.contains("ZARA")) {
            viewModel.postNotificationsToList(SingleTon.notifications)
        }
        viewModel.preferences = SingleTon.preferences

        //will be replaced once backend's ready
        val database = Firebase.database.reference
        val handler = Handler()
        var i = 0
        var a = 1
        handler.postDelayed(object : Runnable {
            override fun run() {
                i += 1
                if (viewModel.offersList.value.isNotEmpty()) {
                    a = viewModel.offersList.value.size - a
                    database.child("offers").child("ZARA$i").setValue(
                        Notification(
                            viewModel.offersList.value[a].image, "ZARA$i",
                            "20% off is the golden ticket!", start = System.currentTimeMillis(),
                            url = "https://www.zara.com/am/en/kids-editorial-10-l313.html?v1=2019990&utm_source=newsletter&utm_medium=email&utm_campaign=2022_04_05_Kids_Latitude_Norte"
                        )
                    )
                }
                handler.postDelayed(this, 60000)
            }
        }, 0)
        val databaseListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                   if (viewModel.selectedCards.value.contains("ZARA")) {
                       viewModel.setNotificationsOpened(false)
                       viewModel.offersList.value.find {
                           it.image.lowercase()
                               .contains("zara")
                       }?.image?.let {
                           viewModel.addToNotifications(
                               Notification(
                                   it,
                                   "ZARA",
                                   "20% off is the golden ticket!",
                                   start = System.currentTimeMillis(),
                                   url = "https://www.zara.com/am/en/kids-editorial-10-l313.html?v1=2019990&utm_source=newsletter&utm_medium=email&utm_campaign=2022_04_05_Kids_Latitude_Norte"
                               )
                           )
                       }
                       val intent = Intent(this@MainActivity, MainActivity::class.java).apply {
                           flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                           putExtra("Notification", "ZARA")
                       }
                       val pendingIntent: PendingIntent =
                           PendingIntent.getActivity(this@MainActivity, 0, intent, FLAG_IMMUTABLE)
                       val builder = NotificationCompat.Builder(this@MainActivity, "GetYourSale")
                           .setSmallIcon(R.drawable.get_your_sale_logo)
                           .setContentTitle("ZARA")
                           .setContentText("20% off is the golden ticket!")
                           .setContentIntent(pendingIntent)
                           .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                       with(NotificationManagerCompat.from(this@MainActivity)) {
                           notify(0, builder.build())
                       }
                   }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }
        database.addValueEventListener(databaseListener)
        //

        if (savedInstanceState != null) {
            savedInstanceState.getString(SELECTED_CARD_NAME)
                ?.let { viewModel.setSelectedBrandName(it) }
            val size = savedInstanceState.getInt(NOTIFICATIONS_SIZE)
            for (l in 0 until size) {
                val notification = savedInstanceState.getStringArray(NOTIFICATIONS + l)
                if (notification != null && viewModel.selectedCards.value.contains("ZARA")) {
                    viewModel.addToNotifications(Notification(notification[0], notification[1], notification[2], start = notification[3].toLong(), url = notification[4]
                    ))
                }
            }
        }

        viewModel.brandList.value.forEach { brand ->
            if (viewModel.preferences?.getStringSet(SELECTED_CARDS, null)
                    ?.contains(brand.name) == true
            ) {
                viewModel.addToSelectedCards(brand.name)
            }
        }

        val orientation = resources.configuration.orientation
        val screenLayoutSize =
            resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK
        if (screenLayoutSize != Configuration.SCREENLAYOUT_SIZE_SMALL && screenLayoutSize != Configuration.SCREENLAYOUT_SIZE_NORMAL) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER
        } else {
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
                if (!viewModel.isConnected.value) {
                    Toast.makeText(this, "Network is unavailable", Toast.LENGTH_LONG).show()
                }
                NavHost(
                    navController = navHostController,
                    startDestination = if (viewModel.firstInstall.value) {
                        if (SingleTon.connected) {
                            Screen.BrandSelection.name
                        } else {
                            Screen.NoNetworkScreen.name
                        }
                    } else {
                        if (viewModel.notificationMessage != "") {
                            Screen.OfferScreen.name
                        } else {
                            Screen.HomePage.name
                        }
                    },
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
                        BrandScreen(viewModel, this@MainActivity)
                    }
                    composable(Screen.HomePage.name) {
                        BackHandler(true) {}
                        HomeScreen(viewModel)
                    }
                    composable(Screen.SetUp.name) {
                        SetUpScreen(viewModel)
                    }
                    composable(Screen.NoNetworkScreen.name) {
                        NoNetWorkScreen(viewModel)
                    }
                    composable(Screen.OfferScreen.name) {
                        OfferScreen(viewModel)
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        viewModel.notificationMessage = intent?.extras?.getString("Notification", null) ?: ""
    }

    @SuppressLint("CommitPrefEdits")
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SELECTED_CARD_NAME, viewModel.selectedBrandName.value)
        outState.putInt(NOTIFICATIONS_SIZE, viewModel.notifications.value.size)
        viewModel.notifications.value.forEach {
            val set = listOf(it.image, it.name, it.description, it.start.toString(), it.url)
            outState.putStringArray(NOTIFICATIONS + viewModel.notifications.value.indexOf(it), set.toTypedArray())
        }
    }

    private fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "GetYourSale",
                "GetYourSale",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "GetYourSale"
            }
            val notificationManager: NotificationManager =
                this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
