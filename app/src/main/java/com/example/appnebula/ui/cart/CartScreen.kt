package com.example.appnebula.ui.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.appnebula.R
import com.example.appnebula.model.CartItem
import com.example.appnebula.model.Product
import com.example.appnebula.ui.catalog.formatPrice
import com.example.appnebula.ui.theme.AppNebulaTheme
import com.example.appnebula.viewmodel.CartUiState
import com.example.appnebula.viewmodel.CartViewModel

@Composable
fun CartScreen(
    cartViewModel: CartViewModel,
    onCheckout: (Double) -> Unit
) {
    val uiState by cartViewModel.uiState.collectAsStateWithLifecycle()

    CartScreenContent(
        uiState = uiState,
        onRemoveProduct = { cartItem -> cartViewModel.removeProductFromCart(cartItem) },
        onAddProduct = { cartItem -> cartViewModel.addProductToCart(cartItem.product) },
        onDeleteItem = { cartItem -> cartViewModel.deleteItemFromCart(cartItem) },
        onCheckout = { onCheckout(uiState.total) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CartScreenContent(
    uiState: CartUiState,
    onRemoveProduct: (CartItem) -> Unit,
    onAddProduct: (CartItem) -> Unit,
    onDeleteItem: (CartItem) -> Unit,
    onCheckout: () -> Unit
) {
    Scaffold(
        bottomBar = {
            if (uiState.items.isNotEmpty()) {
                CartSummary(
                    subtotal = uiState.subtotal,
                    shippingMessage = if (uiState.subtotal >= 50000) "Gratis" else formatPrice(5000), // Ejemplo
                    total = uiState.total,
                    onCheckout = onCheckout
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Text(
                text = "Mi Carrito",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 24.dp, bottom = 16.dp),
            )

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.items.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.error ?: "Tu carrito está vacío.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.items, key = { it.itemId }) { cartItem ->
                        CartItemRow(
                            cartItem = cartItem,
                            onRemove = { onRemoveProduct(cartItem) },
                            onAdd = { onAddProduct(cartItem) },
                            onDelete = { onDeleteItem(cartItem) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemRow(
    cartItem: CartItem,
    onRemove: () -> Unit,
    onAdd: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Card(
                modifier = Modifier
                    .size(90.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.5f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(cartItem.product.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = cartItem.product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(70.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = cartItem.product.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onTertiary,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            "Eliminar",
                            tint = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.7f)
                        )
                    }
                }
                cartItem.product.description?.let { description ->
                    if (description.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = description,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.7f),
                            maxLines = 1
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        IconButton(
                            onClick = onRemove,
                            modifier = Modifier
                                .size(35.dp)
                                .background(MaterialTheme.colorScheme.background, CircleShape)
                                .border(1.dp, MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(Icons.Default.Remove, "Restar", tint = MaterialTheme.colorScheme.onBackground, modifier = Modifier.size(12.dp))
                        }
                        Text(
                            text = "${cartItem.quantity}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onTertiary
                        )
                        IconButton(
                            onClick = onAdd,
                            modifier = Modifier
                                .size(35.dp)
                                .background(MaterialTheme.colorScheme.primary, CircleShape)
                        ) {
                            Icon(Icons.Default.Add, "Sumar", tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(12.dp))
                        }
                    }
                    Text(
                        text = formatPrice((cartItem.product.price * cartItem.quantity)),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onTertiary
                    )
                }
            }
        }
    }
}

@Composable
fun CartSummary(
    subtotal: Double,
    shippingMessage: String,
    total: Double,
    onCheckout: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Subtotal:",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = formatPrice(subtotal.toInt()),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Envío:",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = shippingMessage,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (shippingMessage == "Gratis") Color(0xFF4E4475) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Divider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total:",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    // CORREGIDO: Se convierte el Double a Int
                    text = formatPrice(total.toInt()),
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onCheckout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.add_cart),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Finalizar Compra",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Carrito con Items")
@Composable
fun PreviewCartScreenWithItems() {
    val fakeProduct = Product(
        id = "1",
        name = "Café de Montaña",
        price = 7000,
        imageUrl = "",
        description = "Un café robusto y aromático.", // Usa 'description'
        category = ""
    )
    val fakeSubtotal = 35000.0
    val fakeItems = listOf(CartItem(itemId = 1, product = fakeProduct, quantity = 5))
    AppNebulaTheme {
        val fakeState = CartUiState(
            items = fakeItems,
            subtotal = fakeSubtotal,
            total = fakeSubtotal,
            isLoading = false,
            error = null
        )
        CartScreenContent(
            uiState = fakeState,
            onRemoveProduct = {},
            onAddProduct = {},
            onDeleteItem = {},
            onCheckout = {}
        )
    }
}

@Preview(showBackground = true, name = "Carrito Vacío")
@Composable
fun PreviewCartScreenEmpty() {
    AppNebulaTheme {
        CartScreenContent(
            uiState = CartUiState(items = emptyList(), isLoading = false),
            onRemoveProduct = {},
            onAddProduct = {},
            onDeleteItem = {},
            onCheckout = {}
        )
    }
}
