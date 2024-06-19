@file:Suppress("DEPRECATION")

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
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.util.Log

class StreamStudentActivity : AppCompatActivity() {
    private lateinit var player: SimpleExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_stream_student)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        player = ExoPlayer.Builder(this).build() as SimpleExoPlayer

        val editTextSendMsgInChat = findViewById<EditText>(R.id.editTextSendMsgInChat)
        editTextSendMsgInChat.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                (v as EditText).text.clear()
            }
        }

        val buttonCheckMembers = findViewById<Button>(R.id.buttonCheckMembers)
        buttonCheckMembers.setOnClickListener{
            val intent = Intent(this, MembersStreamingActivity::class.java)
            startActivity(intent)
        }

        val buttonExitFromStream = findViewById<Button>(R.id.buttonExitFromStream)
        buttonExitFromStream.setOnClickListener{
            val intent = Intent(this, ConnectUserStudentActivity::class.java)
            startActivity(intent)
        }

        // Получение URL RTMP из Intent и подготовка проигрывателя
        val rtmpUrl = intent.getStringExtra("rtmp_url")?: return
        preparePlayer(rtmpUrl)
    }

    private fun preparePlayer(rtmpUrl: String) {
        try {
            val mediaItem = MediaItem.fromUri(rtmpUrl)
            player.setMediaItem(mediaItem)
            player.prepare()
            player.playWhenReady = true
        } catch (e: Exception) {
            Log.e("StreamError", "Ошибка при подготовке потока: ${e.message}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
}
