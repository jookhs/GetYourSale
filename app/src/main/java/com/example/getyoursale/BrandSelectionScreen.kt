package com.example.getyoursale

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter

@Composable
fun BrandSelectionScreen(viewModel: GetYourSaleViewModel, name: String) {
    when (name) {
        Screen.BrandSelection.name -> DefaultPreview(viewModel)
        Screen.BrandEdition.name -> EditPreview(viewModel)
    }
}

@OptIn(ExperimentalMaterialApi::class, coil.annotation.ExperimentalCoilApi::class)
@Composable
fun BrandCard(image: String, name: String, viewModel: GetYourSaleViewModel) {
    val cardBorder = if (viewModel.selectedCards.value.contains(name)) 1 else 0
    val borderColor =
        if (viewModel.selectedCards.value.contains(name)) Color.Black else Color.Transparent
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    Column(modifier = Modifier.padding(start = 8.dp, bottom = 8.dp, end = 8.dp, top = 8.dp)) {
        Spacer(modifier = Modifier.height(15.dp))
        Card(
            onClick = {
                viewModel.addToSelectedCards(name)
            },
            border = BorderStroke(cardBorder.dp, borderColor),
            elevation = 3.dp,
            backgroundColor = MaterialTheme.colors.primary
        ) {
            Image(
                painter = rememberImagePainter(
                    data = image,
                    builder = {
                        crossfade(false)
                        placeholder(R.drawable.placeholder)
                    }
                ),
                contentDescription = name,
                contentScale = ContentScale.Crop,
                colorFilter = if (viewModel.selectedCards.value.contains(name)) ColorFilter.tint(
                    Color.LightGray,
                    BlendMode.Multiply
                ) else null,
                modifier = Modifier.size((screenWidth - 48.dp) / 2).background(color = MaterialTheme.colors.primary)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = name,
            modifier = Modifier.fillMaxSize(),
            textAlign = TextAlign.Left,
            color = if (viewModel.selectedCards.value.contains(name)) Color.LightGray else Color.Unspecified
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Cards(list: List<Brand>, viewModel: GetYourSaleViewModel) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp),
        content = {
            items(list) { brandCard ->
                BrandCard(brandCard.image, brandCard.name, viewModel)
            }
        }
    )
}

@Composable
fun Next(viewModel: GetYourSaleViewModel) {
    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp).background(color = MaterialTheme.colors.primaryVariant), horizontalArrangement = Arrangement.SpaceBetween) {
        Text ("Select Brands  ✨", fontSize = 22.sp, color = Color.White, modifier = Modifier.padding(start = 16.dp, bottom = 16.dp, top = 16.dp).align(
            Alignment.CenterVertically), fontWeight = FontWeight.SemiBold)
        IconButton(
            enabled = viewModel.nextEnabled.value,
            onClick = { viewModel.navHostController?.navigate(Screen.SetUp.name) }, modifier = Modifier.align(
                Alignment.CenterVertically)) {
            Icon(Icons.Filled.ArrowForward, contentDescription = "Next", tint = if (viewModel.nextEnabled.value) Color.White else Color.DarkGray)
        }
    }
}

@Composable
fun Done(viewModel: GetYourSaleViewModel) {
    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp).background(color = MaterialTheme.colors.primaryVariant), horizontalArrangement = Arrangement.SpaceBetween) {
        Text ("Select Brands  ✨", fontSize = 22.sp, color = Color.White, modifier = Modifier.padding(start = 16.dp, bottom = 16.dp, top = 16.dp).align(
            Alignment.CenterVertically), fontWeight = FontWeight.SemiBold)
        IconButton(
            onClick = { viewModel.navHostController?.popBackStack() }, modifier = Modifier.align(
                Alignment.CenterVertically)) {
            Icon(Icons.Filled.Done, contentDescription = "Done", tint =  Color.White)
        }
    }
}


@Composable
fun DefaultPreview(viewModel: GetYourSaleViewModel) {
    Column {
        Next(viewModel)
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Cards(viewModel.brandList.value, viewModel)
        }
    }
}

@Composable
fun EditPreview(viewModel: GetYourSaleViewModel) {
    Column {
        Done(viewModel)
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Cards(viewModel.brandList.value, viewModel)
        }
    }
}



data class Brand(val image: String, val name: String)