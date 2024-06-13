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
        val apiLoginCredentials = LoginCredentials(username, password) // Use your custom Credentials class
        val apiLoginResponse = dataService.postUserCredentialsForLogin(apiLoginCredentials)

        val apiLoginStatusCode = apiLoginResponse.code()
        val apiLoginResponseBody = apiLoginResponse.body()

        val apiLoginResult = when {
            // Case 1: Both response body and status code are not null
            apiLoginResponseBody != null && apiLoginStatusCode != null -> {
                apiLoginResponseBody.toDomainModel(apiLoginStatusCode)
            }
            // Case 2: Response body is null but status code is not null
            apiLoginResponseBody == null && apiLoginStatusCode != null -> {
                LoginResultModel(false, apiLoginStatusCode)
            }
            // Case 3: Response body is not null but status code is null
            apiLoginResponseBody != null && apiLoginStatusCode == null -> {
                apiLoginResponseBody.toDomainModel(1)
            }
            // Case 4: Both response body and status code are null
            apiLoginResponseBody == null && apiLoginStatusCode == null -> {
                LoginResultModel(false, 0)
            }
            // Fallback case to handle unexpected scenarios
            else -> {
                LoginResultModel(false, 2)
            }
        }
        emit(apiLoginResult)

    }.catch { error ->
        Log.e("LoginRepository", error.message ?: "No exception message")
        // You can also emit some error state or handle the error in another way
    }
}


//        val apiLoginResult = loginApiResponseBody?.copy(apiResponseStatusCode = loginApiStatusCode)?.toDomainModel()
//                ?: if (loginApiResponseBody == null && loginApiStatusCode != null) {
//                    LoginResultModel(false, loginApiStatusCode)
//                } else {
//                    LoginResultModel(false,0) // Si le corps de la réponse et le code sont nuls, émettre 0 comme code
//                }
//        emit(apiLoginResult)
//
//    }.catch { error ->
//        Log.e("LoginRepository", error.message ?: "No exception message")
//        // You can also emit some error state or handle the error in another way

