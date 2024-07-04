package com.example.streamed_app.client.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.streamed_app.R
import com.example.streamed_app.client.network.ApiService
import com.example.streamed_app.client.network.RetrofitClient
import com.example.streamed_app.client.network.adapters.CourseAdapterForUnlogin
import com.example.streamed_app.client.network.adapters.CoursesAdapterStud
import com.example.streamed_app.client.network.response.CourseResponse
import io.appmetrica.analytics.AppMetrica
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UnloginCoursesActivity : AppCompatActivity() {
    private lateinit var apiService: ApiService
    private lateinit var recyclerView: RecyclerView
    private lateinit var coursesAdapter: CourseAdapterForUnlogin
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_unlogin_courses)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        apiService = RetrofitClient.createApiService(this)

        recyclerView = findViewById(R.id.recyclerViewCoursesForUnlogin) // Изменено ID
        recyclerView.layoutManager = LinearLayoutManager(this)
        coursesAdapter = CourseAdapterForUnlogin(emptyList()) // Используем новый адаптер
        recyclerView.adapter = coursesAdapter

        fetchAllCourses()
    }

    private fun fetchAllCourses() {
        apiService.getAllCourses().enqueue(object : Callback<List<CourseResponse>> {
            override fun onResponse(
                call: Call<List<CourseResponse>>,
                response: Response<List<CourseResponse>>
            ) {
                if (response.isSuccessful) {
                    val courses = response.body()
                    courses?.let {
                        coursesAdapter.updateCourses(it) // обновляем данные адаптера
                    }
                } else {
                    Toast.makeText(this@UnloginCoursesActivity, "Ошибка загрузки курсов", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<CourseResponse>>, t: Throwable) {
                Toast.makeText(this@UnloginCoursesActivity, "Ошибка сети", Toast.LENGTH_SHORT).show()
            }
        })
    }
}