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

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()
    private var userName: String = "Do Duy Tung"
    private var userEmail: String = "doduytungitel@gmail.com"
    private var userBalance: Double = 3224.34

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
        binding.imageView5.setOnClickListener {
            val intent = Intent(this, DepositActivity::class.java)
            intent.putExtra("currentBalance", userBalance)
            depositLauncher.launch(intent)
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
}

