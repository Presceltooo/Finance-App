package com.example.financeapp.Repository

import com.example.financeapp.Domain.ExpenseDomain
import com.example.financeapp.Domain.BudgetDomain

class MainRepository {
    val items= mutableListOf(
        ExpenseDomain("Restaurant", 76.50, "img1", "15 Jun 2023 19:30"),
        ExpenseDomain("Grocery", 124.37, "img2", "12 Jun 2023 10:15"),
        ExpenseDomain("Transportation", 35.90, "img3", "10 Jun 2023 08:45"),
        ExpenseDomain("Entertainment", 89.99, "img1", "05 Jun 2023 20:00"),
    )

    val budget= mutableListOf(
        BudgetDomain("Food", 300.0, 40.0),
        BudgetDomain("Transport", 150.0, 20.0),
        BudgetDomain("Entertainment", 200.0, 26.7),
        BudgetDomain("Groceries", 100.0, 13.3),
    )
}