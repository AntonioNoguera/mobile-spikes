package com.mike_dev.spikescroll.component_bitmap


import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream


fun renderComposableOffscreenToPng(
    context: Context,
    widthPx: Int,
    heightPx: Int,
    fileName: String = "vitmap_offscreen.png",
    authority: String = "${context.packageName}.provider",
    content: @Composable () -> Unit
): Uri? {
    Log.d("Vitmap", "🟢 Iniciando render offscreen (ComposeView)...")
    Log.d("Vitmap", "📏 Dimensiones → width=$widthPx, height=$heightPx")

    return try {
        val bitmap = runBlocking { renderComposableToBitmap(context, widthPx, heightPx, content) }

        val file = File(context.cacheDir, fileName)
        FileOutputStream(file).use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }

        val uri = FileProvider.getUriForFile(context, authority, file)
        Log.d("Vitmap", "✅ PNG guardado y URI generado: $uri")
        uri
    } catch (e: Exception) {
        Log.e("Vitmap", "❌ Error en render offscreen: ${e.message}", e)
        null
    }
}

/**
 * Crea y mide un ComposeView fuera de pantalla, dibujando su contenido a un Bitmap.
 */
private suspend fun renderComposableToBitmap(
    context: Context,
    widthPx: Int,
    heightPx: Int,
    content: @Composable () -> Unit
): Bitmap = withContext(Dispatchers.Main) {
    Log.d("Vitmap", "🎨 Seteando contenido en ComposeView (main thread)...")

    val composeView = ComposeView(context)
    composeView.setContent { content() }

    // Medir y hacer layout manualmente
    composeView.measure(
        View.MeasureSpec.makeMeasureSpec(widthPx, View.MeasureSpec.EXACTLY),
        View.MeasureSpec.makeMeasureSpec(heightPx, View.MeasureSpec.EXACTLY)
    )
    composeView.layout(0, 0, widthPx, heightPx)

    Log.d("Vitmap", "🧩 Composable medido y layout listo.")
    val bitmap = Bitmap.createBitmap(widthPx, heightPx, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    composeView.draw(canvas)

    Log.d("Vitmap", "✅ Render offscreen completado correctamente.")
    bitmap
}