package com.aura.data.repository

import android.util.Log
import com.aura.data.model.LoginCredentials
import com.aura.data.model.LoginResultModel
import com.aura.data.network.LoginClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class LoginRepository(private val dataService: LoginClient) {

    fun fetchLoginData(username: String, password: String): Flow<LoginResultModel> = flow {
        val credentials = LoginCredentials(username, password) // Use your custom Credentials class
        val result = dataService.postUserCredentialsForLogin(credentials)
        val model = result.body()?.toDomainModel() ?: throw Exception("Invalid data")

        emit(model)
    }.catch { error ->
        Log.e("LoginRepository", error.message ?: "")
    }
}
