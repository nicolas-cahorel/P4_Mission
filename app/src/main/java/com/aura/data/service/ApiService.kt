package com.aura.data.service

import com.aura.data.model.Account
import com.aura.data.model.login.Credentials
import com.aura.data.model.login.CredentialsResult
import com.aura.data.model.transfer.Transfer
import com.aura.data.model.transfer.TransferResult

/**
 * An interface for interacting with the API.
 */
interface ApiService {

    /**
     * Logs in a user.
     * @param credentials The user's credentials.
     * @return The user's account information, or an error if the login failed.
     */
    fun login(credentials: Credentials): CredentialsResult

    /**
     * Fetches a user's accounts information.
     * @param id The user's ID.
     * @return A list of the user's accounts.
     */
    fun accounts(id: String): List<Account>

    /**
     * Transfers money between accounts.
     * @param transfer The transfer details.
     * @return The result of the transfer, or an error if the transfer failed.
     */
    fun transfer(transfer: Transfer): TransferResult

}