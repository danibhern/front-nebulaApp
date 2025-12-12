package com.example.appnebula.model.auth

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("token") val token: String? = null
)