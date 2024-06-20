package com.example.streamed_app.client.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.streamed_app.R
import com.example.streamed_app.client.network.ApiService
import com.example.streamed_app.client.network.RetrofitClient
import com.example.streamed_app.client.network.adapters.CoursesAdapter
import com.example.streamed_app.client.network.response.CourseResponse
import io.appmetrica.analytics.AppMetrica
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyCoursesUserTeacherActivity : AppCompatActivity() {

    private lateinit var apiService: ApiService
    private lateinit var recyclerView: RecyclerView
    private lateinit var coursesAdapter: CoursesAdapter

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_my_courses_user_teacher)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        apiService = RetrofitClient.createApiService(this)
        recyclerView = findViewById(R.id.recyclerViewCourses)
        recyclerView.layoutManager = LinearLayoutManager(this)
        coursesAdapter = CoursesAdapter(emptyList(), this, apiService)
        recyclerView.adapter = coursesAdapter

        setupNavigationButtons()
        fetchCourses()
    }

    private fun setupNavigationButtons() {
        findViewById<Button>(R.id.buttonProfile).setOnClickListener {
            startActivity(Intent(this, ProfileUserTeacherActivity::class.java))
        }

        findViewById<Button>(R.id.buttonConnect).setOnClickListener {
            startActivity(Intent(this, ConnectUserTeacherActivity::class.java))
        }

        findViewById<Button>(R.id.buttonCalendar).setOnClickListener {
            startActivity(Intent(this, LessonsUserTeacherActivity::class.java))
        }

        findViewById<Button>(R.id.buttonAddCourseInMyCourses).setOnClickListener {
            AppMetrica.reportEvent("screen_add_course")
            startActivity(Intent(this, CreatingCourseUserTeacherActivity::class.java))
        }
    }

    private fun fetchCourses() {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val jwtToken = sharedPreferences.getString("JWT_TOKEN", "")
        if (!jwtToken.isNullOrBlank()) {
            Log.d("MyCoursesActivity", "JWT Token: $jwtToken") // Логирование JWT-токена

            apiService.getMyCourses(jwtToken.toString()).enqueue(object : Callback<List<CourseResponse>> {
                override fun onResponse(
                    call: Call<List<CourseResponse>>,
                    response: Response<List<CourseResponse>>
                ) {
                    Log.d("MyCoursesActivity", "Server Response Code: ${response.code()}") // Логирование кода ответа
                    Log.d("MyCoursesActivity", "Server Response Body: ${response.body()?.size}") // Логирование размера тела ответа

                    if (response.isSuccessful) {
                        val courses = response.body()
                        if (courses!= null) {
                            Log.d("MyCoursesActivity", "Fetched Courses Count: ${courses.size}") // Логирование количества курсов
                            coursesAdapter.updateCourses(courses)
                        } else {
                            showError("Ошибка: ответ сервера пустой.")
                            Log.e("MyCoursesActivity", "Empty server response body") // Логирование ошибки пустого тела ответа
                        }
                    } else {
                        showError("Ошибка ${response.code()}: ${response.message()}")
                        Log.e("MyCoursesActivity", "Server error: ${response.code()}, Message: ${response.message()}") // Логирование ошибки сервера
                    }
                }

                override fun onFailure(call: Call<List<CourseResponse>>, t: Throwable) {
                    showError("Ошибка при выполнении запроса: ${t.message}")
                    Log.e("MyCoursesActivity", "Request failure: ${t.message}") // Логирование ошибки выполнения запроса
                }
            })
        } else {
            showError("Ошибка: JWT-токен отсутствует.")
            Log.e("MyCoursesActivity", "JWT token is missing") // Логирование отсутствия JWT-токена
        }
    }

    private fun showError(message: String) {
        // Можно добавить логику для отображения ошибки пользователю
        // Например, использовать Snackbar или Toast для вывода сообщения
    }
}