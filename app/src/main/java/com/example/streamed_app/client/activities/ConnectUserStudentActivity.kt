package com.example.streamed_app.client.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.streamed_app.R
import io.appmetrica.analytics.AppMetrica

class ConnectUserStudentActivity : AppCompatActivity() {
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
        editTextCodeForStream.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                (v as EditText).text.clear()
            }
        }

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

        val buttonConnectNext = findViewById<Button>(R.id.buttonConnectNext)
        buttonConnectNext.setOnClickListener{
            val rtmpUrl = "rtmp://158.160.29.10:1935/live/test"

            val intent = Intent(this, StreamStudentActivity::class.java)
            intent.putExtra("rtmp_url", rtmpUrl)
            startActivity(intent)
        }
    }
}