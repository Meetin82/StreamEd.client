package com.example.streamed_app.client.network

import com.example.streamed_app.client.models.RegisterRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("api/v1/signup")
    fun signUpUser(@Body user: RegisterRequest): Call<BaseResponse>
}
