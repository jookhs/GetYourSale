package com.example.getyoursale

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role.Companion.Button
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jakewharton.processphoenix.ProcessPhoenix

@Composable
fun NoNetWorkScreen(viewModel: GetYourSaleViewModel) {
//    Box(modifier = Modifier.fillMaxSize()) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .align(Alignment.Center),
//            horizontalArrangement = Arrangement.Center
//        ) {
//            Text(
//                "Oops..Please check your network \uD83D\uDE1E",
//                color = MaterialTheme.colors.background,
//                fontSize = 30.sp,
//                textAlign = TextAlign.Center
//            )
//        }
//    }
    val activity = (LocalContext.current as? Activity)
    AlertDialog(onDismissRequest = { /*TODO*/ }, text = {
        Text(
            text = "You do not currently have an internet connection. Please connect and retry.",
            color = MaterialTheme.colors.secondary
        )
    }, buttons = {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End, content = {
                TextButton(
                    onClick = { activity?.finish() },
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    Text(text = "CANCEL", color = MaterialTheme.colors.secondary)
                }
                TextButton(onClick = { viewModel.retryNetwork.invoke() }) {
                    Text(text = "RETRY", color = MaterialTheme.colors.secondary)
                }
            }
        )
    })
}