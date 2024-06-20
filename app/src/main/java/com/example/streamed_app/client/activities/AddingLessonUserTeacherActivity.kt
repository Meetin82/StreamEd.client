package com.example.streamed_app.client.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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

class AddingLessonUserTeacherActivity : AppCompatActivity() {

    private lateinit var apiService: ApiService
    private var selectedCourseId: Int? = null
    private lateinit var editTextNameLesson: AutoCompleteTextView
    private lateinit var editTextDateLesson: EditText
    private lateinit var editTextDateLesson2: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adding_lesson_user_teacher)
        enableEdgeToEdge()
        setupViews()
        setupListeners()

        apiService = RetrofitClient.createApiService(this)

        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val jwtToken = sharedPreferences.getString("JWT_TOKEN", "")

        jwtToken?.let {
            fetchCourses(it)
        }
    }

    private fun setupViews() {
        editTextNameLesson = findViewById(R.id.editTextNameLesson)
        editTextDateLesson = findViewById(R.id.editTextDateLesson)
//        editTextDateLesson2 = findViewById(R.id.editTextDateLesson2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupListeners() {
        editTextNameLesson.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                (v as EditText).text.clear()
            }
        }

        editTextDateLesson.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                (v as EditText).text.clear()
            }
        }

//        editTextDateLesson2.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
//            if (hasFocus) {
//                (v as EditText).text.clear()
//            }
//        }

        findViewById<Button>(R.id.buttonProfile).setOnClickListener {
            startActivity(Intent(this, ProfileUserTeacherActivity::class.java))
        }

        findViewById<Button>(R.id.buttonConnect).setOnClickListener {
            startActivity(Intent(this, ConnectUserTeacherActivity::class.java))
        }

        findViewById<Button>(R.id.buttonCourses).setOnClickListener {
            startActivity(Intent(this, MyCoursesUserTeacherActivity::class.java))
            AppMetrica.reportEvent("screen_courses")
        }

        findViewById<Button>(R.id.buttonCalendar).setOnClickListener {
            startActivity(Intent(this, LessonsUserTeacherActivity::class.java))
        }

        findViewById<Button>(R.id.buttonAddLesson).setOnClickListener {
            addWebinar()
        }
    }

    private fun fetchCourses(jwtToken: String) {
        apiService.getMyCourses(jwtToken).enqueue(object : Callback<List<CourseResponse>> {
            override fun onResponse(call: Call<List<CourseResponse>>, response: Response<List<CourseResponse>>) {
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
                    Log.e("API_REQUEST", "Failed to fetch courses: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<CourseResponse>>, t: Throwable) {
                // Обработка ошибки
                Log.e("API_REQUEST", "Error fetching courses: ${t.message}", t)
            }
        })
    }

    private fun addWebinar() {
        val name = editTextNameLesson.text.toString()
        val date = editTextDateLesson.text.toString()
        val courseId = selectedCourseId

        if (name.isBlank() || courseId == null) {
            Toast.makeText(
                this@AddingLessonUserTeacherActivity,
                "Please select a course and enter a lesson name",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val webinarRequest = AddWebinarRequest(name, date, courseId)

        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
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

                            findViewById<TextView>(R.id.textCodeMain)?.apply {
                                text = "Код руководителя: $message"
                            }
                        } else {
                            Log.e("API_REQUEST", "Failed to create webinar: ${response.message()}")
                        }
                    }
                    AppMetrica.reportEvent("add_class")
                    startActivity(Intent(this@AddingLessonUserTeacherActivity, LessonsUserTeacherActivity::class.java))
                    finish()
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    Toast.makeText(
                        this@AddingLessonUserTeacherActivity,
                        "Error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("API_REQUEST", "Error: ${t.message}", t)
                }
            })
    }
}