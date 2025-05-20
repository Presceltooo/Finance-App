import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.financeapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val depositLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val newBalance = result.data?.getFloatExtra("newBalance", 0f) ?: 0f
            updateBalance(newBalance)
        }
    }

    private val withdrawLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val newBalance = result.data?.getFloatExtra("newBalance", 0f) ?: 0f
            updateBalance(newBalance)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Khởi tạo số dư ban đầu
        val sharedPreferences = getSharedPreferences("FinanceApp", Context.MODE_PRIVATE)
        val initialBalance = sharedPreferences.getFloat("balance", 0f)
        updateBalance(initialBalance)

        // Xử lý sự kiện nút Deposit
        binding.depositBtn.setOnClickListener {
            val intent = Intent(this, DepositActivity::class.java)
            depositLauncher.launch(intent)
        }

        // Xử lý sự kiện nút Withdraw
        binding.withdrawBtn.setOnClickListener {
            val intent = Intent(this, WithdrawActivity::class.java)
            withdrawLauncher.launch(intent)
        }
    }

    private fun updateBalance(balance: Float) {
        // Cập nhật UI
        binding.textView7.text = String.format("$ %.2f", balance)
        
        // Lưu vào SharedPreferences
        val sharedPreferences = getSharedPreferences("FinanceApp", Context.MODE_PRIVATE)
        sharedPreferences.edit().putFloat("balance", balance).apply()
    }
} 