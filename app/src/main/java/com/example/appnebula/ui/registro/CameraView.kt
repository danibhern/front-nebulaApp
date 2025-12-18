package com.example.appnebula.ui.registro

import android.content.Context
import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Check
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.appnebula.ComposeFileProvider
import java.io.File
import kotlinx.coroutines.guava.await

@Composable
fun CameraView(
    onImageCaptured: (Uri) -> Unit,
    onError: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val imageCapture = remember { ImageCapture.Builder().build() }
    val previewView = remember { PreviewView(context) }

    LaunchedEffect(Unit) {
        val cameraProvider = try {
            // Se llama a la nueva función
            context.getCameraProvider()
        } catch (e: Exception) {
            onError("No se pudo obtener el proveedor de cámara: ${e.message}")
            return@LaunchedEffect
        }

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                },
                imageCapture
            )
        } catch (exc: Exception) {
            onError("Fallo al iniciar la cámara: ${exc.message}")
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(onClick = {
                takePhoto(context, imageCapture, onImageCaptured, onError)
            }) {
                Icon(Icons.Sharp.Check, contentDescription = "Tomar foto")
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            AndroidView({ previewView }, modifier = Modifier.fillMaxSize())
        }
    }
}

private fun takePhoto(
    context: Context,
    imageCapture: ImageCapture,
    onImageCaptured: (Uri) -> Unit,
    onError: (String) -> Unit
) {
    val photoFile: File = try {
        ComposeFileProvider.createImageFile(context)
    } catch (ex: Exception) {
        onError("No se pudo crear el archivo de imagen: ${ex.message}")
        return
    }

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                val savedUri = output.savedUri ?: Uri.fromFile(photoFile)
                onImageCaptured(savedUri)
            }

            override fun onError(exc: ImageCaptureException) {
                onError("Error al guardar la foto: ${exc.message}")
            }
        }
    )
}

// --- ¡¡FUNCIÓN ACTUALIZADA!! ---
// Esta nueva versión es más corta y usa la función de extensión .await()
// que viene con las dependencias que ya tienes.
private suspend fun Context.getCameraProvider(): ProcessCameraProvider {
    return ProcessCameraProvider.getInstance(this).await()
}
