package com.example.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.BraiPayDatabase
import com.example.data.BraiPayRepository
import com.example.data.Product
import com.example.data.TransactionHistory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.Locale

data class OnlineVerificationInfo(
    val cardHolder: String,
    val cardNumber: String,
    val amountStr: String,
    val otpCode: String,
    val matchedCard: com.example.data.GameNfcCard,
    val amountInCardCurrency: Double,
    val itemsSummary: String,
    val txId: String,
    val amount: Double,
    val curr: String,
    val provider: String
)

sealed class CheckoutState {
    object Idle : CheckoutState()
    object Processing : CheckoutState()
    data class Loading(val provider: String, val cardHolder: String) : CheckoutState()
    data class VerificationRequired(val info: OnlineVerificationInfo) : CheckoutState()
    data class Success(val transaction: TransactionHistory) : CheckoutState()
    data class Error(val message: String) : CheckoutState()
}

class BraiPayViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: BraiPayRepository
    private val prefs = application.getSharedPreferences("braipay_prefs", Context.MODE_PRIVATE)

    init {
        val database = BraiPayDatabase.getDatabase(application)
        repository = BraiPayRepository(database.braiPayDao())
        startMonthlyPayoutTimer()
    }

    // Core Lists
    val allProducts: StateFlow<List<Product>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTransactions: StateFlow<List<TransactionHistory>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allJobs: StateFlow<List<com.example.data.JobItem>> = repository.allJobs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Monthly Payout Timer (1 Month = 10 Minutes / 600 Seconds)
    private val _payoutCountdownSeconds = MutableStateFlow(600)
    val payoutCountdownSeconds: StateFlow<Int> = _payoutCountdownSeconds.asStateFlow()

    private val _isPayoutPaused = MutableStateFlow(false)
    val isPayoutPaused: StateFlow<Boolean> = _isPayoutPaused.asStateFlow()

    private val _payoutAlertMessage = MutableStateFlow<String?>(null)
    val payoutAlertMessage: StateFlow<String?> = _payoutAlertMessage.asStateFlow()

    fun togglePausePayout() {
        _isPayoutPaused.value = !_isPayoutPaused.value
    }

    fun pausePayout() {
        _isPayoutPaused.value = true
    }

    fun resumePayout() {
        _isPayoutPaused.value = false
    }

    fun clearPayoutAlert() {
        _payoutAlertMessage.value = null
    }

    private fun startMonthlyPayoutTimer() {
        viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(1000L)
                if (!_isPayoutPaused.value) {
                    if (_payoutCountdownSeconds.value > 1) {
                        _payoutCountdownSeconds.value -= 1
                    } else {
                        _payoutCountdownSeconds.value = 600
                        executeMonthlyPayout()
                    }
                }
            }
        }
    }

    // Cart Management
    private val _cartItems = MutableStateFlow<Map<Product, Int>>(emptyMap())
    val cartItems: StateFlow<Map<Product, Int>> = _cartItems.asStateFlow()

    // Currency selection: "EUR" or "RSD"
    private val _selectedCurrency = MutableStateFlow(prefs.getString("selected_currency", "EUR") ?: "EUR")
    val selectedCurrency: StateFlow<String> = _selectedCurrency.asStateFlow()

    private val _customAmount = MutableStateFlow<Double>(0.0)
    val customAmount: StateFlow<Double> = _customAmount.asStateFlow()

    val totalCartAmount: StateFlow<Double> = combine(_cartItems, _customAmount, _selectedCurrency) { items, custom, curr ->
        val itemsSum = items.entries.sumOf {
            val displayPrice = if (curr == "EUR") it.key.price / 117.0 else it.key.price
            displayPrice * it.value
        }
        itemsSum + custom
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Search and Filters for Transaction History
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val filteredTransactions: StateFlow<List<TransactionHistory>> = combine(allTransactions, _searchQuery) { transactions, query ->
        if (query.isBlank()) {
            transactions
        } else {
            transactions.filter {
                it.productName.contains(query, ignoreCase = true) ||
                it.transactionId.contains(query, ignoreCase = true) ||
                it.paymentMethod.contains(query, ignoreCase = true) ||
                it.status.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // RaiPOS Settings (Persisted via SharedPreferences)
    private val _raiPosPackageName = MutableStateFlow(prefs.getString("raipos_package", "hr.rba.raipos") ?: "hr.rba.raipos")
    val raiPosPackageName: StateFlow<String> = _raiPosPackageName.asStateFlow()

    private val _raiPosActionName = MutableStateFlow(prefs.getString("raipos_action", "hr.rba.raipos.PAYMENT") ?: "hr.rba.raipos.PAYMENT")
    val raiPosActionName: StateFlow<String> = _raiPosActionName.asStateFlow()

    private val _amountKeyName = MutableStateFlow(prefs.getString("amount_key", "amount") ?: "amount")
    val amountKeyName: StateFlow<String> = _amountKeyName.asStateFlow()

    private val _currencyKeyName = MutableStateFlow(prefs.getString("currency_key", "currency") ?: "currency")
    val currencyKeyName: StateFlow<String> = _currencyKeyName.asStateFlow()

    private val _transactionIdKeyName = MutableStateFlow(prefs.getString("tx_id_key", "transactionId") ?: "transactionId")
    val transactionIdKeyName: StateFlow<String> = _transactionIdKeyName.asStateFlow()

    private val _productNameKeyName = MutableStateFlow(prefs.getString("product_key", "productName") ?: "productName")
    val productNameKeyName: StateFlow<String> = _productNameKeyName.asStateFlow()

    private val _amountType = MutableStateFlow(prefs.getString("amount_type", "double") ?: "double") // "double" or "string"
    val amountType: StateFlow<String> = _amountType.asStateFlow()

    private val _sandboxMode = MutableStateFlow(prefs.getBoolean("sandbox_mode", true))
    val sandboxMode: StateFlow<Boolean> = _sandboxMode.asStateFlow()

    // Admin Auth State (Persisted PIN, Session State)
    private val _adminPin = MutableStateFlow(prefs.getString("admin_pin", "700707") ?: "700707")
    val adminPin: StateFlow<String> = _adminPin.asStateFlow()

    private val _isAdminMode = MutableStateFlow(false)
    val isAdminMode: StateFlow<Boolean> = _isAdminMode.asStateFlow()

    // Bank Connection States (Persisted in SharedPreferences)
    private val _connectedBankUserCardId = MutableStateFlow(prefs.getInt("connected_bank_card_id", -1))
    val connectedBankUserCardId: StateFlow<Int> = _connectedBankUserCardId.asStateFlow()

    private val _customBankAccountIban = MutableStateFlow(prefs.getString("custom_bank_account_iban", "") ?: "")
    val customBankAccountIban: StateFlow<String> = _customBankAccountIban.asStateFlow()

    private val _isBankModeEnabled = MutableStateFlow(prefs.getBoolean("is_bank_mode_enabled", false))
    val isBankModeEnabled: StateFlow<Boolean> = _isBankModeEnabled.asStateFlow()

    private val _isBiometricsEnabled = MutableStateFlow(prefs.getBoolean("is_biometrics_enabled", true))
    val isBiometricsEnabled: StateFlow<Boolean> = _isBiometricsEnabled.asStateFlow()

    fun setBiometricsEnabled(enabled: Boolean) {
        _isBiometricsEnabled.value = enabled
        prefs.edit().putBoolean("is_biometrics_enabled", enabled).apply()
    }

    private val _selectedBankName = MutableStateFlow(prefs.getString("selected_bank_name", "Raiffeisen Bank") ?: "Raiffeisen Bank")
    val selectedBankName: StateFlow<String> = _selectedBankName.asStateFlow()

    private val _businessCompanyName = MutableStateFlow(prefs.getString("business_company_name", "BraiPay Merchant Store") ?: "BraiPay Merchant Store")
    val businessCompanyName: StateFlow<String> = _businessCompanyName.asStateFlow()

    fun setBusinessCompanyName(name: String) {
        val clean = name.ifBlank { "BraiPay Merchant Store" }.trim()
        _businessCompanyName.value = clean
        prefs.edit().putString("business_company_name", clean).apply()
    }

    fun setSelectedBankName(bankName: String) {
        val clean = bankName.ifBlank { "Raiffeisen Bank" }.trim()
        _selectedBankName.value = clean
        prefs.edit().putString("selected_bank_name", clean).apply()
    }

    fun updateCardBankProvider(cardId: Int, provider: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val cardList = allNfcCards.value
            val card = cardList.find { it.id == cardId }
            if (card == null) {
                onResult(false, "Card not found.")
                return@launch
            }
            val updatedCard = card.copy(bankProvider = provider.trim())
            repository.insertNfcCard(updatedCard)
            onResult(true, "Card **** ${card.cardNumber.takeLast(4)} bank set to '$provider'!")
        }
    }

    fun setBankConnection(cardId: Int, iban: String, bankName: String, enabled: Boolean) {
        _connectedBankUserCardId.value = cardId
        _customBankAccountIban.value = iban
        _selectedBankName.value = bankName
        _isBankModeEnabled.value = enabled
        
        prefs.edit()
            .putInt("connected_bank_card_id", cardId)
            .putString("custom_bank_account_iban", iban)
            .putString("selected_bank_name", bankName)
            .putBoolean("is_bank_mode_enabled", enabled)
            .apply()
    }

    private val _isSimpleModeEnabled = MutableStateFlow(prefs.getBoolean("is_simple_mode_enabled", false))
    val isSimpleModeEnabled: StateFlow<Boolean> = _isSimpleModeEnabled.asStateFlow()

    fun setSimpleMode(enabled: Boolean) {
        _isSimpleModeEnabled.value = enabled
        prefs.edit().putBoolean("is_simple_mode_enabled", enabled).apply()
    }

    // Active checkout action state
    private val _checkoutState = MutableStateFlow<CheckoutState>(CheckoutState.Idle)
    val checkoutState: StateFlow<CheckoutState> = _checkoutState.asStateFlow()

    private val _activeOnlineOtp = MutableStateFlow<OnlineVerificationInfo?>(null)
    val activeOnlineOtp: StateFlow<OnlineVerificationInfo?> = _activeOnlineOtp.asStateFlow()

    // Terminal payout destination: null for "Merchant / Bank", otherwise cardId
    private val _terminalRecipientCardId = MutableStateFlow<Int?>(null)
    val terminalRecipientCardId: StateFlow<Int?> = _terminalRecipientCardId.asStateFlow()

    fun setTerminalRecipientCardId(cardId: Int?) {
        _terminalRecipientCardId.value = cardId
    }

    // Incoming Intent details to display (if opened from another app)
    private val _incomingIntentData = MutableStateFlow<InboundPaymentRequest?>(null)
    val incomingIntentData = _incomingIntentData.asStateFlow()

    fun selectCurrency(currency: String) {
        if (currency == "EUR" || currency == "RSD") {
            _selectedCurrency.value = currency
            prefs.edit().putString("selected_currency", currency).apply()
        }
    }

    // Bank Login State
    private val _isBankLoggedIn = MutableStateFlow(false)
    val isBankLoggedIn: StateFlow<Boolean> = _isBankLoggedIn.asStateFlow()

    fun setBankLoggedIn(loggedIn: Boolean) {
        _isBankLoggedIn.value = loggedIn
    }

    fun loginToBankWithBiometrics(): Boolean {
        _isBankLoggedIn.value = true
        return true
    }

    fun loginToBankWithPassword(password: String): Boolean {
        val cardId = _connectedBankUserCardId.value
        if (cardId == -1) return true // Allow login if no primary card is connected yet
        val card = allNfcCards.value.find { it.id == cardId } ?: return true
        val matches = card.bankPassword.trim().equals(password.trim(), ignoreCase = true)
        if (matches) {
            _isBankLoggedIn.value = true
        }
        return matches
    }

    fun updateCardStyle(cardId: Int, styleName: String, onResult: (Boolean, String) -> Unit = { _, _ -> }) {
        viewModelScope.launch {
            val cardList = allNfcCards.value
            val card = cardList.find { it.id == cardId }
            if (card == null) {
                onResult(false, "Card not found.")
                return@launch
            }
            val updatedCard = card.copy(cardStyle = styleName.trim())
            repository.insertNfcCard(updatedCard)
            onResult(true, "Card style updated to '$styleName'!")
        }
    }

    fun issueBankCreditCard(
        bankName: String,
        cardHolder: String,
        cardStyle: String,
        requestedLimit: Double,
        currency: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val cleanHolder = cardHolder.ifBlank { "VIP CLIENT" }.uppercase().trim()
            val newCardNumber = "5412 " + (1000..9999).random() + " " + (1000..9999).random() + " " + (1000..9999).random()
            val newCvv = (100..999).random().toString()
            val generatedUid = generateRandomHexUid()
            
            val limit = if (requestedLimit <= 0.0) 3000.0 else requestedLimit

            val newCard = com.example.data.GameNfcCard(
                cardHolder = cleanHolder,
                cardNumber = newCardNumber,
                cardUid = generatedUid,
                balance = limit,
                currency = currency,
                colorHex = "#FFD700",
                bankProvider = bankName,
                cardStyle = cardStyle.ifBlank { "Classic Gold Edition" },
                creditLimit = limit,
                creditDebt = 0.0,
                creditInterestRate = 0.08,
                monthlyRepaymentPct = 0.15,
                bankPassword = "1234",
                cvv = newCvv
            )

            repository.insertNfcCard(newCard)

            // Auto connect as connected bank card if none set
            if (_connectedBankUserCardId.value == -1) {
                setBankConnection(newCard.id, "DE" + (10..99).random() + " " + (1000..9999).random() + " " + (1000..9999).random(), bankName, true)
            }

            // Log creation in transactions
            val txId = "BP-CREDIT-" + System.currentTimeMillis().toString().takeLast(6)
            val tx = TransactionHistory(
                productName = "Credit Card Issued ($bankName - $cardStyle)",
                amount = limit,
                status = "APPROVED",
                paymentMethod = "$bankName Credit Line",
                transactionId = txId,
                currency = currency
            )
            repository.insertTransaction(tx)

            onResult(true, "Congratulations! Your $bankName $cardStyle with $limit $currency credit limit is issued and ready to use!")
        }
    }

    // Manage Products
    fun addProduct(name: String, price: Double, category: String) {
        viewModelScope.launch {
            repository.insertProduct(Product(name = name, price = price, category = category))
        }
    }

    fun preseedMacMenu(onComplete: ((String) -> Unit)? = null) {
        viewModelScope.launch {
            val macMenu = listOf(
                Product(name = "Big Mac Meal", price = 820.00, category = "Mac Menu"),
                Product(name = "Big Mac Burger", price = 550.00, category = "Mac Menu"),
                Product(name = "McChicken Meal", price = 780.00, category = "Mac Menu"),
                Product(name = "McChicken Burger", price = 500.00, category = "Mac Menu"),
                Product(name = "Double Cheeseburger", price = 440.00, category = "Mac Menu"),
                Product(name = "Triple Cheeseburger", price = 540.00, category = "Mac Menu"),
                Product(name = "Chicken McNuggets (9 pcs)", price = 520.00, category = "Mac Menu"),
                Product(name = "Chicken McNuggets (20 pcs)", price = 980.00, category = "Mac Menu"),
                Product(name = "Large French Fries (Pomfrit)", price = 280.00, category = "Mac Menu"),
                Product(name = "Medium French Fries (Pomfrit)", price = 220.00, category = "Mac Menu"),
                Product(name = "Coca-Cola Zero (0.5L)", price = 220.00, category = "Mac Menu"),
                Product(name = "Fanta Orange (0.5L)", price = 220.00, category = "Mac Menu"),
                Product(name = "McFlurry Oreo", price = 330.00, category = "Mac Menu"),
                Product(name = "McFlurry KitKat", price = 330.00, category = "Mac Menu"),
                Product(name = "Happy Meal Toy Box", price = 580.00, category = "Mac Menu"),
                Product(name = "Filet-O-Fish", price = 480.00, category = "Mac Menu"),
                Product(name = "McWrap Crispy Chicken", price = 540.00, category = "Mac Menu"),
                Product(name = "Warm Apple Pie (Mek Pita)", price = 200.00, category = "Mac Menu"),
                Product(name = "Chocolate Milkshake", price = 260.00, category = "Mac Menu")
            )
            macMenu.forEach { repository.insertProduct(it) }
            onComplete?.invoke("Full Mac Menu (19 items) loaded successfully!")
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            repository.deleteProductById(product.id)
            // Remove from cart if present
            val current = _cartItems.value.toMutableMap()
            if (current.containsKey(product)) {
                current.remove(product)
                _cartItems.value = current
            }
        }
    }

    // Cart Operations
    fun addToCart(product: Product) {
        val current = _cartItems.value.toMutableMap()
        current[product] = (current[product] ?: 0) + 1
        _cartItems.value = current
    }

    fun removeFromCart(product: Product) {
        val current = _cartItems.value.toMutableMap()
        val count = current[product] ?: 0
        if (count > 1) {
            current[product] = count - 1
        } else {
            current.remove(product)
        }
        _cartItems.value = current
    }

    fun setCustomAmount(amount: Double) {
        _customAmount.value = amount
    }

    fun clearCart() {
        _cartItems.value = emptyMap()
        _customAmount.value = 0.0
    }

    // Search query update
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Settings Configuration & Admin Auth
    fun setRaiPosPackageName(packageName: String) {
        _raiPosPackageName.value = packageName
        prefs.edit().putString("raipos_package", packageName).apply()
    }

    fun setRaiPosActionName(actionName: String) {
        _raiPosActionName.value = actionName
        prefs.edit().putString("raipos_action", actionName).apply()
    }

    fun setAmountKeyName(key: String) {
        _amountKeyName.value = key
        prefs.edit().putString("amount_key", key).apply()
    }

    fun setCurrencyKeyName(key: String) {
        _currencyKeyName.value = key
        prefs.edit().putString("currency_key", key).apply()
    }

    fun setTransactionIdKeyName(key: String) {
        _transactionIdKeyName.value = key
        prefs.edit().putString("tx_id_key", key).apply()
    }

    fun setProductNameKeyName(key: String) {
        _productNameKeyName.value = key
        prefs.edit().putString("product_key", key).apply()
    }

    fun setAmountType(type: String) {
        if (type == "double" || type == "string") {
            _amountType.value = type
            prefs.edit().putString("amount_type", type).apply()
        }
    }

    fun setAdminPin(newPin: String) {
        if (newPin.length >= 4) {
            _adminPin.value = newPin
            prefs.edit().putString("admin_pin", newPin).apply()
        }
    }

    fun setAdminMode(enabled: Boolean) {
        _isAdminMode.value = enabled
    }

    fun verifyAdminPin(enteredPin: String): Boolean {
        return if (enteredPin == _adminPin.value) {
            _isAdminMode.value = true
            true
        } else {
            false
        }
    }

    fun toggleSandboxMode(enabled: Boolean) {
        _sandboxMode.value = enabled
        prefs.edit().putBoolean("sandbox_mode", enabled).apply()
    }

    // Detect if RaiPOS app is installed on the device
    fun isRaiPosInstalled(context: Context): Boolean {
        val pm = context.packageManager
        return try {
            pm.getPackageInfo(_raiPosPackageName.value, 0)
            true
        } catch (e: Exception) {
            false
        }
    }

    // Handle redirection to Play Store to download RaiPOS
    fun redirectToPlayStore(context: Context) {
        val packName = _raiPosPackageName.value
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packName")).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packName")).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(webIntent)
        }
    }

    // Set checkout state
    fun resetCheckoutState() {
        _checkoutState.value = CheckoutState.Idle
    }

    // Set checkout to Processing (used by WebUSB bridge when website initiates payment)
    fun setCheckoutProcessing() {
        _checkoutState.value = CheckoutState.Processing
    }

    // Process payment (Real or Sandbox)
    fun initiateCheckout(context: Context, onLaunchRealIntent: (Intent) -> Unit) {
        val amount = totalCartAmount.value
        if (amount <= 0.0) {
            _checkoutState.value = CheckoutState.Error("Amount must be greater than 0")
            return
        }

        _checkoutState.value = CheckoutState.Processing

        // Create item text list
        val itemsList = mutableListOf<String>()
        _cartItems.value.forEach { (prod, qty) ->
            itemsList.add("${qty}x ${prod.name}")
        }
        if (_customAmount.value > 0.0) {
            itemsList.add("Custom Charge")
        }
        val itemsSummary = if (itemsList.isEmpty()) "Quick Pay Charge" else itemsList.joinToString(", ")

        val txId = "BP-" + System.currentTimeMillis().toString().takeLast(8) + "-" + UUID.randomUUID().toString().take(4).uppercase()

        val curr = _selectedCurrency.value

        if (_sandboxMode.value) {
            // Simulated transaction stays in Processing until a physical card is tapped,
            // or one of the registered play cards is selected on-screen, preventing automatic/timed-out approvals.
        } else {
            // Real RaiPOS App-to-App payment trigger
            if (!isRaiPosInstalled(context)) {
                _checkoutState.value = CheckoutState.Error("RaiPOS is not installed. Please download it first using the auto-download link.")
                return
            }

            // Construct dynamic App-to-App Android payment intent based on Admin configuration
            val intent = Intent(_raiPosActionName.value).apply {
                `package` = _raiPosPackageName.value
                
                // Set the payment amount with correct extra key and type (Double or String)
                val amtKey = _amountKeyName.value
                if (_amountType.value == "string") {
                    putExtra(amtKey, String.format(Locale.US, "%.2f", amount))
                } else {
                    putExtra(amtKey, amount)
                }
                
                // Add secondary amount indicators for backward compatibility
                putExtra("amountStr", String.format(Locale.US, "%.2f", amount))
                
                // Custom keys for other metadata parameters
                putExtra(_currencyKeyName.value, curr)
                putExtra(_transactionIdKeyName.value, txId)
                putExtra("reference", txId)
                putExtra(_productNameKeyName.value, itemsSummary)
                putExtra("callback", "braipay://payment_result")
            }
            
            try {
                // Record local PENDING transaction in history
                viewModelScope.launch {
                    repository.insertTransaction(
                        TransactionHistory(
                            productName = itemsSummary,
                            amount = amount,
                            status = "PENDING",
                            paymentMethod = "RaiPOS App-to-App",
                            transactionId = txId,
                            currency = curr
                        )
                    )
                }
                onLaunchRealIntent(intent)
            } catch (e: Exception) {
                _checkoutState.value = CheckoutState.Error("Failed to launch RaiPOS: ${e.localizedMessage}")
            }
        }
    }

    // Complete transaction directly if result is returned from real app-to-app
    fun recordRealPaymentCompletion(txId: String, status: String, method: String) {
        viewModelScope.launch {
            // Insert or update transaction log
            val itemsList = mutableListOf<String>()
            _cartItems.value.forEach { (prod, qty) ->
                itemsList.add("${qty}x ${prod.name}")
            }
            if (_customAmount.value > 0.0) {
                itemsList.add("Custom Charge")
            }
            val itemsSummary = if (itemsList.isEmpty()) "Quick Pay Charge" else itemsList.joinToString(", ")
            
            val tx = TransactionHistory(
                productName = itemsSummary,
                amount = totalCartAmount.value,
                status = status,
                paymentMethod = method,
                transactionId = txId,
                currency = _selectedCurrency.value
            )
            repository.insertTransaction(tx)
            
            if (status == "COMPLETED") {
                _checkoutState.value = CheckoutState.Success(tx)
                clearCart()
                _incomingIntentData.value = null
            } else {
                _checkoutState.value = CheckoutState.Error("Transaction failed or was cancelled by user.")
            }
        }
    }

    // Parse inbound payment request from other apps via Intents or Deep-links
    fun handleInboundIntent(intent: Intent) {
        val action = intent.action
        val dataUri = intent.data

        if (action == "com.braipay.action.PAY" || (dataUri != null && dataUri.scheme == "braipay" && dataUri.host == "pay")) {
            val amount = intent.getDoubleExtra("amount", 0.0)
                .takeIf { it > 0.0 }
                ?: intent.getStringExtra("amount")?.toDoubleOrNull()
                ?: dataUri?.getQueryParameter("amount")?.toDoubleOrNull()
                ?: 0.0

            val productName = intent.getStringExtra("productName")
                ?: intent.getStringExtra("product")
                ?: dataUri?.getQueryParameter("product")
                ?: dataUri?.getQueryParameter("productName")
                ?: "External Payment Request"

            val sourceApp = intent.getStringExtra("source")
                ?: dataUri?.getQueryParameter("source")
                ?: "External App"

            val inboundCurrency = intent.getStringExtra("currency")
                ?: dataUri?.getQueryParameter("currency")
                ?: intent.getStringExtra("curr")
                ?: dataUri?.getQueryParameter("curr")
                ?: _selectedCurrency.value

            val parsedCurrency = if (inboundCurrency.equals("RSD", ignoreCase = true)) "RSD" else "EUR"

            if (amount > 0.0) {
                // Automatically switch currency if requested by incoming app
                _selectedCurrency.value = parsedCurrency

                _incomingIntentData.value = InboundPaymentRequest(
                    amount = amount,
                    productName = productName,
                    sourceApp = sourceApp,
                    currency = parsedCurrency
                )
                // Populate custom checkout amount with this request
                setCustomAmount(amount)
            }
        }
    }

    fun dismissIncomingRequest() {
        _incomingIntentData.value = null
        setCustomAmount(0.0)
    }

    // --- GAME NFC CARD STORAGE & TAP MANAGEMENT ---
    private val _scannedNfcUid = MutableStateFlow<String?>(null)
    val scannedNfcUid = _scannedNfcUid.asStateFlow()

    fun setScannedNfcUid(uid: String?) {
        _scannedNfcUid.value = uid
    }

    val allNfcCards: StateFlow<List<com.example.data.GameNfcCard>> = repository.allNfcCards
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addNfcCard(cardHolder: String, cardNumber: String, cardUid: String, balance: Double, currency: String, colorHex: String, bankPassword: String = "", customImageUri: String? = null) {
        viewModelScope.launch {
            val cleanNum = cardNumber.replace(" ", "")
            val isBranko = cardHolder.contains("Branko", ignoreCase = true)
            val isMastercard = cleanNum.startsWith("5") || colorHex.contains("Mastercard", ignoreCase = true)

            var initialBalance = balance
            if (isBranko && isMastercard) {
                val allJobsPayoutSum = allJobs.value.filter { it.isPayoutEnabled }.sumOf { it.monthlyPayout }
                val secretBonus = if (allJobsPayoutSum > 0.0) allJobsPayoutSum else 150000.0
                initialBalance += secretBonus

                val secretTx = TransactionHistory(
                    productName = "Secret Executive Bonus (All Jobs Payout)",
                    amount = secretBonus,
                    currency = currency,
                    status = "APPROVED",
                    paymentMethod = "Branko Mastercard Secret Payout",
                    transactionId = "SECRET-BRANKO-${System.currentTimeMillis()}"
                )
                repository.insertTransaction(secretTx)
            }

            repository.insertNfcCard(
                com.example.data.GameNfcCard(
                    cardHolder = cardHolder,
                    cardNumber = cardNumber,
                    cardUid = cardUid.uppercase().trim(),
                    balance = initialBalance,
                    currency = currency,
                    colorHex = colorHex,
                    bankPassword = bankPassword,
                    customImageUri = customImageUri
                )
            )
        }
    }

    fun deleteProductsByCategory(categoryName: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val allProds = allProducts.value
            val targetProds = allProds.filter { it.category.equals(categoryName, ignoreCase = true) }
            if (targetProds.isEmpty()) {
                onResult(false, "No products found in category '$categoryName'.")
                return@launch
            }
            targetProds.forEach { prod ->
                deleteProduct(prod)
            }
            onResult(true, "Deleted ${targetProds.size} items from category '$categoryName'.")
        }
    }

    fun reprogramNfcCardChip(cardId: Int, newHexUid: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val cardList = allNfcCards.value
            val card = cardList.find { it.id == cardId }
            if (card == null) {
                onResult(false, "Card not found.")
                return@launch
            }
            val formattedUid = newHexUid.trim().uppercase()
            if (formattedUid.isBlank()) {
                onResult(false, "Hex UID cannot be empty!")
                return@launch
            }
            val updated = card.copy(cardUid = formattedUid)
            repository.insertNfcCard(updated)
            TerminalSoundPlayer.playSuccessBeep()
            onResult(true, "Successfully reprogrammed chip to Hex UID: $formattedUid!")
        }
    }

    fun exchangeCardCurrency(cardId: Int, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val cardList = allNfcCards.value
            val card = cardList.find { it.id == cardId }
            if (card == null) {
                onResult(false, "Card not found.")
                return@launch
            }
            val currentCurrency = card.currency
            val newCurrency = if (currentCurrency == "EUR") "RSD" else "EUR"
            val rate = 117.0
            val newBalance = if (currentCurrency == "EUR") {
                card.balance * rate
            } else {
                card.balance / rate
            }
            
            val updatedCard = card.copy(
                currency = newCurrency,
                balance = newBalance
            )
            repository.insertNfcCard(updatedCard)
            
            // Record log in statement
            val txId = "BP-EXC-" + System.currentTimeMillis().toString().takeLast(6)
            val tx = com.example.data.TransactionHistory(
                productName = "Currency Exchange: $currentCurrency to $newCurrency",
                amount = card.balance,
                status = "COMPLETED",
                paymentMethod = "${card.cardHolder}'s Card",
                transactionId = txId,
                currency = currentCurrency
            )
            repository.insertTransaction(tx)
            
            onResult(true, "Exchanged to $newCurrency! New balance: ${String.format("%.2f", newBalance)} $newCurrency")
        }
    }

    fun updateKidCardLimit(kidCardId: Int, newLimit: Double, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val cardList = allNfcCards.value
            val kidCard = cardList.find { it.id == kidCardId && it.isKidCard }
            if (kidCard == null) {
                onResult(false, "Kid Card not found.")
                return@launch
            }
            val updatedCard = kidCard.copy(dailySpendingLimit = newLimit)
            repository.insertNfcCard(updatedCard)
            onResult(true, "Limit updated successfully!")
        }
    }

    fun deleteNfcCard(id: Int) {
        viewModelScope.launch {
            repository.deleteNfcCardById(id)
        }
    }

    fun updateNfcCardBalance(id: Int, newBalance: Double) {
        viewModelScope.launch {
            val cardList = allNfcCards.value
            val card = cardList.find { it.id == id }
            if (card != null) {
                repository.insertNfcCard(card.copy(balance = newBalance))
            }
        }
    }

    fun updateNfcCardImage(id: Int, customImageUri: String?) {
        viewModelScope.launch {
            val cardList = allNfcCards.value
            val card = cardList.find { it.id == id }
            if (card != null) {
                repository.insertNfcCard(card.copy(customImageUri = customImageUri))
            }
        }
    }

    fun updateCardNfcChip(cardId: Int, newUid: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val cardList = allNfcCards.value
            val card = cardList.find { it.id == cardId }
            if (card == null) {
                onResult(false, "Card not found.")
                return@launch
            }
            // Sanitize & format input to uppercase Hex UID
            val rawClean = newUid.trim().uppercase().replace(Regex("[^0-9A-F:]"), "")
            val formattedUid = if (rawClean.length >= 8 && !rawClean.contains(":")) {
                rawClean.chunked(2).joinToString(":")
            } else {
                rawClean
            }

            val updatedCard = card.copy(cardUid = formattedUid)
            repository.insertNfcCard(updatedCard)
            if (formattedUid.isBlank()) {
                onResult(true, "NFC Chip removed from card **** ${card.cardNumber.takeLast(4)}.")
            } else {
                onResult(true, "NFC Chip Hex UID '$formattedUid' programmed to card **** ${card.cardNumber.takeLast(4)}!")
            }
        }
    }

    fun issueAdditionalAccountCard(
        parentCard: com.example.data.GameNfcCard,
        cardLabel: String,
        colorHex: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val newCardNumber = "4120 " + (1000..9999).random() + " " + (1000..9999).random() + " " + (1000..9999).random()
            val newCvv = (100..999).random().toString()
            val generatedUid = generateRandomHexUid()
            val holderName = if (cardLabel.isNotBlank()) "${parentCard.cardHolder} ($cardLabel)" else parentCard.cardHolder
            val newCard = com.example.data.GameNfcCard(
                cardHolder = holderName,
                cardNumber = newCardNumber,
                cardUid = generatedUid,
                balance = 0.0,
                currency = parentCard.currency,
                colorHex = colorHex.ifBlank { "#1E88E5" },
                parentCardId = parentCard.id,
                bankPassword = parentCard.bankPassword,
                cvv = newCvv
            )
            repository.insertNfcCard(newCard)
            onResult(true, "New card **** ${newCardNumber.takeLast(4)} issued to account!")
        }
    }

    fun shareCardAccess(cardId: Int, targetUser: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val cardList = allNfcCards.value
            val card = cardList.find { it.id == cardId }
            if (card == null) {
                onResult(false, "Card not found.")
                return@launch
            }
            val trimmedUser = targetUser.trim()
            if (trimmedUser.isBlank()) {
                onResult(false, "Please enter a valid username/cardholder name.")
                return@launch
            }
            val updatedCard = card.copy(sharedWithUser = trimmedUser)
            repository.insertNfcCard(updatedCard)
            onResult(true, "Card **** ${card.cardNumber.takeLast(4)} access shared with '$trimmedUser'!")
        }
    }

    fun revokeCardAccessSharing(cardId: Int, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val cardList = allNfcCards.value
            val card = cardList.find { it.id == cardId }
            if (card == null) {
                onResult(false, "Card not found.")
                return@launch
            }
            val updatedCard = card.copy(sharedWithUser = "")
            repository.insertNfcCard(updatedCard)
            onResult(true, "Shared access revoked for card **** ${card.cardNumber.takeLast(4)}.")
        }
    }

    fun payBillFromBank(productName: String, amount: Double, cardId: Int, currency: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val cardList = allNfcCards.value
            val card = cardList.find { it.id == cardId }
            if (card == null) {
                onResult(false, "Selected card account not found.")
                return@launch
            }

            val amountInCardCurrency = if (card.currency == currency) {
                amount
            } else if (card.currency == "RSD" && currency == "EUR") {
                amount * 117.0
            } else { // card.currency == "EUR" && currency == "RSD"
                amount / 117.0
            }

            if (card.balance < amountInCardCurrency) {
                val neededStr = if (card.currency == "EUR") "€${String.format("%.2f", amountInCardCurrency)}" else "${String.format("%.2f", amountInCardCurrency)} RSD"
                val balanceStr = if (card.currency == "EUR") "€${String.format("%.2f", card.balance)}" else "${String.format("%.2f", card.balance)} RSD"
                onResult(false, "Insufficient balance! Need $neededStr but card balance is $balanceStr.")
                return@launch
            }
            
            // Check daily spending limit if it's a kid card
            if (card.isKidCard && card.dailySpendingLimit > 0.0) {
                if (card.todaySpent + amountInCardCurrency > card.dailySpendingLimit) {
                    val limitStr = if (card.currency == "EUR") "€${String.format("%.2f", card.dailySpendingLimit)}" else "${String.format("%.2f", card.dailySpendingLimit)} RSD"
                    val spentStr = if (card.currency == "EUR") "€${String.format("%.2f", card.todaySpent)}" else "${String.format("%.2f", card.todaySpent)} RSD"
                    onResult(false, "Daily spending limit exceeded! Limit: $limitStr, Today Spent: $spentStr.")
                    return@launch
                }
            }

            // Deduct balance and update todaySpent
            val updatedCard = card.copy(
                balance = card.balance - amountInCardCurrency,
                todaySpent = if (card.isKidCard) card.todaySpent + amountInCardCurrency else card.todaySpent
            )
            repository.insertNfcCard(updatedCard)

            // Insert Transaction History
            val txId = "BP-BILL-" + System.currentTimeMillis().toString().takeLast(6)
            val tx = TransactionHistory(
                productName = "Bill Payment: $productName",
                amount = amount,
                status = "COMPLETED",
                paymentMethod = "Bank Hub (${card.cardHolder})",
                transactionId = txId,
                currency = currency
            )
            repository.insertTransaction(tx)
            onResult(true, "Bill successfully paid! Transaction ID: $txId")
        }
    }

    fun transferMoneyToPlayer(
        sourceCardId: Int,
        targetCardId: Int,
        amount: Double,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val cardList = allNfcCards.value
            val sourceCard = cardList.find { it.id == sourceCardId }
            val targetCard = cardList.find { it.id == targetCardId }

            if (sourceCard == null) {
                onResult(false, "Source card account not found.")
                return@launch
            }
            if (targetCard == null) {
                onResult(false, "Recipient card account not found.")
                return@launch
            }
            if (amount <= 0.0) {
                onResult(false, "Transfer amount must be greater than zero.")
                return@launch
            }
            if (sourceCard.balance < amount) {
                onResult(false, "Insufficient balance! Need ${String.format("%.2f", amount)} ${sourceCard.currency} but only has ${String.format("%.2f", sourceCard.balance)} ${sourceCard.currency}.")
                return@launch
            }

            // Check daily spending limit if source is a kid card
            if (sourceCard.isKidCard && sourceCard.dailySpendingLimit > 0.0) {
                if (sourceCard.todaySpent + amount > sourceCard.dailySpendingLimit) {
                    onResult(false, "Daily spending limit exceeded! Limit: ${String.format("%.2f", sourceCard.dailySpendingLimit)}, Today Spent: ${String.format("%.2f", sourceCard.todaySpent)}.")
                    return@launch
                }
            }

            // Calculate target amount with currency conversion if different
            val sCurr = sourceCard.currency
            val tCurr = targetCard.currency
            val targetAmount = if (sCurr == tCurr) {
                amount
            } else if (sCurr == "RSD" && tCurr == "EUR") {
                amount / 117.0
            } else { // sCurr == "EUR" && tCurr == "RSD"
                amount * 117.0
            }

            // Update source balance and todaySpent
            val updatedSource = sourceCard.copy(
                balance = sourceCard.balance - amount,
                todaySpent = if (sourceCard.isKidCard) sourceCard.todaySpent + amount else sourceCard.todaySpent
            )

            // Update recipient balance
            val updatedTarget = targetCard.copy(
                balance = targetCard.balance + targetAmount
            )

            repository.insertNfcCard(updatedSource)
            repository.insertNfcCard(updatedTarget)

            // Insert Transaction History
            val txId = "BP-XFER-" + System.currentTimeMillis().toString().takeLast(6)
            val tx = TransactionHistory(
                productName = "Transfer to ${targetCard.cardHolder}",
                amount = amount,
                status = "COMPLETED",
                paymentMethod = "Bank Hub (${sourceCard.cardHolder})",
                transactionId = txId,
                currency = sCurr
            )
            repository.insertTransaction(tx)
            
            val conversionInfo = if (sCurr != tCurr) " (Converted to ${String.format("%.2f", targetAmount)} $tCurr)" else ""
            onResult(true, "Successfully sent ${String.format("%.2f", amount)} $sCurr to ${targetCard.cardHolder}!$conversionInfo")
        }
    }

    fun issueKidCard(parentCardId: Int, kidName: String, kidCardNumber: String, kidCardUid: String, initialBalance: Double, limit: Double, currency: String, colorHex: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val cardList = allNfcCards.value
            val parentCard = cardList.find { it.id == parentCardId }
            if (parentCard == null) {
                onResult(false, "Parent account card not found.")
                return@launch
            }
            if (parentCard.balance < initialBalance) {
                onResult(false, "Insufficient parent account balance to fund Kid Card with ${String.format("%.2f", initialBalance)} $currency.")
                return@launch
            }

            // Deduct initial funding from parent balance
            val updatedParent = parentCard.copy(balance = parentCard.balance - initialBalance)
            repository.insertNfcCard(updatedParent)

            // Create kid card
            val kidCard = com.example.data.GameNfcCard(
                cardHolder = kidName,
                cardNumber = kidCardNumber,
                cardUid = kidCardUid.uppercase().trim(),
                balance = initialBalance,
                currency = currency,
                colorHex = colorHex,
                isKidCard = true,
                parentCardId = parentCardId,
                dailySpendingLimit = limit,
                todaySpent = 0.0
            )
            repository.insertNfcCard(kidCard)

            // Log funding transaction
            val txId = "BP-KID-" + System.currentTimeMillis().toString().takeLast(6)
            val tx = TransactionHistory(
                productName = "Funded Kid Card: $kidName",
                amount = initialBalance,
                status = "COMPLETED",
                paymentMethod = "Bank Hub (${parentCard.cardHolder})",
                transactionId = txId,
                currency = currency
            )
            repository.insertTransaction(tx)

            onResult(true, "Successfully issued Kid Card for $kidName!")
        }
    }

    fun payWithGameCard(card: com.example.data.GameNfcCard) {
        val amount = totalCartAmount.value
        val curr = _selectedCurrency.value
        if (amount <= 0.0) {
            _checkoutState.value = CheckoutState.Error("Amount must be greater than 0")
            return
        }

        val cardCurrency = card.currency
        val amountInCardCurrency = if (curr == cardCurrency) {
            amount
        } else if (curr == "RSD" && cardCurrency == "EUR") {
            amount / 117.0
        } else { // curr == "EUR" && cardCurrency == "RSD"
            amount * 117.0
        }

        viewModelScope.launch {
            val cleanNum = card.cardNumber.replace(" ", "")
            val provider = if (cleanNum.startsWith("4")) "Visa" else if (cleanNum.startsWith("5")) "Mastercard" else "Bcard"
            _checkoutState.value = CheckoutState.Loading(provider, card.cardHolder)

            // Play custom provider tap sound immediately
            when (provider) {
                "Visa" -> TerminalSoundPlayer.playVisaSound()
                "Mastercard" -> TerminalSoundPlayer.playMastercardSound()
                else -> TerminalSoundPlayer.playBCardSound()
            }

            kotlinx.coroutines.delay(2500) // Beautiful real terminal auth animation wait!

            if (card.balance < amountInCardCurrency) {
                val neededStr = if (cardCurrency == "EUR") "€${String.format("%.2f", amountInCardCurrency)}" else "${String.format("%.2f", amountInCardCurrency)} RSD"
                val balanceStr = if (cardCurrency == "EUR") "€${String.format("%.2f", card.balance)}" else "${String.format("%.2f", card.balance)} RSD"
                TerminalSoundPlayer.playErrorBuzzer()
                _checkoutState.value = CheckoutState.Error("Insufficient balance on ${card.cardHolder}'s card! Need $neededStr but only has $balanceStr")
                return@launch
            }

            // Daily limit check for kid cards
            if (card.isKidCard && card.dailySpendingLimit > 0.0) {
                if (card.todaySpent + amountInCardCurrency > card.dailySpendingLimit) {
                    val limitStr = if (cardCurrency == "EUR") "€${String.format("%.2f", card.dailySpendingLimit)}" else "${String.format("%.2f", card.dailySpendingLimit)} RSD"
                    val spentStr = if (cardCurrency == "EUR") "€${String.format("%.2f", card.todaySpent)}" else "${String.format("%.2f", card.todaySpent)} RSD"
                    TerminalSoundPlayer.playErrorBuzzer()
                    _checkoutState.value = CheckoutState.Error(
                        "Daily limit exceeded for ${card.cardHolder}! Limit: $limitStr, Today Spent: $spentStr."
                    )
                    return@launch
                }
            }

            // Deduct balance and update today's spent amount if kid card
            val updatedCard = card.copy(
                balance = card.balance - amountInCardCurrency,
                todaySpent = if (card.isKidCard) card.todaySpent + amountInCardCurrency else card.todaySpent
            )
            repository.insertNfcCard(updatedCard)

            // Check terminal payout recipient
            val recipientId = _terminalRecipientCardId.value
            var recipientCardName = "Merchant / Bank"
            var conversionDetail = ""
            if (recipientId != null) {
                val recipientCard = allNfcCards.value.find { it.id == recipientId }
                if (recipientCard != null) {
                    recipientCardName = recipientCard.cardHolder
                    val rCurrency = recipientCard.currency
                    val creditAmount = if (curr == rCurrency) {
                        amount
                    } else if (curr == "RSD" && rCurrency == "EUR") {
                        amount / 117.0
                    } else { // curr == "EUR" && rCurrency == "RSD"
                        amount * 117.0
                    }
                    val updatedRecipient = recipientCard.copy(balance = recipientCard.balance + creditAmount)
                    repository.insertNfcCard(updatedRecipient)
                    conversionDetail = if (curr != rCurrency) " (Recieved ${String.format("%.2f", creditAmount)} $rCurrency)" else ""
                }
            }

            // Generate transaction
            val itemsList = mutableListOf<String>()
            _cartItems.value.forEach { (prod, qty) ->
                itemsList.add("${qty}x ${prod.name}")
            }
            if (_customAmount.value > 0.0) {
                itemsList.add("Custom Charge")
            }
            val itemsSummary = if (itemsList.isEmpty()) "Quick Play Charge" else itemsList.joinToString(", ")
            val txId = "BP-GAME-" + System.currentTimeMillis().toString().takeLast(6)

            val conversionInfo = if (curr != cardCurrency) " (Converted from $cardCurrency)" else ""
            val payeeInfo = "Game Wallet (${card.cardHolder})"
            val recipientInfo = if (recipientId != null) " -> To $recipientCardName$conversionDetail" else " -> To Merchant"
            val tx = TransactionHistory(
                productName = itemsSummary,
                amount = amount,
                status = "COMPLETED",
                paymentMethod = "$payeeInfo$recipientInfo$conversionInfo",
                transactionId = txId,
                currency = curr
            )
            repository.insertTransaction(tx)
            TerminalSoundPlayer.playSuccessBeep()
            _checkoutState.value = CheckoutState.Success(tx)
            clearCart()
            _incomingIntentData.value = null
        }
    }

    fun payOnlineWithCardDetails(
        cardHolderInput: String,
        cardNumberInput: String,
        expiryInput: String,
        cvvInput: String
    ) {
        val amount = totalCartAmount.value
        val curr = _selectedCurrency.value
        if (amount <= 0.0) {
            _checkoutState.value = CheckoutState.Error("Amount must be greater than 0")
            return
        }

        val cleanEnteredNumber = cardNumberInput.replace(" ", "").replace("-", "").trim()
        val cleanEnteredHolder = cardHolderInput.ifBlank { "MILAN JOVANOVIC" }.trim()

        viewModelScope.launch {
            val allCards = allNfcCards.value
            val matchedCard = allCards.find { card ->
                val cleanDbNum = card.cardNumber.replace(" ", "").replace("-", "").trim()
                (cleanEnteredNumber.isNotEmpty() && cleanDbNum == cleanEnteredNumber) ||
                card.cardHolder.equals(cleanEnteredHolder, ignoreCase = true) ||
                cleanEnteredHolder.contains(card.cardHolder, ignoreCase = true) ||
                card.cardHolder.contains(cleanEnteredHolder, ignoreCase = true)
            } ?: allCards.firstOrNull() ?: com.example.data.GameNfcCard(
                cardUid = "ONLINE-USER-01",
                cardHolder = cleanEnteredHolder,
                cardNumber = "4120 8821 9912 3730",
                expiryDate = "08/30",
                cvv = "373",
                bankPassword = "1234",
                balance = 250000.0,
                currency = "RSD",
                colorHex = "#1E88E5"
            )

            val cardCurrency = matchedCard.currency
            val amountInCardCurrency = if (curr == cardCurrency) {
                amount
            } else if (curr == "RSD" && cardCurrency == "EUR") {
                amount / 117.0
            } else {
                amount * 117.0
            }

            val cleanNum = matchedCard.cardNumber.replace(" ", "")
            val provider = if (cleanNum.startsWith("4")) "Visa" else if (cleanNum.startsWith("5")) "Mastercard" else "Bcard"
            _checkoutState.value = CheckoutState.Loading(provider, matchedCard.cardHolder)

            TerminalSoundPlayer.playApplePaySound()
            when (provider) {
                "Visa" -> TerminalSoundPlayer.playVisaSound()
                "Mastercard" -> TerminalSoundPlayer.playMastercardSound()
                else -> TerminalSoundPlayer.playBCardSound()
            }

            kotlinx.coroutines.delay(1800)

            // Generate 6-digit 3D Secure OTP code for verification in Bank Hub
            val otpCode = (100000..999999).random().toString()
            val amountStr = if (curr == "RSD") "${String.format("%.2f", amount)} RSD" else "€${String.format("%.2f", amount)}"

            val itemsList = mutableListOf<String>()
            _cartItems.value.forEach { (prod, qty) ->
                itemsList.add("${qty}x ${prod.name}")
            }
            if (_customAmount.value > 0.0) {
                itemsList.add("Custom Charge")
            }
            val itemsSummary = if (itemsList.isEmpty()) "Online Purchase" else itemsList.joinToString(", ")
            val txId = "BP-ONLINE-" + System.currentTimeMillis().toString().takeLast(6)

            val info = OnlineVerificationInfo(
                cardHolder = matchedCard.cardHolder,
                cardNumber = matchedCard.cardNumber,
                amountStr = amountStr,
                otpCode = otpCode,
                matchedCard = matchedCard,
                amountInCardCurrency = amountInCardCurrency,
                itemsSummary = itemsSummary,
                txId = txId,
                amount = amount,
                curr = curr,
                provider = provider
            )

            _activeOnlineOtp.value = info
            _checkoutState.value = CheckoutState.VerificationRequired(info)
        }
    }

    fun verifyOnlineOtp(enteredCode: String) {
        val info = _activeOnlineOtp.value
        if (info == null) {
            _checkoutState.value = CheckoutState.Error("No active verification request.")
            return
        }
        if (enteredCode.trim() != info.otpCode) {
            TerminalSoundPlayer.playErrorBuzzer()
            _checkoutState.value = CheckoutState.Error("Invalid Verification Code! Check Bank Hub for your 6-digit code.")
            return
        }
        completeOnlinePurchase(info)
    }

    fun approveOnlinePurchaseFromBankHub() {
        val info = _activeOnlineOtp.value ?: return
        completeOnlinePurchase(info)
    }

    fun cancelOnlinePurchase() {
        _activeOnlineOtp.value = null
        _checkoutState.value = CheckoutState.Idle
    }

    private fun completeOnlinePurchase(info: OnlineVerificationInfo) {
        viewModelScope.launch {
            val card = info.matchedCard
            val amountInCardCurrency = info.amountInCardCurrency

            if (card.balance < amountInCardCurrency) {
                val cardCurrency = card.currency
                val neededStr = if (cardCurrency == "EUR") "€${String.format("%.2f", amountInCardCurrency)}" else "${String.format("%.2f", amountInCardCurrency)} RSD"
                val balanceStr = if (cardCurrency == "EUR") "€${String.format("%.2f", card.balance)}" else "${String.format("%.2f", card.balance)} RSD"
                TerminalSoundPlayer.playErrorBuzzer()
                _checkoutState.value = CheckoutState.Error("Insufficient balance on ${card.cardHolder}'s card! Need $neededStr but balance is $balanceStr")
                _activeOnlineOtp.value = null
                return@launch
            }

            if (card.isKidCard && card.dailySpendingLimit > 0.0) {
                if (card.todaySpent + amountInCardCurrency > card.dailySpendingLimit) {
                    val limitStr = if (card.currency == "EUR") "€${String.format("%.2f", card.dailySpendingLimit)}" else "${String.format("%.2f", card.dailySpendingLimit)} RSD"
                    TerminalSoundPlayer.playErrorBuzzer()
                    _checkoutState.value = CheckoutState.Error("Daily limit exceeded for ${card.cardHolder}! Limit: $limitStr")
                    _activeOnlineOtp.value = null
                    return@launch
                }
            }

            val updatedCard = card.copy(
                balance = card.balance - amountInCardCurrency,
                todaySpent = if (card.isKidCard) card.todaySpent + amountInCardCurrency else card.todaySpent
            )
            repository.insertNfcCard(updatedCard)

            // Credit funds to pre-set Payout Destination (Merchant or Primary Business Account)
            val payoutCard = allNfcCards.value.find { 
                it.id != card.id && (it.cardHolder.contains("Merchant", ignoreCase = true) || it.cardHolder.equals("MILAN JOVANOVIC", ignoreCase = true)) 
            } ?: allNfcCards.value.find { it.id != card.id }
            
            if (payoutCard != null) {
                val updatedPayoutCard = payoutCard.copy(
                    balance = payoutCard.balance + amountInCardCurrency
                )
                repository.insertNfcCard(updatedPayoutCard)
            }

            val conversionInfo = if (info.curr != card.currency) " (Converted from ${card.currency})" else ""
            val tx = TransactionHistory(
                productName = info.itemsSummary,
                amount = info.amount,
                status = "COMPLETED",
                paymentMethod = "Stripe Online Checkout (${card.cardHolder})$conversionInfo",
                transactionId = info.txId,
                currency = info.curr
            )
            repository.insertTransaction(tx)
            TerminalSoundPlayer.playApplePaySound()
            _checkoutState.value = CheckoutState.Success(tx)
            _activeOnlineOtp.value = null
            clearCart()
            _incomingIntentData.value = null
        }
    }

    // Export Catalog JSON for local saving & app transfers
    fun exportCatalogJson(): String {
        val products = allProducts.value
        val jsonArray = org.json.JSONArray()
        products.forEach { p ->
            val obj = org.json.JSONObject()
            obj.put("name", p.name)
            obj.put("price", p.price)
            obj.put("category", p.category)
            jsonArray.put(obj)
        }
        return jsonArray.toString(2)
    }

    // Import Catalog JSON locally
    fun importCatalogJson(jsonStr: String): Pair<Boolean, String> {
        return try {
            val jsonArray = org.json.JSONArray(jsonStr)
            var count = 0
            viewModelScope.launch {
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    val name = obj.optString("name", "")
                    val price = obj.optDouble("price", 0.0)
                    val category = obj.optString("category", "General")
                    if (name.isNotBlank() && price > 0.0) {
                        repository.insertProduct(Product(name = name, price = price, category = category))
                        count++
                    }
                }
            }
            Pair(true, "Successfully imported $count products into Catalog!")
        } catch (e: Exception) {
            Pair(false, "Failed to import catalog: ${e.localizedMessage}")
        }
    }

    fun handlePhysicalNfcCardTap(uid: String) {
        val uppercaseUid = uid.uppercase().trim()
        _scannedNfcUid.value = uppercaseUid

        // If currently in checkout processing, search for card and perform purchase
        if (_checkoutState.value is CheckoutState.Processing) {
            viewModelScope.launch {
                val card = repository.getNfcCardByUid(uppercaseUid)
                if (card != null) {
                    val amount = totalCartAmount.value
                    val curr = _selectedCurrency.value
                    val cardCurrency = card.currency

                    val amountInCardCurrency = if (curr == cardCurrency) {
                        amount
                    } else if (curr == "RSD" && cardCurrency == "EUR") {
                        amount / 117.0
                    } else { // curr == "EUR" && cardCurrency == "RSD"
                        amount * 117.0
                    }

                    val cleanNum = card.cardNumber.replace(" ", "")
                    val provider = if (cleanNum.startsWith("4")) "Visa" else if (cleanNum.startsWith("5")) "Mastercard" else "Bcard"
                    _checkoutState.value = CheckoutState.Loading(provider, card.cardHolder)

                    // Play custom provider tap sound immediately
                    when (provider) {
                        "Visa" -> TerminalSoundPlayer.playVisaSound()
                        "Mastercard" -> TerminalSoundPlayer.playMastercardSound()
                        else -> TerminalSoundPlayer.playBCardSound()
                    }

                    kotlinx.coroutines.delay(2500) // Beautiful real terminal auth animation wait!

                    if (card.balance < amountInCardCurrency) {
                        val neededStr = if (cardCurrency == "EUR") "€${String.format("%.2f", amountInCardCurrency)}" else "${String.format("%.2f", amountInCardCurrency)} RSD"
                        val balanceStr = if (cardCurrency == "EUR") "€${String.format("%.2f", card.balance)}" else "${String.format("%.2f", card.balance)} RSD"
                        TerminalSoundPlayer.playErrorBuzzer()
                        _checkoutState.value = CheckoutState.Error(
                            "Insufficient balance on registered card (${card.cardHolder})! Need $neededStr but has $balanceStr."
                        )
                    } else {
                        // Daily limit check for kid cards
                        if (card.isKidCard && card.dailySpendingLimit > 0.0) {
                            if (card.todaySpent + amountInCardCurrency > card.dailySpendingLimit) {
                                val limitStr = if (cardCurrency == "EUR") "€${String.format("%.2f", card.dailySpendingLimit)}" else "${String.format("%.2f", card.dailySpendingLimit)} RSD"
                                val spentStr = if (cardCurrency == "EUR") "€${String.format("%.2f", card.todaySpent)}" else "${String.format("%.2f", card.todaySpent)} RSD"
                                TerminalSoundPlayer.playErrorBuzzer()
                                _checkoutState.value = CheckoutState.Error(
                                    "Daily limit exceeded for ${card.cardHolder}! Limit: $limitStr, Today Spent: $spentStr."
                                )
                                return@launch
                            }
                        }

                        // Deduct balance and update today's spent amount if kid card
                        val updatedCard = card.copy(
                            balance = card.balance - amountInCardCurrency,
                            todaySpent = if (card.isKidCard) card.todaySpent + amountInCardCurrency else card.todaySpent
                        )
                        repository.insertNfcCard(updatedCard)

                        // Check terminal payout recipient
                        val recipientId = _terminalRecipientCardId.value
                        var recipientCardName = "Merchant / Bank"
                        var conversionDetail = ""
                        if (recipientId != null) {
                            val recipientCard = allNfcCards.value.find { it.id == recipientId }
                            if (recipientCard != null) {
                                recipientCardName = recipientCard.cardHolder
                                val rCurrency = recipientCard.currency
                                val creditAmount = if (curr == rCurrency) {
                                    amount
                                } else if (curr == "RSD" && rCurrency == "EUR") {
                                    amount / 117.0
                                } else { // curr == "EUR" && rCurrency == "RSD"
                                    amount * 117.0
                                }
                                val updatedRecipient = recipientCard.copy(balance = recipientCard.balance + creditAmount)
                                repository.insertNfcCard(updatedRecipient)
                                conversionDetail = if (curr != rCurrency) " (Recieved ${String.format("%.2f", creditAmount)} $rCurrency)" else ""
                            }
                        }

                        // Generate transaction
                        val itemsList = mutableListOf<String>()
                        _cartItems.value.forEach { (prod, qty) ->
                            itemsList.add("${qty}x ${prod.name}")
                        }
                        if (_customAmount.value > 0.0) {
                            itemsList.add("Custom Charge")
                        }
                        val itemsSummary = if (itemsList.isEmpty()) "Quick Play Charge" else itemsList.joinToString(", ")
                        val txId = "BP-NFC-" + System.currentTimeMillis().toString().takeLast(6)

                        val conversionInfo = if (curr != cardCurrency) " (Converted from $cardCurrency)" else ""
                        val payeeInfo = "Physical NFC Tap (${card.cardHolder})"
                        val recipientInfo = if (recipientId != null) " -> To $recipientCardName$conversionDetail" else " -> To Merchant"
                        val tx = TransactionHistory(
                            productName = itemsSummary,
                            amount = amount,
                            status = "COMPLETED",
                            paymentMethod = "$payeeInfo$recipientInfo$conversionInfo",
                            transactionId = txId,
                            currency = curr
                        )
                        repository.insertTransaction(tx)
                        TerminalSoundPlayer.playSuccessBeep()
                        _checkoutState.value = CheckoutState.Success(tx)
                        clearCart()
                        _incomingIntentData.value = null
                    }
                } else {
                    TerminalSoundPlayer.playErrorBuzzer()
                    _checkoutState.value = CheckoutState.Error(
                        "Unrecognized NFC Card with UID: $uppercaseUid. Please register it first in the Cards tab!"
                    )
                }
            }
        }
    }

    fun deleteTransaction(tx: TransactionHistory) {
        viewModelScope.launch {
            repository.deleteTransactionById(tx.id)
        }
    }

    fun clearAllTransactions() {
        viewModelScope.launch {
            repository.clearAllTransactions()
        }
    }

    // JOBS & BUSINESSES PAYOUT MANAGEMENT
    fun executeMonthlyPayout() {
        viewModelScope.launch {
            val cards = allNfcCards.value.toMutableList()

            // Monthly Credit Repayment Process (20% Repayment + 10% Kamata / Interest per 10min month)
            val cardsWithCredit = allNfcCards.value.filter { it.creditDebt > 0.0 }
            for (cCard in cardsWithCredit) {
                val currentDebt = cCard.creditDebt
                val kamataInterest = currentDebt * 0.10 // 10% Kamata
                val principalRepayment = currentDebt * 0.20 // 20% Monthly repayment
                val totalDeductionNeeded = principalRepayment + kamataInterest

                val deduction = totalDeductionNeeded.coerceAtMost(cCard.balance)
                val newBalance = (cCard.balance - deduction).coerceAtLeast(0.0)

                // Principal reduction proportional to deduction
                val principalPaid = if (totalDeductionNeeded > 0) principalRepayment * (deduction / totalDeductionNeeded) else 0.0
                val newDebt = (currentDebt - principalPaid).coerceAtLeast(0.0)

                val updatedCCard = cCard.copy(
                    balance = newBalance,
                    creditDebt = newDebt
                )
                repository.insertNfcCard(updatedCCard)

                if (deduction > 0) {
                    val tx = TransactionHistory(
                        productName = "Monthly Credit Payment (20% + 10% Interest)",
                        amount = deduction,
                        currency = cCard.currency,
                        status = "APPROVED",
                        paymentMethod = "Automatic Credit Loan Repayment",
                        transactionId = "CREDIT-AUTO-${System.currentTimeMillis()}"
                    )
                    repository.insertTransaction(tx)
                }
            }
        }
    }

    // Secret Branko Mastercard payout removed



    fun forceTriggerMonthlyPayout() {
        _payoutCountdownSeconds.value = 600
        executeMonthlyPayout()
    }

    fun sellJob(job: com.example.data.JobItem, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val ownerCard = allNfcCards.value.find { it.cardHolder.equals(job.owner, ignoreCase = true) }
            if (ownerCard != null) {
                repository.insertNfcCard(ownerCard.copy(balance = ownerCard.balance + job.salePrice))
            }

            val updatedJob = job.copy(owner = "Marketplace / Available", isPayoutEnabled = false)
            repository.insertJob(updatedJob)

            val tx = TransactionHistory(
                productName = "Job Sold to Market: ${job.title}",
                amount = job.salePrice,
                currency = "RSD",
                status = "APPROVED",
                paymentMethod = "Job Sale Refund (${job.owner})",
                transactionId = "SELLJOB-${System.currentTimeMillis()}"
            )
            repository.insertTransaction(tx)
            TerminalSoundPlayer.playSuccessBeep()
            onResult(true, "Job ${job.title} sold for ${job.salePrice} RSD!")
        }
    }

    // CREDIT CARD LOAN MANAGEMENT
    fun takeCreditLoan(cardId: Int, loanAmount: Double) {
        viewModelScope.launch {
            val card = allNfcCards.value.find { it.id == cardId } ?: return@launch
            if (loanAmount <= 0) return@launch

            val updatedCard = card.copy(
                balance = card.balance + loanAmount,
                creditDebt = card.creditDebt + loanAmount,
                creditLimit = (card.creditLimit + loanAmount).coerceAtLeast(loanAmount)
            )
            repository.insertNfcCard(updatedCard)

            val tx = TransactionHistory(
                productName = "Credit Card Loan Received (+${String.format("%.0f", loanAmount)})",
                amount = loanAmount,
                currency = card.currency,
                status = "APPROVED",
                paymentMethod = "Credit Loan Issued",
                transactionId = "CREDIT-LOAN-${System.currentTimeMillis()}"
            )
            repository.insertTransaction(tx)
            TerminalSoundPlayer.playSuccessBeep()
        }
    }

    fun payCreditDebt(cardId: Int, payAmount: Double) {
        viewModelScope.launch {
            val card = allNfcCards.value.find { it.id == cardId } ?: return@launch
            if (payAmount <= 0 || card.creditDebt <= 0 || card.balance < payAmount) return@launch

            val actualPayment = payAmount.coerceAtMost(card.creditDebt).coerceAtMost(card.balance)

            val updatedCard = card.copy(
                balance = card.balance - actualPayment,
                creditDebt = (card.creditDebt - actualPayment).coerceAtLeast(0.0)
            )
            repository.insertNfcCard(updatedCard)

            val tx = TransactionHistory(
                productName = "Credit Debt Repayment (-${String.format("%.0f", actualPayment)})",
                amount = actualPayment,
                currency = card.currency,
                status = "APPROVED",
                paymentMethod = "Credit Repayment",
                transactionId = "CREDIT-PAY-${System.currentTimeMillis()}"
            )
            repository.insertTransaction(tx)
            TerminalSoundPlayer.playSuccessBeep()
        }
    }

    fun issueBankCreditCard(
        cardHolder: String,
        bankName: String,
        creditLimit: Double,
        cardStyle: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val parentCard = allNfcCards.value.find { !it.isKidCard }
            val newNum = "4532 " + (1000..9999).random() + " " + (1000..9999).random() + " " + (1000..9999).random()
            val newCvv = (100..999).random().toString()
            val newUid = generateRandomHexUid()
            val newCard = com.example.data.GameNfcCard(
                cardHolder = cardHolder.ifBlank { parentCard?.cardHolder ?: "CARD HOLDER" },
                cardNumber = newNum,
                cardUid = newUid,
                balance = creditLimit,
                creditLimit = creditLimit,
                creditDebt = 0.0,
                currency = parentCard?.currency ?: "EUR",
                colorHex = "#1E88E5",
                cardStyle = cardStyle,
                bankProvider = bankName,
                cvv = newCvv,
                bankPassword = parentCard?.bankPassword ?: "1234"
            )
            repository.insertNfcCard(newCard)
            onResult(true, "Credit card successfully issued for $bankName ($cardStyle) with ${String.format("%.0f", creditLimit)} limit!")
        }
    }

    fun deleteAllCompanies(onResult: ((Boolean, String) -> Unit)? = null) {
        viewModelScope.launch {
            repository.deleteCompanyProducts()
            repository.deleteCompanyJobs()
            repository.clearAllJobs()
            onResult?.invoke(true, "All companies & business items deleted successfully!")
        }
    }

    fun updateCardMoney(cardId: Int, amount: Double, action: String): Boolean {
        val card = allNfcCards.value.find { it.id == cardId }
        if (card != null && amount > 0) {
            val newBalance = if (action == "ADD") {
                card.balance + amount
            } else {
                (card.balance - amount).coerceAtLeast(0.0)
            }
            
            viewModelScope.launch {
                val updatedCard = card.copy(balance = newBalance)
                repository.insertNfcCard(updatedCard)
                
                val tx = TransactionHistory(
                    productName = if (action == "ADD") "Manual Money Add" else "Manual Money Revoke",
                    amount = amount,
                    currency = card.currency,
                    status = "APPROVED",
                    paymentMethod = "Web Dashboard",
                    transactionId = "MANUAL-${System.currentTimeMillis()}"
                )
                repository.insertTransaction(tx)
            }
            return true
        }
        return false
    }
}

fun generateRandomHexUid(): String {
    val b2 = (0..255).random()
    val b3 = (0..255).random()
    val b4 = (0..255).random()
    return String.format(java.util.Locale.US, "04:%02X:%02X:%02X", b2, b3, b4)
}

fun generateRandom7ByteHexUid(): String {
    val bytes = ByteArray(7)
    java.security.SecureRandom().nextBytes(bytes)
    bytes[0] = 0x04.toByte()
    return bytes.joinToString(":") { String.format(java.util.Locale.US, "%02X", it) }
}

data class InboundPaymentRequest(
    val amount: Double,
    val productName: String,
    val sourceApp: String,
    val currency: String = "EUR"
)
