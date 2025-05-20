import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.financeapp.databinding.ActivityDepositBinding

class DepositActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDepositBinding
    private lateinit var sharedPreferences: android.content.SharedPreferences
    private var currentBalance: Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDepositBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Đọc số dư từ SharedPreferences
        sharedPreferences = getSharedPreferences("FinanceApp", Context.MODE_PRIVATE)
        updateBalance()

        // Xử lý sự kiện nút Deposit
        binding.depositButton.setOnClickListener {
            performDeposit()
        }
    }

    private fun updateBalance() {
        currentBalance = sharedPreferences.getFloat("balance", 0f)
        binding.currentBalanceText.text = String.format("Current Balance: $%.2f", currentBalance)
    }

    private fun performDeposit() {
        val amountStr = binding.amountEditText.text.toString()
        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Please enter amount", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountStr.toFloatOrNull()
        if (amount == null || amount <= 0) {
            Toast.makeText(this, "Please enter valid amount", Toast.LENGTH_SHORT).show()
            return
        }

        // Cập nhật số dư
        val newBalance = currentBalance + amount
        sharedPreferences.edit().putFloat("balance", newBalance).apply()
        
        // Cập nhật UI
        updateBalance()
        
        // Thông báo thành công
        Toast.makeText(this, "Deposit successful!", Toast.LENGTH_SHORT).show()
        
        // Gửi kết quả về MainActivity
        val resultIntent = Intent()
        resultIntent.putExtra("newBalance", newBalance)
        setResult(RESULT_OK, resultIntent)
        
        // Quay lại màn hình chính
        finish()
    }
} 