# Finance App

A simple personal finance management application developed in Kotlin for Android.

## Team

Đỗ Duy Tùng - 22110197    Hoàng Đức Việt - 22110178

## Main Features

### 1. Balance Management
- Display current balance
- Automatic balance update after each transaction
- Store balance in SharedPreferences

### 2. Deposit
- Support multiple payment methods:
  - Bank Card
  - E-Wallet
  - Bank Transfer
- Save deposit transaction history
- Display success/failure notifications

### 3. Withdraw
- Support withdrawal through:
  - Bank Account
  - E-Wallet
- Require destination account information
- Balance check before withdrawal
- Save withdrawal transaction history

### 4. Transaction Management
- Display transaction history
- Categorize transactions by type (deposit/withdraw)
- Store detailed transaction information:
  - Transaction ID
  - Amount
  - Transaction Type
  - Method
  - Status
  - Date & Time
  - Description

### 5. User Interface
- Modern design with Blur effect
- Display user information
- Time-sorted transaction list
- Intuitive notifications via Snackbar

.....

## Technologies Used
- Kotlin
- Android Jetpack
- ViewBinding
- RecyclerView
- SharedPreferences
- Gson
- BlurView

## System Requirements
- Android 5.0 (API level 21) or higher
- Kotlin 1.5.0 or higher
- Android Studio Arctic Fox or higher 
