package com.example.streamed_app.client.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.streamed_app.R
import com.example.streamed_app.client.models.AddSubscribe
import com.example.streamed_app.client.network.ApiService
import com.example.streamed_app.client.network.RetrofitClient
import com.example.streamed_app.client.network.adapters.CoursesAdapterStud
import com.example.streamed_app.client.network.response.BaseResponse
import com.example.streamed_app.client.network.response.CourseResponse
import io.appmetrica.analytics.AppMetrica
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CatologCoursesActivity : AppCompatActivity() {

    private lateinit var apiService: ApiService
    private lateinit var recyclerView: RecyclerView
    private lateinit var coursesAdapter: CoursesAdapterStud

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_catolog_courses)

        apiService = RetrofitClient.createApiService(this)

        recyclerView = findViewById(R.id.recyclerViewCoursesStud)
        recyclerView.layoutManager = LinearLayoutManager(this)
        coursesAdapter = CoursesAdapterStud(emptyList()) { courseId ->
            subscribeToCourse(courseId)
        }
        recyclerView.adapter = coursesAdapter

        fetchAllCourses()

        setupButtons()
    }

    private fun setupButtons() {
        findViewById<Button>(R.id.buttonConnect).setOnClickListener {
            startActivity(Intent(this, ConnectUserStudentActivity::class.java))
        }

        findViewById<Button>(R.id.buttonTimetable).setOnClickListener {
            startActivity(Intent(this, TimetableStudentActivity::class.java))
        }

        findViewById<Button>(R.id.buttonVideo).setOnClickListener {
            startActivity(Intent(this, VideoStudentActivity::class.java))
        }

        findViewById<Button>(R.id.buttonProfile).setOnClickListener {
            startActivity(Intent(this, ProfileUserStudentActivity::class.java))
        }
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
                        coursesAdapter.updateCourses(it)
                    }
                    AppMetrica.reportEvent("view_course")
                } else {
                    Toast.makeText(this@CatologCoursesActivity, "Ошибка загрузки курсов", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<CourseResponse>>, t: Throwable) {
                Toast.makeText(this@CatologCoursesActivity, "Ошибка сети", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun subscribeToCourse(courseId: Int) {
        val subscribeRequest = AddSubscribe(courseId)
        apiService.subscribeUser(subscribeRequest).enqueue(object : Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (response.isSuccessful) {
                    coursesAdapter.updateSubscription(courseId, true)
                    Toast.makeText(this@CatologCoursesActivity, "Вы успешно подписались на курс", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@CatologCoursesActivity, "Ошибка подписки на курс", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                Toast.makeText(this@CatologCoursesActivity, "Ошибка сети", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun enableEdgeToEdge() {
        val mainView = findViewById<View>(R.id.recyclerViewCoursesStud)
        mainView?.let {
            ViewCompat.setOnApplyWindowInsetsListener(it) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }
    }
}
