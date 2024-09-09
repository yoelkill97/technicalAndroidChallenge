package com.example.technicalchallenge.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.technicalchallenge.domain.model.Client
import com.example.technicalchallenge.domain.useCases.RegisterClientUseCase
import com.example.technicalchallenge.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(private val useCase: RegisterClientUseCase) :
    ViewModel() {
    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.IDLE)
    val registerState = _registerState.asStateFlow()

    fun registerClient(client: Client) {
        _registerState.value = RegisterState.LOADING
        viewModelScope.launch {
            useCase(client).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _registerState.value =
                            RegisterState.SUCCESS("Cliente registrado correctamente")
                    }

                    is Resource.Loading -> {
                        _registerState.value = RegisterState.LOADING
                    }

                    is Resource.Error -> {
                        _registerState.value = RegisterState.ERROR(result.message.orEmpty())
                    }


                }
            }
        }
    }
    fun registerResetStatus() {
        _registerState.value = RegisterState.IDLE
    }
}

sealed class RegisterState {
    object IDLE : RegisterState()
    object LOADING : RegisterState()
    data class SUCCESS(val successMessage: String) : RegisterState()
    data class ERROR(val errorMessage: String) : RegisterState()

}