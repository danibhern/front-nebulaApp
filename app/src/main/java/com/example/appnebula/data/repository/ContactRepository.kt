package com.example.appnebula.data.repository

import android.content.Context
import android.util.Log
import com.example.appnebula.model.contacto.ContactDto
import com.example.appnebula.network.ApiClient
import java.io.IOException

class ContactRepository(private val context: Context) {
    private val apiService = ApiClient.getInstance(context)

    suspend fun sendMessage(form: ContactDto): Result<Unit> {
        return try {
            Log.d("ContactRepository", "Enviando mensaje de contacto")
            val response = apiService.enviarContacto(form)

            if (response.isSuccessful) {
                Log.d("ContactRepository", "Mensaje enviado exitosamente")
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Sin mensaje de error"
                Log.e("ContactRepository", "Error HTTP ${response.code()}: $errorBody")
                Result.failure(
                    when (response.code()) {
                        400 -> Exception("Datos inválidos")
                        500 -> Exception("Error del servidor")
                        else -> Exception("Error ${response.code()}")
                    }
                )
            }
        } catch (e: IOException) {
            Log.e("ContactRepository", "Error de red: ${e.message}", e)
            Result.failure(Exception("Sin conexión a internet"))
        } catch (e: Exception) {
            Log.e("ContactRepository", "Error inesperado: ${e.message}", e)
            Result.failure(Exception("Error inesperado"))
        }
    }
}
