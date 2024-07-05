package com.aura.data.repository

import android.util.Log
import com.aura.data.model.LoginCredentials
import com.aura.data.model.LoginResultModel
import com.aura.data.network.LoginClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

/**
 * Repository responsible for handling login-related data operations.
 * This class interacts with the [LoginClient] to perform login API requests.
 */
class LoginRepository(private val dataService: LoginClient) {

    /**
     * Fetches login data based on the provided username and password.
     *
     * @param username The username used for authentication.
     * @param password The password associated with the username.
     * @return A [Flow] emitting [LoginResultModel] objects based on the API response.
     */
    fun fetchLoginData(username: String, password: String): Flow<LoginResultModel> = flow {

        // Create login credentials object
        val loginApiCredentials =
            LoginCredentials(username, password)

        // Make login API request
        val loginApiResponse = dataService.postCredentialsForLogin(loginApiCredentials)

        // Extract status code and response body from the API response
        val loginApiStatusCode = loginApiResponse.code()
        val loginApiResponseBody = loginApiResponse.body()

        // Determine the LoginResultModel based on the API response
        val loginApiResult = when {

            // Case 1: Both response body and status code are not null
            loginApiResponseBody != null && loginApiStatusCode != null -> {
                loginApiResponseBody.toDomainModel(loginApiStatusCode)
            }

            // Case 2: Response body is null but status code is not null
            loginApiResponseBody == null && loginApiStatusCode != null -> {
                LoginResultModel(false, loginApiStatusCode)
            }

            // Case 3: Response body is not null but status code is null
            loginApiResponseBody != null && loginApiStatusCode == null -> {
                loginApiResponseBody.toDomainModel(1)
            }

            // Case 4: Both response body and status code are null
            loginApiResponseBody == null && loginApiStatusCode == null -> {
                LoginResultModel(false, 0)
            }

            // Fallback case to handle unexpected scenarios
            else -> {
                LoginResultModel(false, 2)
            }
        }

        // Emit the LoginResultModel to the flow
        emit(loginApiResult)

    }.catch { error ->
        // Handle any errors that occur during the flow
        Log.e("LoginRepository", error.message ?: "No exception message")
    }
}