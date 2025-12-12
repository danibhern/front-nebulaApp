package com.example.appnebula.model.reserva

data class ReservaResponse(
    val id: Long,
    val nombreCliente: String,
    val fecha: String,
    val hora: String,
    val mensaje: String
)
