package com.sample.exchange.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ErrorScreen(errorMessage: String?, click : () -> Unit){
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(text = "Error: $errorMessage")
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { click() },
                modifier = Modifier.padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text(
                    text = "Retry",
                    color = Color.White,
                    fontSize = 18.sp
                )
            }
        }
    }
}