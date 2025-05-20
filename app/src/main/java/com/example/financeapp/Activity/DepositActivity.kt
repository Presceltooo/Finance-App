package com.example.financeapp.Activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.financeapp.Adapter.TransactionAdapter
import com.example.financeapp.Domain.TransactionDomain
import com.example.financeapp.databinding.ActivityDepositBinding
import com.google.android.material.snackbar.Snackbar
import java.text.NumberFormat
import java.util.Locale
import java.util.UUID
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DepositActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDepositBinding
    private var currentBalance: Double = 3224.34
    private lateinit var transactionAdapter: TransactionAdapter
    private val transactions = mutableListOf<TransactionDomain>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDepositBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        // Nhận số dư từ com.example.financeapp.Activity.MainActivity
        currentBalance = intent.getDoubleExtra("currentBalance", 0.0)
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
        // Hiển thị số dư hiện tại
        updateBalanceDisplay()

        // Thiết lập RecyclerView cho lịch sử giao dịch
        transactionAdapter = TransactionAdapter(transactions)
        binding.transactionsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.transactionsRecyclerView.adapter = transactionAdapter
    }

    private fun setupListeners() {
        binding.depositButton.setOnClickListener {
            val amount = binding.amountEditText.text.toString().toDoubleOrNull()
            val selectedMethod = when (binding.paymentMethodGroup.checkedRadioButtonId) {
                binding.bankCardRadio.id -> "BANK_CARD"
                binding.eWalletRadio.id -> "E_WALLET"
                binding.bankTransferRadio.id -> "BANK_TRANSFER"
                else -> null
            }

            when {
                amount == null -> {
                    showError("Please enter a valid amount")
                }
                amount <= 0 -> {
                    showError("Amount must be greater than 0")
                }
                selectedMethod == null -> {
                    showError("Please select a payment method")
                }
                else -> {
                    processDeposit(amount, selectedMethod)
                }
            }
        }
    }

    private fun processDeposit(amount: Double, method: String) {
        // Tạo giao dịch mới
        val transaction = TransactionDomain(
            id = UUID.randomUUID().toString(),
            amount = amount,
            type = "DEPOSIT",
            method = method,
            status = "SUCCESS",
            date = java.util.Date(),
            description = "Deposit via $method"
        )

        // Thêm giao dịch vào danh sách
        transactions.add(0, transaction)
        transactionAdapter.notifyItemInserted(0)

        // Lưu lại lịch sử giao dịch toàn cục
        saveToGlobalTransactionHistory(transaction)

        // Cập nhật số dư
        currentBalance += amount
        updateBalanceDisplay()

        // Lưu số dư mới vào SharedPreferences
        saveBalance(currentBalance)

        // Xóa input
        binding.amountEditText.text?.clear()
        binding.paymentMethodGroup.clearCheck()

        showSuccess("Successfully deposited ${formatAmount(amount)} via $method")

        // Trả về số dư mới cho com.example.financeapp.Activity.MainActivity và finish
        val resultIntent = android.content.Intent().apply {
            putExtra("newBalance", currentBalance)
            putExtra("newTransaction", transaction)
        }
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    private fun saveBalance(balance: Double) {
        val sharedPref = getSharedPreferences("user_balance", Context.MODE_PRIVATE)
        sharedPref.edit().putFloat("balance", balance.toFloat()).apply()
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
            // Chỉ lấy các giao dịch DEPOSIT
            transactions.addAll(list.filter { it.type == "DEPOSIT" })
        }
    }
}