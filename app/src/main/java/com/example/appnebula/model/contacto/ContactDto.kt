package com.example.appnebula.model.contacto

data class ContactDto(
    val nombre: String,
    val email: String,
    val telefono: String?,
    val asunto: String,
    val mensaje: String
)
