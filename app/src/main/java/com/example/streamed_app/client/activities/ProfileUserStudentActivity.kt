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
                editor.remove("USER_NAME")
                editor.apply()
                editor.remove("USER_SURNAME")
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
