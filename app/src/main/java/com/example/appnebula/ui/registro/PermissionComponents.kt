package com.example.appnebula.registro // Asegúrate de que el paquete sea el correcto

import android.Manifest
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

/**
 * Un Composable que gestiona la solicitud del permiso de la cámara
 * y muestra un botón para una acción específica.
 *
 * @param onPermissionGranted La acción a ejecutar cuando el permiso es concedido.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun FeatureThatRequiresCameraPermission(
    onPermissionGranted: () -> Unit,
) {
    // 1. Declarar el estado del permiso que necesitamos (CÁMARA)
    val cameraPermissionState = rememberPermissionState(
        Manifest.permission.CAMERA
    )

    // 2. Comprobar el estado del permiso
    if (cameraPermissionState.status.isGranted) {
        // Si el permiso YA está concedido, mostramos el botón final.
        Button(onClick = onPermissionGranted) {
            Text("Añadir Foto de Perfil")
        }
    } else {
        // Si el permiso NO está concedido, mostramos un botón que lo solicitará.
        Column {
            val textToShow = if (cameraPermissionState.status.shouldShowRationale) {
                // Texto a mostrar si el usuario ya denegó el permiso una vez.
                // Es buena práctica explicar por qué lo necesitas.
                "Para añadir una foto a tu perfil, la aplicación necesita acceso a la cámara. Por favor, acepta el permiso."
            } else {
                // Texto para la primera vez que se pide el permiso.
                "Pulsa para activar el acceso a la cámara y añadir tu foto."
            }

            Text(textToShow)
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                Text("Solicitar Permiso")
            }
        }
    }
}
