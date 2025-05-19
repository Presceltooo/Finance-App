package com.example.financeapp.Activity

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.financeapp.Adapter.TransactionAdapter
import com.example.financeapp.Domain.TransactionDomain
import com.example.financeapp.databinding.ActivityDepositBinding
import com.google.android.material.snackbar.Snackbar
import java.text.NumberFormat
import java.util.Locale
import java.util.UUID

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

        // Nhận số dư từ MainActivity
        currentBalance = intent.getDoubleExtra("currentBalance", 0.0)
        updateBalanceDisplay()

        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        // Hiển thị số dư hiện tại
        updateBalanceDisplay()

        // Thiết lập RecyclerView cho lịch sử giao dịch
        transactionAdapter = TransactionAdapter(transactions)
        binding.transactionsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.transactionsRecyclerView.adapter = transactionAdapter

        // Thêm một số giao dịch mẫu
        addSampleTransactions()
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

        // Cập nhật số dư
        currentBalance += amount
        updateBalanceDisplay()
        
        // Xóa input
        binding.amountEditText.text?.clear()
        binding.paymentMethodGroup.clearCheck()

        showSuccess("Successfully deposited ${formatAmount(amount)} via $method")

        // Trả về số dư mới cho MainActivity và finish
        val resultIntent = android.content.Intent().apply {
            putExtra("newBalance", currentBalance)
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

    private fun addSampleTransactions() {
        val sampleTransactions = listOf(
            TransactionDomain(
                id = UUID.randomUUID().toString(),
                amount = 500.0,
                type = "DEPOSIT",
                method = "BANK_CARD",
                status = "SUCCESS",
                date = java.util.Date(System.currentTimeMillis() - 86400000), // Yesterday
                description = "Deposit via Bank Card"
            ),
            TransactionDomain(
                id = UUID.randomUUID().toString(),
                amount = 1000.0,
                type = "DEPOSIT",
                method = "E_WALLET",
                status = "SUCCESS",
                date = java.util.Date(System.currentTimeMillis() - 172800000), // 2 days ago
                description = "Deposit via E-Wallet"
            )
        )
        transactions.addAll(sampleTransactions)
        transactionAdapter.notifyDataSetChanged()
    }
} 