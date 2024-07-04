package com.example.streamed_app.client.activities

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
class ConnectUserStudentActivity : AppCompatActivity() {
    private lateinit var apiService: ApiService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_connect_user_student)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val editTextCodeForStream = findViewById<EditText>(R.id.editTextCodeForStream)
        setEditTextFocusListener(editTextCodeForStream, "Код подключения")

        val buttonCatalog = findViewById<Button>(R.id.buttonCatalog)
        buttonCatalog.setOnClickListener{
            val intent = Intent(this, CatologCoursesActivity::class.java)
            AppMetrica.reportEvent("screen_courses")
            startActivity(intent)
        }

        val buttonTimetable = findViewById<Button>(R.id.buttonTimetable)
        buttonTimetable.setOnClickListener{
            val intent = Intent(this, TimetableStudentActivity::class.java)
            startActivity(intent)
        }

        val buttonVideo = findViewById<Button>(R.id.buttonVideo)
        buttonVideo.setOnClickListener{
            val intent = Intent(this, VideoStudentActivity::class.java)
            startActivity(intent)
        }

        val buttonProfile = findViewById<Button>(R.id.buttonProfile)
        buttonProfile.setOnClickListener{
            val intent = Intent(this, ProfileUserStudentActivity::class.java)
            startActivity(intent)
        }

        apiService = RetrofitClient.createApiService(this)

        val buttonConnectNext = findViewById<Button>(R.id.buttonConnectNext)
        buttonConnectNext.setOnClickListener {
            val streamKey = editTextCodeForStream.text.toString()
            val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
            val jwtToken = sharedPreferences.getString("JWT_TOKEN", "") ?: return@setOnClickListener

            apiService.getWebinarByCode(streamKey, jwtToken).enqueue(object :
                Callback<List<WebinarResponse>> {
                override fun onResponse(call: Call<List<WebinarResponse>>, response: Response<List<WebinarResponse>>) {
                    if (response.isSuccessful) {
                        val webinarList = response.body()
                        if (!webinarList.isNullOrEmpty()) {
                            val webinarId = webinarList[0].id
                            val rtmpUrl = "rtmp://158.160.29.10:1935/live/$streamKey"
                            val intent = Intent(this@ConnectUserStudentActivity, StreamStudentActivity::class.java)
                            intent.putExtra("rtmp_url", rtmpUrl)
                            intent.putExtra("webinar_id", webinarId)
                            AppMetrica.reportEvent("start_class")
                            startActivity(intent)
                        } else {
                            Toast.makeText(this@ConnectUserStudentActivity, "Вебинар не найден", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@ConnectUserStudentActivity, "Ошибка ответа сервера", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<WebinarResponse>>, t: Throwable) {
                    Toast.makeText(this@ConnectUserStudentActivity, "Ошибка сети: ${t.message}", Toast.LENGTH_SHORT).show()
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