package com.aura.data

import com.aura.data.model.transfer.Transfer
import com.aura.data.repository.ApiRepository
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

/**
 * The main entry point for the application.
 */
fun main() {
    // Starts an embedded Netty server on port 8080.
    embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }

        // Installs the CORS feature, which allows the server to respond to requests
        // from any host.
        install(CORS) {
            anyHost()
            allowHeader(HttpHeaders.ContentType)
        }

        // Defines the routing for the server.
        routing {
            // Defines a POST endpoint for logging in users.
            post("/login") {
                val credentials = call.receive<com.aura.data.model.login.Credentials>()
                call.respond(ApiRepository.login(credentials))
            }

            // Defines a GET endpoint for fetching a user's account information.
            get("/accounts/{id}") {
                val id = call.parameters["id"] ?: throw IllegalArgumentException("Missing id path params")
                call.respond(ApiRepository.accounts(id))
            }

            // Defines a POST endpoint for transferring money between accounts.
            post("transfer") {
                val transfer = call.receive<Transfer>()
                call.respond(ApiRepository.transfer(transfer))
            }

            // Serves the Swagger UI documentation, which allows clients to explore the API.
            swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml")
        }
    }.start(wait = true)
}