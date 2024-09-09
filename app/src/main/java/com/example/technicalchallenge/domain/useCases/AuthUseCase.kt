package com.example.technicalchallenge.domain.useCases

import com.example.technicalchallenge.domain.repositories.AuthRepository
import com.example.technicalchallenge.util.Resource
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(credential: AuthCredential): Flow<Resource<AuthResult>> =
        withContext(
            Dispatchers.IO
        ) { authRepository.googleSignIn(credential) }
}