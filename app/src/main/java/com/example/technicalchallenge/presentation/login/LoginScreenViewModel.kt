package com.example.technicalchallenge.presentation.login

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.technicalchallenge.domain.useCases.AuthUseCase
import com.example.technicalchallenge.domain.useCases.PhoneVerificationUseCase
import com.example.technicalchallenge.util.Resource
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginScreenViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val phoneVerificationUseCase: PhoneVerificationUseCase
) : ViewModel() {

    var showModalValidationPhone = MutableStateFlow(false)
        private set
     var storedVerificationId = MutableStateFlow("")
         private set
    private val resendToken = MutableStateFlow<PhoneAuthProvider.ForceResendingToken?>(null)
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState = _authState.asStateFlow()
    fun signWhitFirebase(credential: AuthCredential) =
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            authUseCase(credential).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _authState.value = AuthState.Success(result.data!!)
                    }
                    is Resource.Loading -> {
                        _authState.value = AuthState.Loading
                    }
                    is Resource.Error -> {
                        _authState.value = AuthState.Error(result.message.orEmpty())
                    }
                }
            }
        }
    fun phoneVerification(phoneNumber: String , activity: Activity) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            phoneVerificationUseCase(
                phoneNumber,
                activity ,
                onCodeSent = { verificationId, token ->
                    _authState.value = AuthState.CodeSent(verificationId, token)
                    storedVerificationId.value = verificationId
                    resendToken.value = token
                    showModalValidationPhone.value = true
                },
                onVerificationCompleted = { credential ->
                    _authState.value = AuthState.VerificationCompleted(credential)
                    signWhitFirebase(credential)
                },
                onVerificationFailed = { exception ->
                    _authState.value = AuthState.Error(exception.message.orEmpty())
                }
            )
        }
    }
    fun loadingStateChange( loadingState: Boolean){
        _authState.value = if(loadingState) AuthState.Loading else AuthState.Idle
    }

    fun loginEventConsumed() {
        _authState.value = AuthState.Idle
    }

}


sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()

    data class CodeSent(
        val verificationId: String,
        val resendToken: PhoneAuthProvider.ForceResendingToken
    ) : AuthState()

    data class VerificationCompleted(val credential: PhoneAuthCredential) : AuthState()

    data class Success(val authResult: AuthResult) : AuthState()

    data class Error(val message: String) : AuthState()
}