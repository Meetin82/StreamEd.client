package com.example.streamed_app.client.activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.streamed_app.R
import com.example.streamed_app.client.network.ApiService
import com.example.streamed_app.client.network.RetrofitClient
import com.example.streamed_app.client.network.response.UserInfoResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("DEPRECATION")
class ProfileUserTeacherActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE_EXIT = 1001
    }

    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_user_teacher)

        // Инициализация Retrofit сервиса
        apiService = RetrofitClient.createApiService(this)

        // Получение информации о пользователе и обновление UI
        getUserInfoAndUpdateUI()

        // Настройка кнопок для перехода на другие активити
        setupButtons()
    }

    private fun getUserInfoAndUpdateUI() {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val jwtToken = sharedPreferences.getString("JWT_TOKEN", "")

        if (!jwtToken.isNullOrBlank()) {
            apiService.getUserInfo("Bearer $jwtToken").enqueue(object : Callback<UserInfoResponse> {
                override fun onResponse(call: Call<UserInfoResponse>, response: Response<UserInfoResponse>) {
                    if (response.isSuccessful) {
                        val userInfo = response.body()
                        userInfo?.let {
                            updateUI(userInfo)
                        }
                    } else {
                        // Обработка ошибки получения данных о пользователе
                        Toast.makeText(this@ProfileUserTeacherActivity, "Ошибка при получении данных о пользователе", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<UserInfoResponse>, t: Throwable) {
                    // Обработка ошибки сети
                    Toast.makeText(this@ProfileUserTeacherActivity, "Ошибка соединения", Toast.LENGTH_SHORT).show()
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
        findViewById<Button>(R.id.buttonCalendar).setOnClickListener {
            startActivity(Intent(this, LessonsUserTeacherActivity::class.java))
        }

        findViewById<Button>(R.id.buttonConnect).setOnClickListener {
            startActivity(Intent(this, ConnectUserTeacherActivity::class.java))
        }

        findViewById<Button>(R.id.buttonCourses).setOnClickListener {
            startActivity(Intent(this, MyCoursesUserTeacherActivity::class.java))
        }

        findViewById<Button>(R.id.buttonEditProfile).setOnClickListener {
            startActivity(Intent(this, EditingProfileUserActivity::class.java))
        }

        findViewById<Button>(R.id.buttonExitFromAcc).setOnClickListener {
            showDialogExitConfirmation()
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

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_EXIT) {
            // Действия после выхода из аккаунта
        }
    }
}
