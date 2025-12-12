
package com.example.appnebula.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appnebula.data.repository.ReservaRepository
import com.example.appnebula.data.SessionManager
import com.example.appnebula.model.reserva.ReservaDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ReservaErrores(
    val nombre: String? = null,
    val email: String? = null,
    val fecha: String? = null,
    val hora: String? = null,
    val personas: String? = null
)

data class ReservaEstado(
    val nombre: String = "",
    val email: String = "",
    val fecha: String = "",
    val hora: String = "",
    val personas: String = "",
    val errores: ReservaErrores = ReservaErrores(),
    val mensaje: String? = null,
    val isLoading: Boolean = false,
    val reservaSuccess: Boolean = false
)

class ReservaViewModel(
    private val reservaRepository: ReservaRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _estado = MutableStateFlow(ReservaEstado())
    val estado = _estado.asStateFlow()

    init {
        viewModelScope.launch {
            val userEmail = sessionManager.userEmailFlow.first()
            if (userEmail != null) {
                _estado.update { it.copy(email = userEmail) }
            }
        }
    }

    fun onNombreChange(value: String) = _estado.update { it.copy(nombre = value) }
    fun onEmailChange(value: String) = _estado.update { it.copy(email = value) }
    fun onFechaChange(value: String) = _estado.update { it.copy(fecha = value) }
    fun onHoraChange(value: String) = _estado.update { it.copy(hora = value) }
    fun onPersonasChange(value: String) = _estado.update { it.copy(personas = value) }

    private fun validarFormulario(): Boolean {
        val actual = _estado.value
        val errores = ReservaErrores(
            nombre = if (actual.nombre.isBlank()) "Debe completar con su nombre" else null,
            email = if (!actual.email.contains("@")) "Correo inválido" else null,
            fecha = if (actual.fecha.isBlank()) "Debe seleccionar fecha" else null,
            hora = if (actual.hora.isBlank()) "Debe Seleccionar una hora" else null,
            personas = if (actual.personas.isBlank() || actual.personas.toIntOrNull() == null) "Debe ingresar un número válido de personas" else null
        )
        _estado.update { it.copy(errores = errores) }
        return listOfNotNull(errores.nombre, errores.email, errores.fecha, errores.hora, errores.personas).isEmpty()
    }

    fun enviarReserva() {
        if (!validarFormulario()) return

        _estado.update { it.copy(isLoading = true, mensaje = null) }

        viewModelScope.launch {            val estadoActual = _estado.value
            val userId = sessionManager.userIdFlow.first()?.toLongOrNull()

            val reservaRequest = ReservaDto(
                nombreCliente = estadoActual.nombre.trim(),
                emailCliente = estadoActual.email.trim(),
                fecha = estadoActual.fecha,
                hora = estadoActual.hora,
                cantidadPersonas = estadoActual.personas.toInt(),
                userId = userId
            )

            val response = reservaRepository.crearReserva(reservaRequest)

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!

                if (apiResponse.success) {
                    _estado.update {
                        it.copy(
                            isLoading = false,
                            reservaSuccess = true,
                            mensaje = apiResponse.message ?: "Reserva realizada con éxito"
                        )
                    }
                } else {
                    _estado.update {
                        it.copy(
                            isLoading = false,
                            mensaje = apiResponse.message ?: "Error al procesar la reserva."
                        )
                    }
                }
            } else {
                _estado.update {
                    it.copy(
                        isLoading = false,
                        mensaje = "Error: No se pudo realizar la reserva. Inténtalo de nuevo."
                    )
                }
            }
        }
    }

    fun limpiarMensaje() = _estado.update { it.copy(mensaje = null) }
    fun resetReservaStatus() = _estado.update { it.copy(reservaSuccess = false, nombre = "", fecha = "", hora = "", personas = "") }
}
