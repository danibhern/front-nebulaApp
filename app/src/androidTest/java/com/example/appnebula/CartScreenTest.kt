package com.example.appnebula.ui.cart

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.appnebula.model.CartItem
import com.example.appnebula.model.Product
import com.example.appnebula.ui.theme.AppNebulaTheme
import com.example.appnebula.viewmodel.CartUiState
import com.example.appnebula.viewmodel.CartViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CartScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockViewModel: CartViewModel
    private val uiStateFlow = MutableStateFlow(CartUiState())

    private var onCheckoutCalled = false
    private var checkoutTotal: Double = 0.0

    private val product1 = Product(id = "1", name = "Producto 1", price = 1000, description = "Desc 1", category = "cat1", imageUrl = "url1")
    private val product2 = Product(id = "2", name = "Producto 2", price = 2000, description = "Desc 2", category = "cat2", imageUrl = "url2")

    @Before
    fun setUp() {
        mockViewModel = mockk(relaxed = true) {
            every { uiState } returns uiStateFlow
        }

        onCheckoutCalled = false
        checkoutTotal = 0.0

        composeTestRule.setContent {
            AppNebulaTheme {
                CartScreen(
                    cartViewModel = mockViewModel,
                    onCheckout = { total ->
                        onCheckoutCalled = true
                        checkoutTotal = total
                    }
                )
            }
        }
    }

    private fun setState(newState: CartUiState) {
        uiStateFlow.value = newState
    }


    @Test
    fun cuandoElCarritoEstaVacio_muestraMensajeCorrecto() {
        setState(CartUiState(items = emptyList(), error = null, isLoading = false))
        composeTestRule.onNodeWithText("Tu carrito está vacío.").assertIsDisplayed()
    }

    @Test
    fun alPulsarBotonSumar_seLlamaAFuncionDelViewModel() {
        val cartItem = CartItem(itemId = 1, product = product1, quantity = 1)
        setState(CartUiState(items = listOf(cartItem)))

        composeTestRule.onNodeWithContentDescription("Sumar").performClick()

        verify { mockViewModel.addProductToCart(cartItem.product) }
    }

    @Test
    fun alPulsarBotonRestar_seLlamaAFuncionDelViewModel() {
        val cartItem = CartItem(itemId = 1, product = product1, quantity = 2)
        setState(CartUiState(items = listOf(cartItem)))

        composeTestRule.onNodeWithContentDescription("Restar").performClick()

        verify { mockViewModel.removeProductFromCart(cartItem) }
    }

    @Test
    fun cuandoHayUnError_seMuestraElMensajeDeError() {
        val errorMessage = "No se pudo cargar el carrito"
        setState(CartUiState(error = errorMessage))
        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
    }

    @Test
    fun cuandoHayItemsEnElCarrito_seMuestranEnLaLista() {
        val cartItems = listOf(
            CartItem(itemId = 1, product = product1, quantity = 2),
            CartItem(itemId = 2, product = product2, quantity = 1)
        )
        setState(CartUiState(items = cartItems, isLoading = false))
        composeTestRule.onNodeWithText("Producto 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Producto 2").assertIsDisplayed()
        composeTestRule.onNodeWithText("2").assertIsDisplayed()
    }
}
