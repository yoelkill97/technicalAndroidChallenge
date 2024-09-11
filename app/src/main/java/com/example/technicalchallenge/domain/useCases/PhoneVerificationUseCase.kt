package com.example.technicalchallenge.domain.useCases

import android.app.Activity
import com.example.technicalchallenge.domain.repositories.AuthRepository
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import javax.inject.Inject

class PhoneVerificationUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(
        phoneNumber: String,
        activity: Activity,
        onCodeSent: (verificationId: String, token: PhoneAuthProvider.ForceResendingToken) -> Unit,
        onVerificationCompleted: (credential: PhoneAuthCredential) -> Unit,
        onVerificationFailed: (exception: FirebaseException) -> Unit
    ) {
        authRepository.startPhoneVerification(
            phoneNumber,
            activity,
            onCodeSent,
            onVerificationCompleted,
            onVerificationFailed
        )
    }
}