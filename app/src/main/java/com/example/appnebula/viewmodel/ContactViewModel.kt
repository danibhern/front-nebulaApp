package com.example.appnebula.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appnebula.data.SessionManager
import com.example.appnebula.data.repository.ContactRepository
import com.example.appnebula.model.contacto.ContactDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ContactErrores(
    val nombre: String? = null,
    val email: String? = null,
    val telefono: String? = null,
    val asunto: String? = null,
    val mensaje: String? = null
)

data class ContactEstado(
    val nombre: String = "",
    val email: String = "",
    val telefono: String = "",
    val asunto: String = "",
    val mensajeTexto: String = "",
    val errores: ContactErrores = ContactErrores(),
    val isSuccess: Boolean = false,
    val isSending: Boolean = false,
    val feedbackMessage: String? = null
)

open class ContactViewModel(
    private val repository: ContactRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _estado = MutableStateFlow(ContactEstado())
    open val estado = _estado.asStateFlow()

    init {
        viewModelScope.launch {
            val userEmail = sessionManager.userEmailFlow.first()
            if (userEmail != null) {
                _estado.update { it.copy(email = userEmail) }
            }
        }
    }

    open fun onNombreChange(value: String) = _estado.update { it.copy(nombre = value) }
    open fun onEmailChange(value: String) = _estado.update { it.copy(email = value) }
    open fun onTelefonoChange(value: String) = _estado.update { it.copy(telefono = value) }
    open fun onAsuntoChange(value: String) = _estado.update { it.copy(asunto = value) }
    open fun onMensajeChange(value: String) = _estado.update { it.copy(mensajeTexto = value) }

    private fun validarFormulario(): Boolean {
        val estadoActual = _estado.value
        val errores = ContactErrores(
            nombre = if (estadoActual.nombre.isBlank()) "Campo obligatorio" else null,
            email = if (!android.util.Patterns.EMAIL_ADDRESS.matcher(estadoActual.email).matches()) "Correo inválido" else null,
            asunto = if (estadoActual.asunto.isBlank()) "Campo obligatorio" else null,
            mensaje = if (estadoActual.mensajeTexto.isBlank()) "Campo obligatorio" else null
        )
        _estado.update { it.copy(errores = errores) }
        return listOfNotNull(errores.nombre, errores.email, errores.asunto, errores.mensaje).isEmpty()
    }

    open fun sendMessage() {
        if (!validarFormulario()) return

        viewModelScope.launch {
            _estado.update { it.copy(isSending = true, feedbackMessage = null) }

            val form = ContactDto(
                nombre = _estado.value.nombre,
                email = _estado.value.email,
                telefono = _estado.value.telefono.ifBlank { null },
                asunto = _estado.value.asunto,
                mensaje = _estado.value.mensajeTexto
            )

            val result = repository.sendMessage(form)

            result.fold(
                onSuccess = {
                    _estado.update {
                        it.copy(
                            isSending = false,
                            isSuccess = true,
                            feedbackMessage = "Mensaje enviado con éxito"
                        )
                    }
                },
                onFailure = { error ->
                    _estado.update {
                        it.copy(
                            isSending = false,
                            isSuccess = false,
                            feedbackMessage = error.message ?: "Error al enviar el mensaje. Inténtalo de nuevo."
                        )
                    }
                }
            )
        }
    }

    open fun resetStatus() {
        _estado.update {
            it.copy(
                isSuccess = false,
                isSending = false,
                feedbackMessage = null,
                nombre = "",
                telefono = "",
                asunto = "",
                mensajeTexto = ""
            )
        }
    }
}