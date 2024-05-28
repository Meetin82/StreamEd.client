package com.example.streamed_app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ProfileUserStudentActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile_user_student)
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

        val buttonCatalog = findViewById<Button>(R.id.buttonCatalog)
        buttonCatalog.setOnClickListener{
            val intent = Intent(this, CatologCoursesActivity::class.java)
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

        val buttonEditProfile = findViewById<Button>(R.id.buttonEditProfile)
        buttonEditProfile.setOnClickListener{
            val intent = Intent(this, EditingProfileUserActivity::class.java)
            startActivity(intent)
        }

        val buttonExitFromAcc = findViewById<Button>(R.id.buttonExitFromAcc)
        buttonExitFromAcc.setOnClickListener{
            val intent = Intent(this, LoginUserActivity::class.java)
            startActivity(intent)
        }
    }
}