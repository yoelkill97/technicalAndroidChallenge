package com.example.technicalchallenge.domain.repositories

import com.example.technicalchallenge.domain.model.Client
import com.example.technicalchallenge.util.Resource
import kotlinx.coroutines.flow.Flow
interface RegisterRepository {
    suspend fun registerClient(client: Client): Flow<Resource<Boolean>>
}