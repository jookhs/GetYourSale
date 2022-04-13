package com.example.getyoursale

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.yield

@Composable
fun HomeScreen(viewModel: GetYourSaleViewModel) {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
        ) {
            when (viewModel.tabIndex.value) {
                0 -> HomePage(viewModel)
                1 -> Favorites(viewModel)
                2 -> Notifications(viewModel)
            }

        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            Tabs(viewModel)
        }
    }
}

@Composable
fun Tabs(viewModel: GetYourSaleViewModel) {
    val tabData = listOf(
        Icons.Filled.Home,
        Icons.Filled.Favorite,
        Icons.Filled.Notifications
    )
    TabRow(
        selectedTabIndex = viewModel.tabIndex.value,
        backgroundColor = Color.Black,
        contentColor = Color.LightGray,
    ) {
        tabData.forEachIndexed { index, icon ->
            Tab(selected = viewModel.tabIndex.value == index, onClick = {
                viewModel.setTabIndex(index)
            }, icon = {
                Icon(imageVector = icon, contentDescription = null)
            })
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, coil.annotation.ExperimentalCoilApi::class)
@Composable
fun BrandCardHome(image: String, name: String, viewModel: GetYourSaleViewModel) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    Column(modifier = Modifier.padding(start = 8.dp, bottom = 8.dp, end = 8.dp, top = 8.dp)) {
        Spacer(modifier = Modifier.height(15.dp))
        Card(
            onClick = {
                viewModel.setSelectedBrandName(name)
                viewModel.navHostController?.navigate(Screen.BrandScreen.name)
            },
            elevation = 3.dp
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
                modifier = Modifier.size((screenWidth - 68.dp) / 2)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = name,
            modifier = Modifier.fillMaxSize(),
            textAlign = TextAlign.Left
        )
    }
}

@OptIn(
    ExperimentalPagerApi::class, coil.annotation.ExperimentalCoilApi::class,
    androidx.compose.material.ExperimentalMaterialApi::class
)
@Composable
fun HomePage(viewModel: GetYourSaleViewModel) {
    val pagerState = rememberPagerState(viewModel.offersList.value.size)
    val columnModifier = if (viewModel.orientation.value == Configuration.ORIENTATION_LANDSCAPE && viewModel.requestedOrientation.value == ActivityInfo.SCREEN_ORIENTATION_USER) Modifier.verticalScroll(rememberScrollState()) else Modifier
    Column (modifier = columnModifier) {
        SearchView(viewModel)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp, start = 10.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(fontSize = 25.sp, text = "Hot Offers \uD83D\uDD25", fontWeight = FontWeight.SemiBold)
        }
        Box(
            modifier = Modifier
                .fillMaxHeight(0.4f)
                .fillMaxWidth()
        ) {
            HorizontalPager(
                state = pagerState, modifier = Modifier
                    .wrapContentHeight()
                    .wrapContentWidth()
                    .background(color = MaterialTheme.colors.primary)
            ) { page ->
                val image = viewModel.offersList.value[page].image
                Card(onClick = { /*TODO*/ }, elevation = 0.dp, backgroundColor = MaterialTheme.colors.primary) {
                    Image(
                        painter = rememberImagePainter(
                            data = image,
                            builder = {
                                crossfade(true)
                                placeholder(R.drawable.placeholder)
                            }
                        ),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
                LaunchedEffect(page) {
                    while (true) {
                        yield()
                        delay(3000)
                        pagerState.animateScrollToPage(
                            (pagerState.currentPage + 1) % (pagerState.pageCount)
                        )
                    }
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, start = 10.dp, bottom = 20.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(fontSize = 25.sp, text = "All Brands", fontWeight = FontWeight.SemiBold)
        }
        Divider(color = Color.DarkGray, thickness = 0.dp)
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            items(viewModel.brandList.value) { brandCard ->
                BrandCardHome(brandCard.image, brandCard.name, viewModel)
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchView(viewModel: GetYourSaleViewModel) {
    val view = LocalView.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    LazyColumn(
        state = rememberLazyListState(),
        modifier = Modifier.heightIn(max = TextFieldDefaults.MinHeight * 6)
    ) {
        item {
            TextField(
                value = viewModel.searchText.value,
                onValueChange = { value ->
                    viewModel.setSearchText(value)
                },
                placeholder = {
                    Text(text = "Search Brands", fontSize = 20.sp)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                textStyle = TextStyle(color = Color.DarkGray, fontSize = 20.sp),
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "",
                        modifier = Modifier
                            .padding(15.dp)
                            .size(28.dp)
                    )
                },
                trailingIcon = {
                    if (viewModel.searchText.value != "") {
                        IconButton(
                            onClick = {
                                viewModel.setSearchText("")
                                focusManager.clearFocus()
                            }
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "",
                                modifier = Modifier
                                    .padding(15.dp)
                                    .size(28.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors(
                    textColor = Color.DarkGray,
                    cursorColor = Color.DarkGray,
                    leadingIconColor = Color.Black,
                    trailingIconColor = Color.Black,
                    backgroundColor = Color.Transparent,
                    focusedIndicatorColor = Color.LightGray,
                    unfocusedIndicatorColor = Color.LightGray,
                    disabledIndicatorColor = Color.LightGray,
                    placeholderColor = Color.LightGray
                ),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                }),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )
        }
        if (viewModel.searchPredictions.value.count() > 0) {
            items(viewModel.searchPredictions.value) { brand ->
                Row(
                    Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .clickable {
                            view.clearFocus()
                            viewModel.setSelectedBrandName(brand)
                            focusManager.clearFocus(true)
                            viewModel.navHostController?.navigate(Screen.BrandScreen.name)
                        }
                ) {
                    Text(brand, fontSize = 20.sp)
                }

            }
        }
    }
}

@Composable
fun Buttons(viewModel: GetYourSaleViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .background(color = MaterialTheme.colors.primaryVariant), horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Favorites ❤️ ", fontSize = 22.sp, color = Color.White, modifier = Modifier
            .padding(start = 16.dp, bottom = 16.dp, top = 16.dp)
            .align(
                Alignment.CenterVertically
            ), fontWeight = FontWeight.SemiBold)
        IconButton(
            onClick = { viewModel.navHostController?.navigate(Screen.BrandEdition.name)  }, modifier = Modifier.align(
                Alignment.CenterVertically)) {
            Icon(Icons.Filled.Edit, contentDescription = "Edit", tint =  Color.White)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, coil.annotation.ExperimentalCoilApi::class)
@Composable
fun Favorites(viewModel: GetYourSaleViewModel) {
    Column {
        Buttons(viewModel)
        if (viewModel.getSelectedBrands().isNotEmpty()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 8.dp),
                    content = {
                        items(viewModel.getSelectedBrands()) { brandCard ->
                            val screenWidth = LocalConfiguration.current.screenWidthDp.dp
                            Column(
                                modifier = Modifier.padding(
                                    start = 8.dp,
                                    bottom = 8.dp,
                                    end = 8.dp,
                                    top = 8.dp
                                )
                            ) {
                                Spacer(modifier = Modifier.height(15.dp))
                                Card(
                                    onClick = {
                                        viewModel.setSelectedBrandName(brandCard.name)
                                        viewModel.navHostController?.navigate(Screen.BrandScreen.name)
                                    },
                                    elevation = 3.dp
                                ) {
                                    Image(
                                        painter = rememberImagePainter(
                                            data = brandCard.image,
                                            builder = {
                                                crossfade(false)
                                                placeholder(R.drawable.placeholder)
                                            }
                                        ),
                                        contentDescription = brandCard.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.size((screenWidth - 48.dp) / 2)
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = brandCard.name,
                                    modifier = Modifier.fillMaxSize(),
                                    textAlign = TextAlign.Left,
                                    color = Color.Unspecified
                                )
                            }
                        }
                    }
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 76.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "You have no favorits \uD83D\uDE22 ",
                    fontSize = 20.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun Notifications(viewModel: GetYourSaleViewModel) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .background(color = MaterialTheme.colors.primaryVariant),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "What's new \uD83C\uDF89 ",
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
                onClick = { },
                modifier = Modifier.align(
                    Alignment.CenterVertically)
            ) {
                Icon(Icons.Filled.Notifications, contentDescription = "Done", tint = Color.White)
            }
        }
        // after backend is set up, here will be some code for notifications
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 76.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Oops \uD83E\uDD2D, you have no news!",
                color = Color.Gray,
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

data class Offer(val image: String, val name: String)