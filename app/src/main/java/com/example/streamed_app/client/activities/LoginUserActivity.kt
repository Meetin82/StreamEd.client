package com.example.streamed_app.client.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.streamed_app.R
import com.example.streamed_app.client.models.LoginRequest
import com.example.streamed_app.client.network.ApiService
import com.example.streamed_app.client.network.RetrofitClient
import com.example.streamed_app.client.network.response.BaseResponse
import com.example.streamed_app.client.network.response.UserInfoResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginUserActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    private lateinit var apiService: ApiService

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login_user)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        apiService = RetrofitClient.createApiService(this)

        val editTextTextEmailAddress = findViewById<EditText>(R.id.editTextTextEmailAddress)
        editTextTextEmailAddress.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                (v as EditText).text.clear()
            }
        }

        val editTextTextPassword = findViewById<EditText>(R.id.editTextTextPassword)
        editTextTextPassword.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                (v as EditText).text.clear()
            }
        }

        val buttonRegistr = findViewById<Button>(R.id.buttonRegistr)
        buttonRegistr.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }

        val buttonForgetPass = findViewById<Button>(R.id.buttonForgetPass)
        buttonForgetPass.setOnClickListener {
            val intent = Intent(this, ForgetPasswordActivity::class.java)
            startActivity(intent)
        }

        val buttonNextLogin = findViewById<Button>(R.id.buttonNextLogin)
        buttonNextLogin.setOnClickListener {
            val email = findViewById<EditText>(R.id.editTextTextEmailAddress).text.toString()
            val password = findViewById<EditText>(R.id.editTextTextPassword).text.toString()

            Log.d("LoginData", "Email: $email, Password: $password")

            val loginRequest = LoginRequest(email, password)

            apiService.loginUser(loginRequest).enqueue(object : Callback<BaseResponse> {
                override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                    Log.d("LoginResponse", "Status Code: ${response.code()}, Success: ${response.isSuccessful}, Message: ${response.message()}")

                    if (response.isSuccessful) {
                        val baseResponse = response.body()
                        if (baseResponse?.success == true) {
                            val jwtToken = baseResponse.message ?: ""
                            Log.d("LoginSuccess", "JWT Token: $jwtToken")

                            val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString("JWT_TOKEN", jwtToken)
                            editor.apply()

                            // Пересоздайте apiService с новым токеном
                            apiService = RetrofitClient.createApiService(this@LoginUserActivity)

                            apiService.getUserInfo("Bearer $jwtToken").enqueue(object : Callback<UserInfoResponse> {
                                override fun onResponse(call: Call<UserInfoResponse>, response: Response<UserInfoResponse>) {
                                    Log.d("UserInfoResponse", "Status Code: ${response.code()}, Success: ${response.isSuccessful}, Message: ${response.message()}")

                                    if (response.isSuccessful) {
                                        val userInfo = response.body()

                                        if (userInfo != null) {
                                            editor.putString("USER_NAME", userInfo.name)
                                            editor.apply()
                                            editor.putString("USER_SURNAME", userInfo.surname)
                                            editor.apply()
                                            Log.d("UserInfoResponse", "User Role: ${userInfo.role}")
                                            val targetActivity = if (userInfo.role == "PROFESSOR") ConnectUserTeacherActivity::class.java else ConnectUserStudentActivity::class.java
                                            val intent = Intent(this@LoginUserActivity, targetActivity)
                                            startActivity(intent)
                                        } else {
                                            Toast.makeText(this@LoginUserActivity, "Ошибка получения информации о пользователе", Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        Toast.makeText(this@LoginUserActivity, "Ошибка сервера (код: ${response.code()})", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                override fun onFailure(call: Call<UserInfoResponse>, t: Throwable) {
                                    Log.e("NetworkError", "Ошибка соединения: ${t.message}", t)
                                    Toast.makeText(this@LoginUserActivity, "Ошибка соединения", Toast.LENGTH_SHORT).show()
                                }
                            })
                        } else {
                            Toast.makeText(this@LoginUserActivity, baseResponse?.message ?: "Неизвестная ошибка", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@LoginUserActivity, "Ошибка сервера", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    Log.e("NetworkError", "Ошибка соединения: ${t.message}", t)
                    Toast.makeText(this@LoginUserActivity, "Ошибка соединения", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
