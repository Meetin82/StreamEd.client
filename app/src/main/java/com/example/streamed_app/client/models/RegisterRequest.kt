package com.example.streamed_app.client.models

data class RegisterRequest(
    val email: String,
    val name: String,
    val surname: String,
    val password: String,
    val role: String
)

