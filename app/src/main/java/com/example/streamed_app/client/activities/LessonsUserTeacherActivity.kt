package com.example.streamed_app.client.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.example.streamed_app.client.network.adapters.WebinarAdapter
import com.example.streamed_app.client.network.response.WebinarResponse
import io.appmetrica.analytics.AppMetrica
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LessonsUserTeacherActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: WebinarAdapter
    private lateinit var apiService: ApiService
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lessons_user_teacher)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        apiService = RetrofitClient.createApiService(this)

        recyclerView = findViewById(R.id.recyclerViewWebinars)
        recyclerView.layoutManager = LinearLayoutManager(this)

        loadWebinars()

        val buttonProfile = findViewById<Button>(R.id.buttonProfile)
        buttonProfile.setOnClickListener{
            val intent = Intent(this, ProfileUserTeacherActivity::class.java)
            startActivity(intent)
        }

        val buttonConnect = findViewById<Button>(R.id.buttonConnect)
        buttonConnect.setOnClickListener{
            val intent = Intent(this, ConnectUserTeacherActivity::class.java)
            startActivity(intent)
        }

        val buttonCourses = findViewById<Button>(R.id.buttonCourses)
        buttonCourses.setOnClickListener{
            val intent = Intent(this, MyCoursesUserTeacherActivity::class.java)
            startActivity(intent)
        }

        val buttonAddLessonInMyCourses = findViewById<Button>(R.id.buttonAddLessonInMyCourses)
        buttonAddLessonInMyCourses.setOnClickListener{
            val intent = Intent(this, AddingLessonUserTeacherActivity::class.java)
            AppMetrica.reportEvent("screen_add_class")
            startActivity(intent)
        }
    }

    private fun loadWebinars() {
        apiService.getAllWebinarsForProf().enqueue(object : Callback<List<WebinarResponse>> {
            override fun onResponse(call: Call<List<WebinarResponse>>, response: Response<List<WebinarResponse>>) {
                if (response.isSuccessful) {
                    val webinars = response.body() ?: emptyList()
                    adapter = WebinarAdapter(webinars)
                    recyclerView.adapter = adapter
                } else {
                    Toast.makeText(this@LessonsUserTeacherActivity, "Failed to load webinars", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<WebinarResponse>>, t: Throwable) {
                Toast.makeText(this@LessonsUserTeacherActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}