package com.example.financeapp.Activity

import android.R
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
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

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()

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
    }
    private fun setVariable() {
        binding.cardBtn.setOnClickListener{ startActivity(Intent(this, ReportActivity::class.java) ) }
    }

    private fun setBlueEffect(){
        val radius = 10f
        val decorView = this.window.decorView
        val rootView = decorView.findViewById<View>(R.id.content) as ViewGroup
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
}

