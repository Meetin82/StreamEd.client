package com.example.streamed_app.client.activities

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
import com.example.streamed_app.client.network.adapters.VideoListAdapter
import com.example.streamed_app.client.network.response.WebinarResponse
import io.appmetrica.analytics.AppMetrica
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VideoStudentActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: VideoListAdapter
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_video_student)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        apiService = RetrofitClient.createApiService(this)

        recyclerView = findViewById(R.id.recyclerViewVideos)
        recyclerView.layoutManager = LinearLayoutManager(this)

        loadWebinars()

        val buttonConnect = findViewById<Button>(R.id.buttonConnect)
        buttonConnect.setOnClickListener {
            val intent = Intent(this, ConnectUserStudentActivity::class.java)
            startActivity(intent)
        }

        val buttonCatalog = findViewById<Button>(R.id.buttonCatalog)
        buttonCatalog.setOnClickListener {
            val intent = Intent(this, CatologCoursesActivity::class.java)
            AppMetrica.reportEvent("screen_courses")
            startActivity(intent)
        }

        val buttonTimetable = findViewById<Button>(R.id.buttonTimetable)
        buttonTimetable.setOnClickListener {
            val intent = Intent(this, TimetableStudentActivity::class.java)
            startActivity(intent)
        }

        val buttonProfile = findViewById<Button>(R.id.buttonProfile)
        buttonProfile.setOnClickListener {
            val intent = Intent(this, ProfileUserStudentActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadWebinars() {
        apiService.getAllWebinarsForSub().enqueue(object : Callback<List<WebinarResponse>> {
            override fun onResponse(call: Call<List<WebinarResponse>>, response: Response<List<WebinarResponse>>) {
                if (response.isSuccessful) {
                    val webinars = response.body() ?: emptyList()
                    adapter = VideoListAdapter(webinars, apiService)
                    recyclerView.adapter = adapter
                } else {
                    Toast.makeText(this@VideoStudentActivity, "Failed to load webinars", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<WebinarResponse>>, t: Throwable) {
                Log.e("LessonsUserTeacher", "Error: ${t.message}", t)
                Toast.makeText(this@VideoStudentActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}