package com.example.appnebula.network

import com.example.appnebula.model.auth.UserLoginDto
import com.example.appnebula.model.auth.UserRegisterDto
import com.example.appnebula.model.auth.UserResponse
import com.example.appnebula.model.contacto.ContactDto
import com.example.appnebula.model.reserva.ReservaDto
import com.example.appnebula.model.reserva.ReservaResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("users/register")
    suspend fun register(@Body userRegisterDto: UserRegisterDto): Response<ApiResponse<UserResponse>>

    @POST("users/login")
    suspend fun login(@Body userLoginDto: UserLoginDto): Response<ApiResponse<UserResponse>>

    @GET("products")
    suspend fun getProducts(): Response<ApiResponse<List<ProductResponse>>>

    @GET("api/cart")
    suspend fun getCart(): Response<ApiResponse<CartResponse>>

    @POST("api/cart/add")
    suspend fun addProductToCart(
        @Body request: AddToCartRequest
    ): Response<ApiResponse<CartResponse>>

    @PATCH("api/cart/remove")
    suspend fun removeProductFromCart(
        @Query("productId") productId: Long,
        @Query("quantity") quantity: Int
    ): Response<ApiResponse<CartResponse>>

    @DELETE("api/cart/item/{itemId}")
    suspend fun deleteItemFromCart(
        @Path("itemId") itemId: Long
    ): Response<ApiResponse<Void>>

    @POST("reservas")
    suspend fun crearReserva(@Body reservaDto: ReservaDto): Response<ApiResponse<ReservaResponse>>

    @POST("contact")
    suspend fun enviarContacto(@Body contactDto: ContactDto): Response<ApiResponse<Void>>
}
