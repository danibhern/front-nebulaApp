package com.example.appnebula

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ContactScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun navegarHaciaLaPantallaDeContacto() {
        composeRule.waitUntil(timeoutMillis = 10_000) {
            composeRule.onAllNodesWithText("Home")
                .fetchSemanticsNodes().size == 1
        }

        composeRule.onNodeWithText("Contacto").performClick()
        composeRule.waitForIdle()
    }

    @Test
    fun laPantallaDeContacto_muestraTodosLosElementos() {
        composeRule.onNodeWithText("Contáctanos").assertIsDisplayed()
        composeRule.onNodeWithText("Nombre Completo *").assertIsDisplayed()
        composeRule.onNodeWithText("Email *").assertIsDisplayed()
        composeRule.onNodeWithText("Teléfono").assertIsDisplayed()
        composeRule.onNodeWithText("Asunto *").assertIsDisplayed()
        composeRule.onNodeWithText("Mensaje *").assertIsDisplayed()
        composeRule.onNodeWithText("Enviar Mensaje").assertIsDisplayed()
        composeRule.onNodeWithText("Encuéntranos aquí").assertIsDisplayed()
    }

    @Test
    fun alEnviarFormularioVacio_muestraMensajesDeErrorCorrectos() {
        composeRule.onNodeWithText("Enviar Mensaje").performScrollTo()
        composeRule.onNodeWithText("Enviar Mensaje").performClick()
        composeRule.waitForIdle()


        val nodes = composeRule.onAllNodesWithText("Campo obligatorio")
        nodes.assertCountEquals(3) // Para Nombre, Asunto y Mensaje
        composeRule.onNodeWithText("Correo inválido").assertIsDisplayed()
    }

    @Test
    fun alEscribirEnElFormulario_losCamposSeActualizanCorrectamente() {
        val nombre = "Dani Test"
        val email = "dani.test@email.com"
        val asunto = "Consulta sobre pedido"

        composeRule.onNodeWithText("Mensaje *").performScrollTo()

        composeRule.onNodeWithText("Nombre Completo *").performTextInput(nombre)
        composeRule.onNodeWithText(nombre).assertExists()

        composeRule.onNodeWithText("Email *").performTextClearance()
        composeRule.onNodeWithText("Email *").performTextInput(email)
        composeRule.onNodeWithText(email).assertExists()

        composeRule.onNodeWithText("Asunto *").performTextInput(asunto)
        composeRule.onNodeWithText(asunto).assertExists()
    }
}
