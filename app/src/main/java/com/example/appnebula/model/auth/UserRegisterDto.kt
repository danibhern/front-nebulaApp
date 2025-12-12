package com.example.appnebula.model.auth


data class UserRegisterDto(
    val name: String,
    val email: String,
    val password: String
)