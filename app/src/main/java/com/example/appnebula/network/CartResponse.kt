package com.example.appnebula.network

import com.google.gson.annotations.SerializedName

data class CartItemResponse(
    @SerializedName("id") val itemId: Long,
    @SerializedName("productId") val productId: String,
    @SerializedName("productName") val productName: String,
    @SerializedName("price") val price: Int,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("imageUrl") val imageUrl: String?
)


data class CartResponse(
    @SerializedName("id") val cartId: Long,
    @SerializedName("items") val items: List<CartItemResponse>,
    @SerializedName("subtotal") val subtotal: Int,
    @SerializedName("total") val total: Int
)
