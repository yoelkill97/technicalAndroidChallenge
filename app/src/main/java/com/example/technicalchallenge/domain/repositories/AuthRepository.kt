package com.example.technicalchallenge.domain.repositories

import android.app.Activity
import com.example.technicalchallenge.util.Resource
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
   suspend fun googleSignIn(credential: AuthCredential): Flow<Resource<AuthResult>>
   suspend fun startPhoneVerification(
      phoneNumber: String,
      activity: Activity,
      onCodeSent: (verificationId: String, token: PhoneAuthProvider.ForceResendingToken) -> Unit,
      onVerificationCompleted: (credential: PhoneAuthCredential) -> Unit,
      onVerificationFailed: (exception: FirebaseException) -> Unit
   )
}

