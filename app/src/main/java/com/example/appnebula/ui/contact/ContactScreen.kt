package com.example.appnebula.ui.contact

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Subject
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appnebula.R
import com.example.appnebula.data.SessionManager
import com.example.appnebula.data.repository.ContactRepository
import com.example.appnebula.viewmodel.ContactViewModel
import com.example.appnebula.viewmodel.ContactViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

@Composable
fun ContactScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val viewModel: ContactViewModel = viewModel(
        factory = ContactViewModelFactory(
            repository = ContactRepository(context.applicationContext),
            sessionManager = SessionManager(context.applicationContext)
        )
    )

    val uiState by viewModel.estado.collectAsState()
    val roundedShape = RoundedCornerShape(16.dp)

    if (uiState.isSuccess) {
        AlertDialog(
            onDismissRequest = { viewModel.resetStatus() },
            title = { Text("Mensaje Enviado") },
            text = { Text(uiState.feedbackMessage ?: "Gracias por contactarnos. Te responderemos a la brevedad.") },
            confirmButton = {
                Button(onClick = { viewModel.resetStatus() }) {
                    Text("Aceptar")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.nebula),
            contentDescription = "Logo Nébula Café - Volver a Home",
            modifier = Modifier
                .size(100.dp)
                .clickable {
                    navController.navigate("home") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        launchSingleTop = true
                    }
                }
        )
        Spacer(Modifier.height(16.dp))

        Text("Contáctanos", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.nombre,
            onValueChange = viewModel::onNombreChange,
            label = { Text("Nombre Completo *") },
            modifier = Modifier.fillMaxWidth(),
            shape = roundedShape,
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Nombre") },
            isError = uiState.errores.nombre != null,
            enabled = !uiState.isSending
        )
        uiState.errores.nombre?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = uiState.email,
            onValueChange = viewModel::onEmailChange,
            label = { Text("Email *") },
            modifier = Modifier.fillMaxWidth(),
            shape = roundedShape,
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
            isError = uiState.errores.email != null,
            enabled = !uiState.isSending
        )
        uiState.errores.email?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = uiState.telefono,
            onValueChange = viewModel::onTelefonoChange,
            label = { Text("Teléfono") },
            modifier = Modifier.fillMaxWidth(),
            shape = roundedShape,
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Teléfono") },
            isError = uiState.errores.telefono != null,
            enabled = !uiState.isSending
        )
        uiState.errores.telefono?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = uiState.asunto,
            onValueChange = viewModel::onAsuntoChange,
            label = { Text("Asunto *") },
            modifier = Modifier.fillMaxWidth(),
            shape = roundedShape,
            leadingIcon = { Icon(Icons.Default.Subject, contentDescription = "Asunto") },
            isError = uiState.errores.asunto != null,
            enabled = !uiState.isSending
        )
        uiState.errores.asunto?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = uiState.mensajeTexto,
            onValueChange = viewModel::onMensajeChange,
            label = { Text("Mensaje *") },
            modifier = Modifier.fillMaxWidth(),
            shape = roundedShape,
            leadingIcon = { Icon(Icons.Default.Message, contentDescription = "Mensaje") },
            isError = uiState.errores.mensaje != null,
            enabled = !uiState.isSending
        )
        uiState.errores.mensaje?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { viewModel.sendMessage() },
            enabled = !uiState.isSending,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (uiState.isSending) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Enviar Mensaje")
            }
        }

        val feedback = uiState.feedbackMessage
        if (feedback != null && !uiState.isSuccess) {
            Text(
                text = feedback,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }


        Spacer(Modifier.height(24.dp))

        Text("Encuéntranos aquí", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))

        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            factory = { context ->
                val mapView = MapView(context)
                mapView.onCreate(null)
                mapView.getMapAsync { map ->
                    val melipilla = LatLng(-33.6847, -71.2167)
                    map.addMarker(MarkerOptions().position(melipilla).title("Serrano 1105, Melipilla"))
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(melipilla, 15f))
                }
                mapView
            },
            update = { mapView ->
                mapView.onResume()
            }
        )
    }
}
