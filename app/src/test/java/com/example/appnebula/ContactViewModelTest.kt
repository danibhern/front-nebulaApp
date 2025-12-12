package com.example.appnebula.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.appnebula.data.SessionManager
import com.example.appnebula.data.repository.ContactRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@ExperimentalCoroutinesApi
class ContactViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var mockRepository: ContactRepository
    private lateinit var mockSessionManager: SessionManager

    private lateinit var viewModel: ContactViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockRepository = mockk(relaxed = true)
        mockSessionManager = mockk(relaxed = true)

        coEvery { mockSessionManager.userEmailFlow } returns flowOf(null)

        viewModel = spyk(ContactViewModel(mockRepository, mockSessionManager))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `cuando el ViewModel se inicia y usuario está logueado, el email se autocompleta`() = runTest(testDispatcher) {
        val userEmail = "test@user.com"
        coEvery { mockSessionManager.userEmailFlow } returns flowOf(userEmail)

        val newViewModel = ContactViewModel(mockRepository, mockSessionManager)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(userEmail, newViewModel.estado.value.email)
    }

    @Test
    fun `cuando se envía un mensaje exitosamente, la validación se simula como 'true'`() = runTest {
        every { viewModel["validarFormulario"]() } returns true

        coEvery { mockRepository.sendMessage(any()) } returns Result.success(Unit)

        viewModel.sendMessage()
        testDispatcher.scheduler.advanceUntilIdle()

        val estadoFinal = viewModel.estado.value
        assertFalse(estadoFinal.isSending)
        assertTrue(estadoFinal.isSuccess)
        assertEquals("Mensaje enviado con éxito", estadoFinal.feedbackMessage)
    }

    @Test
    fun `cuando el envío falla, la validación se simula como 'true'`() = runTest {
        every { viewModel["validarFormulario"]() } returns true

        val mensajeError = "Error de red simulado"
        coEvery { mockRepository.sendMessage(any()) } returns Result.failure(Exception(mensajeError))

        viewModel.sendMessage()
        testDispatcher.scheduler.advanceUntilIdle()

        val estadoFinal = viewModel.estado.value
        assertFalse(estadoFinal.isSending)
        assertFalse(estadoFinal.isSuccess)
        assertEquals(mensajeError, estadoFinal.feedbackMessage)
    }

    @Test
    fun `cuando la validación falla, no se intenta enviar el mensaje`() = runTest {
        every { viewModel["validarFormulario"]() } returns false

        viewModel.sendMessage()
        testDispatcher.scheduler.advanceUntilIdle()

        val estadoFinal = viewModel.estado.value
        assertFalse(estadoFinal.isSending)
        assertFalse(estadoFinal.isSuccess)
        assertNull(estadoFinal.feedbackMessage)
    }
}
