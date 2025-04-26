package click.ryangst.sensors.presentation

import android.app.AlertDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.wear.compose.material.dialog.Alert
import kotlinx.coroutines.delay

@Composable
fun StretchingScreen(
    onBackToResting: () -> Unit,
    onRestartListener: () -> Unit
) {

    var currentTime by remember { mutableStateOf(0) }
    var isRunning by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning) {
        if (isRunning && currentTime < 10) {
            while (currentTime < 10) {
                delay(1000L)
                currentTime++
            }
            isRunning = false
            showDialog = true // Quando acaba, mostra o Dialog
        }
    }

    if (showDialog) {
        StretchingCompletedDialog(
            onOkClicked = {
                showDialog = false
                onBackToResting()
                onRestartListener()
            }
        )
    }

//    if (currentTime == 10) {
//        AlertDialog(onDismissRequest = {},
//            title = { Text("Bom trabalho!") },
//            text = { Text("Você fez um belo alongamento!") },
//            confirmButton = {
//                Button(
//                    onClick = {}
//                ) {
//                    Text("OK")
//                }
//            }
//        )
//    }


    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFD7D7D7)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "$currentTime",
                style = MaterialTheme.typography.displayLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    isRunning = true
                    currentTime = 0
                },
                enabled = !isRunning
            ) {
                Text("Go!")
            }
            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = {
                    isRunning = !isRunning
                },
            ) {
                Text(if (isRunning) "Pause" else "Play")
            }
        }
    }

}

@Composable
fun StretchingCompletedDialog(
    onOkClicked: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onOkClicked,
        title = { Text("Bom trabalho!") },
        text = { Text("Você fez um belo alongamento!") },
        confirmButton = {
            Button(
                onClick = { onOkClicked() }
            ) {
                Text("OK")
            }
        }
    )
}