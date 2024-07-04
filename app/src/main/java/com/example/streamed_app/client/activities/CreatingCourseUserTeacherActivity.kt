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

@Suppress("DEPRECATION")
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
        val editTextCostingCourse = findViewById<EditText>(R.id.editTextCostingCourse)
        val editTextLongTimeCourse = findViewById<EditText>(R.id.editTextLongTimeCourse)
        val editTextDirectionCourse = findViewById<EditText>(R.id.editTextDirectionCourse)

        setEditTextFocusListener(editTextNameCourse, "Название")
        setEditTextFocusListener(editTextCostingCourse, "Стоимость")
        setEditTextFocusListener(editTextLongTimeCourse, "Длительность")
        setEditTextFocusListener(editTextDirectionCourse, "Направление")

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

        val courseRequest = AddCourseRequest(duration, price, theme, name, "Курс активен")
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val jwtToken = sharedPreferences.getString("JWT_TOKEN", "") ?: ""


        RetrofitClient.createApiService(this).createCourse(jwtToken, courseRequest).enqueue(object : Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (response.isSuccessful) {
                    val baseResponse = response.body()
                    if (baseResponse?.success == true) {
                        Toast.makeText(this@CreatingCourseUserTeacherActivity, "Курс успешно создан", Toast.LENGTH_SHORT).show()
                        AppMetrica.reportEvent("add_course")
                        finish()
                    } else {
                        Toast.makeText(this@CreatingCourseUserTeacherActivity, baseResponse?.message ?: "Ошибка создания курса", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@CreatingCourseUserTeacherActivity, "Ошибка сервера", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                Log.e("CreateCourse", "Ошибка соединения: ${t.message}", t)
                Toast.makeText(this@CreatingCourseUserTeacherActivity, "Ошибка соединения", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setEditTextFocusListener(editText: EditText, hint: String, isPassword: Boolean = false) {
        editText.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus && editText.text.toString() == hint) {
                editText.setText("")
                editText.setTextColor(resources.getColor(android.R.color.black))
                if (isPassword) {
                    editText.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                }
            } else if (!hasFocus && editText.text.toString().isEmpty()) {
                editText.setText(hint)
                editText.setTextColor(resources.getColor(android.R.color.darker_gray))
                if (isPassword) {
                    editText.inputType = android.text.InputType.TYPE_CLASS_TEXT
                }
            }
        }
    }
}