package com.example.streamed_app.client.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.streamed_app.R
import io.appmetrica.analytics.AppMetrica

class UnloginCoursesActivity : AppCompatActivity() {
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

        val buttonUnlogin1 = findViewById<Button>(R.id.buttonLoginInCourse1)
        buttonUnlogin1.setOnClickListener{
            val intent = Intent(this, LoginUserActivity::class.java)
            startActivity(intent)
            AppMetrica.reportEvent("screen_login")
        }

        val buttonUnlogin2 = findViewById<Button>(R.id.buttonLoginInCourse2)
        buttonUnlogin2.setOnClickListener{
            val intent = Intent(this, LoginUserActivity::class.java)
            startActivity(intent)
            AppMetrica.reportEvent("screen_login")
        }
    }
}