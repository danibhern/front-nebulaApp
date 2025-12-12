package com.example.appnebula.data

import android.content.Context
import android.util.Log
import com.example.appnebula.model.auth.UserLoginDto
import com.example.appnebula.model.auth.UserRegisterDto
import com.example.appnebula.model.auth.UserResponse
import com.example.appnebula.network.ApiClient
import java.io.IOException


class AuthRepository(private val context: Context) {
    private val apiService = ApiClient.getInstance(context)
    suspend fun login(userLoginDto: UserLoginDto): Result<UserResponse> {
        return try {
            Log.d("AuthRepository", "Iniciando login para: ${userLoginDto.email}")
            val response = apiService.login(userLoginDto)

            if (response.isSuccessful) {
                val apiResponse = response.body()
                Log.d("AuthRepository", "Respuesta API login: $apiResponse")

                if (apiResponse != null && apiResponse.success == true) {
                    val userResponse = apiResponse.data
                    if (userResponse != null) {
                        Log.d("AuthRepository", "Login exitoso: $userResponse")
                        Result.success(userResponse)
                    } else {
                        Log.e("AuthRepository", "Datos de usuario nulos en respuesta")
                        Result.failure(Exception(apiResponse.message ?: "Datos de usuario no disponibles"))
                    }
                } else {
                    val errorMsg = apiResponse?.message ?: "Error desconocido en login"
                    Log.e("AuthRepository", "Login fallido: $errorMsg")
                    Result.failure(Exception(errorMsg))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Error ${response.code()}"
                Log.e("AuthRepository", "Error HTTP login: $errorBody")
                Result.failure(Exception("Error en el login: ${response.code()} - $errorBody"))
            }
        } catch (e: IOException) {
            Log.e("AuthRepository", "Error de red en login: ${e.message}", e)
            Result.failure(Exception("No se pudo conectar al servidor. Verifica tu conexión a internet."))
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error inesperado en login: ${e.message}", e)
            Result.failure(Exception("Ocurrió un error inesperado: ${e.message}"))
        }
    }

    suspend fun register(userRegisterDto: UserRegisterDto): Result<UserResponse> {
        return try {
            Log.d("AuthRepository", "Iniciando registro para: ${userRegisterDto.email}")
            val response = apiService.register(userRegisterDto)

            if (response.isSuccessful) {
                val apiResponse = response.body()
                Log.d("AuthRepository", "Respuesta API registro: $apiResponse")

                if (apiResponse != null && apiResponse.success == true) {
                    val userResponse = apiResponse.data
                    if (userResponse != null) {
                        Log.d("AuthRepository", "Registro exitoso: $userResponse")
                        Result.success(userResponse)
                    } else {
                        Log.e("AuthRepository", "Datos de usuario nulos en respuesta")
                        Result.failure(Exception(apiResponse.message ?: "Datos de usuario no disponibles"))
                    }
                } else {
                    val errorMsg = apiResponse?.message ?: "Error desconocido en registro"
                    Log.e("AuthRepository", "Registro fallido: $errorMsg")
                    Result.failure(Exception(errorMsg))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Error ${response.code()}"
                Log.e("AuthRepository", "Error HTTP registro: $errorBody")
                Result.failure(Exception("Error en el registro: ${response.code()} - $errorBody"))
            }
        } catch (e: IOException) {
            Log.e("AuthRepository", "Error de red en registro: ${e.message}", e)
            Result.failure(Exception("No se pudo conectar al servidor. Verifica tu conexión a internet."))
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error inesperado en registro: ${e.message}", e)
            Result.failure(Exception("Ocurrió un error inesperado: ${e.message}"))
        }
    }
}