package com.example.technicalchallenge.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.technicalchallenge.domain.useCases.AuthUseCase
import com.example.technicalchallenge.util.Resource
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginScreenViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
) : ViewModel() {

    private val _loadingState = MutableStateFlow<LoadingState>(LoadingState.IDLE)
    val loadingState = _loadingState.asStateFlow()
    fun signWhitFirebase(credential: AuthCredential) =
        viewModelScope.launch {
            _loadingState.value = LoadingState.LOADING

            authUseCase(credential).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _loadingState.value = LoadingState.SUCCESS(result.data!!)
                    }
                    is Resource.Loading -> {
                        _loadingState.value = LoadingState.LOADING
                    }
                    is Resource.Error -> {
                        _loadingState.value = LoadingState.ERROR(result.message.orEmpty())
                    }
                }
            }
        }
    fun loadingStateChange( loadingState: Boolean){
        _loadingState.value = if(loadingState) LoadingState.LOADING else LoadingState.IDLE
    }

}


sealed class LoadingState {
    object IDLE : LoadingState()
    object LOADING : LoadingState()
    data class SUCCESS(val authResult: AuthResult) : LoadingState()
    data class ERROR(val error: String) : LoadingState()
}