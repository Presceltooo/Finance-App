package com.example.financeapp.Activity

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.financeapp.databinding.ActivityProfileBinding
import android.util.Patterns
import com.google.android.material.snackbar.Snackbar

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        // Lấy thông tin hiện tại từ MainActivity
        val currentName = intent.getStringExtra("currentName") ?: ""
        val currentEmail = intent.getStringExtra("currentEmail") ?: ""

        // Hiển thị thông tin hiện tại
        binding.nameEditText.setText(currentName)
        binding.emailEditText.setText(currentEmail)

        // Xử lý sự kiện khi nhấn nút Save
        binding.saveButton.setOnClickListener {
            val newName = binding.nameEditText.text.toString().trim()
            val newEmail = binding.emailEditText.text.toString().trim()

            // Kiểm tra dữ liệu đầu vào
            when {
                newName.isBlank() -> {
                    showError("Please enter your name")
                    binding.nameInputLayout.error = "Name is required"
                    return@setOnClickListener
                }
                newEmail.isBlank() -> {
                    showError("Please enter your email")
                    binding.emailInputLayout.error = "Email is required"
                    return@setOnClickListener
                }
                !isValidEmail(newEmail) -> {
                    showError("Please enter a valid email address")
                    binding.emailInputLayout.error = "Invalid email format"
                    return@setOnClickListener
                }
            }

            // Xóa thông báo lỗi nếu có
            binding.nameInputLayout.error = null
            binding.emailInputLayout.error = null

            // Trả về kết quả cho MainActivity
            val resultIntent = Intent().apply {
                putExtra("newName", newName)
                putExtra("newEmail", newEmail)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }
} 