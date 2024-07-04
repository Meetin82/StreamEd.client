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
import com.example.streamed_app.client.network.ApiService
import com.example.streamed_app.client.network.RetrofitClient
import com.example.streamed_app.client.network.response.WebinarResponse
import io.appmetrica.analytics.AppMetrica
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("DEPRECATION")
class ConnectUserTeacherActivity : AppCompatActivity() {
    private lateinit var apiService: ApiService
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_connect_user_teacher)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        apiService = RetrofitClient.createApiService(this)

        val editTextCodeForStream = findViewById<EditText>(R.id.editTextCodeForStream)
        setEditTextFocusListener(editTextCodeForStream, "Код подключения")

        val buttonProfile = findViewById<Button>(R.id.buttonProfile)
        buttonProfile.setOnClickListener{
            val intent = Intent(this, ProfileUserTeacherActivity::class.java)
            startActivity(intent)
        }

        val buttonCalendar = findViewById<Button>(R.id.buttonCalendar)
        buttonCalendar.setOnClickListener{
            val intent = Intent(this, LessonsUserTeacherActivity::class.java)
            startActivity(intent)
        }

        val buttonCourses = findViewById<Button>(R.id.buttonCourses)
        buttonCourses.setOnClickListener{
            val intent = Intent(this, MyCoursesUserTeacherActivity::class.java)
            startActivity(intent)
        }

        val buttonConnectNext = findViewById<Button>(R.id.buttonConnectNext)
        buttonConnectNext.setOnClickListener {
            val streamKey = editTextCodeForStream.text.toString()
            val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
            val jwtToken = sharedPreferences.getString("JWT_TOKEN", "") ?: return@setOnClickListener

            apiService.getWebinarByCode(streamKey, jwtToken).enqueue(object : Callback<List<WebinarResponse>> {
                override fun onResponse(call: Call<List<WebinarResponse>>, response: Response<List<WebinarResponse>>) {
                    if (response.isSuccessful) {
                        val webinarList = response.body()
                        if (!webinarList.isNullOrEmpty()) {
                            val webinarId = webinarList[0].id
                            val rtmpUrl = "rtmp://158.160.29.10:1935/live/$streamKey"
                            val intent = Intent(this@ConnectUserTeacherActivity, StreamTeacherActivity::class.java)
                            intent.putExtra("rtmp_url", rtmpUrl)
                            intent.putExtra("webinar_id", webinarId)
                            AppMetrica.reportEvent("start_class")
                            startActivity(intent)
                        } else {
                            Toast.makeText(this@ConnectUserTeacherActivity, "Вебинар не найден", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@ConnectUserTeacherActivity, "Ошибка ответа сервера", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<WebinarResponse>>, t: Throwable) {
                    Toast.makeText(this@ConnectUserTeacherActivity, "Ошибка сети: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        val buttonForReadingManual = findViewById<Button>(R.id.buttonForReadingManual)
        buttonForReadingManual.setOnClickListener{
            val intent = Intent(this, ManualForStreamingActivity::class.java)
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
