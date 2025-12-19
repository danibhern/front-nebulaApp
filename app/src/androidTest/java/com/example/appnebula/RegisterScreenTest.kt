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

    class FakeUserViewModel : UserViewModel(
        mockk<AuthRepository>(relaxed = true),
        mockk<SessionManager>(relaxed = true)
    ) {
        private val _fakeEstado = MutableStateFlow(UserState())
        override val estado = _fakeEstado.asStateFlow()

        override fun onNombreChange(nombre: String) { _fakeEstado.value = _fakeEstado.value.copy(name = nombre) }
        override fun onCorreoChange(correo: String) { _fakeEstado.value = _fakeEstado.value.copy(email = correo) }
        override fun onClaveChange(clave: String) { _fakeEstado.value = _fakeEstado.value.copy(password = clave) }
        override fun onTerminosChange(acepta: Boolean) { _fakeEstado.value = _fakeEstado.value.copy(aceptaTerminos = acepta) }
    }

    @Before
    fun setup() {
        fakeViewModel = FakeUserViewModel()
        composeRule.setContent {
            AppNebulaTheme {
                RegisterScreen(
                    navController = rememberNavController(),
                    viewModel = fakeViewModel
                )
            }
        }
    }


    @Test
    fun laPantallaDeRegistro_muestraTodosLosElementosIniciales() {
        composeRule.onNodeWithTag("NombreTextField").assertIsDisplayed()
        composeRule.onNodeWithTag("EmailTextField").assertIsDisplayed()
        composeRule.onNodeWithTag("PasswordTextField").assertIsDisplayed()

        composeRule.onNodeWithText("Acepto los términos y condiciones").assertIsDisplayed()
        composeRule.onNodeWithText("Registrarse").assertIsDisplayed()
    }

    @Test
    fun alEscribirEnElFormularioYMarcarCheckbox_losCamposSeActualizan() {
        val nombreTest = "Dani"
        val emailTest = "dani@test.com"

        composeRule.onNodeWithTag("NombreTextField").performTextInput(nombreTest)
        composeRule.onNodeWithTag("EmailTextField").performTextInput(emailTest)
        composeRule.onNodeWithTag("CheckboxTerminos").performClick()

        composeRule.onNodeWithText(nombreTest).assertExists()
        composeRule.onNodeWithText(emailTest).assertExists()

        composeRule.onNode(
            hasParent(hasTestTag("CheckboxTerminos")) and isToggleable()
        ).assertIsOn()
    }

    @Test
    fun alPulsarElIconoDeVisibilidad_cambiaElEstadoDeLaContraseña() {
        composeRule.onNodeWithTag("PasswordTextField").performTextInput("123456")

        val iconoMostrar = composeRule.onNodeWithContentDescription("Mostrar contraseña")
        iconoMostrar.assertExists("El icono para mostrar la contraseña no fue encontrado")
        iconoMostrar.performClick()

        val iconoOcultar = composeRule.onNodeWithContentDescription("Ocultar contraseña")
        iconoOcultar.assertExists("El icono para ocultar la contraseña no apareció después de hacer clic")
    }
}
