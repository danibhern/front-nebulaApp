package com.example.appnebula.ui.registro

import android.Manifest
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

@Composable
fun FeatureThatRequiresCameraPermission(
    onPermissionGranted: () -> Unit,
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            if (isGranted) {
                onPermissionGranted()
            } else {
            }
        }
    )
    Button(onClick = {
        launcher.launch(Manifest.permission.CAMERA)
    }) {
        Text("Añadir Foto de Perfil")
    }
}
