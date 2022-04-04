package com.example.getyoursale

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import coil.compose.rememberImagePainter
import com.example.getyoursale.ui.theme.GetYourSaleTheme
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.services.gmail.Gmail
import com.google.firebase.storage.FirebaseStorage

const val APPLICATION_NAME = "GetYourSALLE"
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
//        val gmailApi: GmailApi = GmailApi()
//        val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
//        val service = Gmail.Builder(httpTransport, JSON_FACTORY, gmailApi.getCredentials(httpTransport, this))
//            .setApplicationName(APPLICATION_NAME)
//            .build()

        installSplashScreen()
//        content.viewTreeObserver.addOnPreDrawListener(
//            object : ViewTreeObserver.OnPreDrawListener {
//                override fun onPreDraw(): Boolean {
//                    // Check if the initial data is ready.
//                    return if (viewModel.isReady) {
//                        // The content is ready; start drawing.
//                        content.viewTreeObserver.removeOnPreDrawListener(this)
//                        true
//                    } else {
//                        // The content is not ready; suspend.
//                        false
//                    }
//                }
//            }
//        )
        

        setContent {
            GetYourSaleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    DefaultPreview()
                }
            }
        }
    }

    @OptIn(ExperimentalMaterialApi::class, coil.annotation.ExperimentalCoilApi::class)
    @Composable
    fun BrandCard(image: String, name: String) {
        val cardBorder = if (viewModel.selectedCards.value.contains(name)) 1 else 0
        val borderColor = if (viewModel.selectedCards.value.contains(name)) Color.Gray else Color.Transparent
        val screenWidth = LocalConfiguration.current.screenWidthDp.dp
        Column (modifier = Modifier.padding(start = 8.dp, bottom = 8.dp, end = 8.dp, top = 8.dp)) {
            Spacer(modifier = Modifier.height(15.dp))
            Card(
                onClick = {
                    viewModel.addToSelectedCards(name)
                },
                border = BorderStroke(cardBorder.dp, borderColor),
                elevation = 3.dp
            ) {
                Image(
                    painter = rememberImagePainter(
                        data = image,
                        builder = {
                            crossfade(false)
                            placeholder(R.drawable.loading_icon)
                        }
                    ),
                    contentDescription = name,
                    contentScale = ContentScale.Crop,
                    colorFilter = if (viewModel.selectedCards.value.contains(name)) ColorFilter.tint(
                        Color.LightGray,
                        BlendMode.Multiply
                    ) else null,
                    modifier = Modifier.size((screenWidth - 48.dp)/2)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = name, modifier = Modifier.fillMaxSize(), textAlign = TextAlign.Left, color = if (viewModel.selectedCards.value.contains(name)) Color.Gray else Color.Unspecified)
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun Cards(list: List<Brand>) {
        LazyVerticalGrid(
            cells = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp),
            content = {
                items(list) { brandCard ->
                    BrandCard(brandCard.image, brandCard.name)
                }
            }
        )
    }

    @Composable
    fun Next() {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            IconButton(enabled = viewModel.nextEnabled.value, onClick = { /*TODO*/ }) {
                Icon(Icons.Filled.KeyboardArrowRight, contentDescription = "Next")
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        GetYourSaleTheme {
            Column {
                Next()
                Column (horizontalAlignment = Alignment.CenterHorizontally) {
                    Cards(viewModel.brandList.value)
                }
            }
        }
    }
}

data class Brand(val image: String, val name: String)