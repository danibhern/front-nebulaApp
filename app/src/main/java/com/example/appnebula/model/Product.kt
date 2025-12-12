package com.example.appnebula.model

data class Product(
    val id: String,
    val name: String,
    val description: String?,
    val price: Int,
    val imageUrl: String,
    val isFavorite: Boolean = false,
    val category: String?
)