package com.example.streamed_app.client.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import io.appmetrica.analytics.AppMetrica
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("DEPRECATION")
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

        val editTextTextPassword = findViewById<EditText>(R.id.editTextTextPassword)

        setEditTextFocusListener(editTextTextEmailAddress, "Эл. почта")
        setEditTextFocusListener(editTextTextPassword, "Пароль", isPassword = true)

        val buttonRegistr = findViewById<Button>(R.id.buttonRegistr)
        buttonRegistr.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            AppMetrica.reportEvent("screen_registration")
            startActivity(intent)
        }

        val buttonForgetPass = findViewById<Button>(R.id.buttonForgetPass)
        buttonForgetPass.setOnClickListener {
            val intent = Intent(this, ForgetPasswordActivity::class.java)
            AppMetrica.reportEvent("screen_forgot_password")
            startActivity(intent)
        }

        val buttonNextLogin = findViewById<Button>(R.id.buttonNextLogin)
        buttonNextLogin.setOnClickListener {
            val email = findViewById<EditText>(R.id.editTextTextEmailAddress).text.toString()
            val password = findViewById<EditText>(R.id.editTextTextPassword).text.toString()

            val loginRequest = LoginRequest(email, password)

            apiService.loginUser(loginRequest).enqueue(object : Callback<BaseResponse> {
                override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {

                    if (response.isSuccessful) {
                        val baseResponse = response.body()
                        if (baseResponse?.success == true) {
                            val jwtToken = baseResponse.message ?: ""

                            val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString("JWT_TOKEN", jwtToken)
                            editor.apply()

                            apiService = RetrofitClient.createApiService(this@LoginUserActivity)

                            apiService.getUserInfo("Bearer $jwtToken").enqueue(object : Callback<UserInfoResponse> {
                                override fun onResponse(call: Call<UserInfoResponse>, response: Response<UserInfoResponse>) {
                                    if (response.isSuccessful) {
                                        val userInfo = response.body()

                                        if (userInfo != null) {
                                            editor.putString("USER_NAME", userInfo.name)
                                            editor.apply()
                                            editor.putString("USER_SURNAME", userInfo.surname)
                                            editor.apply()
                                            editor.putString("USER_ROLE", userInfo.role)
                                            editor.apply()
                                            Log.d("UserInfoResponse", "User Role: ${userInfo.role}")
                                            val targetActivity = if (userInfo.role == "PROFESSOR") ConnectUserTeacherActivity::class.java else ConnectUserStudentActivity::class.java
                                            val intent = Intent(this@LoginUserActivity, targetActivity)
                                            AppMetrica.reportEvent("login_succes")
                                            startActivity(intent)
                                        } else {
                                            Toast.makeText(this@LoginUserActivity, "Ошибка получения информации о пользователе", Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        Toast.makeText(this@LoginUserActivity, "Ошибка сервера (код: ${response.code()})", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                override fun onFailure(call: Call<UserInfoResponse>, t: Throwable) {
                                    Toast.makeText(this@LoginUserActivity, "Ошибка соединения", Toast.LENGTH_SHORT).show()
                                }
                            })
                        } else {
                            Toast.makeText(this@LoginUserActivity, baseResponse?.message ?: "Неизвестная ошибка", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@LoginUserActivity, "Неправильно введён логин или пароль", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    Log.e("NetworkError", "Ошибка соединения: ${t.message}", t)
                    Toast.makeText(this@LoginUserActivity, "Ошибка соединения", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun setEditTextFocusListener(editText: EditText, hint: String, isPassword: Boolean = false) {
        editText.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus && editText.text.toString() == hint) {
                editText.setText("")
                editText.setTextColor(resources.getColor(android.R.color.black))
                if (isPassword) {
                    editText.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                }
            } else if (!hasFocus && editText.text.toString().isEmpty()) {
                editText.setText(hint)
                editText.setTextColor(resources.getColor(android.R.color.darker_gray))
                if (isPassword) {
                    editText.inputType = android.text.InputType.TYPE_CLASS_TEXT
                }
            }
        }
    }
}
