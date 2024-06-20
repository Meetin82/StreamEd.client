package com.example.streamed_app.client.network.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.streamed_app.R
import com.example.streamed_app.client.network.response.CourseResponse

class CoursesAdapterStud(
    private var courses: List<CourseResponse>,
    private val subscribeClickListener: (Int) -> Unit
) : RecyclerView.Adapter<CoursesAdapterStud.CourseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_course, parent, false)
        return CourseViewHolder(view)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        holder.bind(courses[position])
    }

    override fun getItemCount(): Int = courses.size

    inner class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textNameCourse: TextView = itemView.findViewById(R.id.textNameCourse)
//        private val textNameTeacher: TextView = itemView.findViewById(R.id.textNameTeacher)
        private val textDescCourse: TextView = itemView.findViewById(R.id.textDescCourse)
        private val textTimeCourse: TextView = itemView.findViewById(R.id.textTimeCourse)
        private val buttonSubscribe: Button = itemView.findViewById(R.id.buttonSubscribeOnCourse)
        private val imageViewCheck: ImageView = itemView.findViewById(R.id.imageViewCheck)
        private val textSubscribedMessage: TextView = itemView.findViewById(R.id.textSubscribedMessage)

        @SuppressLint("SetTextI18n")
        fun bind(course: CourseResponse) {
            textNameCourse.text = course.name
//            textNameTeacher.text = course.ownerId.toString() // Измените на корректное значение, если требуется
            textDescCourse.text = course.theme
            textTimeCourse.text = course.duration

            if (course.subscribed) {
                imageViewCheck.visibility = View.VISIBLE
                buttonSubscribe.visibility = View.GONE
                textSubscribedMessage.visibility = View.VISIBLE
            } else {
                imageViewCheck.visibility = View.GONE
                buttonSubscribe.visibility = View.VISIBLE
                textSubscribedMessage.visibility = View.GONE
            }

            buttonSubscribe.setOnClickListener {
                subscribeClickListener.invoke(course.id)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateCourses(newCourses: List<CourseResponse>) {
        courses = newCourses.sortedByDescending { it.subscribed }
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateSubscription(courseId: Int, subscribed: Boolean) {
        val updatedCourses = courses.map { course ->
            if (course.id == courseId) {
                course.copy(subscribed = subscribed)
            } else {
                course
            }
        }
        courses = updatedCourses.sortedByDescending { it.subscribed }
        notifyDataSetChanged()
    }
}
