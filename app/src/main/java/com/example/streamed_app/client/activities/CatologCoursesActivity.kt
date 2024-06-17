package com.example.streamed_app.client.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.streamed_app.R

class CatologCoursesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_catolog_courses)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val buttonConnect = findViewById<Button>(R.id.buttonConnect)
        buttonConnect.setOnClickListener{
            val intent = Intent(this, ConnectUserStudentActivity::class.java)
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
    }
}