package com.example.appnebula.model.reserva

data class ReservaDto(
    val nombreCliente: String,
    val emailCliente: String,
    val fecha: String,
    val hora: String,
    val cantidadPersonas: Int,
    val userId: Long?
)
