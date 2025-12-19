package com.example.appnebula

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ComposeFileProvider {
    fun createImageFile(context: Context): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = context.cacheDir
        return File.createTempFile(
            imageFileName,
            ".jpg",
            storageDir
        )
    }

    fun getUriForFile(context: Context, file: File): Uri {
        val authority = "${context.packageName}.provider"
        return FileProvider.getUriForFile(
            context,
            authority,
            file
        )
    }
}
