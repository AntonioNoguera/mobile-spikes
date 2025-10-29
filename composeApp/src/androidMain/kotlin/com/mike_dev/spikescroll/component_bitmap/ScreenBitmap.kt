package com.mike_dev.spikescroll.component_bitmap

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

/**
 * Pantalla que demuestra cómo renderizar un Composable a PNG fuera de pantalla.
 */
@Composable
fun RenderOffscreenToPngScreen() {
    val context = LocalContext.current
    var generatedUri by remember { mutableStateOf<Uri?>(null) }

    Scaffold(
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 👇 Muestra una vista previa visual del composable
            CardPreviewComposable()

            // 👇 Botón para generar el PNG fuera de pantalla
            Button(onClick = {
                Log.d("Vitmap", "🔘 Botón presionado para render offscreen")

                val density = context.resources.displayMetrics.density
                val widthPx = (300 * density).toInt()
                val heightPx = (180 * density).toInt()

                val uri = renderComposableOffscreenToPng(
                    context = context,
                    widthPx = widthPx,
                    heightPx = heightPx
                ) {
                    // ⬇️ Composable que se renderiza a PNG
                    CardPreviewComposable()
                }

                if (uri != null) {
                    generatedUri = uri
                    Log.d("Vitmap", "✅ PNG generado exitosamente: $uri")
                } else {
                    Log.e("Vitmap", "❌ Error al generar el PNG")
                }
            }) {
                Text("Generar PNG")
            }

            // 👇 Botón para compartir la imagen generada
            generatedUri?.let { uri ->
                Button(
                    onClick = { shareImage(context, uri) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A86B))
                ) {
                    Text("Compartir imagen")
                }
            }
        }
    }
}


/**
 * Composable de ejemplo para renderizar a PNG
 */
@Composable
fun CardPreviewComposable() {
    Card(
        modifier = Modifier
            .width(300.dp)
            .height(180.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEDF2F7)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFEDF2F7))
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Reporte Semanal", style = MaterialTheme.typography.titleMedium)
            Text("Productividad: 67%", style = MaterialTheme.typography.bodyLarge)
            Text("Actualizado hoy", style = MaterialTheme.typography.bodySmall)
        }
    }
}

/**
 * Función para compartir el PNG generado
 */
fun shareImage(context: Context, uri: Uri) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Compartir imagen"))
}
