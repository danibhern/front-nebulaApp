package com.example.appnebula

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegisterScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun navegarHaciaLaPantallaDeRegistro() {
        composeRule.waitUntil(timeoutMillis = 10_000) {
            composeRule.onAllNodesWithContentDescription("Iniciar Sesión")
                .fetchSemanticsNodes().size == 1
        }

        composeRule.onNodeWithContentDescription("Iniciar Sesión").performClick()
        composeRule.waitForIdle()

        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText("Regístrate aquí")
                .fetchSemanticsNodes().size == 1
        }

        composeRule.onNodeWithText("Regístrate aquí").performClick()
        composeRule.waitForIdle()
    }

    @Test
    fun laPantallaDeRegistro_muestraTodosLosElementosCorrectamente() {
        composeRule.onNodeWithText("Crear Cuenta").assertIsDisplayed()
        composeRule.onNodeWithText("Nombre").assertIsDisplayed()
        composeRule.onNodeWithText("Email").assertIsDisplayed()
        composeRule.onNodeWithText("Contraseña").assertIsDisplayed()
        composeRule.onNodeWithText("Acepto los términos y condiciones").assertIsDisplayed()
        composeRule.onNodeWithText("Registrarse").assertIsDisplayed()
        composeRule.onNodeWithText("¿Ya tienes una cuenta? Inicia sesión aquí").assertIsDisplayed()
    }

    @Test
    fun alEscribirEnElFormulario_losCamposSeActualizan() {
        val nombre = "Dani"
        val email = "dani@test.com"
        val password = "password123"

        composeRule.onNodeWithTag("NombreTextField").performTextInput(nombre)
        composeRule.onNodeWithTag("EmailTextField").performTextInput(email)
        composeRule.onNodeWithTag("PasswordTextField").performTextInput(password)
        composeRule.onNodeWithTag("CheckboxTerminos").performClick()
        composeRule.onNodeWithText(nombre).assertExists()
        composeRule.onNodeWithText(email).assertExists()

        composeRule.onNodeWithTag("CheckboxTerminos").onChild().assertIsOn()
    }

    @Test
    fun alPulsarElIconoDeVisibilidad_cambiaElEstadoDeLaContraseña() {
        val iconoOjo = composeRule.onNodeWithContentDescription("Mostrar contraseña")

        iconoOjo.assertExists()
        iconoOjo.performClick()

        composeRule.onNodeWithContentDescription("Ocultar contraseña").assertExists()
    }
}
