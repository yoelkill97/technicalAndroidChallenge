package com.example.technicalchallenge.domain.useCases

import com.example.technicalchallenge.domain.repositories.RegisterRepository
import com.example.technicalchallenge.domain.model.Client
import com.example.technicalchallenge.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RegisterClientUseCase @Inject constructor (val repository: RegisterRepository){
    suspend operator  fun invoke(client: Client): Flow<Resource<Boolean>> =       withContext(
        Dispatchers.IO
    ) {  repository.registerClient(client)}
}