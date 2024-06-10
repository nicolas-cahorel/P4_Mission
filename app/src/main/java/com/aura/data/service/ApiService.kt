package com.aura.data.service

import com.aura.data.response.ApiResponse
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Interface définissant les points d'entrée vers les fonctionnalités de l'API.
 */
interface ApiService {
    /**
     * Requête POST pour effectuer la connexion.
     * @param credentials Les identifiants de l'utilisateur.
     * @return L'objet [ApiResponse] contenant le résultat de la connexion.
     */
    @POST("login")
    suspend fun login(@Body credentials: Map<String, String>): ApiResponse
}
