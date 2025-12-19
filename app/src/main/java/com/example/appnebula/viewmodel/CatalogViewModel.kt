package com.example.appnebula.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel // <-- NECESITARÁS ESTE IMPORT PARA LA FACTORY
import androidx.lifecycle.ViewModelProvider // <-- Y ESTE TAMBIÉN
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.AndroidViewModel
import com.example.appnebula.data.repository.ProductRepository
import com.example.appnebula.model.Product
import com.example.appnebula.ui.catalog.ProductCategory
import com.example.appnebula.ui.catalog.SortOrder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CatalogUiState(
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val selectedCategory: ProductCategory? = null,
    val sortOrder: SortOrder = SortOrder.NONE,
    val products: List<Product> = emptyList()
)

class CatalogViewModel(application: Application) : AndroidViewModel(application) {

    private val productRepository = ProductRepository(application.applicationContext)
    private var allProductsFromApi: List<Product> = emptyList()

    private val _uiState = MutableStateFlow(CatalogUiState())
    val uiState: StateFlow<CatalogUiState> = _uiState.asStateFlow()

    init {
        fetchProducts()
    }

    private fun fetchProducts() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            allProductsFromApi = productRepository.getAllProducts()
            updateProductList()
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun toggleFavorite(productId: String) {
        allProductsFromApi = allProductsFromApi.map { product ->
            if (product.id == productId) {
                product.copy(isFavorite = !product.isFavorite)
            } else {
                product
            }
        }
        updateProductList()
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        updateProductList()
    }

    fun selectCategory(category: ProductCategory) {
        _uiState.update { currentState ->
            val newCategory = if (currentState.selectedCategory == category) null else category
            currentState.copy(selectedCategory = newCategory)
        }
        updateProductList()
    }

    fun setSortOrder() {
        _uiState.update { currentState ->
            val newSortOrder = if (currentState.sortOrder == SortOrder.NONE) {
                SortOrder.PRICE_ASC
            } else {
                SortOrder.NONE
            }
            currentState.copy(sortOrder = newSortOrder)
        }
        updateProductList()
    }

    private fun updateProductList() {
        val currentCategory = _uiState.value.selectedCategory
        val currentSortOrder = _uiState.value.sortOrder
        val currentQuery = _uiState.value.searchQuery

        val categoryName = when (currentCategory) {
            ProductCategory.COFFEE -> "Café"
            ProductCategory.ACCESSORIES -> "Insumos"
            null -> null
        }

        val filteredByCategory = if (categoryName != null) {
            allProductsFromApi.filter { it.category.equals(categoryName, ignoreCase = true) }
        } else {
            allProductsFromApi
        }

        val filteredBySearch = if (currentQuery.isBlank()) {
            filteredByCategory
        } else {
            filteredByCategory.filter { product ->
                product.name.contains(currentQuery, ignoreCase = true)
            }
        }

        val sortedList = when (currentSortOrder) {
            SortOrder.PRICE_ASC -> filteredBySearch.sortedBy { it.price }
            SortOrder.NONE -> filteredBySearch
        }

        _uiState.update { currentState ->
            currentState.copy(products = sortedList)
        }
    }
}
class CatalogViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CatalogViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CatalogViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
