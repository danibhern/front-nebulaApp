package com.example.appnebula.data.repository

import android.content.Context
import com.example.appnebula.network.AddToCartRequest
import com.example.appnebula.network.ApiClient
import com.example.appnebula.network.ApiResponse
import com.example.appnebula.network.CartResponse
import retrofit2.Response

class CartRepository(private val context: Context) {

    private val apiService = ApiClient.getInstance(context)
    private val tag = "CartRepository"

    suspend fun getCart(): Response<ApiResponse<CartResponse>> {
        return apiService.getCart()
    }

    suspend fun addProduct(productId: String, quantity: Int): Response<ApiResponse<CartResponse>> {
        val request = AddToCartRequest(productId = productId, quantity = quantity)
        return apiService.addProductToCart(request)
    }

    suspend fun removeProduct(productId: Long, quantity: Int): Response<ApiResponse<CartResponse>> {
        return apiService.removeProductFromCart(productId, quantity)
    }

    suspend fun deleteItem(itemId: Long): Response<ApiResponse<Void>> {
        return apiService.deleteItemFromCart(itemId)
    }
}
