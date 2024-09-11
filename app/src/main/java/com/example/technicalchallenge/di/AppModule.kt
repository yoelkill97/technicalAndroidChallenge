package com.example.technicalchallenge.di

import android.content.Context
import com.example.technicalchallenge.domain.repositories.AuthRepository
import com.example.technicalchallenge.data.repositories.AuthRepositoryImpl
import com.example.technicalchallenge.domain.repositories.RegisterRepository
import com.example.technicalchallenge.data.repositories.RegisterRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun providesFirebaseAuth()  = FirebaseAuth.getInstance()


    @Provides
    @Singleton
    fun providesFirebaseDataBase()  = FirebaseDatabase.getInstance()

    @Singleton
    @Provides
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }

    @Provides
    @Singleton
    fun providesRepositoryImpl(firebaseAuth: FirebaseAuth): AuthRepository {
        return AuthRepositoryImpl(firebaseAuth)
    }

    @Provides
    @Singleton
    fun providesRegisterRepositoryImpl(firebaseDatabase: FirebaseDatabase): RegisterRepository = RegisterRepositoryImpl(firebaseDatabase)


}