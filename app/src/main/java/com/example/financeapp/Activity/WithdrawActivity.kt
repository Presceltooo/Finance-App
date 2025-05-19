package com.example.financeapp.Activity

import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.financeapp.Adapter.TransactionAdapter
import com.example.financeapp.Domain.TransactionDomain
import com.example.financeapp.databinding.ActivityWithdrawBinding
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.NumberFormat
import java.util.Locale
import java.util.UUID

class WithdrawActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWithdrawBinding
    private var currentBalance: Double = 3000.0
    private lateinit var transactionAdapter: TransactionAdapter
    private val transactions = mutableListOf<TransactionDomain>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWithdrawBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        // Nhận số dư từ MainActivity nếu có
        currentBalance = intent.getDoubleExtra("currentBalance", 3000.0)
        updateBalanceDisplay()

        // Đọc lịch sử giao dịch từ SharedPreferences
        loadTransactions()

        setupUI()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        // Luôn lấy số dư mới nhất từ SharedPreferences
        val sharedPref = getSharedPreferences("user_balance", Context.MODE_PRIVATE)
        currentBalance = sharedPref.getFloat("balance", 0f).toDouble()
        updateBalanceDisplay()
        // Luôn load lại lịch sử giao dịch
        loadTransactions()
        transactionAdapter.notifyDataSetChanged()
    }

    private fun setupUI() {
        updateBalanceDisplay()
        transactionAdapter = TransactionAdapter(transactions)
        binding.transactionsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.transactionsRecyclerView.adapter = transactionAdapter
    }

    private fun setupListeners() {
        binding.withdrawButton.setOnClickListener {
            val amount = binding.amountEditText.text.toString().toDoubleOrNull()
            val selectedMethod = when (binding.withdrawMethodGroup.checkedRadioButtonId) {
                binding.bankRadio.id -> "BANK"
                binding.eWalletRadio.id -> "E_WALLET"
                else -> null
            }
            val accountNumber = binding.accountNumberEditText.text.toString().trim()
            val accountName = binding.accountNameEditText.text.toString().trim()

            when {
                amount == null -> showError("Please enter a valid amount")
                amount <= 0 -> showError("Amount must be greater than 0")
                amount > currentBalance -> showError("Insufficient balance")
                selectedMethod == null -> showError("Please select a withdraw method")
                accountNumber.isBlank() -> showError("Please enter account/wallet number")
                accountName.isBlank() -> showError("Please enter account holder name")
                else -> processWithdraw(amount, selectedMethod, accountNumber, accountName)
            }
        }
    }

    private fun processWithdraw(amount: Double, method: String, accountNumber: String, accountName: String) {
        // Tạo giao dịch mới
        val transaction = TransactionDomain(
            id = UUID.randomUUID().toString(),
            amount = amount,
            type = "WITHDRAW",
            method = method,
            status = "SUCCESS",
            date = java.util.Date(),
            description = "Withdraw to $method: $accountNumber ($accountName)"
        )
        transactions.add(0, transaction)
        transactionAdapter.notifyItemInserted(0)
        saveToGlobalTransactionHistory(transaction)

        // Cập nhật số dư
        currentBalance -= amount
        updateBalanceDisplay()

        // Xóa input
        binding.amountEditText.text?.clear()
        binding.withdrawMethodGroup.clearCheck()
        binding.accountNumberEditText.text?.clear()
        binding.accountNameEditText.text?.clear()

        showSuccess("Successfully withdrew ${formatAmount(amount)} to $method")

        // Trả về số dư mới và transaction cho MainActivity
        val resultIntent = android.content.Intent().apply {
            putExtra("newBalance", currentBalance)
            putExtra("newTransaction", transaction)
        }
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    private fun updateBalanceDisplay() {
        binding.currentBalanceText.text = "Current Balance: ${formatAmount(currentBalance)}"
    }

    private fun formatAmount(amount: Double): String {
        return NumberFormat.getCurrencyInstance(Locale.US).format(amount)
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun showSuccess(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun saveToGlobalTransactionHistory(transaction: TransactionDomain) {
        val sharedPref = getSharedPreferences("transaction_history", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPref.getString("transactions", null)
        val type = object : TypeToken<MutableList<TransactionDomain>>() {}.type
        val list: MutableList<TransactionDomain> = if (json != null) gson.fromJson(json, type) else mutableListOf()
        list.add(0, transaction)
        sharedPref.edit().putString("transactions", gson.toJson(list)).apply()
    }

    private fun loadTransactions() {
        val sharedPref = getSharedPreferences("transaction_history", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPref.getString("transactions", null)
        transactions.clear()
        if (json != null) {
            val type = object : TypeToken<MutableList<TransactionDomain>>() {}.type
            val list: MutableList<TransactionDomain> = gson.fromJson(json, type)
            // Chỉ lấy các giao dịch WITHDRAW
            transactions.addAll(list.filter { it.type == "WITHDRAW" })
        }
    }
} 