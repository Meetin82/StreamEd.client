package com.example.streamed_app.client.activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.streamed_app.R
import com.example.streamed_app.client.network.ApiService
import com.example.streamed_app.client.network.RetrofitClient
import com.example.streamed_app.client.network.response.UserInfoResponse
import io.appmetrica.analytics.AppMetrica
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("DEPRECATION")
class ProfileUserStudentActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE_EXIT = 1001
    }

    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_user_student)

        apiService = RetrofitClient.createApiService(this)

        // Получение информации о студенте и обновление UI
        getStudentInfoAndUpdateUI()

        // Настройка кнопок для перехода на другие активити
        setupButtons()
    }

    private fun getStudentInfoAndUpdateUI() {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val jwtToken = sharedPreferences.getString("JWT_TOKEN", "")

        if (!jwtToken.isNullOrBlank()) {
            apiService.getUserInfo("Bearer $jwtToken").enqueue(object : Callback<UserInfoResponse> {
                override fun onResponse(call: Call<UserInfoResponse>, response: Response<UserInfoResponse>) {
                    if (response.isSuccessful) {
                        val studentInfo = response.body()
                        studentInfo?.let {
                            updateUI(studentInfo)
                        }
                    } else {
                        Toast.makeText(this@ProfileUserStudentActivity, "Ошибка при получении данных о студенте", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<UserInfoResponse>, t: Throwable) {
                    Toast.makeText(this@ProfileUserStudentActivity, "Ошибка соединения", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(userInfo: UserInfoResponse) {
        findViewById<TextView>(R.id.textNameInMyProfile).text = userInfo.name
        findViewById<TextView>(R.id.textSurnameInMyProfile2).text = userInfo.surname
        findViewById<TextView>(R.id.textEmailInMyProfile).text = "E-mail: ${userInfo.email}"
        // Добавьте другие поля, если нужно
    }

    private fun setupButtons() {
        findViewById<Button>(R.id.buttonConnect).setOnClickListener {
            startActivity(Intent(this, ConnectUserStudentActivity::class.java))
        }

        findViewById<Button>(R.id.buttonCatalog).setOnClickListener {
            AppMetrica.reportEvent("screen_courses")
            startActivity(Intent(this, CatologCoursesActivity::class.java))
        }

        findViewById<Button>(R.id.buttonTimetable).setOnClickListener {
            startActivity(Intent(this, TimetableStudentActivity::class.java))
        }

        findViewById<Button>(R.id.buttonVideo).setOnClickListener {
            startActivity(Intent(this, VideoStudentActivity::class.java))
        }

        findViewById<Button>(R.id.buttonEditProfile).setOnClickListener {
            startActivity(Intent(this, EditingProfileUserActivity::class.java))
        }

        findViewById<Button>(R.id.buttonExitFromAcc).setOnClickListener {
            showDialogExitConfirmation()
        }

        findViewById<Button>(R.id.buttonDeleteProfile).setOnClickListener {
            // Показать диалог подтверждения удаления аккаунта
            showDialogDeleteConfirmation()
        }
    }

    private fun showDialogExitConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Выход из аккаунта")
            .setMessage("Вы действительно хотите выйти из своего аккаунта?")
            .setPositiveButton(android.R.string.yes) { _, _ ->
                val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.remove("JWT_TOKEN")
                editor.apply()

                val intent = Intent(this, LoginUserActivity::class.java)
                startActivityForResult(intent, REQUEST_CODE_EXIT)
            }
            .setNegativeButton(android.R.string.no, null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }

    private fun showDialogDeleteConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Удаление аккаунта")
            .setMessage("Вы действительно хотите удалить свой аккаунт? Все данные будут удалены.")
            .setPositiveButton(android.R.string.yes) { _, _ ->
                deleteUser()
            }
            .setNegativeButton(android.R.string.no, null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }

    private fun deleteUser() {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val jwtToken = sharedPreferences.getString("JWT_TOKEN", "")

        if (!jwtToken.isNullOrBlank()) {
            apiService.deleteUser("Bearer $jwtToken").enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    Log.d("ProfileUserStudent", "deleteUser onResponse: ${response.code()} ${response.message()}")
                    handleDeleteUserResponse(response)
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.e("ProfileUserStudent", "deleteUser onFailure: ${t.message}", t)
                    Toast.makeText(this@ProfileUserStudentActivity, "Ошибка соединения", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Log.e("ProfileUserStudent", "JWT Token is null or blank")
            Toast.makeText(this, "Токен не найден", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleDeleteUserResponse(response: Response<String>?) {
        if (response != null && response.isSuccessful) {
            val responseBody = response.body()
            if (responseBody == "User deleted") {
                val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.remove("JWT_TOKEN")
                editor.apply()

                // Переход на экран входа
                val intent = Intent(this@ProfileUserStudentActivity, LoginUserActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)

                Log.d("ProfileUserStudent", "User deleted. Starting LoginActivity.")
            } else {
                Toast.makeText(this, "Не удалось удалить пользователя", Toast.LENGTH_SHORT).show()
                Log.d("ProfileUserStudent", "Unexpected response: $responseBody")
            }
        } else {
            Toast.makeText(this, "Не удалось удалить пользователя", Toast.LENGTH_SHORT).show()
            Log.d("ProfileUserStudent", "Failed to delete user: ${response?.errorBody()?.string()}")
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_EXIT) {
            // Действия после выхода из аккаунта
        }
    }
}
