package com.example.streamed_app.client.network.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.streamed_app.R
import com.example.streamed_app.client.network.ApiService
import com.example.streamed_app.client.network.response.BaseResponse
import com.example.streamed_app.client.network.response.CourseResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CoursesAdapter(private var coursesList: List<CourseResponse>, private val context: Context, private val apiService: ApiService) :
    RecyclerView.Adapter<CoursesAdapter.CourseViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_my_courses_professor, parent, false)
        return CourseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val currentItem = coursesList[position]
        holder.bind(currentItem, holder.itemView.context)
    }

    override fun getItemCount(): Int {
        return coursesList.size
    }

    fun updateCourses(newCourses: List<CourseResponse>) {
        coursesList = newCourses
        notifyDataSetChanged()
    }

    inner class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.textViewCourseName)
        private val themeTextView: TextView = itemView.findViewById(R.id.textViewCourseDirection)
        private val durationTextView: TextView = itemView.findViewById(R.id.textViewCourseDuration)
        private val teacherNameTextView: TextView = itemView.findViewById(R.id.textViewTeacherName)
        private val deleteButton: Button = itemView.findViewById(R.id.buttonDeleteCourse)

        @SuppressLint("SetTextI18n")
        fun bind(course: CourseResponse, context: Context) {
            nameTextView.text = course.name
            themeTextView.text = course.theme
            durationTextView.text = course.duration

            val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
            val userName = sharedPreferences.getString("USER_NAME", "")
            val userSurname = sharedPreferences.getString("USER_SURNAME", "")
            teacherNameTextView.text = "$userName $userSurname"

            // Обработка нажатия на кнопку удаления курса
            deleteButton.setOnClickListener {
                val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                val jwtToken = sharedPreferences.getString("JWT_TOKEN", "")

                if (!jwtToken.isNullOrEmpty()) {
                    Log.d("DeleteCourse", "Attempting to delete course with ID: ${course.id}, JWT Token: $jwtToken")

                    apiService.deleteCourse(course.id, jwtToken)
                        .enqueue(object : Callback<BaseResponse> {
                            override fun onResponse(
                                call: Call<BaseResponse>,
                                response: Response<BaseResponse>
                            ) {
                                if (response.isSuccessful) {
                                    Log.d("DeleteCourse", "Course deleted successfully")
                                    // Обновление списка курсов после удаления
                                    val updatedCourses = coursesList.filter { it.id!= course.id }
                                    updateCourses(updatedCourses)
                                } else {
                                    Log.e("DeleteCourse", "Failed to delete course, server responded with status: ${response.code()}, message: ${response.message()}")
                                }
                            }

                            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                                Log.e("DeleteCourse", "Request failed to delete course, exception: ${t.message}", t)
                            }
                        })
                } else {
                    Log.e("DeleteCourse", "JWT token is missing")
                }
            }
        }
    }
}

