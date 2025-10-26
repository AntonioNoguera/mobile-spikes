package com.mike_dev.spikescroll.meta

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import coil.util.DebugLogger
import com.mike_dev.spikescroll.R
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream


private const val TAG = "IGStoryShare"

@Composable
fun ShareOnlineImageScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // 🔗 URL de ejemplo (puedes reemplazar por cualquier otra)
    val imageUrl =
        "https://upload.wikimedia.org/wikipedia/commons/b/bf/Nuevo_vivaaerobus_logotipo_original.jpg"

    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (isLoading) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(12.dp))
                    Text("Descargando imagen...")
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Compartir imagen online a historia de Instagram")
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = {
                        scope.launch {
                            isLoading = true
                            message = null
                            try {
                                withContext(Dispatchers.IO) {
                                    shareOnlineImageToInstagramStory(context, imageUrl)
                                }
                                message = "Abriendo Instagram..."
                            } catch (e: Exception) {
                                Log.e(TAG, "Error al compartir", e)
                                message = "Error al compartir: ${e.message}"
                            } finally {
                                isLoading = false
                            }
                        }
                    }) {
                        Text("Compartir historia")
                    }

                    if (message != null) {
                        Spacer(Modifier.height(12.dp))
                        Text(message!!, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

suspend fun shareOnlineImageToInstagramStory(context: Context, imageUrl: String) {
    Log.d(TAG, "Iniciando descarga de imagen: $imageUrl")

    val loader = ImageLoader.Builder(context)
        .logger(DebugLogger())  // Logger pre-construido de Coil
        .build()

    val request = ImageRequest.Builder(context)
        .data(imageUrl)
        .addHeader("User-Agent", "Mozilla/5.0 (Android) SpikeScroll/1.0")
        .allowHardware(false)
        .build()

    Log.d(TAG, "Ejecutando ImageRequest...")
    val result = (loader.execute(request) as? SuccessResult)?.drawable

    if (result == null) {
        Log.e(TAG, "Error: la imagen no se pudo descargar (result == null)")
        return
    }

    Log.d(TAG, "Imagen descargada correctamente, convirtiendo a Bitmap...")
    val bitmap = try {
        result.toBitmap()
    } catch (e: Exception) {
        Log.e(TAG, "Error al convertir drawable a bitmap", e)
        null
    }

    if (bitmap == null) {
        Log.e(TAG, "Error: bitmap nulo, cancelando.")
        return
    }

    Log.d(TAG, "Bitmap generado con tamaño: ${bitmap.width}x${bitmap.height}")
    withContext(Dispatchers.Main) {
        Log.d(TAG, "Listo para compartir en historia de Instagram...")
        shareDrawableToInstagramStory(context)
        //shareBitmapToInstagramStory(context, bitmap)
    }
}

private fun shareBitmapToInstagramStory(context: Context, bitmap: Bitmap) {
    try {
        Log.d(TAG, "Guardando bitmap en archivo temporal...")
        val cachePath = File(context.cacheDir, "shared_images")
        cachePath.mkdirs()
        val file = File(cachePath, "shared_online_image.png")
        FileOutputStream(file).use { stream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        }

        Log.d(TAG, "Archivo guardado en: ${file.absolutePath}")

        val imageUri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )

        Log.d(TAG, "URI generado: $imageUri")

        val intent = Intent("com.instagram.share.ADD_TO_STORY").apply {
            //setDataAndType(imageUri, "image/*")
            putExtra("interactive_asset_uri", imageUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            putExtra("top_background_color", "#FFFFFF")
            putExtra("bottom_background_color", "#F54927")
            putExtra("attribution_link_url", "https://michael-noguera-portfolio.vercel.app/")
            putExtra("content_url", "https://michael-noguera-portfolio.vercel.app/")
        }

        context.grantUriPermission(
            "com.instagram.android",
            imageUri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )


        val canHandle = intent.resolveActivity(context.packageManager) != null
        Log.d(TAG, "Instagram puede manejar el intent: $canHandle")

        if (canHandle) {
            Log.d(TAG, "Lanzando intent de Instagram Story 🚀")
            context.startActivity(intent)
        } else {
            Log.e(TAG, "Instagram no está instalado o no puede manejar el intent.")
        }
    } catch (e: Exception) {
        Log.e(TAG, "Error durante el shareBitmapToInstagramStory()", e)
    }
}

private fun shareDrawableToInstagramStory(context: Context) {
    // 1️⃣ Convertir drawable a archivo temporal
    val drawableId = R.drawable.ashbaby // tu drawable
    val drawableUri = getUriFromDrawable(context, drawableId)

    // 2️⃣ Crear el intent para historia
    val intent = Intent("com.instagram.share.ADD_TO_STORY").apply {
        setDataAndType(null, "image/jpeg")
        putExtra("interactive_asset_uri", drawableUri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        putExtra("attribution_link_url", "https://michael-noguera-portfolio.vercel.app/")
        putExtra("top_background_color", "#32a852")
        putExtra("bottom_background_color", "#000000")
    }

    // 3️⃣ Conceder permisos a Instagram
    context.grantUriPermission(
        "com.instagram.android",
        drawableUri,
        Intent.FLAG_GRANT_READ_URI_PERMISSION
    )

    // 4️⃣ Lanzar actividad si existe
    if (context.packageManager.resolveActivity(intent, 0) != null) {
        context.startActivity(intent)
    }
}

/**
 * Convierte un drawable a archivo temporal y devuelve su URI con FileProvider.
 */
private fun getUriFromDrawable(context: Context, drawableResId: Int): Uri {
    val drawable = context.resources.openRawResource(drawableResId)
    val tempFile = File(context.cacheDir, "temp_story_image.jpg")

    drawable.use { input ->
        FileOutputStream(tempFile).use { output ->
            input.copyTo(output)
        }
    }

    // ⚠️ Debes tener configurado un FileProvider en tu AndroidManifest.xml
    return FileProvider.getUriForFile(context, "${context.packageName}.provider", tempFile)
}