package com.example.streamed_app.client.network.response

data class UserInfoResponse(
    val id: String,
    val name: String,
    val surname: String,
    val password: String,
    val email: String,
    val role: String,
    val isActive: Boolean
)
