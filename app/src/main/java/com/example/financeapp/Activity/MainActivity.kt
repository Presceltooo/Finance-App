package com.example.financeapp.Activity

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.financeapp.ViewModel.MainViewModel
import com.example.financeapp.databinding.ActivityMainBinding
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.financeapp.Adapter.ExpenseListAdapter
import android.view.View
import android.view.ViewGroup
import eightbitlab.com.blurview.RenderScriptBlur
import android.view.ViewOutlineProvider
import com.example.financeapp.R
import com.example.financeapp.Adapter.TransactionAdapter
import com.example.financeapp.Domain.TransactionDomain
import android.content.Context

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()
    private var userName: String = "Do Duy Tung"
    private var userEmail: String = "doduytungitel@gmail.com"
    private var userBalance: Double = 3224.34
    private val transactions = mutableListOf<TransactionDomain>()
    private lateinit var transactionAdapter: TransactionAdapter

    private val profileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.let { data ->
                userName = data.getStringExtra("newName") ?: userName
                userEmail = data.getStringExtra("newEmail") ?: userEmail
                updateUserInfo()
            }
        }
    }

    private val depositLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.let { data ->
                val newBalance = data.getDoubleExtra("newBalance", userBalance)
                userBalance = newBalance
                updateBalanceDisplay()
                // Nhận giao dịch mới
                val newTransaction = data.getParcelableExtra<TransactionDomain>("newTransaction")
                if (newTransaction != null) {
                    transactions.add(0, newTransaction)
                    transactionAdapter.notifyItemInserted(0)
                }
            }
        }
    }

    private val withdrawLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.let { data ->
                val newBalance = data.getDoubleExtra("newBalance", userBalance)
                userBalance = newBalance
                updateBalanceDisplay()
                val newTransaction = data.getParcelableExtra<TransactionDomain>("newTransaction")
                if (newTransaction != null) {
                    transactions.add(0, newTransaction)
                    transactionAdapter.notifyItemInserted(0)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        initRecyclerView()
        setBlueEffect()
        setVariable()
        updateUserInfo()
        updateBalanceDisplay()
        initTransactionRecyclerView()
    }

    private fun updateUserInfo() {
        binding.textView.text = "Hello $userName"
        binding.textView2.text = userEmail
    }

    private fun setVariable() {
        binding.cardBtn.setOnClickListener { 
            startActivity(Intent(this, ReportActivity::class.java))
        }

        // Thêm sự kiện click cho nút Profile
        binding.root.findViewById<View>(R.id.profile_layout)?.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java).apply {
                putExtra("currentName", userName)
                putExtra("currentEmail", userEmail)
            }
            profileLauncher.launch(intent)
        }

        // Thêm sự kiện click cho nút Deposit
        binding.depositBtn.setOnClickListener {
            val intent = Intent(this, DepositActivity::class.java)
            intent.putExtra("currentBalance", userBalance)
            depositLauncher.launch(intent)
        }

        // Thêm sự kiện click cho nút Withdraw (ví dụ withdrawBtn)
        binding.withdrawBtn.setOnClickListener {
            val intent = Intent(this, WithdrawActivity::class.java)
            intent.putExtra("currentBalance", userBalance)
            withdrawLauncher.launch(intent)
        }

        // Thêm sự kiện click cho nút Bookmark (ví dụ bookmarkBtn)
        binding.bookmarkBtn.setOnClickListener {
            val intent = Intent(this, BookmarkActivity::class.java)
            intent.putExtra("currentBalance", userBalance)
            startActivity(intent)
        }
    }

    private fun setBlueEffect() {
        val radius = 10f
        val decorView = this.window.decorView
        val rootView = decorView.findViewById<View>(android.R.id.content) as ViewGroup
        val windowBackground = decorView.background
        binding.blueView.setupWith(
            rootView,
            RenderScriptBlur(this)
        )
            .setFrameClearDrawable(windowBackground)
            .setBlurRadius(radius)

        binding.blueView.setOutlineProvider(ViewOutlineProvider.BACKGROUND)
        binding.blueView.clipToOutline = true
    }

    private fun initRecyclerView() {
        binding.view1.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.view1.adapter = ExpenseListAdapter(mainViewModel.loadData())
        binding.view1.isNestedScrollingEnabled = false
    }

    private fun updateBalanceDisplay() {
        binding.textView6.text = "Your Balance"
        binding.textView7.text = formatAmount(userBalance)
    }

    private fun formatAmount(amount: Double): String {
        return java.text.NumberFormat.getCurrencyInstance(java.util.Locale.US).format(amount)
    }

    private fun initTransactionRecyclerView() {
        // Giả sử bạn có 1 RecyclerView trong layout, ví dụ: binding.transactionRecyclerView
        // Nếu chưa có, hãy thêm vào layout activity_main.xml
        transactionAdapter = TransactionAdapter(transactions)
        binding.view1.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.view1.adapter = transactionAdapter
        binding.view1.isNestedScrollingEnabled = false
    }

    override fun onResume() {
        super.onResume()
        loadBalanceAndTransactions()
    }

    private fun loadBalanceAndTransactions() {
        // Đọc lại số dư từ SharedPreferences
        val sharedPref = getSharedPreferences("user_balance", Context.MODE_PRIVATE)
        val balance = sharedPref.getFloat("balance", 0f)
        userBalance = balance.toDouble()
        updateBalanceDisplay()

        // Đọc lại lịch sử giao dịch từ SharedPreferences "transaction_history"
        val transPref = getSharedPreferences("transaction_history", Context.MODE_PRIVATE)
        val gson = com.google.gson.Gson()
        val json = transPref.getString("transactions", null)
        val type = object : com.google.gson.reflect.TypeToken<MutableList<com.example.financeapp.Domain.TransactionDomain>>() {}.type
        transactions.clear()
        if (json != null) {
            val list: MutableList<com.example.financeapp.Domain.TransactionDomain> = gson.fromJson(json, type)
            transactions.addAll(list)
        }
        transactionAdapter.notifyDataSetChanged()
    }
}

