package com.example.streamed_app.client.network.adapters

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.streamed_app.R
import com.example.streamed_app.client.network.response.WebinarResponse

class WebinarAdapter(private val webinars: List<WebinarResponse>) :
    RecyclerView.Adapter<WebinarAdapter.WebinarViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WebinarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_webinar, parent, false)
        return WebinarViewHolder(view, parent.context)
    }

    override fun onBindViewHolder(holder: WebinarViewHolder, position: Int) {
        val webinar = webinars[position]
        holder.bind(webinar)
        holder.itemView.findViewById<Button>(R.id.buttonCopyCode).apply {
            visibility = View.VISIBLE
            setOnClickListener {
                copyTextToClipboard(holder.itemView.context, webinar.inviteCode)
            }
        }
    }

    override fun getItemCount(): Int = webinars.size

    class WebinarViewHolder(itemView: View, private val context: Context) : RecyclerView.ViewHolder(itemView) {
        private val textDateCourse: TextView = itemView.findViewById(R.id.textDateCourse)
        private val textNamingCourseInList: TextView = itemView.findViewById(R.id.textNamingCourseInList)
        private val textCodeMain: TextView = itemView.findViewById(R.id.textCodeMain)

        fun bind(webinar: WebinarResponse) {
            textDateCourse.text = webinar.date
            textNamingCourseInList.text = webinar.name
            textCodeMain.text = "Код руководителя: ${webinar.inviteCode}"
        }
    }

    private fun copyTextToClipboard(context: Context, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("label", text)
        clipboard.setPrimaryClip(clip)
    }

}