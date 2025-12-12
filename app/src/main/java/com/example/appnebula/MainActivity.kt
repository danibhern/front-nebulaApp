package com.example.appnebula

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.appnebula.ui.cart.CartScreen
import com.example.appnebula.ui.catalog.CatalogScreen
import com.example.appnebula.ui.contact.ContactScreen
import com.example.appnebula.ui.home.HomeScreen
import com.example.appnebula.ui.home.SplashScreen
import com.example.appnebula.ui.login.LoginScreen
import com.example.appnebula.ui.payment.PaymentScreen
import com.example.appnebula.ui.registro.RegisterScreen
import com.example.appnebula.ui.reserva.ReservaScreen
import com.example.appnebula.ui.theme.AppNebulaTheme
import com.example.appnebula.viewmodel.CartViewModel
import com.example.appnebula.viewmodel.CartViewModelFactory
import com.example.appnebula.viewmodel.CatalogViewModel
import com.example.appnebula.viewmodel.CatalogViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppNebulaTheme {
                MainScreenView()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenView() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val application = context.applicationContext as Application

    val cartViewModel: CartViewModel = viewModel(factory = CartViewModelFactory(application))

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val routesWithoutBars = listOf("splash", "login", "register", "payment/{totalAmount}")
    val showBars = currentRoute != null && currentRoute !in routesWithoutBars

    Scaffold(
        topBar = {
            if (showBars) {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.nebula),
                                contentDescription = "Logo",
                                modifier = Modifier.size(60.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "NEBULA CAFÉ",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                        actionIconContentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    actions = {
                        IconButton(onClick = { navController.navigate("login") }) {
                            Icon(
                                imageVector = Icons.Outlined.AccountCircle,
                                contentDescription = "Iniciar Sesión",
                                modifier = Modifier.size(32.dp),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (showBars) {
                BottomNavigationBar(navController = navController, currentRoute = currentRoute)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "splash",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("splash") {
                SplashScreen(navController = navController)
            }
            composable(BottomNavItem.Home.route) {
                HomeScreen(
                    onNavigateToCatalog = { navController.navigate(BottomNavItem.Catalog.route) },
                    onNavigateToReserve = { navController.navigate(BottomNavItem.Reserve.route) }
                )
            }
            composable(BottomNavItem.Catalog.route) {
                val catalogViewModel: CatalogViewModel = viewModel(factory = CatalogViewModelFactory(application))
                CatalogScreen(
                    catalogViewModel = catalogViewModel,
                    cartViewModel = cartViewModel,
                    onCartClick = {
                        navController.navigate("cart")
                    }
                )
            }
            composable(BottomNavItem.Reserve.route) {
                ReservaScreen(navController = navController)
            }
            composable(BottomNavItem.Contact.route) {
                ContactScreen(navController = navController)
            }
            composable("login") {
                LoginScreen(navController = navController)
            }
            composable("register") {
                RegisterScreen(navController = navController)
            }
            composable("cart") {
                CartScreen(
                    cartViewModel = cartViewModel,
                    onCheckout = { total ->
                        navController.navigate("payment/${total.toFloat()}")
                    }
                )
            }
            composable(
                route = "payment/{totalAmount}",
                arguments = listOf(navArgument("totalAmount") { type = NavType.FloatType })
            ) { backStackEntry ->
                val total = backStackEntry.arguments?.getFloat("totalAmount") ?: 0.0f
                PaymentScreen(
                    navController = navController,
                    totalAmount = total.toDouble()
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController, currentRoute: String?) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Catalog,
        BottomNavItem.Reserve,
        BottomNavItem.Contact
    )
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        item.icon,
                        contentDescription = item.title,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = { Text(text = item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

sealed class BottomNavItem(var title: String, var icon: ImageVector, var route: String) {
    object Home : BottomNavItem("Home", Icons.Default.Home, "home")
    object Catalog : BottomNavItem("Catálogo", Icons.AutoMirrored.Filled.List, "catalog")
    object Reserve : BottomNavItem("Reservar", Icons.Default.DateRange, "reserve")
    object Contact : BottomNavItem("Contacto", Icons.Default.Phone, "contact")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AppNebulaTheme {
        MainScreenView()
    }
}
