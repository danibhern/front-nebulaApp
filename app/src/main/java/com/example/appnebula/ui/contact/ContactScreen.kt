package com.example.appnebula.ui.contact

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.appnebula.R
import com.example.appnebula.viewmodel.ContactViewModel

@Composable
fun ContactScreen(
    navController: NavController,
    viewModel: ContactViewModel = hiltViewModel()
) {
    val estado by viewModel.estado.collectAsState()
    val focusManager = LocalFocusManager.current
    val roundedShape = RoundedCornerShape(24.dp)

    // DIÁLOGO DE ÉXITO
    if (estado.isSuccess) {
        AlertDialog(
            onDismissRequest = { viewModel.resetStatus() },
            title = { Text("¡Mensaje Enviado!") },
            text = { Text(estado.feedbackMessage ?: "Gracias por contactarnos. Te responderemos pronto.") },
            confirmButton = {
                Button(onClick = {
                    viewModel.resetStatus() // Limpia el estado del VM
                    navController.popBackStack() // Vuelve a la pantalla anterior
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
                // 🌌 IMAGEN NEBULA
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
                    text = "Contacta con nosotros",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Envíanos tu consulta",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(48.dp))

                // FORMULARIO EN CARD FLOTANTE
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(36.dp)
                    ) {
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

                        // NOMBRE
                        OutlinedTextField(
                            value = estado.nombre,
                            onValueChange = viewModel::onNombreChange,
                            label = { Text("Nombre", color = MaterialTheme.colorScheme.primary) },
                            isError = estado.errores.nombre != null,
                            modifier = Modifier.fillMaxWidth().testTag("ContactNombreTextField"),
                            shape = roundedShape,
                            leadingIcon = { Icon(Icons.Default.Person, null, tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)) },
                            enabled = !estado.isSending,
                            colors = textFieldColors,
                            singleLine = true
                        )
                        estado.errores.nombre?.let {
                            Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 8.dp, top = 2.dp, bottom = 8.dp))
                        }

                        // EMAIL
                        OutlinedTextField(
                            value = estado.email,
                            onValueChange = viewModel::onEmailChange,
                            label = { Text("Email", color = MaterialTheme.colorScheme.primary) },
                            isError = estado.errores.email != null,
                            modifier = Modifier.fillMaxWidth().testTag("ContactEmailTextField"),
                            shape = roundedShape,
                            leadingIcon = { Icon(Icons.Default.Email, null, tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)) },
                            enabled = !estado.isSending,
                            colors = textFieldColors,
                            singleLine = true
                        )
                        estado.errores.email?.let {
                            Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 8.dp, top = 2.dp, bottom = 8.dp))
                        }

                        // TELÉFONO
                        OutlinedTextField(
                            value = estado.telefono,
                            onValueChange = viewModel::onTelefonoChange,
                            label = { Text("Teléfono (Opcional)", color = MaterialTheme.colorScheme.primary) },
                            modifier = Modifier.fillMaxWidth().testTag("ContactPhoneTextField"),
                            shape = roundedShape,
                            leadingIcon = { Icon(Icons.Default.Phone, null, tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)) },
                            enabled = !estado.isSending,
                            colors = textFieldColors,
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // ASUNTO
                        OutlinedTextField(
                            value = estado.asunto,
                            onValueChange = viewModel::onAsuntoChange,
                            label = { Text("Asunto", color = MaterialTheme.colorScheme.primary) },
                            isError = estado.errores.asunto != null,
                            modifier = Modifier.fillMaxWidth().testTag("ContactSubjectTextField"),
                            shape = roundedShape,
                            leadingIcon = { Icon(Icons.Default.Subject, null, tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)) },
                            enabled = !estado.isSending,
                            colors = textFieldColors,
                            singleLine = true
                        )
                        estado.errores.asunto?.let {
                            Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 8.dp, top = 2.dp, bottom = 8.dp))
                        }

                        // MENSAJE
                        OutlinedTextField(
                            value = estado.mensajeTexto,
                            onValueChange = viewModel::onMensajeChange, // Usa 'viewModel::onMensajeChange'
                            label = { Text("Mensaje", color = MaterialTheme.colorScheme.primary) },
                            isError = estado.errores.mensaje != null, // Usa 'estado.errores.mensaje'
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .testTag("ContactMessageTextField"),
                            shape = roundedShape,
                            leadingIcon = { Icon(Icons.Default.Message, null, tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)) },
                            enabled = !estado.isSending,
                            colors = textFieldColors,
                            maxLines = 4
                        )
                        estado.errores.mensaje?.let { // Usa 'estado.errores.mensaje'
                            Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 8.dp, top = 2.dp, bottom = 8.dp))
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // BOTÓN
                        Button(
                            onClick = {
                                focusManager.clearFocus()
                                viewModel.sendMessage()
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(28.dp),
                            enabled = !estado.isSending,
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 12.dp)
                        ) {
                            if (estado.isSending) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.5.dp
                                )
                            } else {
                                Text(
                                    "Enviar Mensaje",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        if (estado.feedbackMessage != null && !estado.isSuccess) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = estado.feedbackMessage!!,
                                color = Color.Red,
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
