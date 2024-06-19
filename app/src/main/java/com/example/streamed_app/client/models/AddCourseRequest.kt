package com.example.streamed_app.client.models

data class AddCourseRequest(
    val duration: String,
    val price: String,
    val theme: String,
    val name: String,
    val description: String
)