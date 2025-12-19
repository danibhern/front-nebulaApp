package com.example.appnebula.ui.contact

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.appnebula.R
import com.example.appnebula.ui.theme.AppNebulaTheme
import com.example.appnebula.viewmodel.ContactViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun ContactScreen(
    navController: NavController,
    viewModel: ContactViewModel = viewModel()
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val snackbarHostState = remember { SnackbarHostState() }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(estado.isSuccess) {
        if (estado.isSuccess) {
            showDialog = true
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                viewModel.resetStatus()
            },
            title = { Text("Mensaje Enviado") },
            text = { Text(estado.feedbackMessage ?: "Tu mensaje se envió correctamente.") },
            confirmButton = {
                Button(onClick = {
                    showDialog = false
                    viewModel.resetStatus()
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

                Text(
                    text = "Ponte en Contacto",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Rellena el formulario o visítanos.",
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
                        val roundedShape = RoundedCornerShape(24.dp)
                        val textFieldColors = OutlinedTextFieldDefaults.colors(
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

                        // Nombre
                        OutlinedTextField(
                            value = estado.nombre,
                            onValueChange = viewModel::onNombreChange,
                            label = { Text("Nombre", color = MaterialTheme.colorScheme.primary) },
                            isError = estado.errores.nombre != null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 2.dp),
                            shape = roundedShape,
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)) },
                            enabled = !estado.isSending,
                            colors = textFieldColors
                        )
                        if (estado.errores.nombre != null) {
                            Text(
                                text = estado.errores.nombre!!,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 16.dp, bottom = 12.dp)
                            )
                        } else {
                            Spacer(modifier = Modifier.height(14.dp)) // Espacio para mantener alineación
                        }


                        // Email
                        OutlinedTextField(
                            value = estado.email,
                            onValueChange = viewModel::onEmailChange,
                            label = { Text("Correo", color = MaterialTheme.colorScheme.primary) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            isError = estado.errores.email != null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 2.dp),
                            shape = roundedShape,
                            leadingIcon = { Icon(Icons.Default.AlternateEmail, contentDescription = null, tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)) },
                            enabled = !estado.isSending,
                            colors = textFieldColors
                        )
                        if (estado.errores.email != null) {
                            Text(
                                text = estado.errores.email!!,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 16.dp, bottom = 12.dp)
                            )
                        } else {
                            Spacer(modifier = Modifier.height(14.dp))
                        }

                        // Teléfono
                        OutlinedTextField(
                            value = estado.telefono,
                            onValueChange = viewModel::onTelefonoChange,
                            label = { Text("Teléfono (opcional)", color = MaterialTheme.colorScheme.primary) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            shape = roundedShape,
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)) },
                            enabled = !estado.isSending,
                            colors = textFieldColors
                        )

                        // Asunto
                        OutlinedTextField(
                            value = estado.asunto,
                            onValueChange = viewModel::onAsuntoChange,
                            label = { Text("Asunto", color = MaterialTheme.colorScheme.primary) },
                            isError = estado.errores.asunto != null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 2.dp),
                            shape = roundedShape,
                            leadingIcon = { Icon(Icons.Default.Create, contentDescription = null, tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)) },
                            enabled = !estado.isSending,
                            colors = textFieldColors
                        )
                        if (estado.errores.asunto != null) {
                            Text(
                                text = estado.errores.asunto!!,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 16.dp, bottom = 12.dp)
                            )
                        } else {
                            Spacer(modifier = Modifier.height(14.dp))
                        }

                        // Mensaje
                        OutlinedTextField(
                            value = estado.mensajeTexto,
                            onValueChange = viewModel::onMensajeChange,
                            label = { Text("Mensaje", color = MaterialTheme.colorScheme.primary) },
                            isError = estado.errores.mensaje != null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            shape = roundedShape,
                            leadingIcon = { Icon(Icons.Default.Message, contentDescription = null, tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)) },
                            enabled = !estado.isSending,
                            colors = textFieldColors
                        )
                        if (estado.errores.mensaje != null) {
                            Text(
                                text = estado.errores.mensaje!!,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 16.dp, top = 4.dp, bottom = 12.dp)
                            )
                        } else {
                            Spacer(modifier = Modifier.height(14.dp))
                        }

                        // Botón
                        Button(
                            onClick = viewModel::sendMessage,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(28.dp),
                            enabled = !estado.isSending,
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 12.dp)
                        ) {
                            if (estado.isSending) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.5.dp)
                            } else {
                                Text("Enviar Mensaje", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }


                Spacer(Modifier.height(48.dp))
                Text(
                    text = "Nuestra Ubicación",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))

                val cafeLocation = LatLng(-33.689304, -71.213962)
                val cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(cafeLocation, 16f)
                }
                GoogleMap(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(RoundedCornerShape(32.dp)),
                    cameraPositionState = cameraPositionState
                ) {
                    Marker(
                        state = MarkerState(position = cafeLocation),
                        title = "Duoc UC: Sede Melipilla",
                        snippet = "Serrano 1105, Melipilla"
                    )
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ContactScreenPreview() {
    AppNebulaTheme {
        ContactScreen(navController = rememberNavController())
    }
}
