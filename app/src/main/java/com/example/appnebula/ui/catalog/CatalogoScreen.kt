package com.example.appnebula.ui.catalog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.appnebula.R
import com.example.appnebula.model.Product
import com.example.appnebula.ui.theme.AppNebulaTheme
import com.example.appnebula.viewmodel.CatalogUiState
import com.example.appnebula.viewmodel.CatalogViewModel
import com.example.appnebula.viewmodel.CartViewModel
import java.text.NumberFormat
import java.util.Locale

enum class ProductCategory { COFFEE, ACCESSORIES }
enum class SortOrder { NONE, PRICE_ASC }

fun formatPrice(price: Int): String {
    val clLocale = Locale.Builder().setLanguage("es").setRegion("CL").build()
    val format = NumberFormat.getCurrencyInstance(clLocale)
    format.maximumFractionDigits = 0
    return format.format(price)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    catalogViewModel: CatalogViewModel = viewModel(),
    cartViewModel: CartViewModel,
    onCartClick: () -> Unit
) {
    val uiState by catalogViewModel.uiState.collectAsStateWithLifecycle()
    val cartState by cartViewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nebula Café") },
                actions = {
                    // Icono del carrito con badge
                    IconButton(onClick = onCartClick) {
                        Box {
                            Icon(
                                painter = painterResource(id = R.drawable.add_cart),
                                contentDescription = "Carrito de compras",
                                tint = MaterialTheme.colorScheme.onBackground
                            )

                            // Badge con cantidad
                            if (cartState.items.isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .offset(x = 8.dp, y = (-4).dp)
                                        .size(18.dp)
                                        .background(Color.Red, CircleShape)
                                ) {
                                    Text(
                                        text = "${cartState.items.size}",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }
                            }
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        CatalogScreenContent(
            uiState = uiState,
            onSelectCategory = { catalogViewModel.selectCategory(it) },
            onSortClick = { catalogViewModel.setSortOrder() },
            onSearchQueryChanged = { catalogViewModel.onSearchQueryChanged(it) },
            onToggleFavorite = { productId -> catalogViewModel.toggleFavorite(productId) },
            onAddToCartClick = { product -> cartViewModel.addProductToCart(product) },
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CatalogScreenContent(
    uiState: CatalogUiState,
    onSelectCategory: (ProductCategory) -> Unit,
    onSortClick: () -> Unit,
    onAddToCartClick: (Product) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Text(
            text = "Nuestro Catálogo",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp, bottom = 16.dp),
            textAlign = TextAlign.Start
        )

        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = { newQuery -> onSearchQueryChanged(newQuery) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 16.dp),
            placeholder = { Text("Buscar café, insumos...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar"
                )
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.5f),
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedContainerColor = MaterialTheme.colorScheme.tertiary,
                focusedContainerColor = MaterialTheme.colorScheme.tertiary,
                focusedTextColor = MaterialTheme.colorScheme.onTertiary,
                unfocusedTextColor = MaterialTheme.colorScheme.onTertiary,
                unfocusedPlaceholderColor = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.7f),
                focusedPlaceholderColor = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.7f),
                focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                unfocusedLeadingIconColor = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.7f)
            ),
            singleLine = true
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                val coffeeSelected = uiState.selectedCategory == ProductCategory.COFFEE
                val coffeeButtonColors = if (coffeeSelected) {
                    ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary)
                } else {
                    ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary, contentColor = MaterialTheme.colorScheme.onTertiary)
                }
                Button(
                    onClick = { onSelectCategory(ProductCategory.COFFEE) },
                    colors = coffeeButtonColors,
                    shape = RoundedCornerShape(8.dp),
                    border = if (!coffeeSelected) BorderStroke(1.dp, MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.5f)) else null,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(painter = painterResource(id = R.drawable.coffe_grain), contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ver Café Premium", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                }

                val accessorySelected = uiState.selectedCategory == ProductCategory.ACCESSORIES
                val accessoryButtonColors = if (accessorySelected) {
                    ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary)
                } else {
                    ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary, contentColor = MaterialTheme.colorScheme.onTertiary)
                }
                Button(
                    onClick = { onSelectCategory(ProductCategory.ACCESSORIES) },
                    colors = accessoryButtonColors,
                    shape = RoundedCornerShape(8.dp),
                    border = if (!accessorySelected) BorderStroke(1.dp, MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.5f)) else null,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(painter = painterResource(id = R.drawable.insums), contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ver Insumos", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                }
            }

            OutlinedButton(
                onClick = { onSortClick() },
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.5f)),
                contentPadding = PaddingValues(all = 8.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onBackground
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.filter),
                    contentDescription = if (uiState.sortOrder == SortOrder.PRICE_ASC) "Ordenado por precio" else "Ordenar por precio",
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            if (uiState.products.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (uiState.searchQuery.isNotBlank()) {
                                "No se encontraron productos para \"${uiState.searchQuery}\""
                            } else {
                                "No hay productos en esta categoría."
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(uiState.products) { product ->
                    ProductGridCard(
                        product = product,
                        onAddToCart = { onAddToCartClick(product) },
                        onToggleFavorite = { onToggleFavorite(product.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun ProductGridCard(
    product: Product,
    onAddToCart: (Product) -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(product.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = product.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                )

                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                ) {
                    Icon(
                        imageVector = if (product.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = if (product.isFavorite) "Quitar de favoritos" else "Añadir a favoritos",
                        tint = if (product.isFavorite) Color.Red else MaterialTheme.colorScheme.onTertiary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .padding(top = 8.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = product.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onTertiary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                product.description?.let {
                    Text(
                        text = it,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatPrice(product.price),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onTertiary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { onAddToCart(product) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .padding(bottom = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.add_cart),
                    contentDescription = "Añadir",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Añadir al carrito", fontSize = 12.sp)
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 740)
@Composable
fun PreviewCatalogScreenNewDesign() {
    val previewProducts = listOf(
        Product(id = "1", name = "Café de Grano Especial", description = "Origen Colombia", price = 12990, imageUrl = "", category = "coffee", isFavorite = true),
        Product(id = "2", name = "Café Molido Intenso", description = "Tostado oscuro", price = 8990, imageUrl = "", category = "coffee"),
        Product(id = "3", name = "Prensa Francesa", description = "Capacidad 1L", price = 15990, imageUrl = "", category = "accessories")
    )

    AppNebulaTheme {
        CatalogScreenContent(
            uiState = CatalogUiState(
                products = previewProducts,
                searchQuery = "Café"
            ),
            onSelectCategory = {},
            onSortClick = {},
            onAddToCartClick = {},
            onSearchQueryChanged = {},
            onToggleFavorite = {},
            modifier = Modifier
        )
    }
}