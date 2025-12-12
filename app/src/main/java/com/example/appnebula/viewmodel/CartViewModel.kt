package com.example.appnebula.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.appnebula.data.SessionManager
import com.example.appnebula.data.repository.CartRepository
import com.example.appnebula.model.CartItem
import com.example.appnebula.model.Product
import com.example.appnebula.network.ApiResponse
import com.example.appnebula.network.CartResponse
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.Response

data class CartUiState(
    val items: List<CartItem> = emptyList(),
    val subtotal: Double = 0.0,
    val total: Double = 0.0,
    val isLoading: Boolean = false,
    val error: String? = null
)

class CartViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    private val cartRepository = CartRepository(application.applicationContext)
    private val sessionManager = SessionManager(application.applicationContext)

    init {
        viewModelScope.launch {
            sessionManager.authTokenFlow.collect { token ->
                if (!token.isNullOrBlank()) {
                    loadCart()
                } else {
                    _uiState.value = CartUiState(error = "Inicia sesión para ver tu carrito.")
                }
            }
        }
    }

    fun loadCart() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val response = cartRepository.getCart()
                processCartResponse(response)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error de conexión: ${e.message}", isLoading = false) }
            }
        }
    }

    fun addProductToCart(product: Product) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                // 'product.id' ya es un String, así que coincide con el método del repositorio.
                val response = cartRepository.addProduct(productId = product.id, quantity = 1)
                processCartResponse(response)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error de conexión: ${e.message}", isLoading = false) }
            }
        }
    }

    fun removeProductFromCart(cartItem: CartItem) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                if (cartItem.quantity <= 1) {
                    // 'cartItem.itemId' ya es un Long, coincide con el método del repositorio.
                    val response = cartRepository.deleteItem(itemId = cartItem.itemId)
                    if (response.isSuccessful) {
                        loadCart()
                    } else {
                        _uiState.update { it.copy(error = "Error al eliminar el ítem", isLoading = false) }
                    }
                } else {
                    // ERROR 1 CORREGIDO:
                    // El método `removeProduct` del repositorio espera un Long.
                    // Convertimos el `id` del producto (que es un String) a Long antes de pasarlo.
                    val productIdAsLong = cartItem.product.id.toLongOrNull() ?: 0L
                    val response = cartRepository.removeProduct(productId = productIdAsLong, quantity = 1)
                    processCartResponse(response)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error de conexión: ${e.message}", isLoading = false) }
            }
        }
    }

    fun deleteItemFromCart(cartItem: CartItem) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val response = cartRepository.deleteItem(itemId = cartItem.itemId)
                if (response.isSuccessful && response.body()?.success == true) {
                    loadCart()
                } else {
                    val errorMsg = response.body()?.message ?: "Error desconocido al eliminar"
                    _uiState.update { it.copy(error = errorMsg, isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error de conexión: ${e.message}", isLoading = false) }
            }
        }
    }

    private fun processCartResponse(response: Response<ApiResponse<CartResponse>>) {
        if (response.isSuccessful) {
            val apiResponse = response.body()
            if (apiResponse != null && apiResponse.success) {
                apiResponse.data?.let { cartData ->
                    updateUiFromResponse(cartData)
                } ?: _uiState.update { it.copy(error = "No se recibieron datos del carrito.", isLoading = false) }
            } else {
                _uiState.update { it.copy(error = apiResponse?.message ?: "Error desconocido del servidor", isLoading = false) }
            }
        } else {
            _uiState.update { it.copy(error = "Error de red: ${response.code()}", isLoading = false) }
        }
    }

    private fun updateUiFromResponse(cartData: CartResponse) {
        val uiItems = cartData.items.map { apiItem ->
            CartItem(
                itemId = apiItem.itemId,
                product = Product(
                    // ERROR 2 CORREGIDO:
                    // No se necesita conversión. `apiItem.productId` (String) se asigna a `Product.id` (String).
                    id = apiItem.productId,
                    name = apiItem.productName,
                    // ERROR 3 CORREGIDO:
                    // No se necesita conversión. `apiItem.price` (Int) se asigna a `Product.price` (Int).
                    price = apiItem.price,
                    imageUrl = apiItem.imageUrl ?: "",
                    description = "", // Valor por defecto
                    category = ""       // Valor por defecto
                ),
                quantity = apiItem.quantity
            )
        }

        _uiState.update {
            it.copy(
                items = uiItems,
                subtotal = cartData.subtotal.toDouble(),
                total = cartData.total.toDouble(),
                isLoading = false,
                error = null
            )
        }
    }

    fun refreshCart() {
        loadCart()
    }
}

class CartViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CartViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
