package com.example.technicalchallenge.domain.repositories

import com.example.technicalchallenge.util.Resource
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
   suspend fun googleSignIn(credential: AuthCredential): Flow<Resource<AuthResult>>
}

