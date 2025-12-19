package com.example.appnebula.ui.reserva

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appnebula.R
import com.example.appnebula.data.repository.ReservaRepository
import com.example.appnebula.data.SessionManager
import com.example.appnebula.viewmodel.ReservaViewModel
import com.example.appnebula.viewmodel.ReservaViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservaScreen(navController: NavController) {

    val context = LocalContext.current
    val viewModel: ReservaViewModel = viewModel(
        factory = ReservaViewModelFactory(
            reservaRepository = ReservaRepository(context.applicationContext),
            sessionManager = SessionManager(context.applicationContext)
        )
    )

    val uiState by viewModel.estado.collectAsState()
    val roundedShape = RoundedCornerShape(24.dp)

    if (uiState.reservaSuccess) {
        AlertDialog(
            onDismissRequest = { viewModel.resetReservaStatus() },
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
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        Scaffold(
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 28.dp, vertical = 40.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.nebula),
                    contentDescription = "Nebula Logo",
                    modifier = Modifier
                        .size(140.dp)
                        .padding(bottom = 24.dp),
                    contentScale = ContentScale.Fit
                )

                // TÍTULOS
                Text(
                    text = "Reservar Mesa",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Completa tus datos",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(48.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(36.dp)
                    ) {
                        // NOMBRE
                        OutlinedTextField(
                            value = uiState.nombre,
                            onValueChange = viewModel::onNombreChange,
                            label = { Text("Nombre", color = MaterialTheme.colorScheme.primary) },
                            isError = uiState.errores.nombre != null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            shape = roundedShape,
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                )
                            },
                            enabled = !uiState.isLoading,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.primary,
                                unfocusedTextColor = MaterialTheme.colorScheme.primary,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                errorBorderColor = Color.Red,
                                focusedLabelColor = MaterialTheme.colorScheme.primary,
                                unfocusedLabelColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                            )
                        )
                        uiState.errores.nombre?.let {
                            Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                        }

                        // EMAIL
                        OutlinedTextField(
                            value = uiState.email,
                            onValueChange = viewModel::onEmailChange,
                            label = { Text("Correo", color = MaterialTheme.colorScheme.primary) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            isError = uiState.errores.email != null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            shape = roundedShape,
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Email,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                )
                            },
                            enabled = !uiState.isLoading,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.primary,
                                unfocusedTextColor = MaterialTheme.colorScheme.primary,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                errorBorderColor = Color.Red,
                                focusedLabelColor = MaterialTheme.colorScheme.primary,
                                unfocusedLabelColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                            )
                        )
                        uiState.errores.email?.let {
                            Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                        }

                        // FECHA
                        OutlinedTextField(
                            value = uiState.fecha,
                            onValueChange = viewModel::onFechaChange,
                            label = { Text("Fecha (YYYY-MM-DD)", color = MaterialTheme.colorScheme.primary) },
                            isError = uiState.errores.fecha != null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            shape = roundedShape,
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Event,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                )
                            },
                            enabled = !uiState.isLoading,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.primary,
                                unfocusedTextColor = MaterialTheme.colorScheme.primary,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                errorBorderColor = Color.Red,
                                focusedLabelColor = MaterialTheme.colorScheme.primary,
                                unfocusedLabelColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                            )
                        )
                        uiState.errores.fecha?.let {
                            Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                        }

                        // HORA
                        OutlinedTextField(
                            value = uiState.hora,
                            onValueChange = viewModel::onHoraChange,
                            label = { Text("Hora (HH:mm)", color = MaterialTheme.colorScheme.primary) },
                            isError = uiState.errores.hora != null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            shape = roundedShape,
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                )
                            },
                            enabled = !uiState.isLoading,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.primary,
                                unfocusedTextColor = MaterialTheme.colorScheme.primary,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                errorBorderColor = Color.Red,
                                focusedLabelColor = MaterialTheme.colorScheme.primary,
                                unfocusedLabelColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                            )
                        )
                        uiState.errores.hora?.let {
                            Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                        }

                        // PERSONAS
                        OutlinedTextField(
                            value = uiState.personas,
                            onValueChange = viewModel::onPersonasChange,
                            label = { Text("Número de personas", color = MaterialTheme.colorScheme.primary) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            isError = uiState.errores.personas != null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 24.dp),
                            shape = roundedShape,
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Group,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                )
                            },
                            enabled = !uiState.isLoading,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.primary,
                                unfocusedTextColor = MaterialTheme.colorScheme.primary,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                errorBorderColor = Color.Red,
                                focusedLabelColor = MaterialTheme.colorScheme.primary,
                                unfocusedLabelColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                            )
                        )
                        uiState.errores.personas?.let {
                            Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                        }

                        // BOTÓN
                        Button(
                            onClick = { viewModel.enviarReserva() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(28.dp),
                            enabled = !uiState.isLoading,
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 12.dp)
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.5.dp
                                )
                            } else {
                                Text(
                                    "Reservar Mesa",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        if (uiState.mensaje != null && !uiState.reservaSuccess) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = uiState.mensaje!!,
                                color = Color.Red,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
