package com.example.appnebula.ui.reserva

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appnebula.data.repository.ReservaRepository
import com.example.appnebula.data.SessionManager
import com.example.appnebula.viewmodel.ReservaViewModel
import com.example.appnebula.viewmodel.ReservaViewModelFactory

@Composable
fun ReservaScreen(navController: NavController) {

    val context = LocalContext.current
    val viewModel: ReservaViewModel = viewModel(
        factory = ReservaViewModelFactory(
            // --- CORRECCIÓN AQUÍ ---
            // Simplemente pásale el 'context' que ya tienes al constructor
            reservaRepository = ReservaRepository(context.applicationContext),
            // ---------------------
            sessionManager = SessionManager(context.applicationContext)
        )
    )

    val uiState by viewModel.estado.collectAsState()
    val roundedShape = RoundedCornerShape(16.dp)

    if (uiState.reservaSuccess) {
        AlertDialog(
            onDismissRequest = {
                viewModel.resetReservaStatus()
            },
            title = { Text("¡Reserva Confirmada!") },
            text = { Text(uiState.mensaje ?: "Tu reserva se ha realizado con éxito.") },
            confirmButton = {
                Button(onClick = {
                    viewModel.resetReservaStatus()
                    navController.popBackStack()
                }) {
                    Text("Aceptar")
                }
            }
        )
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(39.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("Formulario de Reserva", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.nombre,
                onValueChange = viewModel::onNombreChange,
                label = { Text("Nombre") },
                isError = uiState.errores.nombre != null,
                modifier = Modifier.fillMaxWidth(0.9f),
                shape = roundedShape,
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = "Nombre")
                },
                enabled = !uiState.isLoading
            )
            uiState.errores.nombre?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.email,
                onValueChange = viewModel::onEmailChange,
                label = { Text("Correo") },
                isError = uiState.errores.email != null,
                modifier = Modifier.fillMaxWidth(0.9f),
                shape = roundedShape,
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = "Correo")
                },
                enabled = !uiState.isLoading
            )
            uiState.errores.email?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.fecha,
                onValueChange = viewModel::onFechaChange,
                label = { Text("Fecha (YYYY-MM-DD)") },
                isError = uiState.errores.fecha != null,
                modifier = Modifier.fillMaxWidth(0.9f),
                shape = roundedShape,
                leadingIcon = {
                    Icon(Icons.Default.Event, contentDescription = "Fecha")
                },
                enabled = !uiState.isLoading
            )
            uiState.errores.fecha?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.hora,
                onValueChange = viewModel::onHoraChange,
                label = { Text("Hora (HH:mm)") },
                isError = uiState.errores.hora != null,
                modifier = Modifier.fillMaxWidth(0.9f),
                shape = roundedShape,
                leadingIcon = {
                    Icon(Icons.Default.Schedule, contentDescription = "Hora")
                },
                enabled = !uiState.isLoading
            )
            uiState.errores.hora?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.personas,
                onValueChange = viewModel::onPersonasChange,
                label = { Text("Número de personas") },
                isError = uiState.errores.personas != null,
                modifier = Modifier.fillMaxWidth(0.9f),
                shape = roundedShape,
                leadingIcon = {
                    Icon(Icons.Default.Group, contentDescription = "Personas")
                },
                enabled = !uiState.isLoading
            )
            uiState.errores.personas?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.enviarReserva() },
                modifier = Modifier.fillMaxWidth(0.9f),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Reservar Mesa")
                }
            }
            if (uiState.mensaje != null && !uiState.reservaSuccess) {
                Text(
                    text = uiState.mensaje!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
