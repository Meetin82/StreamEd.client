package com.example.streamed_app.client.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
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
import com.example.streamed_app.client.network.response.BaseResponse
import com.example.streamed_app.client.network.RetrofitClient
import io.appmetrica.analytics.AppMetrica
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("DEPRECATION")
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
        val editTextSurnameRegistration = findViewById<EditText>(R.id.editTextSurnameRegistration)
        val editTextEmailRegistration = findViewById<EditText>(R.id.editTextEmailRegistration)
        val editTextPasswordRegistration1 = findViewById<EditText>(R.id.editTextPasswordRegistration1)
        val editTextPasswordRegistration2 = findViewById<EditText>(R.id.editTextPasswordRegistration2)

        setEditTextFocusListener(editTextNameRegistration, "Имя")
        setEditTextFocusListener(editTextSurnameRegistration, "Фамилия")
        setEditTextFocusListener(editTextEmailRegistration, "Эл. почта")
        setEditTextFocusListener(editTextPasswordRegistration1, "Пароль", isPassword = true)
        setEditTextFocusListener(editTextPasswordRegistration2, "Повторите пароль", isPassword = true)

        val buttonRegistrationNext = findViewById<Button>(R.id.buttonRegistrationNext)
        buttonRegistrationNext.setOnClickListener {
            val name = editTextNameRegistration.text.toString()
            val surname = editTextSurnameRegistration.text.toString()
            val email = editTextEmailRegistration.text.toString()
            val password = editTextPasswordRegistration1.text.toString()
            val confirmPassword = editTextPasswordRegistration2.text.toString()
            val checkBox = findViewById<CheckBox>(R.id.checkBox)
            val role = if (checkBox.isChecked) "professor" else "student"

            if (name.any { it.isDigit() }) {
                Toast.makeText(this, "Имя не должно содержать цифры", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (surname.any { it.isDigit() }) {
                Toast.makeText(this, "Фамилия не должна содержать цифры", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Некорректный адрес электронной почты", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length <= 8) {
                Toast.makeText(this, "Пароль должен быть больше 8 символов", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val registerRequest = RegisterRequest(email, name, surname, password, role)
            RetrofitClient.createApiService(this).signUpUser(registerRequest).enqueue(object : Callback<BaseResponse> {
                override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                    if (response.isSuccessful) {
                        val baseResponse = response.body()
                        if (baseResponse?.success == true) {
                            val jwtToken = baseResponse.message ?: ""
                            val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString("JWT_TOKEN", jwtToken)
                            editor.apply()
                            editor.putString("USER_NAME", name)
                            editor.apply()
                            editor.putString("USER_SURNAME", surname)
                            editor.apply()

                            val targetActivity = if (role == "professor") ConnectUserTeacherActivity::class.java else ConnectUserStudentActivity::class.java
                            val intent = Intent(this@RegistrationActivity, targetActivity)
                            AppMetrica.reportEvent("registration_complete")
                            startActivity(intent)
                        } else {
                            Toast.makeText(this@RegistrationActivity, baseResponse?.message ?: "Unknown error", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@RegistrationActivity, "Server error", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    Log.e("NetworkError", "Connection error: ${t.message}", t)
                    Toast.makeText(this@RegistrationActivity, "Connection error", Toast.LENGTH_SHORT).show()
                }
            })
        }

        val buttonBack = findViewById<Button>(R.id.buttonBack)
        buttonBack.setOnClickListener {
            val intent = Intent(this, LoginUserActivity::class.java)
            startActivity(intent)
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