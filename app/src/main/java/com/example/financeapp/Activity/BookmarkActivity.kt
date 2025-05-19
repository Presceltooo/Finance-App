package com.example.financeapp.Activity

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.financeapp.Adapter.BookmarkAdapter
import com.example.financeapp.Domain.BookmarkTransaction
import com.example.financeapp.Domain.TransactionDomain
import com.example.financeapp.databinding.ActivityBookmarkBinding
import com.example.financeapp.databinding.DialogAddBookmarkBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.view.LayoutInflater

class BookmarkActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookmarkBinding
    private val pendingList = mutableListOf<BookmarkTransaction>()
    private val doneList = mutableListOf<BookmarkTransaction>()
    private lateinit var pendingAdapter: BookmarkAdapter
    private lateinit var doneAdapter: BookmarkAdapter
    private val handler = Handler(Looper.getMainLooper())
    private val checkInterval: Long = 30_000 // 30 giây kiểm tra 1 lần

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookmarkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadBookmarks()
        setupRecyclerViews()
        setupAddButton()
        checkAndUpdateTransactions()
    }

    private fun setupRecyclerViews() {
        pendingAdapter = BookmarkAdapter(pendingList,
            onEdit = { bookmark -> showEditBookmarkDialog(bookmark) },
            onDelete = { bookmark -> confirmDeleteBookmark(bookmark) }
        )
        doneAdapter = BookmarkAdapter(doneList, { }, { })
        binding.pendingRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.pendingRecyclerView.adapter = pendingAdapter
        binding.doneRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.doneRecyclerView.adapter = doneAdapter
    }

    private fun setupAddButton() {
        binding.addBookmarkBtn.setOnClickListener {
            showAddBookmarkDialog()
        }
    }

    private fun showAddBookmarkDialog() {
        val dialogBinding = DialogAddBookmarkBinding.inflate(LayoutInflater.from(this))
        val dialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .create()

        var scheduledMillis: Long? = null
        val calendar = Calendar.getInstance()

        dialogBinding.dateBtn.setOnClickListener {
            val now = Calendar.getInstance()
            DatePickerDialog(this, { _, y, m, d ->
                calendar.set(Calendar.YEAR, y)
                calendar.set(Calendar.MONTH, m)
                calendar.set(Calendar.DAY_OF_MONTH, d)
                dialogBinding.scheduledTimeTxt.text = "Ngày: %02d/%02d/%04d".format(d, m+1, y)
            }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)).show()
        }
        dialogBinding.timeBtn.setOnClickListener {
            val now = Calendar.getInstance()
            TimePickerDialog(this, { _, h, min ->
                calendar.set(Calendar.HOUR_OF_DAY, h)
                calendar.set(Calendar.MINUTE, min)
                calendar.set(Calendar.SECOND, 0)
                dialogBinding.scheduledTimeTxt.text =
                    (dialogBinding.scheduledTimeTxt.text.toString() + "  Giờ: %02d:%02d".format(h, min))
            }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true).show()
        }

        dialogBinding.saveBtn.setOnClickListener {
            // Validate
            val type = when (dialogBinding.typeRadioGroup.checkedRadioButtonId) {
                dialogBinding.depositRadio.id -> "DEPOSIT"
                dialogBinding.withdrawRadio.id -> "WITHDRAW"
                else -> null
            }
            val amount = dialogBinding.amountEditText.text.toString().toDoubleOrNull()
            val method = when (dialogBinding.methodRadioGroup.checkedRadioButtonId) {
                dialogBinding.bankRadio.id -> "BANK"
                dialogBinding.eWalletRadio.id -> "E_WALLET"
                else -> null
            }
            val accountNumber = dialogBinding.accountNumberEditText.text.toString().takeIf { it.isNotBlank() }
            val accountName = dialogBinding.accountNameEditText.text.toString().takeIf { it.isNotBlank() }
            scheduledMillis = calendar.timeInMillis

            when {
                type == null -> dialogBinding.typeRadioGroup.requestFocus()
                amount == null || amount <= 0 -> dialogBinding.amountEditText.error = "Enter a valid amount"
                method == null -> dialogBinding.methodRadioGroup.requestFocus()
                scheduledMillis == null || scheduledMillis!! < System.currentTimeMillis() -> dialogBinding.scheduledTimeTxt.error = "Choose a valid date & time"
                else -> {
                    val bookmark = BookmarkTransaction(
                        id = UUID.randomUUID().toString(),
                        type = type,
                        amount = amount,
                        method = method,
                        accountNumber = accountNumber,
                        accountName = accountName,
                        scheduledTime = scheduledMillis!!,
                        status = "PENDING"
                    )
                    pendingList.add(0, bookmark)
                    pendingAdapter.notifyItemInserted(0)
                    saveBookmarks()
                    dialog.dismiss()
                }
            }
        }
        dialog.show()
    }

    private fun loadBookmarks() {
        val sharedPref = getSharedPreferences("bookmark_history", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPref.getString("bookmarks", null)
        val allList = if (json != null) {
            val type = object : TypeToken<MutableList<BookmarkTransaction>>() {}.type
            gson.fromJson<MutableList<BookmarkTransaction>>(json, type)
        } else mutableListOf()
        pendingList.clear()
        doneList.clear()
        val now = System.currentTimeMillis()
        for (item in allList) {
            if (item.status == "DONE" || (item.status == "PENDING" && item.scheduledTime <= now)) {
                doneList.add(item.copy(status = "DONE"))
            } else {
                pendingList.add(item.copy(status = "PENDING"))
            }
        }
    }

    private fun saveBookmarks() {
        val sharedPref = getSharedPreferences("bookmark_history", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        val gson = Gson()
        val allList = mutableListOf<BookmarkTransaction>()
        allList.addAll(pendingList)
        allList.addAll(doneList)
        val json = gson.toJson(allList)
        editor.putString("bookmarks", json)
        editor.apply()
    }

    private fun checkAndUpdateTransactions() {
        val now = System.currentTimeMillis()
        val iterator = pendingList.iterator()
        var changed = false
        while (iterator.hasNext()) {
            val item = iterator.next()
            if (item.scheduledTime <= now) {
                iterator.remove()
                doneList.add(0, item.copy(status = "DONE"))
                changed = true
                // Lưu vào lịch sử giao dịch (transaction_history)
                saveTransactionHistory(item)
                // Cập nhật số dư
                updateUserBalance(item)
            }
        }
        if (changed) {
            pendingAdapter.notifyDataSetChanged()
            doneAdapter.notifyDataSetChanged()
            saveBookmarks()
        }
        handler.removeCallbacksAndMessages(null)
        handler.postDelayed({ checkAndUpdateTransactions() }, checkInterval)
    }

    private fun saveTransactionHistory(bookmark: BookmarkTransaction) {
        val sharedPref = getSharedPreferences("transaction_history", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPref.getString("transactions", null)
        val type = object : TypeToken<MutableList<TransactionDomain>>() {}.type
        val list: MutableList<TransactionDomain> = if (json != null) gson.fromJson(json, type) else mutableListOf()
        // Chuyển BookmarkTransaction thành TransactionDomain
        val transaction = TransactionDomain(
            id = bookmark.id,
            amount = bookmark.amount,
            type = bookmark.type,
            method = bookmark.method,
            status = "SUCCESS",
            date = java.util.Date(bookmark.scheduledTime),
            description = if (bookmark.type == "DEPOSIT") "Deposit (Bookmark)" else "Withdraw (Bookmark)"
        )
        list.add(0, transaction)
        val editor = sharedPref.edit()
        editor.putString("transactions", gson.toJson(list))
        editor.apply()
    }

    private fun updateUserBalance(bookmark: BookmarkTransaction) {
        val sharedPref = getSharedPreferences("user_balance", Context.MODE_PRIVATE)
        val currentBalance = sharedPref.getFloat("balance", 0f)
        val newBalance = when (bookmark.type) {
            "DEPOSIT" -> currentBalance + bookmark.amount.toFloat()
            "WITHDRAW" -> currentBalance - bookmark.amount.toFloat()
            else -> currentBalance
        }
        sharedPref.edit().putFloat("balance", newBalance).apply()
    }

    private fun showEditBookmarkDialog(bookmark: BookmarkTransaction) {
        val dialogBinding = DialogAddBookmarkBinding.inflate(LayoutInflater.from(this))
        val dialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .create()

        val calendar = Calendar.getInstance().apply { timeInMillis = bookmark.scheduledTime }
        // Set dữ liệu cũ
        if (bookmark.type == "DEPOSIT") dialogBinding.depositRadio.isChecked = true
        else dialogBinding.withdrawRadio.isChecked = true
        dialogBinding.amountEditText.setText(bookmark.amount.toString())
        if (bookmark.method == "BANK") dialogBinding.bankRadio.isChecked = true
        else dialogBinding.eWalletRadio.isChecked = true
        dialogBinding.accountNumberEditText.setText(bookmark.accountNumber ?: "")
        dialogBinding.accountNameEditText.setText(bookmark.accountName ?: "")
        dialogBinding.scheduledTimeTxt.text = formatDateTime(bookmark.scheduledTime)

        dialogBinding.dateBtn.setOnClickListener {
            val now = Calendar.getInstance()
            DatePickerDialog(this, { _, y, m, d ->
                calendar.set(Calendar.YEAR, y)
                calendar.set(Calendar.MONTH, m)
                calendar.set(Calendar.DAY_OF_MONTH, d)
                dialogBinding.scheduledTimeTxt.text = "Ngày: %02d/%02d/%04d".format(d, m+1, y)
            }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)).show()
        }
        dialogBinding.timeBtn.setOnClickListener {
            val now = Calendar.getInstance()
            TimePickerDialog(this, { _, h, min ->
                calendar.set(Calendar.HOUR_OF_DAY, h)
                calendar.set(Calendar.MINUTE, min)
                calendar.set(Calendar.SECOND, 0)
                dialogBinding.scheduledTimeTxt.text =
                    (dialogBinding.scheduledTimeTxt.text.toString() + "  Giờ: %02d:%02d".format(h, min))
            }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true).show()
        }

        dialogBinding.saveBtn.setOnClickListener {
            val type = when (dialogBinding.typeRadioGroup.checkedRadioButtonId) {
                dialogBinding.depositRadio.id -> "DEPOSIT"
                dialogBinding.withdrawRadio.id -> "WITHDRAW"
                else -> null
            }
            val amount = dialogBinding.amountEditText.text.toString().toDoubleOrNull()
            val method = when (dialogBinding.methodRadioGroup.checkedRadioButtonId) {
                dialogBinding.bankRadio.id -> "BANK"
                dialogBinding.eWalletRadio.id -> "E_WALLET"
                else -> null
            }
            val accountNumber = dialogBinding.accountNumberEditText.text.toString().takeIf { it.isNotBlank() }
            val accountName = dialogBinding.accountNameEditText.text.toString().takeIf { it.isNotBlank() }
            val scheduledMillis = calendar.timeInMillis

            when {
                type == null -> dialogBinding.typeRadioGroup.requestFocus()
                amount == null || amount <= 0 -> dialogBinding.amountEditText.error = "Enter a valid amount"
                method == null -> dialogBinding.methodRadioGroup.requestFocus()
                scheduledMillis < System.currentTimeMillis() -> dialogBinding.scheduledTimeTxt.error = "Choose a valid date & time"
                else -> {
                    // Cập nhật lại bookmark
                    val index = pendingList.indexOfFirst { it.id == bookmark.id }
                    if (index != -1) {
                        pendingList[index] = bookmark.copy(
                            type = type,
                            amount = amount,
                            method = method,
                            accountNumber = accountNumber,
                            accountName = accountName,
                            scheduledTime = scheduledMillis
                        )
                        pendingAdapter.notifyItemChanged(index)
                        saveBookmarks()
                    }
                    dialog.dismiss()
                }
            }
        }
        dialog.show()
    }

    private fun confirmDeleteBookmark(bookmark: BookmarkTransaction) {
        AlertDialog.Builder(this)
            .setTitle("Delete Bookmark")
            .setMessage("Are you sure you want to delete this scheduled transaction?")
            .setPositiveButton("Delete") { _, _ ->
                val index = pendingList.indexOfFirst { it.id == bookmark.id }
                if (index != -1) {
                    pendingList.removeAt(index)
                    pendingAdapter.notifyItemRemoved(index)
                    saveBookmarks()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun formatDateTime(millis: Long): String {
        val sdf = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(millis))
    }

    override fun onResume() {
        super.onResume()
        loadBookmarks()
        pendingAdapter.notifyDataSetChanged()
        doneAdapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
} 