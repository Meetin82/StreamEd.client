package com.example.streamed_app.client.network

import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.Interceptor
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://158.160.29.10:8080/"

    fun createApiService(context: Context): ApiService {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(JwtInterceptor(context))
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

class JwtInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val jwtToken = sharedPreferences.getString("JWT_TOKEN", "")
        var request = chain.request()

        if (!jwtToken.isNullOrBlank()) {
            Log.d("JwtInterceptor", "Using JWT Token: $jwtToken")
            request = request.newBuilder()
                .header("Authorization", "Bearer $jwtToken")
                .build()
        }
        return chain.proceed(request)
    }
}


