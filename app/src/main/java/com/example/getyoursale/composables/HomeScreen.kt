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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.ModifierLocalReadScope
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
        Spacer(modifier = Modifier.height(10.dp))
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
                if (index == 2) {
                    viewModel.setNotificationsOpened(true)
                }
            }, icon = {
                BadgedBox(badge = { if (viewModel.notifications.value.isNotEmpty() && index == 2 && viewModel.tabIndex.value != index && !viewModel.notificationsOpened.value) Text(text = "\uD83D\uDD34", fontSize = 6.sp) else {} }) {
                    Icon(imageVector = icon, contentDescription = null)
                }
            })
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, coil.annotation.ExperimentalCoilApi::class)
@Composable
fun BrandCardHome(image: String, name: String, viewModel: GetYourSaleViewModel) {
    val uriHandler = LocalUriHandler.current
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    //url from backend
    val urlParam =
        "https://www.zara.com/am/en/kids-editorial-10-l313.html?v1=2019990&utm_source=newsletter&utm_medium=email&utm_campaign=2022_04_05_Kids_Latitude_Norte"
    Column(modifier = Modifier.padding(start = 8.dp, bottom = 8.dp, end = 8.dp, top = 8.dp)) {
        Spacer(modifier = Modifier.height(15.dp))
        Card(
            onClick = {
                if (viewModel.selectedBrandName.value == name) {
                    uriHandler.openUri(urlParam)
                    viewModel.setSelectedBrandName(name)
                } else {
                    viewModel.setSelectedBrandName(name)
                    viewModel.navHostController?.navigate(Screen.BrandScreen.name)
                }
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
            textAlign = TextAlign.Left,
            color = MaterialTheme.colors.secondary
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
    val uriHandler = LocalUriHandler.current
    val columnModifier =
        if (viewModel.orientation.value == Configuration.ORIENTATION_LANDSCAPE && viewModel.requestedOrientation.value == ActivityInfo.SCREEN_ORIENTATION_USER) Modifier.verticalScroll(
            rememberScrollState()
        ) else Modifier
    Column(modifier = columnModifier) {
        SearchView(viewModel)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp, start = 10.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                fontSize = 25.sp,
                text = "Hot Offers \uD83D\uDD25",
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colors.secondary
            )
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
                val urlParam = viewModel.offersList.value[page].url
                Card(onClick = {
                    uriHandler.openUri(urlParam)
                }, elevation = 0.dp, backgroundColor = MaterialTheme.colors.primary) {
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
            Text(
                fontSize = 25.sp,
                text = "All Brands",
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colors.secondary
            )
        }
        Divider(color = MaterialTheme.colors.secondary, thickness = 0.dp)
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
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val onClicked: () -> Unit = {}
    LazyColumn(
        state = rememberLazyListState(),
        modifier = Modifier.heightIn(max = TextFieldDefaults.MinHeight * 6)
    ) {
        item {
            DisposableEffect(key1 = onClicked) {
                onDispose {
                    keyboardController?.hide()
                    focusManager.clearFocus(true)
                    keyboardController?.hide()
                    focusManager.clearFocus(true)
                }
            }
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
                    textColor = MaterialTheme.colors.primaryVariant,
                    cursorColor = MaterialTheme.colors.primaryVariant,
                    leadingIconColor = MaterialTheme.colors.secondary,
                    trailingIconColor = MaterialTheme.colors.secondary,
                    backgroundColor = Color.Transparent,
                    focusedIndicatorColor = MaterialTheme.colors.secondaryVariant,
                    unfocusedIndicatorColor = MaterialTheme.colors.secondaryVariant,
                    disabledIndicatorColor = MaterialTheme.colors.secondaryVariant,
                    placeholderColor = MaterialTheme.colors.secondaryVariant
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
                            onClicked.invoke()
                            viewModel.setSelectedBrandName(brand)
                            viewModel.navHostController?.navigate(Screen.BrandScreen.name)
                        }
                ) {
                    Text(brand, fontSize = 20.sp, color = MaterialTheme.colors.secondary)
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
            .background(color = MaterialTheme.colors.primaryVariant),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            "Favourites",
            fontSize = 22.sp,
            color = MaterialTheme.colors.primary,
            modifier = Modifier
                .padding(start = 16.dp, bottom = 16.dp, top = 16.dp)
                .align(
                    Alignment.CenterVertically
                ),
            fontWeight = FontWeight.SemiBold
        )
        IconButton(
            onClick = { viewModel.navHostController?.navigate(Screen.BrandEdition.name) },
            modifier = Modifier.align(
                Alignment.CenterVertically
            )
        ) {
            Icon(
                Icons.Filled.Edit,
                contentDescription = "Edit",
                tint = MaterialTheme.colors.primary
            )
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
                                    color = MaterialTheme.colors.secondary
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

@OptIn(ExperimentalCoilApi::class, androidx.compose.material.ExperimentalMaterialApi::class)
@Composable
fun Notifications(viewModel: GetYourSaleViewModel) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    Column (modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp)
                .background(color = MaterialTheme.colors.primaryVariant),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Notifications",
                fontSize = 22.sp,
                color = MaterialTheme.colors.primary,
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
                    Alignment.CenterVertically
                )
            ) {
                Icon(
                    Icons.Filled.Notifications,
                    contentDescription = "Done",
                    tint = MaterialTheme.colors.primary
                )
            }
        }
        Text(text = "What's new", fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colors.secondary, fontSize = 16.sp, modifier = Modifier.padding(bottom = 6.dp, start = 16.dp))
        Divider(color = MaterialTheme.colors.secondary, thickness = 0.dp, modifier = Modifier.padding(bottom = 6.dp))
        if (viewModel.notifications.value.isNotEmpty()) {
            LazyColumn(state = listState, reverseLayout = true, verticalArrangement = Arrangement.Top, modifier = Modifier.padding(bottom = 48.dp), content = {
                items(viewModel.notifications.value) { notification ->
                    Card(onClick = {
                        //change state in db also
                        notification.stateRead = true
                        viewModel.notificationMessage = notification.name
                        viewModel.navHostController?.navigate(Screen.OfferScreen.name)
                    }, backgroundColor = MaterialTheme.colors.primary) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 4.dp)
                                .background(color = if (notification.stateRead) MaterialTheme.colors.primary else MaterialTheme.colors.secondaryVariant),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Image(painter = rememberImagePainter(
                                data = notification.image,
                                builder = {
                                    crossfade(false)
                                    placeholder(R.drawable.placeholder)
                                }
                            ),
                                contentDescription = notification.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(screenWidth / 4)
                                    .padding(start = 8.dp)
                                    .background(color = if (notification.stateRead) MaterialTheme.colors.primary else MaterialTheme.colors.secondaryVariant))
                            Column(modifier = Modifier.padding(start = 16.dp)) {
                                Text(
                                    text = if (notification.stateRead) notification.name else notification.name + " \uD83D\uDD14",
                                    fontWeight = if (notification.stateRead) null else FontWeight.Bold,
                                    color = MaterialTheme.colors.secondary,
                                    modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)
                                )
                                Text(
                                    text = notification.description,
                                    color = MaterialTheme.colors.secondary,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                                Text(
                                    text = getTimeFromMilliesText(viewModel, System.currentTimeMillis() - notification.start),
                                    color = if (notification.stateRead) MaterialTheme.colors.secondaryVariant else MaterialTheme.colors.surface,
                                    fontSize = 13.sp
                                )

                            }
                        }
                    }
                }
                coroutineScope.launch {
                    if (!listState.isScrollInProgress) {
                        listState.scrollToItem(viewModel.notifications.value.lastIndex)
                    }
                }
            })
        } else {
            if (viewModel.isConnected.value) {
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
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 76.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Oops \uD83E\uDD2D, no internet connection.",
                        color = Color.Gray,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

fun getTimeFromMilliesText(viewModel: GetYourSaleViewModel, currentTime: Long): String {
    return when  {
        currentTime/1000 < 60 -> "now"
        currentTime/1000 in 60..3599 -> (currentTime/1000/60).toInt().toString() + "m"
        currentTime/1000 in 3600..86399 -> (currentTime/1000/3600).toInt().toString() + "h"
        currentTime/1000 in 86400..604799 -> (currentTime/1000/86400).toInt().toString() + "d"
        currentTime/1000 >= 604800 -> (currentTime/1000/604800).toInt().toString() + "w"
        else -> ""
    }
}
