package com.example.streamed_app.client.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.streamed_app.R
import com.example.streamed_app.client.models.UpdUserRequest
import com.example.streamed_app.client.network.RetrofitClient
import com.example.streamed_app.client.network.ApiService
import com.example.streamed_app.client.network.response.BaseResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditingProfileUserActivity : AppCompatActivity() {
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editing_profile_user)

        apiService = RetrofitClient.createApiService(this)

        val editTextNameForEditting = findViewById<EditText>(R.id.editTextNameForEditting)
        val editTextSurnameForEditting = findViewById<EditText>(R.id.editTextSurnameForEditting)
        val editTextMailForEditting = findViewById<EditText>(R.id.editTextMailForEditting)

        val buttonEditProfileInEdittingPage = findViewById<Button>(R.id.buttonEditProfileInEdittingPage)
        buttonEditProfileInEdittingPage.setOnClickListener {
            val name = editTextNameForEditting.text.toString()
            val surname = editTextSurnameForEditting.text.toString()
            val email = editTextMailForEditting.text.toString()

            val updUserRequest = UpdUserRequest(name, surname, email)

            // Получаем JWT токен из SharedPreferences
            val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
            val jwtToken = sharedPreferences.getString("JWT_TOKEN", "")

            if (!jwtToken.isNullOrBlank()) {
                // Выполняем запрос на обновление профиля
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
                            // Обработка ошибки
                            // Например, вывод сообщения об ошибке
                        }
                    }

                    override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                        // Обработка ошибки
                        // Например, вывод сообщения об ошибке
                    }
                })
            } else {
                // JWT токен не найден или пустой, возможно пользователь не авторизован
                // Обработка ситуации, например, перенаправление на страницу авторизации
            }
        }

        val buttonBack = findViewById<Button>(R.id.buttonBack)
        buttonBack.setOnClickListener {
            val intent = Intent(this, ProfileUserStudentActivity::class.java)
            startActivity(intent)
        }
    }
}
