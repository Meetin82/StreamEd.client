package com.example.streamed_app.client.network.response

data class CourseResponse(
    val id: Int,
    val duration: String,
    val price: String,
    val theme: String,
    val name: String,
    val description: String,
    val ownerId: Int
)