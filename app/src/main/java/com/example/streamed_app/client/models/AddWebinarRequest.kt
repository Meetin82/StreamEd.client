package com.example.streamed_app.client.models

data class AddWebinarRequest(
    val name: String,
    val date: String,
    val courseId: Int
)