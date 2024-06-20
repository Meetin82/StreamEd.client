package com.example.streamed_app.client.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.streamed_app.R
import com.example.streamed_app.client.models.AddWebinarRequest
import com.example.streamed_app.client.network.ApiService
import com.example.streamed_app.client.network.RetrofitClient
import com.example.streamed_app.client.network.response.BaseResponse
import com.example.streamed_app.client.network.response.CourseResponse
import io.appmetrica.analytics.AppMetrica
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private lateinit var apiService: ApiService
private var selectedCourseId: Int? = null

@Suppress("NAME_SHADOWING")
class AddingLessonUserTeacherActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_adding_lesson_user_teacher)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        apiService = RetrofitClient.createApiService(this)

        val editTextNameLesson = findViewById<AutoCompleteTextView>(R.id.editTextNameLesson)
        val editTextDateLesson = findViewById<EditText>(R.id.editTextDateLesson)
        val editTextDateLesson2 = findViewById<EditText>(R.id.editTextDateLesson2)

        editTextNameLesson.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                (v as EditText).text.clear()
            }
        }

        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val jwtToken = sharedPreferences.getString("JWT_TOKEN", "")

        if (jwtToken != null) {
            apiService.getMyCourses(jwtToken).enqueue(object : Callback<List<CourseResponse>> {
                override fun onResponse(
                    call: Call<List<CourseResponse>>,
                    response: Response<List<CourseResponse>>
                ) {
                    if (response.isSuccessful) {
                        val courses = response.body() ?: emptyList()
                        val coursesMap = mutableMapOf<String, Int>()
                        courses.forEach { course ->
                            coursesMap[course.name] = course.id
                        }
                        val adapter = ArrayAdapter(
                            this@AddingLessonUserTeacherActivity,
                            android.R.layout.simple_dropdown_item_1line,
                            courses.map { it.name }
                        )
                        editTextNameLesson.setAdapter(adapter)

                        editTextNameLesson.setOnItemClickListener { parent, view, position, id ->
                            val courseName = parent.getItemAtPosition(position) as String
                            selectedCourseId = coursesMap[courseName]
                        }

                    } else {
                        // Обработка ошибки
                    }
                }

                override fun onFailure(call: Call<List<CourseResponse>>, t: Throwable) {
                    // Обработка ошибки
                }
            })
        }

        editTextDateLesson.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                (v as EditText).text.clear()
            }
        }

        editTextDateLesson2.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                (v as EditText).text.clear()
            }
        }

        val buttonProfile = findViewById<Button>(R.id.buttonProfile)
        buttonProfile.setOnClickListener {
            val intent = Intent(this, ProfileUserTeacherActivity::class.java)
            startActivity(intent)
        }

        val buttonConnect = findViewById<Button>(R.id.buttonConnect)
        buttonConnect.setOnClickListener {
            val intent = Intent(this, ConnectUserTeacherActivity::class.java)
            startActivity(intent)
        }

        val buttonCourses = findViewById<Button>(R.id.buttonCourses)
        buttonCourses.setOnClickListener {
            val intent = Intent(this, MyCoursesUserTeacherActivity::class.java)
            AppMetrica.reportEvent("screen_courses")
            startActivity(intent)
        }

        val buttonCalendar = findViewById<Button>(R.id.buttonCalendar)
        buttonCalendar.setOnClickListener {
            val intent = Intent(this, LessonsUserTeacherActivity::class.java)
            startActivity(intent)
        }

        val buttonAddLesson = findViewById<Button>(R.id.buttonAddLesson)
        buttonAddLesson.setOnClickListener {
            val name = editTextNameLesson.text.toString() // Получаем текст из AutoCompleteTextView
            val date = editTextDateLesson.text.toString()
            val courseId = selectedCourseId

            if (name.isBlank() || courseId == null) {
                Toast.makeText(
                    this@AddingLessonUserTeacherActivity,
                    "Please select a course and enter a lesson name",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val webinarRequest = AddWebinarRequest(name, date, courseId)

            val jwtToken = sharedPreferences.getString("JWT_TOKEN", "") ?: ""

            apiService.createWebinar(jwtToken, webinarRequest)
                .enqueue(object : Callback<BaseResponse> {
                    override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                        Log.d("API_REQUEST", "Response code: ${response.code()}")
                        Log.d("API_REQUEST", "Response body: ${response.body()}")
                        if (response.isSuccessful) {
                            val body = response.body()
                            if (body != null && body.success == true) {
                                val message = body.message ?: "Webinar created successfully"

                                Toast.makeText(
                                    this@AddingLessonUserTeacherActivity,
                                    message,
                                    Toast.LENGTH_LONG
                                ).show()

                                // Обновляем TextView с кодом руководителя
                                val textCodeMain = findViewById<TextView>(R.id.textCodeMain)
                                textCodeMain.text = "Код руководителя: $message"
                            } else {
                                // Логгирование для отслеживания проблемы
                                Log.e("API_REQUEST", "Failed to create webinar: ${response.message()}")
                            }
                        } else {
                        }
                        AppMetrica.reportEvent("add_class")
                        // Переход на LessonsUserTeacherActivity
                        val intent = Intent(this@AddingLessonUserTeacherActivity, LessonsUserTeacherActivity::class.java)
                        startActivity(intent)
                        finish() // Закрываем текущую активность, чтобы пользователь не мог вернуться назад
                    }




                    override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                        Toast.makeText(
                            this@AddingLessonUserTeacherActivity,
                            "Error: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                        // Логгирование для отслеживания проблемы
                        Log.e("API_REQUEST", "Error: ${t.message}", t)
                    }
                })

        }
    }
}
