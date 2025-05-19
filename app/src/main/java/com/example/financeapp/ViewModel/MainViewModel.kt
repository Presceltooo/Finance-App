package com.example.financeapp.ViewModel

import androidx.lifecycle.ViewModel
import com.example.financeapp.Repository.MainRepository

class MainViewModel(val repository: MainRepository) : ViewModel() {
    constructor() : this(MainRepository()) {
        // Initialize any other properties or perform setup here
    }
    fun loadData() = repository.items
    fun loadBudget() = repository.budget
}