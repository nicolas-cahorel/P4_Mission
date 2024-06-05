package com.aura.data.repository

import com.aura.data.model.Account
import com.aura.data.model.login.Credentials
import com.aura.data.model.login.CredentialsResult
import com.aura.data.model.transfer.Transfer
import com.aura.data.model.transfer.TransferResult
import com.aura.data.service.ApiService
import com.aura.data.service.LocalApiService

/**
 * An object that provides a single point of access to the API.
 */
object ApiRepository : ApiService {

    /**
     * The API service.
     */
    private val apiService: LocalApiService = LocalApiService()

    /**
     * Logs in a user with the provided credentials.
     * @param credentials The user's credentials.
     * @return The result of the login attempt.
     */
    override fun login(credentials: Credentials): CredentialsResult {
        return apiService.login(credentials)
    }

    /**
     * Retrieves the accounts associated with the specified user ID.
     * @param id The user's ID.
     * @return The list of accounts.
     */
    override fun accounts(id: String): List<Account> {
        return apiService.accounts(id)
    }

    /**
     * Initiates a transfer of money between accounts.
     * @param transfer The transfer details.
     * @return The result of the transfer attempt.
     */
    override fun transfer(transfer: Transfer): TransferResult {
        return apiService.transfer(transfer)
    }

}