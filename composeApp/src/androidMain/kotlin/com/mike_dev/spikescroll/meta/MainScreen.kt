package com.mike_dev.spikescroll.meta

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import org.jetbrains.compose.resources.painterResource
import com.mike_dev.spikescroll.R
import java.io.File
import java.io.FileOutputStream

@Composable
fun InstagramStoryScreen() {
    val context = LocalContext.current

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text("Compartir en historia de Instagram", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { shareImageToInstagramStory(context) }) {
                //Icon(painter = painterResource(id = R.drawable.ic_instagram), contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Compartir historia")
            }
        }
    }
}

private fun shareImageToInstagramStory(context: Context) {
    // Verificar si Instagram está instalado
    val pm: PackageManager = context.packageManager
    val isInstalled = try {
        pm.getPackageInfo("com.instagram.android", 0)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }

    if (!isInstalled) {
        // Puedes mostrar un Toast o redirigir al Play Store
        return
    }

    // Generar un archivo temporal con una imagen del drawable
    val drawable = context.getDrawable(R.drawable.ashbaby) as BitmapDrawable
    val bitmap = drawable.bitmap
    val cachePath = File(context.cacheDir, "shared_images")
    cachePath.mkdirs()
    val file = File(cachePath, "image.png")
    val stream = FileOutputStream(file)
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
    stream.close()

    val imageUri: Uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )

    val intent = Intent("com.instagram.share.ADD_TO_STORY").apply {
        setDataAndType(imageUri, "image/*")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        putExtra("top_background_color", "#07eb22")
        putExtra("bottom_background_color", "#06360c")
        //fadesito
    }

    // Dar permiso explícito a Instagram
    context.grantUriPermission(
        "com.instagram.android",
        imageUri,
        Intent.FLAG_GRANT_READ_URI_PERMISSION
    )

    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    }
}