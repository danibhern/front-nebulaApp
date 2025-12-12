package com.example.appnebula.network

import com.google.gson.annotations.SerializedName


data class ProductResponse(
    @SerializedName("id")
    val id: Long?,

    @SerializedName("name")
    val name: String?,

    @SerializedName("price")
    val price: Int?,

    @SerializedName("description")
    val description: String?,

    @SerializedName("imageUrl")
    val imageUrl: String?,

    @SerializedName("category")
    val category: String?
)
