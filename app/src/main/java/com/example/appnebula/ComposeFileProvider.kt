package com.example.appnebula

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ComposeFileProvider {

    // Esta función crea un archivo único con nombre basado en la fecha y hora.
    // Es la forma recomendada por Google para CameraX.
    fun createImageFile(context: Context): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = context.cacheDir // Usamos el directorio de caché
        return File.createTempFile(
            imageFileName, /* prefijo */
            ".jpg",        /* sufijo */
            storageDir     /* directorio */
        )
    }

    // Esta función obtiene la URI para un archivo usando la autoridad del FileProvider.
    // Es crucial para que la cámara y otras apps puedan acceder al archivo de forma segura.
    fun getUriForFile(context: Context, file: File): Uri {
        val authority = "${context.packageName}.provider"
        return FileProvider.getUriForFile(
            context,
            authority,
            file
        )
    }
}
