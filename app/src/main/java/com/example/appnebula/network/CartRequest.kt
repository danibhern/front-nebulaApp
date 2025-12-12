package com.example.appnebula.network

import com.google.gson.annotations.SerializedName

data class AddToCartRequest(
    @SerializedName("productId") val productId: String,
    @SerializedName("quantity") val quantity: Int
)
