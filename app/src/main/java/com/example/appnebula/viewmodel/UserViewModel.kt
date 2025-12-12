package com.example.appnebula.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appnebula.data.AuthRepository
import com.example.appnebula.data.SessionManager
import com.example.appnebula.model.auth.UserLoginDto
import com.example.appnebula.model.auth.UserRegisterDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UserState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val aceptaTerminos: Boolean = false,
    val fotoUri: Uri? = null,
    val isLoading: Boolean = false,
    val loginSuccess: Boolean = false,
    val mensaje: String? = null,
    val errores: UserErrors = UserErrors()
) {
    data class UserErrors(
        val name: String? = null,
        val email: String? = null,
        val password: String? = null
    )
}

class UserViewModel(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _estado = MutableStateFlow(UserState())
    val estado: StateFlow<UserState> = _estado.asStateFlow()

    fun onCorreoChange(correo: String) = _estado.update { it.copy(email = correo) }
    fun onClaveChange(clave: String) = _estado.update { it.copy(password = clave) }
    fun onNombreChange(nombre: String) = _estado.update { it.copy(name = nombre) }
    fun onTerminosChange(acepta: Boolean) = _estado.update { it.copy(aceptaTerminos = acepta) }
    fun setFoto(uri: Uri) = _estado.update { it.copy(fotoUri = uri) }
    fun limpiarMensaje() = _estado.update { it.copy(mensaje = null) }
    fun resetLoginStatus() = _estado.update { it.copy(loginSuccess = false) }

    fun registrarUsuario() {
        if (!validarRegistro()) return

        viewModelScope.launch {
            _estado.update { it.copy(isLoading = true, mensaje = null) }

            val nombreLimpio = estado.value.name.trim()
            val emailLimpio = estado.value.email.trim().lowercase()

            val userDto = UserRegisterDto(
                name = nombreLimpio,
                email = emailLimpio,
                password = estado.value.password
            )

            authRepository.register(userDto)
                .onSuccess { userResponse ->
                    if (userResponse?.token != null && userResponse.id != null && userResponse.email != null) {
                        sessionManager.saveSession(
                            userId = userResponse.id.toString(),
                            userEmail = userResponse.email,
                            token = userResponse.token
                        )
                        _estado.update { it.copy(loginSuccess = true, isLoading = false) }
                    } else {
                        _estado.update { it.copy(mensaje = "Respuesta inesperada del servidor.", isLoading = false) }
                    }
                }
                .onFailure { error ->
                    Log.e("UserViewModel", "Error de registro: ${error.message}")
                    _estado.update {
                        it.copy(
                            mensaje = "Error en el registro. El usuario puede que ya exista.",
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun iniciarSesion() {
        if (!validarLogin()) return

        viewModelScope.launch {
            _estado.update { it.copy(isLoading = true, mensaje = null) }
            val emailLimpio = estado.value.email.trim().lowercase()

            val userDto = UserLoginDto(
                email = emailLimpio,
                password = estado.value.password
            )

            authRepository.login(userDto)
                .onSuccess { userResponse ->
                    if (userResponse?.token != null && userResponse.id != null && userResponse.email != null) {
                        sessionManager.saveSession(
                            userId = userResponse.id.toString(),
                            userEmail = userResponse.email,
                            token = userResponse.token
                        )
                        _estado.update { it.copy(loginSuccess = true, isLoading = false) }
                    } else {
                        _estado.update { it.copy(mensaje = "Email o contraseña incorrectos.", isLoading = false) }
                    }
                }
                .onFailure { error ->
                    Log.e("UserViewModel", "Error de inicio de sesión: ${error.message}")
                    _estado.update {
                        it.copy(
                            mensaje = "Email o contraseña incorrectos.",
                            isLoading = false
                        )
                    }
                }
        }
    }

    private fun validarRegistro(): Boolean {
        //1. Limpia y normaliza los datos ANTES de usarlos.
        val emailLimpio = estado.value.email.trim().lowercase()
        val nombreLimpio = estado.value.name.trim()

        // 2. Realiza la validación sobre los datos ya limpios.
        val errores = UserState.UserErrors(
            name = if (nombreLimpio.isBlank()) "El nombre no puede estar vacío" else null,
            // 3. Validación de email más robusta.
            email = if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailLimpio).matches()) {
                "El formato del correo es inválido"
            } else null,
            password = if (estado.value.password.length < 6) "La contraseña debe tener al menos 6 caracteres" else null
        )

        _estado.update { it.copy(errores = errores) }

        if (!estado.value.aceptaTerminos) {
            _estado.update { it.copy(mensaje = "Debe aceptar los términos y condiciones") }
            return false
        }

        // 4. Comprueba que no haya errores de validación.
        return errores.name == null && errores.email == null && errores.password == null
    }

    private fun validarLogin(): Boolean {
        return estado.value.email.isNotBlank() && estado.value.password.isNotBlank()
    }
}
