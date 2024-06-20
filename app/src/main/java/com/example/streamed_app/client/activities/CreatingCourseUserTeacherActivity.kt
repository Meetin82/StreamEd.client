package com.example.streamed_app.client.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.streamed_app.R
import com.example.streamed_app.client.models.AddCourseRequest
import com.example.streamed_app.client.network.ApiService
import com.example.streamed_app.client.network.RetrofitClient
import com.example.streamed_app.client.network.response.BaseResponse
import io.appmetrica.analytics.AppMetrica
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreatingCourseUserTeacherActivity : AppCompatActivity() {

    private lateinit var apiService: ApiService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_creating_course_user_teacher)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        apiService = RetrofitClient.createApiService(this)


        val editTextNameCourse = findViewById<EditText>(R.id.editTextNameCourse)
        editTextNameCourse.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                (v as EditText).text.clear()
            }
        }

        val editTextCostingCourse = findViewById<EditText>(R.id.editTextCostingCourse)
        editTextCostingCourse.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                (v as EditText).text.clear()
            }
        }

        val editTextLongTimeCourse = findViewById<EditText>(R.id.editTextLongTimeCourse)
        editTextLongTimeCourse.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                (v as EditText).text.clear()
            }
        }

        val editTextDirectionCourse = findViewById<EditText>(R.id.editTextDirectionCourse)
        editTextDirectionCourse.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                (v as EditText).text.clear()
            }
        }

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

        val buttonCalendar = findViewById<Button>(R.id.buttonCalendar)
        buttonCalendar.setOnClickListener{
            val intent = Intent(this, LessonsUserTeacherActivity::class.java)
            startActivity(intent)
        }

        val buttonCourses = findViewById<Button>(R.id.buttonCourses)
        buttonCourses.setOnClickListener{
            createCourse()
            val intent = Intent(this, MyCoursesUserTeacherActivity::class.java)
            startActivity(intent)
        }

        val buttonAddCreatedCourse = findViewById<Button>(R.id.buttonAddCreatedCourse)
        buttonAddCreatedCourse.setOnClickListener{
            val intent = Intent(this, MyCoursesUserTeacherActivity::class.java)
            createCourse()
            startActivity(intent)
        }
    }

    private fun createCourse() {
        val name = findViewById<EditText>(R.id.editTextNameCourse).text.toString()
        val price = findViewById<EditText>(R.id.editTextCostingCourse).text.toString()
        val duration = findViewById<EditText>(R.id.editTextLongTimeCourse).text.toString()
        val theme = findViewById<EditText>(R.id.editTextDirectionCourse).text.toString()

        Log.d("CreateCourse", "Название курса: $name")
        Log.d("CreateCourse", "Цена курса: $price")
        Log.d("CreateCourse", "Продолжительность курса: $duration")
        Log.d("CreateCourse", "Тема курса: $theme")

        val courseRequest = AddCourseRequest(duration, price, theme, name, "Курс активен")
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val jwtToken = sharedPreferences.getString("JWT_TOKEN", "") ?: ""

        Log.d("CreateCourse", "JWT Token: $jwtToken")
        Log.d("CreateCourse", "Course Request: $courseRequest")

        RetrofitClient.createApiService(this).createCourse(jwtToken, courseRequest).enqueue(object : Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                Log.d("CreateCourse", "Response code: ${response.code()}")
                Log.d("CreateCourse", "Response body: ${response.body()}")
                if (response.isSuccessful) {
                    val baseResponse = response.body()
                    if (baseResponse?.success == true) {
                        Toast.makeText(this@CreatingCourseUserTeacherActivity, "Курс успешно создан", Toast.LENGTH_SHORT).show()
                        Log.d("CreateCourse", "Курс успешно создан")
                        AppMetrica.reportEvent("add_course")
                        finish()
                    } else {
                        Toast.makeText(this@CreatingCourseUserTeacherActivity, baseResponse?.message ?: "Ошибка создания курса", Toast.LENGTH_SHORT).show()
                        Log.e("CreateCourse", "Ошибка создания курса: ${baseResponse?.message}")
                    }
                } else {
                    Log.e("CreateCourse", "Server Error: ${response.errorBody()?.string()}")
                    Toast.makeText(this@CreatingCourseUserTeacherActivity, "Ошибка сервера", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                Log.e("CreateCourse", "Ошибка соединения: ${t.message}", t)
                Toast.makeText(this@CreatingCourseUserTeacherActivity, "Ошибка соединения", Toast.LENGTH_SHORT).show()
            }
        })
    }
}