package com.example.streamed_app.client.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.streamed_app.R
import com.example.streamed_app.client.models.RegisterRequest
import com.example.streamed_app.client.network.BaseResponse
import com.example.streamed_app.client.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistrationActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId", "CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registration)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val editTextNameRegistration = findViewById<EditText>(R.id.editTextNameRegistration)
        editTextNameRegistration.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                (v as EditText).text.clear()
            }
        }

        val editTextSurnameRegistration = findViewById<EditText>(R.id.editTextSurnameRegistration)
        editTextSurnameRegistration.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                (v as EditText).text.clear()
            }
        }

        val editTextEmailRegistration = findViewById<EditText>(R.id.editTextEmailRegistration)
        editTextEmailRegistration.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                (v as EditText).text.clear()
            }
        }

        val editTextPasswordRegistration1 = findViewById<EditText>(R.id.editTextPasswordRegistration1)
        editTextPasswordRegistration1.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                (v as EditText).text.clear()
            }
        }

        val editTextPasswordRegistration2 = findViewById<EditText>(R.id.editTextPasswordRegistration2)
        editTextPasswordRegistration2.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                (v as EditText).text.clear()
            }
        }

        val buttonRegistrationNext = findViewById<Button>(R.id.buttonRegistrationNext)
        buttonRegistrationNext.setOnClickListener {
            val name = findViewById<EditText>(R.id.editTextNameRegistration).text.toString()
            val surname = findViewById<EditText>(R.id.editTextSurnameRegistration).text.toString()
            val email = findViewById<EditText>(R.id.editTextEmailRegistration).text.toString()
            val password = findViewById<EditText>(R.id.editTextPasswordRegistration1).text.toString()
            val checkBox = findViewById<CheckBox>(R.id.checkBox)
            val role = if (checkBox.isChecked) "PROFESSOR" else "STUDENT"

            val registerRequest = RegisterRequest(email, name, surname, password, role)

            RetrofitClient.createApiService(this).signUpUser(registerRequest).enqueue(object : Callback<BaseResponse> {
                override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                    if (response.isSuccessful) {
                        val baseResponse = response.body()
                        if (baseResponse?.success == true) {
                            // Извлечение JWT токена из сообщения
                            val jwtToken = baseResponse.message?: ""

                            // Сохраняем JWT токен
                            val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString("JWT_TOKEN", jwtToken)
                            editor.apply()

                            Toast.makeText(this@RegistrationActivity, baseResponse.message, Toast.LENGTH_SHORT).show()
                            val targetActivity = if (role == "PROFESSOR") ConnectUserTeacherActivity::class.java else ConnectUserStudentActivity::class.java
                            val intent = Intent(this@RegistrationActivity, targetActivity)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this@RegistrationActivity, baseResponse?.message?: "Неизвестная ошибка", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@RegistrationActivity, "Ошибка сервера", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    Log.e("NetworkError", "Ошибка соединения: ${t.message}", t)
                    Toast.makeText(this@RegistrationActivity, "Ошибка соединения", Toast.LENGTH_SHORT).show()
                }
            })
        }


        val buttonBack = findViewById<Button>(R.id.buttonBack)
        buttonBack.setOnClickListener{
            val intent = Intent(this, LoginUserActivity::class.java)
            startActivity(intent)
        }
    }
}