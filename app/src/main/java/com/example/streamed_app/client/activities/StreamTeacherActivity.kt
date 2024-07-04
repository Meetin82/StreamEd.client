package com.example.streamed_app.client.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.streamed_app.R
import com.example.streamed_app.client.models.AddCommentRequest
import com.example.streamed_app.client.network.ApiService
import com.example.streamed_app.client.network.RetrofitClient
import com.example.streamed_app.client.network.adapters.CommentsAdapterForTeacher
import com.example.streamed_app.client.network.response.BaseResponse
import com.example.streamed_app.client.network.response.CommentResponse
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("DEPRECATION", "NAME_SHADOWING")
class StreamTeacherActivity : AppCompatActivity() {
    private lateinit var player: ExoPlayer
    private lateinit var audioManager: AudioManager
    private lateinit var audioFocusRequest: AudioManager.OnAudioFocusChangeListener
    private lateinit var playerView: PlayerView
    private lateinit var recyclerViewComments: RecyclerView
    private lateinit var commentsAdapter: CommentsAdapterForTeacher
    private lateinit var apiService: ApiService

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_stream_teacher)

        apiService = RetrofitClient.createApiService(this)

        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        audioFocusRequest = AudioManager.OnAudioFocusChangeListener { focusChange ->
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(insets.left, insets.top, insets.right, insets.bottom)
            WindowInsetsCompat.CONSUMED
        }

        playerView = findViewById(R.id.player_view)
        player = ExoPlayer.Builder(this).build()
        playerView.player = player

        val buttonExitFromStream = findViewById<Button>(R.id.buttonExitFromStream)
        buttonExitFromStream.setOnClickListener {
            player.stop()
            releaseAudioFocus()
            val intent = Intent(this, ConnectUserTeacherActivity::class.java)
            startActivity(intent)
        }

        val rtmpUrl = intent.getStringExtra("rtmp_url") ?: return
        val webinarId = intent.getIntExtra("webinar_id", -1)
        if (webinarId == -1) return

        preparePlayer(rtmpUrl)

        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val jwtToken = sharedPreferences.getString("JWT_TOKEN", "")

        val editTextSendMsgInChat = findViewById<EditText>(R.id.editTextSendMsgInChat)
        setEditTextFocusListener(editTextSendMsgInChat, "Введите ваш комментарий")
        val buttonSendMessage = findViewById<Button>(R.id.buttonSendMessage)
        recyclerViewComments = findViewById(R.id.recyclerViewComments)
        recyclerViewComments.layoutManager = LinearLayoutManager(this)
        commentsAdapter = CommentsAdapterForTeacher(mutableListOf(), apiService, jwtToken.toString())
        recyclerViewComments.adapter = commentsAdapter

        buttonSendMessage.setOnClickListener {
            val commentText = editTextSendMsgInChat.text.toString()
            if (commentText.isNotEmpty()) {
                val addCommentRequest = AddCommentRequest(
                    webinarId = webinarId,
                    text = commentText,
                    isAnon = false
                )

                if (jwtToken != null) {
                    apiService.addComment(jwtToken, addCommentRequest).enqueue(object : Callback<BaseResponse> {
                        override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                            if (response.isSuccessful) {
                                editTextSendMsgInChat.text.clear()
                                loadComments(webinarId)
                            } else {
                                Log.e("Error", "Failed to add comment")
                            }
                        }

                        override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                            Log.e("Error", t.message.toString())
                        }
                    })
                }
            }
        }

        loadComments(webinarId)
    }

    private fun loadComments(webinarId: Int) {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val jwtToken = sharedPreferences.getString("JWT_TOKEN", "")
        if (jwtToken != null) {
            apiService.getAllComments(webinarId, jwtToken).enqueue(object : Callback<List<CommentResponse>> {
                override fun onResponse(call: Call<List<CommentResponse>>, response: Response<List<CommentResponse>>) {
                    if (response.isSuccessful) {
                        commentsAdapter.setComments(response.body() ?: emptyList())
                    } else {
                        Log.e("Error", "Failed to load comments")
                    }
                }

                override fun onFailure(call: Call<List<CommentResponse>>, t: Throwable) {
                    Log.e("Error", t.message.toString())
                }
            })
        }
    }

    private fun preparePlayer(rtmpUrl: String) {
        try {
            val mediaItem = MediaItem.fromUri(rtmpUrl)
            player.setMediaItem(mediaItem)
            player.prepare()
            player.playWhenReady = true
        } catch (e: Exception) {
            Log.e("StreamError", "Ошибка при подготовке потока: ${e.message}", e)
        }
    }

    override fun onStart() {
        super.onStart()
        player.playWhenReady = true
    }

    override fun onStop() {
        super.onStop()
        player.playWhenReady = false
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }

    private fun releaseAudioFocus() {
        audioManager.abandonAudioFocus(audioFocusRequest)
    }

    private fun setEditTextFocusListener(editText: EditText, hint: String, isPassword: Boolean = false) {
        editText.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus && editText.text.toString() == hint) {
                editText.setText("")
                editText.setTextColor(resources.getColor(android.R.color.white))
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
