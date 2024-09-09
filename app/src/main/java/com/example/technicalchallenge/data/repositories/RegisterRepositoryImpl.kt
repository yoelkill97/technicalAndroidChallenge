package com.example.technicalchallenge.data.repositories

import com.example.technicalchallenge.domain.model.Client
import com.example.technicalchallenge.domain.repositories.RegisterRepository
import com.example.technicalchallenge.util.Resource
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RegisterRepositoryImpl @Inject constructor(val database: FirebaseDatabase) :
    RegisterRepository {
    override suspend fun registerClient(client: Client): Flow<Resource<Boolean>> {
        return flow {
            emit(Resource.Loading())

            val clientExists = database.getReference("clientes")
                .orderByChild("nombre")
                .equalTo(client.nombre)
            val clientExistsSnapshot = clientExists.get().await()

            if (clientExistsSnapshot.exists()) {
                emit(Resource.Error("Un cliente con ese nombre ya existe"))
                return@flow
            }

            val clientId = database.getReference("clientes").push().key ?: ""
            database.getReference("clientes").child(clientId).setValue(client).await()
            emit(Resource.Success(true))
        }.catch {
            emit(Resource.Error(it.message.toString()))
        }
    }
}