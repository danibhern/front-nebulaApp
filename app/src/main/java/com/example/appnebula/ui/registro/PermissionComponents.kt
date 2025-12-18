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

    // No usamos 'rememberPermissionState', sino el lanzador de resultados de actividad.
    // Esto es más robusto y es la forma moderna recomendada.
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            if (isGranted) {
                // Permiso concedido por el usuario, ejecutamos la acción.
                onPermissionGranted()
            } else {
                // El usuario denegó el permiso.
                // Aquí podrías mostrar un Snackbar o un Toast informando al usuario.
            }
        }
    )

    // Simplemente mostramos un botón. Al hacer clic, se solicitará el permiso.
    // Si el permiso ya está concedido, el sistema no volverá a preguntar
    // y la lógica de onResult se ejecutará indirectamente a través del flujo de la cámara.
    // En nuestro caso, el botón siempre abrirá la cámara o pedirá el permiso si es necesario.
    Button(onClick = {
        // Lanzamos la solicitud del permiso de la cámara.
        launcher.launch(Manifest.permission.CAMERA)
    }) {
        Text("Añadir Foto de Perfil")
    }
}
