package com.example.appnebula.viewmodel

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.appnebula.data.AuthRepository
import com.example.appnebula.data.SessionManager
import com.example.appnebula.model.auth.UserResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.spyk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class LoginViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var mockAuthRepository: AuthRepository
    private lateinit var mockSessionManager: SessionManager
    private lateinit var viewModel: UserViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockAuthRepository = mockk(relaxed = true)
        mockSessionManager = mockk(relaxed = true)

        mockkStatic(Log::class)
        every { Log.e(any(), any()) } returns 0

        viewModel = spyk(UserViewModel(mockAuthRepository, mockSessionManager))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `cuando el login falla debido a validación, no se llama al repositorio`() = runTest {
        every { viewModel["validarLogin"]() } returns false
        viewModel.iniciarSesion()
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify(exactly = 0) { mockAuthRepository.login(any()) }
        assertFalse(viewModel.estado.value.loginSuccess)
    }

    @Test
    fun `cuando el login es exitoso, se guarda la sesión y el estado refleja éxito`() = runTest {
        every { viewModel["validarLogin"]() } returns true
        val userResponse = UserResponse(id = 1, name = "Dani", email = "dani@test.com", token = "real-token-123")
        coEvery { mockAuthRepository.login(any()) } returns Result.success(userResponse)
        viewModel.iniciarSesion()
        testDispatcher.scheduler.advanceUntilIdle()
        val finalState = viewModel.estado.value
        assertFalse(finalState.isLoading)
        assertTrue(finalState.loginSuccess)
        assertNull(finalState.mensaje)
        coVerify {
            mockSessionManager.saveSession(
                userId = userResponse.id.toString(),
                userEmail = userResponse.email!!,
                token = userResponse.token!!
            )
        }
    }


    @Test
    fun `cuando el repositorio falla durante el login, el estado refleja el error`() = runTest {
        every { viewModel["validarLogin"]() } returns true
        val errorMessage = "Contraseña incorrecta"
        coEvery { mockAuthRepository.login(any()) } returns Result.failure(Exception(errorMessage))


        viewModel.iniciarSesion()
        testDispatcher.scheduler.advanceUntilIdle()

        val finalState = viewModel.estado.value
        assertFalse(finalState.isLoading)
        assertFalse(finalState.loginSuccess)
        assertEquals("Email o contraseña incorrectos.", finalState.mensaje)
    }

    @Test
    fun `cuando el login devuelve una respuesta inesperada (token nulo), el estado refleja el error`() = runTest {
        every { viewModel["validarLogin"]() } returns true
        val invalidResponse = UserResponse(id = 1, name = "Dani", email = "dani@test.com", token = null)
        coEvery { mockAuthRepository.login(any()) } returns Result.success(invalidResponse)

        viewModel.iniciarSesion()
        testDispatcher.scheduler.advanceUntilIdle()

        val finalState = viewModel.estado.value
        assertFalse(finalState.isLoading)
        assertFalse(finalState.loginSuccess)
        assertEquals("Email o contraseña incorrectos.", finalState.mensaje)
    }
}

