package com.example.technicalchallenge.presentation.register

import com.example.technicalchallenge.domain.repositories.RegisterRepository
import com.example.technicalchallenge.domain.model.Client
import com.example.technicalchallenge.domain.useCases.RegisterClientUseCase
import com.example.technicalchallenge.util.Resource
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class RegisterViewModelTest{

    @RelaxedMockK
    private lateinit var repository: RegisterRepository

    @RelaxedMockK
    private lateinit var useCase: RegisterClientUseCase


    private lateinit var viewModel: RegisterViewModel


    @Before
    fun onBefore(){
        MockKAnnotations.init(this)
        Dispatchers.setMain(UnconfinedTestDispatcher())
        useCase = RegisterClientUseCase(repository)
        viewModel = RegisterViewModel(useCase)

    }

    @After
    fun onAfter(){
        Dispatchers.resetMain()
    }

    @Test
    fun `test registerClient`() = runTest {
        val client = Client("pepe", "Doe", 30, "1990/01/01")
        // Configurar el comportamiento del repositorio simulado

        coEvery{ repository.registerClient(any()) } returns flow {
            emit(Resource.Success(true))
        }

        viewModel.registerClient(client)

        coVerify { repository.registerClient(any()) }

        // Print the state for debugging
        println("Current Register State: ${viewModel.registerState.value}")
        assert(viewModel.registerState.value is RegisterState.SUCCESS)
    }
}