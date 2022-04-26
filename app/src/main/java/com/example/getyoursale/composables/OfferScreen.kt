package com.example.getyoursale

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import java.util.*

@OptIn(ExperimentalCoilApi::class)
@Composable
fun OfferScreen(viewModel: GetYourSaleViewModel) {
    val uriHandler = LocalUriHandler.current
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .background(color = MaterialTheme.colors.primaryVariant),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(
                onClick = {
                    viewModel.navHostController?.navigate(Screen.HomePage.name)
                },
                modifier = Modifier.align(
                    Alignment.CenterVertically
                )
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colors.primary
                )
            }
            // same as in notification header from backend
            Text(
                viewModel.notificationMessage,
                fontSize = 22.sp,
                color = MaterialTheme.colors.primary,
                modifier = Modifier
                    .padding(start = 16.dp, bottom = 16.dp, top = 16.dp)
                    .align(
                        Alignment.CenterVertically
                    ),
                fontWeight = FontWeight.SemiBold
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = rememberImagePainter(
                    data = viewModel.offersList.value.find {
                        it.image.lowercase()
                            .contains(viewModel.notificationMessage.lowercase())
                    }?.image
                ),
                contentDescription = viewModel.notificationMessage,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size((screenWidth - 160.dp))
                    .background(color = MaterialTheme.colors.primary)
                    .shadow(elevation = 3.dp, clip = true)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            //Notification description
            viewModel.notifications.value.find { it.name == viewModel.notificationMessage }?.description?.let {
                Text(
                    text = it,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.secondary,
                    fontSize = 24.sp,
                )
            }
        }
        viewModel.notifications.value.find { it.name == viewModel.notificationMessage }?.url?.let {
            Button(
                onClick = { uriHandler.openUri(it) },
                shape = RoundedCornerShape(100),
                colors = ButtonDefaults.textButtonColors(
                    backgroundColor = MaterialTheme.colors.secondary
                ),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Catch the offer!",
                    color = MaterialTheme.colors.primary,
                    fontSize = 20.sp
                )
            }
        }
    }
}