package com.example.streamed_app.client.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.streamed_app.R
import com.example.streamed_app.client.models.ResetPassRequest
import com.example.streamed_app.client.network.RetrofitClient
import com.example.streamed_app.client.network.response.BaseResponse
import io.appmetrica.analytics.AppMetrica
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("DEPRECATION")
class ForgetPasswordActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_forget_password)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val editTextEmailAddressForget = findViewById<EditText>(R.id.editTextEmailAddressForget)
        setEditTextFocusListener(editTextEmailAddressForget, "Эл. почта")

        val buttonForgetNext = findViewById<Button>(R.id.buttonForgetNext)
        buttonForgetNext.setOnClickListener {
            val email = editTextEmailAddressForget.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, "Введите адрес электронной почты", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val resetPassRequest = ResetPassRequest(email)
            val apiService = RetrofitClient.createApiService(this)
            apiService.resetPasswordRequest(resetPassRequest).enqueue(object :
                Callback<BaseResponse> {
                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        val intent =
                            Intent(this@ForgetPasswordActivity, ForgetPasswordActivity2::class.java)
                        intent.putExtra("email", email)
                        AppMetrica.reportEvent("send_recovery_code")
                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            this@ForgetPasswordActivity,
                            "Ошибка при отправке запроса на сброс пароля",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    Toast.makeText(
                        this@ForgetPasswordActivity,
                        "Ошибка сети: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

            val buttonBack = findViewById<Button>(R.id.buttonBack)
            buttonBack.setOnClickListener {
                val intent = Intent(this, LoginUserActivity::class.java)
                startActivity(intent)
            }
        }
    }
    private fun setEditTextFocusListener(
        editText: EditText,
        hint: String,
        isPassword: Boolean = false
    ) {
        editText.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus && editText.text.toString() == hint) {
                editText.setText("")
                editText.setTextColor(resources.getColor(android.R.color.black))
                if (isPassword) {
                    editText.inputType =
                        android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
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
