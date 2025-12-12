package com.example.appnebula.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PaymentState(
    val cardNumber: String = "",
    val cardHolderName: String = "",
    val expiryDate: String = "",
    val cvv: String = "",
    val isLoading: Boolean = false,
    val paymentMessage: String? = null
)

class PaymentViewModel : ViewModel() {

    private val _state = MutableStateFlow(PaymentState())
    val state = _state.asStateFlow()

    fun onCardNumberChange(value: String) {
        if (value.all { it.isDigit() } && value.length <= 16) {
            _state.update { it.copy(cardNumber = value) }
        }
    }

    fun onNameChange(value: String) {
        _state.update { it.copy(cardHolderName = value) }
    }

    fun onExpiryChange(value: String) {
        if (value.all { it.isDigit() || it == '/' } && value.length <= 5) {
            _state.update { it.copy(expiryDate = value) }
        }
    }

    fun onCvvChange(value: String) {
        if (value.all { it.isDigit() } && value.length <= 3) {
            _state.update { it.copy(cvv = value) }
        }
    }

    fun onPayClicked() {
        if (validateForm()) {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true, paymentMessage = null) }
                delay(2500)
                _state.update { it.copy(
                    isLoading = false,
                    paymentMessage = "¡Pago realizado con éxito!"
                )}
            }
        } else {
            _state.update { it.copy(paymentMessage = "Por favor, revisa los campos.") }
        }
    }

    private fun validateForm(): Boolean {
        val s = _state.value
        return s.cardNumber.length == 16 &&
                s.cardHolderName.isNotBlank() &&
                s.expiryDate.length == 5 &&
                s.cvv.length == 3
    }
}