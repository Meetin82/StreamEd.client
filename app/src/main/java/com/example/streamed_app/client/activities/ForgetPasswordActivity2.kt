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
import com.example.streamed_app.client.models.VerifyAndUpdPassRequest
import com.example.streamed_app.client.network.RetrofitClient
import com.example.streamed_app.client.network.response.BaseResponse
import io.appmetrica.analytics.AppMetrica
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("DEPRECATION")
class ForgetPasswordActivity2 : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_forget_password2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val email = intent.getStringExtra("email") ?: return
        val editCode = findViewById<EditText>(R.id.editCodePassForget)
        val editPassNew1 = findViewById<EditText>(R.id.editPassNew1)
        val editPassNew2 = findViewById<EditText>(R.id.editPassNew2)

        setEditTextFocusListener(editCode, "Введите код из письма")
        setEditTextFocusListener(editPassNew1, "Новый пароль")
        setEditTextFocusListener(editPassNew2, "Повторите пароль")

        val buttonBack = findViewById<Button>(R.id.buttonBack)
        buttonBack.setOnClickListener{
            val intent = Intent(this, ForgetPasswordActivity::class.java)
            startActivity(intent)
        }

        val buttonSendAgain = findViewById<Button>(R.id.buttonSendAgain)
        buttonSendAgain.setOnClickListener {
            val resetPassRequest = ResetPassRequest(email)
            val apiService = RetrofitClient.createApiService(this)
            apiService.resetPasswordRequest(resetPassRequest).enqueue(object : Callback<BaseResponse> {
                override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(this@ForgetPasswordActivity2, "Код для сброса пароля отправлен повторно", Toast.LENGTH_SHORT).show()
                        AppMetrica.reportEvent("send_recovery_code")
                    } else {
                        Toast.makeText(this@ForgetPasswordActivity2, "Ошибка при повторной отправке кода", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    Toast.makeText(this@ForgetPasswordActivity2, "Ошибка сети: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        val buttonForgetPassNext2 = findViewById<Button>(R.id.buttonForgetPassNext2)
        buttonForgetPassNext2.setOnClickListener{
            val resetToken = editCode.text.toString().trim()
            val password = editPassNew1.text.toString().trim()
            val confirmPassword = editPassNew2.text.toString().trim()
            if (password != confirmPassword) {
                Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length <= 8) {
                Toast.makeText(this, "Пароль должен быть больше 8 символов", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val verifyAndUpdPassRequest = VerifyAndUpdPassRequest(email, resetToken, password)
            val apiService = RetrofitClient.createApiService(this)
            apiService.resetPassword(verifyAndUpdPassRequest).enqueue(object : Callback<BaseResponse> {
                override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(this@ForgetPasswordActivity2, "Пароль успешно обновлен", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@ForgetPasswordActivity2, LoginUserActivity::class.java)
                        AppMetrica.reportEvent("recovery_succes")
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@ForgetPasswordActivity2, "Неправильный код сброса", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    Toast.makeText(this@ForgetPasswordActivity2, "Ошибка сети: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
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