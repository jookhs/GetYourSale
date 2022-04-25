package com.example.getyoursale

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun NoNetWorkScreen(viewModel: GetYourSaleViewModel) {
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