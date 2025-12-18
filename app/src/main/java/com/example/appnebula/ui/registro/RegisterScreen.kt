package com.example.appnebula.ui.registro

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.appnebula.viewmodel.UserViewModel
import kotlinx.coroutines.delay

@Composable
fun RegisterScreen(navController: NavController, viewModel: UserViewModel) {
    val context = LocalContext.current


    // 1. Estado para controlar si mostramos la cámara en la UI o el formulario
    var showCamera by remember { mutableStateOf(false) }
    // Este estado guardará la URI de la imagen seleccionada para mostrarla en el <Image>
    var imageUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    // --- FIN DE CAMBIOS PARA LA CÁMARA ---


    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    // --- Asignamos la URI al ViewModel cuando cambie ---
    LaunchedEffect(imageUri) {
        imageUri?.let { viewModel.onFotoChange(it) }
    }

    LaunchedEffect(estado.mensaje) {
        estado.mensaje?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.limpiarMensaje()
        }
    }

    LaunchedEffect(estado.loginSuccess) {
        if (estado.loginSuccess) {
            snackbarHostState.showSnackbar("¡Registro exitoso! Redirigiendo...")
            delay(1500)
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
            }
            viewModel.resetLoginStatus()
        }
    }

    // --- LÓGICA DE INTERCAMBIO DE UI ---
    if (showCamera) {
        // Si 'showCamera' es true, mostramos nuestra vista de cámara personalizada
        CameraView(
            onImageCaptured = { uri ->
                // Cuando la foto se captura, actualizamos nuestro estado y ocultamos la cámara
                imageUri = uri
                showCamera = false
            },
            onError = { error ->
                // Manejamos cualquier error de la cámara
                viewModel.mostrarError(error)
                showCamera = false
            }
        )
    } else {
        // Si 'showCamera' es false, mostramos el formulario de registro (tu código original)
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
                snackbarHost = { SnackbarHost(snackbarHostState) },
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

                    // Foto de perfil
                    if (imageUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(model = imageUri),
                            contentDescription = "Foto de perfil capturada",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                        )
                    } else {
                        // Un placeholder si no hay imagen
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    FeatureThatRequiresCameraPermission(
                        onPermissionGranted = {
                            // Cuando el permiso se concede, en lugar de lanzar una cámara externa,
                            // simplemente activamos nuestro estado para mostrar la cámara en la app.
                            showCamera = true
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Título (sin cambios)
                    Text(
                        text = "Crear tu cuenta",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Registrate con tus datos",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(48.dp))

                    // Formulario en card flotante (sin cambios)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(32.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.onSurface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 20.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(36.dp)
                        ) {
                            // Nombre
                            OutlinedTextField(
                                value = estado.name,
                                onValueChange = { viewModel.onNombreChange(it) },
                                label = {
                                    Text(
                                        "Nombre",
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                },
                                singleLine = true,
                                isError = estado.errores.name != null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp)
                                    .testTag("NombreTextField"),
                                shape = RoundedCornerShape(24.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = MaterialTheme.colorScheme.primary,
                                    unfocusedTextColor = MaterialTheme.colorScheme.primary,
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                    errorBorderColor = Color.Red,
                                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                                    unfocusedLabelColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                )
                            )

                            // Email
                            OutlinedTextField(
                                value = estado.email,
                                onValueChange = { viewModel.onCorreoChange(it) },
                                label = {
                                    Text(
                                        "Email",
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                },
                                singleLine = true,
                                isError = estado.errores.email != null,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp)
                                    .testTag("EmailTextField"),
                                shape = RoundedCornerShape(24.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = MaterialTheme.colorScheme.primary,
                                    unfocusedTextColor = MaterialTheme.colorScheme.primary,
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                    errorBorderColor = Color.Red,
                                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                                    unfocusedLabelColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                )
                            )

                            if (estado.errores.email != null) {
                                Text(
                                    text = estado.errores.email ?: "",
                                    color = Color.Red,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }

                            // Contraseña
                            OutlinedTextField(
                                value = estado.password,
                                onValueChange = { viewModel.onClaveChange(it) },
                                label = {
                                    Text(
                                        "Contraseña",
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                },
                                singleLine = true,
                                isError = estado.errores.password != null,
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                trailingIcon = {
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(
                                            imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                        )
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 24.dp)
                                    .testTag("PasswordTextField"),
                                shape = RoundedCornerShape(24.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = MaterialTheme.colorScheme.primary,
                                    unfocusedTextColor = MaterialTheme.colorScheme.primary,
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                    errorBorderColor = Color.Red,
                                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                                    unfocusedLabelColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                )
                            )
                            if (estado.errores.password != null) {
                                Text(
                                    text = estado.errores.password ?: "",
                                    color = Color.Red,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }

                            // Términos
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.onTerminosChange(!estado.aceptaTerminos) }
                                    .testTag("CheckboxTerminos")
                                    .padding(vertical = 8.dp)
                            ) {
                                Checkbox(
                                    checked = estado.aceptaTerminos,
                                    onCheckedChange = { viewModel.onTerminosChange(it) },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = MaterialTheme.colorScheme.primary,
                                        uncheckedColor = MaterialTheme.colorScheme.primary
                                    )
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Acepto los términos y condiciones",
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Botón de registro
                    Button(
                        onClick = { viewModel.registrarUsuario() },
                                modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        shape = RoundedCornerShape(24.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        if (estado.isLoading) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 3.dp
                            )
                        } else {
                            Text(
                                "Registrarme",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}
