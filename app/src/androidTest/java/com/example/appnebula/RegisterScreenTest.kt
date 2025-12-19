package com.example.appnebula

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.appnebula.data.AuthRepository
import com.example.appnebula.data.SessionManager
import com.example.appnebula.ui.registro.RegisterScreen
import com.example.appnebula.ui.theme.AppNebulaTheme
import com.example.appnebula.viewmodel.UserState
import com.example.appnebula.viewmodel.UserViewModel
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegisterScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private lateinit var fakeViewModel: FakeUserViewModel

    // Clase Falsa que hereda del ViewModel real.
    // Recuerda que la clase UserViewModel y sus funciones públicas deben ser 'open'.
    class FakeUserViewModel : UserViewModel(
        mockk<AuthRepository>(relaxed = true),
        mockk<SessionManager>(relaxed = true)
    ) {
        private val _fakeEstado = MutableStateFlow(UserState())
        override val estado = _fakeEstado.asStateFlow()

        // Sobrescribimos las funciones para controlar el estado del test de forma aislada.
        override fun onNombreChange(nombre: String) { _fakeEstado.value = _fakeEstado.value.copy(name = nombre) }
        override fun onCorreoChange(correo: String) { _fakeEstado.value = _fakeEstado.value.copy(email = correo) }
        override fun onClaveChange(clave: String) { _fakeEstado.value = _fakeEstado.value.copy(password = clave) }
        override fun onTerminosChange(acepta: Boolean) { _fakeEstado.value = _fakeEstado.value.copy(aceptaTerminos = acepta) }
    }

    @Before
    fun setup() {
        // 1. Creamos una instancia fresca de nuestro ViewModel falso antes de cada test.
        fakeViewModel = FakeUserViewModel()

        // 2. Inyectamos nuestro ViewModel falso directamente en el Composable.
        // Esto es posible porque modificaste RegisterScreen para que acepte un viewModel.
        composeRule.setContent {
            AppNebulaTheme {
                RegisterScreen(
                    navController = rememberNavController(),
                    viewModel = fakeViewModel
                )
            }
        }
    }

    // --- TESTS COMPLETOS Y VALIDADOS ---

    @Test
    fun laPantallaDeRegistro_muestraTodosLosElementosIniciales() {
        // Verificamos que los campos de texto son visibles usando su testTag.
        composeRule.onNodeWithTag("NombreTextField").assertIsDisplayed()
        composeRule.onNodeWithTag("EmailTextField").assertIsDisplayed()
        composeRule.onNodeWithTag("PasswordTextField").assertIsDisplayed()

        // Verificamos que el texto del checkbox y el botón son visibles.
        composeRule.onNodeWithText("Acepto los términos y condiciones").assertIsDisplayed()
        composeRule.onNodeWithText("Registrarse").assertIsDisplayed()
    }

    @Test
    fun alEscribirEnElFormularioYMarcarCheckbox_losCamposSeActualizan() {
        val nombreTest = "Dani"
        val emailTest = "dani@test.com"

        // Escribimos en los campos de texto.
        composeRule.onNodeWithTag("NombreTextField").performTextInput(nombreTest)
        composeRule.onNodeWithTag("EmailTextField").performTextInput(emailTest)

        // Hacemos clic en el Row que contiene el checkbox usando su testTag.
        composeRule.onNodeWithTag("CheckboxTerminos").performClick()

        // Verificamos que el texto que escribimos ahora es visible en la UI.
        composeRule.onNodeWithText(nombreTest).assertExists()
        composeRule.onNodeWithText(emailTest).assertExists()

        // --- ALTERNATIVA SIN hasRole ---
        // 1. Buscamos un nodo que sea hijo del Row con el testTag "CheckboxTerminos".
        // 2. Y que además sea un elemento "toggleable" (como un Checkbox).
        // 3. Verificamos que esté activado (`assertIsOn`).
        composeRule.onNode(
            hasParent(hasTestTag("CheckboxTerminos")) and isToggleable()
        ).assertIsOn()
    }

    @Test
    fun alPulsarElIconoDeVisibilidad_cambiaElEstadoDeLaContraseña() {
        // Escribimos una contraseña en el campo correspondiente.
        composeRule.onNodeWithTag("PasswordTextField").performTextInput("123456")

        // Buscamos el icono "Mostrar" por su descripción de contenido y hacemos clic.
        val iconoMostrar = composeRule.onNodeWithContentDescription("Mostrar contraseña")
        iconoMostrar.assertExists("El icono para mostrar la contraseña no fue encontrado")
        iconoMostrar.performClick()

        // Verificamos que, tras el clic, el icono que se muestra es el de "Ocultar".
        val iconoOcultar = composeRule.onNodeWithContentDescription("Ocultar contraseña")
        iconoOcultar.assertExists("El icono para ocultar la contraseña no apareció después de hacer clic")
    }
}
