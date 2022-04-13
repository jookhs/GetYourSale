package com.example.getyoursale

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter

@OptIn(ExperimentalCoilApi::class, androidx.compose.ui.ExperimentalComposeUiApi::class)
@Composable
fun BrandScreen(viewModel: GetYourSaleViewModel) {
    val iconVector =
        if (viewModel.selectedCards.value.contains(viewModel.selectedBrandName.value)) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .background(color = MaterialTheme.colors.primaryVariant),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                viewModel.selectedBrandName.value,
                fontSize = 22.sp,
                color = Color.White,
                modifier = Modifier
                    .padding(start = 16.dp, bottom = 16.dp, top = 16.dp)
                    .align(
                        Alignment.CenterVertically
                    ),
                fontWeight = FontWeight.SemiBold
            )
            IconButton(
                onClick = { viewModel.addToSelectedCards(viewModel.selectedBrandName.value) },
                modifier = Modifier.align(
                    Alignment.CenterVertically
                )
            ) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = "Favourite",
                    tint = MaterialTheme.colors.primary
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = rememberImagePainter(
                    data = viewModel.getSelectedBrandUrl()
                ),
                contentDescription = viewModel.selectedBrandName.value,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size((screenWidth - 260.dp))
                    .background(color = MaterialTheme.colors.primary)
                    .shadow(elevation = 3.dp, shape = CircleShape, clip = true)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp, start = 16.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = "Catch The Hot News \uD83D\uDD25",
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Start,
                color = MaterialTheme.colors.secondary,
                fontSize = 24.sp,
            )
        }
        Divider(color = Color.DarkGray, thickness = 0.dp)
        if (viewModel.getOffersForBrand(viewModel.selectedBrandName.value).isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp),
                content = {
                    items(viewModel.getOffersForBrand(viewModel.selectedBrandName.value)) { offer ->
                        //instead of name we should write an offer name
                        BrandCardHome(offer.image, offer.name, viewModel)
                    }
                }
            )
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp, start = 16.dp, top = 76.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No offers yet.",
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.secondaryVariant,
                    fontSize = 24.sp,
                )
            }
        }
    }
}