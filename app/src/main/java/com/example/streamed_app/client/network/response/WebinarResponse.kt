package com.example.streamed_app.client.network.response

data class WebinarResponse(
    val id: Int,
    val name: String,
    val date: String,
    val courseId: Int,
    val code: String
)