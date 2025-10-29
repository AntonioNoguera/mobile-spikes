package com.mike_dev.spikescroll

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mike_dev.spikescroll.component_bitmap.RenderOffscreenToPngScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            RenderOffscreenToPngScreen()
        }
    }
}
@Composable
fun MainScreen() {
    var showNestedScrollDemo by remember { mutableStateOf(false) }

    if (showNestedScrollDemo) {
        NestedScrollDemoScreen(
            onBack = { showNestedScrollDemo = false }
        )
    } else {
        // Tu pantalla normal
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = { showNestedScrollDemo = true }) {
                Text("Ver Demo Nested Scroll")
            }
        }
    }
}


//

@Composable
fun MiPantalla() {
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { showDialog = true }) {
            Text("Abrir Steps con Animación")
        }
    }

    // Ahora pasas el estado 'visible' directamente
    FullScreenStepDialog(
        visible = showDialog,
        onDismiss = { showDialog = false },
        steps = listOf(
            { Step1Screen() },
            { Step2Screen() },
            { Step3Screen() }
        )
    )
}