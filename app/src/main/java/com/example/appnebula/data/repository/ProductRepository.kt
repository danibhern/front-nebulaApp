package com.example.appnebula.data.repository

import android.content.Context // <-- 1. IMPORTA EL CONTEXT
import com.example.appnebula.model.Product
import com.example.appnebula.network.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// 2. PIDE EL CONTEXT EN EL CONSTRUCTOR
class ProductRepository(private val context: Context) {

    // 3. CORRIGE LA INICIALIZACIÓN DE apiService
    private val apiService = ApiClient.getInstance(context)

    suspend fun getAllProducts(): List<Product> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getProducts()

                if (response.isSuccessful) {
                    val apiResponse = response.body()

                    if (apiResponse != null && apiResponse.success) {
                        val productResponses = apiResponse.data ?: emptyList()

                        productResponses.map { apiProduct ->
                            Product(
                                id = apiProduct.id?.toString() ?: "-1",
                                name = apiProduct.name ?: "Nombre no disponible",
                                description = apiProduct.description ?: "",
                                price = apiProduct.price ?: 0,
                                imageUrl = apiProduct.imageUrl ?: "",
                                isFavorite = false, // Lo gestionas localmente
                                category = apiProduct.category ?: ""
                            )
                        }
                    } else {
                        // El cuerpo de la respuesta indica un error
                        emptyList()
                    }
                } else {
                    // La respuesta HTTP no fue exitosa (ej. 404, 500)
                    emptyList()
                }
            } catch (e: Exception) {
                // Error de red u otra excepción
                e.printStackTrace() // Es útil para depurar
                emptyList()
            }
        }
    }
}
