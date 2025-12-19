package com.example.appnebula

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.appnebula.data.SessionManager
import com.example.appnebula.data.repository.ContactRepository
import com.example.appnebula.model.contacto.ContactDto
import com.example.appnebula.ui.contact.ContactScreen
import com.example.appnebula.ui.theme.AppNebulaTheme
import com.example.appnebula.viewmodel.ContactViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ContactScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private lateinit var mockRepository: ContactRepository
    private lateinit var mockSessionManager: SessionManager
    private lateinit var viewModel: ContactViewModel

    @Test
    fun laPantallaDeContacto_muestraTodosLosCamposIniciales() {

        mockRepository = mockk()
        mockSessionManager = mockk()

        every { mockSessionManager.userEmailFlow } returns flowOf("test@email.com")

        viewModel = ContactViewModel(mockRepository, mockSessionManager)

        composeRule.setContent {
            AppNebulaTheme {
                ContactScreen(
                    navController = rememberNavController(),
                    viewModel = viewModel
                )
            }
        }

        composeRule.onNodeWithTag("ContactNombreTextField").assertIsDisplayed()
        composeRule.onNodeWithTag("ContactEmailTextField").assertIsDisplayed()
        composeRule.onNodeWithTag("ContactPhoneTextField").assertIsDisplayed()
        composeRule.onNodeWithTag("ContactSubjectTextField").assertIsDisplayed()
        composeRule.onNodeWithTag("ContactMessageTextField").assertIsDisplayed()
        composeRule.onNodeWithText("Enviar Mensaje").assertIsDisplayed()

        composeRule.onNodeWithText("test@email.com").assertIsDisplayed()
    }

    @Test
    fun alEscribirEnLosCampos_elTextoSeActualizaCorrectamente() {
        // --- Setup ---
        mockRepository = mockk()
        mockSessionManager = mockk()
        every { mockSessionManager.userEmailFlow } returns flowOf("test@email.com")
        viewModel = ContactViewModel(mockRepository, mockSessionManager)

        composeRule.setContent {
            AppNebulaTheme {
                ContactScreen(
                    navController = rememberNavController(),
                    viewModel = viewModel
                )
            }
        }

        val nombre = "Dani"
        val asunto = "Test de UI"
        val mensaje = "Esto es un mensaje de prueba."

        composeRule.onNodeWithTag("ContactNombreTextField").performTextInput(nombre)
        composeRule.onNodeWithTag("ContactSubjectTextField").performTextInput(asunto)
        composeRule.onNodeWithTag("ContactMessageTextField").performTextInput(mensaje)

        // --- Verificación ---
        composeRule.onNodeWithText(nombre).assertIsDisplayed()
        composeRule.onNodeWithText(asunto).assertIsDisplayed()
        composeRule.onNodeWithText(mensaje).assertIsDisplayed()
    }


    @Test
    fun alHacerClicEnEnviarConDatosValidos_seMuestraDialogoDeExito() {
        mockRepository = mockk()
        mockSessionManager = mockk()
        every { mockSessionManager.userEmailFlow } returns flowOf("email@valido.com")

        coEvery { mockRepository.sendMessage(any<ContactDto>()) } returns Result.success(Unit)

        viewModel = ContactViewModel(mockRepository, mockSessionManager)

        composeRule.setContent {
            AppNebulaTheme {
                ContactScreen(
                    navController = rememberNavController(),
                    viewModel = viewModel
                )
            }
        }

        composeRule.onNodeWithTag("ContactNombreTextField").performTextInput("Nombre válido")
        composeRule.onNodeWithTag("ContactSubjectTextField").performTextInput("Asunto válido")
        composeRule.onNodeWithTag("ContactMessageTextField").performTextInput("Mensaje válido")

        composeRule.onNodeWithText("Enviar Mensaje").performClick()

        composeRule.onNodeWithText("¡Mensaje Enviado!").assertIsDisplayed()
        composeRule.onNodeWithText("Mensaje enviado con éxito").assertIsDisplayed()
    }
}
