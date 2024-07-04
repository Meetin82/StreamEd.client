package com.example.streamed_app.client.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.streamed_app.R
import com.example.streamed_app.client.models.UpdUserRequest
import com.example.streamed_app.client.network.RetrofitClient
import com.example.streamed_app.client.network.ApiService
import com.example.streamed_app.client.network.response.BaseResponse
import io.appmetrica.analytics.AppMetrica
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("DEPRECATION")
class EditingProfileUserActivity : AppCompatActivity() {
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editing_profile_user)

        apiService = RetrofitClient.createApiService(this)

        val editTextNameForEditting = findViewById<EditText>(R.id.editTextNameForEditting)
        val editTextSurnameForEditting = findViewById<EditText>(R.id.editTextSurnameForEditting)
        val editTextMailForEditting = findViewById<EditText>(R.id.editTextMailForEditting)

        setEditTextFocusListener(editTextNameForEditting, "Имя")
        setEditTextFocusListener(editTextSurnameForEditting, "Фамилия")
        setEditTextFocusListener(editTextMailForEditting, "Эл. почта")

        val buttonEditProfileInEdittingPage = findViewById<Button>(R.id.buttonEditProfileInEdittingPage)
        buttonEditProfileInEdittingPage.setOnClickListener {
            val name = editTextNameForEditting.text.toString()
            val surname = editTextSurnameForEditting.text.toString()
            val email = editTextMailForEditting.text.toString()

            val updUserRequest = UpdUserRequest(name, surname, email)

            val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
            val jwtToken = sharedPreferences.getString("JWT_TOKEN", "")

            if (!jwtToken.isNullOrBlank()) {
                apiService.updateUser("Bearer $jwtToken", updUserRequest).enqueue(object : Callback<BaseResponse> {
                    override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                        if (response.isSuccessful && response.body()?.success == true) {
                            val newJwtToken = response.headers()["Authorization"]?.removePrefix("Bearer ")
                            if (!newJwtToken.isNullOrBlank()) {
                                sharedPreferences.edit().putString("JWT_TOKEN", newJwtToken).apply()
                            }
                            val intent = Intent(this@EditingProfileUserActivity, LoginUserActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this@EditingProfileUserActivity, "Ошибка изменения профиля", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                        Toast.makeText(this@EditingProfileUserActivity, "Ошибка изменения профиля", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                val intent = Intent(this, LoginUserActivity::class.java)
                startActivity(intent)
            }
        }

        val buttonBack = findViewById<Button>(R.id.buttonBack)
        buttonBack.setOnClickListener {
            val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
            val role = sharedPreferences.getString("USER_ROLE", "")
            val targetActivity = if (role == "PROFESSOR") ProfileUserTeacherActivity::class.java else ProfileUserStudentActivity::class.java
            val intent = Intent(this@EditingProfileUserActivity, targetActivity)
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
