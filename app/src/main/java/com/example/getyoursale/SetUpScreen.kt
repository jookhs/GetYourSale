package com.example.getyoursale

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun SetUpScreen(viewModel: GetYourSaleViewModel) {
    Box(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(enabled = true, onClick = { viewModel.navHostController?.popBackStack() }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalArrangement = Arrangement.Center
        ) {
            Text("You're All Set Up!", color = Color.Black, fontSize = 50.sp)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 26.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                shape = RoundedCornerShape(100),
                onClick = { viewModel.navHostController?.navigate(Screen.HomePage.name) },
                colors = ButtonDefaults.textButtonColors(
                    backgroundColor = Color.Black
                )
            ) {
                Text("      Done     ", color = Color.LightGray, fontSize = 20.sp)
            }
        }
    }
}