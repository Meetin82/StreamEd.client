package com.example.streamed_app.client.activities

import android.annotation.SuppressLint
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

class EditingProfileUserActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editing_profile_user)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val editTextNameForEditting = findViewById<EditText>(R.id.editTextNameForEditting)
        editTextNameForEditting.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                (v as EditText).text.clear()
            }
        }

        val editTextSurnameForEditting = findViewById<EditText>(R.id.editTextSurnameForEditting)
        editTextSurnameForEditting.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                (v as EditText).text.clear()
            }
        }

        val editTextMailForEditting = findViewById<EditText>(R.id.editTextMailForEditting)
        editTextMailForEditting.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                (v as EditText).text.clear()
            }
        }

        val buttonEditProfileInEdittingPage = findViewById<Button>(R.id.buttonEditProfileInEdittingPage)
        buttonEditProfileInEdittingPage.setOnClickListener{
            val intent = Intent(this, ProfileUserStudentActivity::class.java)
            startActivity(intent)
        }

        val buttonBack = findViewById<Button>(R.id.buttonBack)
        buttonBack.setOnClickListener{
            val intent = Intent(this, ProfileUserStudentActivity::class.java)
            startActivity(intent)
        }
    }
}