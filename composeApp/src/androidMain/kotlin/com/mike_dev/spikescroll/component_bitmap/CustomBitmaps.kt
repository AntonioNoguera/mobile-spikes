package com.mike_dev.spikescroll.component_bitmap

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

@Composable
fun GenerateBitmapScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var generatedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(Color.Blue),
            contentAlignment = Alignment.Center
        ) {
            Text("Hola Compose!", color = Color.White)
        }

        Spacer(Modifier.height(24.dp))

        Button(onClick = {
            coroutineScope.launch {
                val bitmap = renderComposableToBitmap(
                    context = context,
                    widthPx = 600,
                    heightPx = 600
                ) {
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .background(Color.Blue),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Renderizado!", color = Color.White)
                    }
                }

                generatedBitmap = bitmap
                val file = saveBitmapToCache(context, bitmap)
                Toast.makeText(context, "Guardado en ${file.absolutePath}", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text("Generar Bitmap")
        }

        generatedBitmap?.let { bmp ->
            Spacer(Modifier.height(32.dp))
            Image(bitmap = bmp.asImageBitmap(), contentDescription = "preview")
        }
    }
}

/**
 * Renderiza un composable directamente en un Bitmap sin ComposeView.
 */
suspend fun renderComposableToBitmap(
    context: Context,
    widthPx: Int,
    heightPx: Int,
    content: @Composable () -> Unit
): Bitmap = withContext(Dispatchers.Main) {
    val bitmap = Bitmap.createBitmap(widthPx, heightPx, Bitmap.Config.ARGB_8888)
    val androidCanvas = Canvas(bitmap)

    val composeCanvas = androidx.compose.ui.graphics.Canvas(androidCanvas)
    val drawScope = CanvasDrawScope()

    drawScope.draw(
        density = context.resources.displayMetrics.density,
        layoutDirection = androidx.compose.ui.unit.LayoutDirection.Ltr,
        canvas = composeCanvas,
        size = Size(widthPx.toFloat(), heightPx.toFloat())
    ) {
        // Dibujar el contenido Compose
        drawIntoCanvas {
            content()
        }
    }

    bitmap
}

fun saveBitmapToCache(context: Context, bitmap: Bitmap): File {
    val file = File(context.cacheDir, "square_compose.png")
    FileOutputStream(file).use {
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
    }
    Log.d("BitmapCompose", "Guardado en ${file.absolutePath}")
    return file
}
