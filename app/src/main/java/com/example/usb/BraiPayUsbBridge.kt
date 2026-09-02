package com.example.usb

import android.hardware.usb.UsbAccessory
import android.hardware.usb.UsbManager
import android.os.ParcelFileDescriptor
import android.util.Log
import com.example.data.GameNfcCard
import com.example.data.Product
import com.example.data.TransactionHistory
import com.example.ui.BraiPayViewModel
import com.example.ui.CheckoutState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import org.json.JSONArray
import org.json.JSONObject
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

/**
 * Bridges the phone app to the WebUSB website (web/index.html) over Android Open Accessory.
 *
 * Wire format is newline-delimited JSON in both directions. The website sends {"cmd":"..."}
 * requests; the phone answers with {"type":"..."} responses and also pushes unsolicited
 * events (NFC taps, checkout progress, live card/product/transaction lists).
 */
class BraiPayUsbBridge(
    private val usbManager: UsbManager,
    private val viewModel: BraiPayViewModel
) {
    private val TAG = "BraiPayUsbBridge"

    private var fileDescriptor: ParcelFileDescriptor? = null
    private var inputStream: FileInputStream? = null
    private var outputStream: FileOutputStream? = null

    private var accessory: UsbAccessory? = null

    private val bridgeScope = CoroutineScope(Dispatchers.IO + Job())
    private var readJob: Job? = null
    private var stateCollectionJob: Job? = null

    fun start(accessory: UsbAccessory) {
        if (this.accessory == accessory) return // Already running for this accessory

        try {
            fileDescriptor = usbManager.openAccessory(accessory)
            fileDescriptor?.let { fd ->
                this.accessory = accessory
                inputStream = FileInputStream(fd.fileDescriptor)
                outputStream = FileOutputStream(fd.fileDescriptor)

                Log.d(TAG, "USB Bridge Started")

                startReading()
                startStateCollection()
            } ?: run {
                Log.e(TAG, "Failed to open accessory")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception starting USB bridge", e)
        }
    }

    fun stop() {
        Log.d(TAG, "Stopping USB Bridge")
        readJob?.cancel()
        stateCollectionJob?.cancel()

        try {
            inputStream?.close()
        } catch (e: Exception) { }
        try {
            outputStream?.close()
        } catch (e: Exception) { }
        try {
            fileDescriptor?.close()
        } catch (e: Exception) { }

        inputStream = null
        outputStream = null
        fileDescriptor = null
        accessory = null
    }

    fun isConnected(): Boolean {
        return accessory != null && fileDescriptor != null
    }

    private fun startReading() {
        readJob?.cancel()
        readJob = bridgeScope.launch {
            val buffer = ByteArray(16384)
            var leftover = ""
            while (isActive && isConnected()) {
                try {
                    val readCount = inputStream?.read(buffer) ?: -1
                    if (readCount > 0) {
                        val chunk = String(buffer, 0, readCount)
                        val lines = (leftover + chunk).split('\n')

                        // All except the last are complete lines
                        for (i in 0 until lines.size - 1) {
                            val line = lines[i].trim()
                            if (line.isNotEmpty()) {
                                processCommand(line)
                            }
                        }
                        leftover = lines.last()
                    }
                } catch (e: IOException) {
                    Log.e(TAG, "Error reading from USB", e)
                    stop()
                    break
                }
            }
        }
    }

    // ---------------- Payload builders ----------------

    private fun cardJson(c: GameNfcCard): JSONObject = JSONObject().apply {
        put("id", c.id)
        put("cardHolder", c.cardHolder)
        put("cardNumber", c.cardNumber)
        put("cardUid", c.cardUid)
        put("balance", c.balance)
        put("currency", c.currency)
        put("colorHex", c.colorHex)
        put("isKidCard", c.isKidCard)
        put("bankProvider", c.bankProvider)
        put("cardStyle", c.cardStyle)
        put("expiryDate", c.expiryDate)
        put("dailySpendingLimit", c.dailySpendingLimit)
        put("todaySpent", c.todaySpent)
        put("creditLimit", c.creditLimit)
        put("creditDebt", c.creditDebt)
    }

    private fun cardsPayload(cards: List<GameNfcCard>): JSONObject {
        val arr = JSONArray()
        cards.forEach { arr.put(cardJson(it)) }
        return JSONObject().put("type", "CARDS").put("data", arr)
    }

    private fun productsPayload(products: List<Product>): JSONObject {
        val arr = JSONArray()
        products.forEach { p ->
            arr.put(
                JSONObject()
                    .put("id", p.id)
                    .put("name", p.name)
                    .put("price", p.price)
                    .put("category", p.category)
            )
        }
        return JSONObject().put("type", "PRODUCTS").put("data", arr)
    }

    private fun transactionsPayload(txs: List<TransactionHistory>): JSONObject {
        val arr = JSONArray()
        txs.forEach { t ->
            arr.put(
                JSONObject()
                    .put("id", t.id)
                    .put("productName", t.productName)
                    .put("amount", t.amount)
                    .put("status", t.status)
                    .put("paymentMethod", t.paymentMethod)
                    .put("transactionId", t.transactionId)
                    .put("currency", t.currency)
                    .put("timestamp", t.timestamp)
            )
        }
        return JSONObject().put("type", "TRANSACTIONS").put("data", arr)
    }

    private fun ok(message: String, reqId: String = ""): JSONObject =
        JSONObject().put("type", "OK").put("message", message).withReqId(reqId)

    private fun error(message: String, reqId: String = ""): JSONObject =
        JSONObject().put("type", "ERROR").put("message", message).withReqId(reqId)

    /** Echoes the website's correlation id so it can resolve the matching request. */
    private fun JSONObject.withReqId(reqId: String): JSONObject =
        if (reqId.isNotEmpty()) this.put("reqId", reqId) else this

    // ---------------- Command handling ----------------

    private fun processCommand(commandStr: String) {
        try {
            val json = JSONObject(commandStr)
            val cmd = json.optString("cmd")
            val reqId = json.optString("reqId")
            Log.d(TAG, "Received cmd: $cmd")

            when (cmd) {
                "GET_CARDS" -> sendResponse(cardsPayload(viewModel.allNfcCards.value))
                "GET_PRODUCTS" -> sendResponse(productsPayload(viewModel.allProducts.value))
                "GET_TRANSACTIONS" -> {
                    val limit = json.optInt("limit", 20)
                    sendResponse(transactionsPayload(viewModel.allTransactions.value.take(limit)))
                }
                "GET_STATUS" -> {
                    val statusObj = JSONObject()
                    statusObj.put("type", "STATUS")
                    statusObj.put("connected", true)
                    statusObj.put("currency", viewModel.selectedCurrency.value)
                    statusObj.put("total", viewModel.totalCartAmount.value)
                    statusObj.put("checkoutState", viewModel.checkoutState.value.javaClass.simpleName)
                    statusObj.put("businessName", viewModel.businessCompanyName.value)
                    statusObj.put("isSimpleModeEnabled", viewModel.isSimpleModeEnabled.value)
                    statusObj.put("isBankModeEnabled", viewModel.isBankModeEnabled.value)
                    statusObj.put("connectedBankCardId", viewModel.connectedBankUserCardId.value)
                    statusObj.put("selectedBankName", viewModel.selectedBankName.value)
                    statusObj.put("bankIban", viewModel.customBankAccountIban.value)
                    statusObj.put("terminalRecipientCardId", viewModel.terminalRecipientCardId.value ?: -1)
                    sendResponse(statusObj)
                }
                "SET_CURRENCY" -> {
                    val currency = json.optString("currency", "RSD")
                    viewModel.selectCurrency(currency)
                    sendResponse(ok("Currency set to $currency", reqId))
                }
                "SET_SIMPLE_MODE" -> {
                    val enabled = json.optBoolean("enabled", false)
                    viewModel.setSimpleMode(enabled)
                    sendResponse(ok("Simple mode set to $enabled", reqId))
                }
                "SET_BANK_CONNECTION" -> {
                    val cardId = json.optInt("cardId", -1)
                    val iban = json.optString("iban", "")
                    val bankName = json.optString("bankName", "Raiffeisen Bank")
                    val enabled = json.optBoolean("enabled", true)
                    viewModel.setBankConnection(cardId, iban, bankName, enabled)
                    sendResponse(ok("Bank mode updated: $bankName ($iban)", reqId))
                }
                "SET_TERMINAL_RECIPIENT" -> {
                    val cardId = json.optInt("cardId", -1)
                    val recipientId = if (cardId <= 0) null else cardId
                    viewModel.setTerminalRecipientCardId(recipientId)
                    val targetName = if (recipientId != null) {
                        viewModel.allNfcCards.value.find { it.id == recipientId }?.cardHolder ?: "Player #$recipientId"
                    } else "Merchant Store"
                    sendResponse(ok("Terminal payout set to: $targetName", reqId))
                }
                "TAKE_LOAN" -> {
                    val cardId = json.optInt("cardId", -1)
                    val amount = json.optDouble("amount", 0.0)
                    if (cardId > 0 && amount > 0) {
                        viewModel.takeCreditLoan(cardId, amount)
                        sendResponse(ok("Loan of $amount approved and deposited", reqId))
                    } else {
                        sendResponse(error("Invalid card or loan amount", reqId))
                    }
                }
                "REPAY_LOAN" -> {
                    val cardId = json.optInt("cardId", -1)
                    val amount = json.optDouble("amount", 0.0)
                    if (cardId > 0 && amount > 0) {
                        viewModel.payCreditDebt(cardId, amount)
                        sendResponse(ok("Loan repayment of $amount processed", reqId))
                    } else {
                        sendResponse(error("Invalid card or payment amount", reqId))
                    }
                }
                "BANK_LOGIN" -> {
                    val password = json.optString("password", "")
                    val success = viewModel.loginToBankWithPassword(password)
                    if (success) {
                        sendResponse(ok("Bank login successful", reqId))
                    } else {
                        sendResponse(error("Incorrect bank password", reqId))
                    }
                }
                "SIMULATE_NFC_TAP" -> {
                    val uid = json.optString("uid", "04:A2:8B:1F")
                    viewModel.handlePhysicalNfcCardTap(uid)
                    sendResponse(ok("NFC Tap dispatched: $uid", reqId))
                }
                "UPDATE_MONEY" -> {
                    val cardId = json.optInt("cardId", -1)
                    val amount = json.optDouble("amount", 0.0)
                    val action = json.optString("action", "ADD")
                    val success = viewModel.updateCardMoney(cardId, amount, action)
                    if (success) {
                        sendResponse(ok("Money updated successfully", reqId))
                    } else {
                        sendResponse(error("Failed to update money or card not found", reqId))
                    }
                }
                "PAY_WITH_CARD" -> {
                    val cardId = json.optInt("cardId", -1)
                    val card = viewModel.allNfcCards.value.find { it.id == cardId }
                    if (card != null) {
                        viewModel.payWithGameCard(card)
                        sendResponse(ok("Payment charged to ${card.cardHolder}", reqId))
                    } else {
                        sendResponse(error("Card not found", reqId))
                    }
                }
                "PAY" -> {
                    val amount = json.optDouble("amount", 0.0)
                    val currency = json.optString("currency", "")
                    val recipientCardId = json.optInt("recipientCardId", -1)
                    if (recipientCardId > 0) {
                        viewModel.setTerminalRecipientCardId(recipientCardId)
                    } else if (json.has("recipientCardId") && recipientCardId <= 0) {
                        viewModel.setTerminalRecipientCardId(null)
                    }
                    if (amount > 0) {
                        // Set currency if provided
                        if (currency.isNotBlank()) {
                            viewModel.selectCurrency(currency)
                        }
                        // Clear cart and set custom amount from website
                        viewModel.clearCart()
                        viewModel.setCustomAmount(amount)
                        // Trigger checkout processing - phone now waits for NFC card tap
                        viewModel.setCheckoutProcessing()
                        sendResponse(ok("Payment of $amount $currency initiated — tap NFC card on phone", reqId))
                        sendResponse(JSONObject().put("type", "CHECKOUT_UPDATE").put("state", "Processing"))
                    } else {
                        sendResponse(error("Amount must be greater than 0", reqId))
                    }
                }
                "CANCEL_PAY" -> {
                    viewModel.resetCheckoutState()
                    sendResponse(ok("Payment cancelled", reqId))
                }
                "TRANSFER" -> handleTransfer(json, reqId)
                "ADD_CARD" -> handleAddCard(json, reqId)
                "ADD_PRODUCT" -> handleAddProduct(json, reqId)
                "ADD_TO_CART" -> {
                    val productId = json.optInt("productId")
                    val product = viewModel.allProducts.value.find { it.id == productId }
                    if (product != null) {
                        viewModel.addToCart(product)
                        sendResponse(ok("Added to cart", reqId))
                    } else {
                        sendResponse(error("Product not found", reqId))
                    }
                }
                "CLEAR_CART" -> {
                    viewModel.clearCart()
                    sendResponse(ok("Cart cleared", reqId))
                }
                "PING" -> {
                    sendResponse(JSONObject().put("type", "PONG").put("timestamp", System.currentTimeMillis()))
                }
                else -> {
                    Log.w(TAG, "Unknown command: $cmd")
                    sendResponse(error("Unknown command: $cmd", reqId))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error processing command", e)
            sendResponse(error("Malformed command: ${e.message}"))
        }
    }

    /** Card-to-card money transfer ("pay someone") driven from the website. */
    private fun handleTransfer(json: JSONObject, reqId: String) {
        val sourceCardId = json.optInt("sourceCardId", -1)
        val targetCardId = json.optInt("targetCardId", -1)
        val amount = json.optDouble("amount", 0.0)

        if (sourceCardId <= 0) {
            sendResponse(error("Pick the card the money is sent from", reqId))
            return
        }
        if (targetCardId <= 0) {
            sendResponse(error("Pick the card that receives the money", reqId))
            return
        }
        if (sourceCardId == targetCardId) {
            sendResponse(error("Sender and recipient must be different cards", reqId))
            return
        }
        if (amount <= 0.0) {
            sendResponse(error("Transfer amount must be greater than 0", reqId))
            return
        }

        viewModel.transferMoneyToPlayer(sourceCardId, targetCardId, amount) { success, message ->
            sendResponse(
                JSONObject()
                    .put("type", "TRANSFER_RESULT")
                    .put("success", success)
                    .put("message", message)
                    .withReqId(reqId)
            )
        }
    }

    /** Creates a new play card from the website, same fields the phone's Add Card sheet uses. */
    private fun handleAddCard(json: JSONObject, reqId: String) {
        val cardHolder = json.optString("cardHolder").trim()
        val cardNumber = json.optString("cardNumber").trim()
        val cardUid = json.optString("cardUid").trim()
        val balance = json.optDouble("balance", 0.0)
        val currency = json.optString("currency", "RSD").ifBlank { "RSD" }
        val colorHex = json.optString("colorHex", "#6200EE").ifBlank { "#6200EE" }
        val bankPassword = json.optString("bankPassword", "")

        if (cardHolder.isEmpty()) {
            sendResponse(error("Card holder name is required", reqId))
            return
        }
        if (cardNumber.replace(" ", "").length < 12) {
            sendResponse(error("Card number must be at least 12 digits", reqId))
            return
        }
        if (currency != "EUR" && currency != "RSD") {
            sendResponse(error("Currency must be EUR or RSD", reqId))
            return
        }

        val duplicateUid = cardUid.isNotEmpty() &&
            viewModel.allNfcCards.value.any { it.cardUid.equals(cardUid, ignoreCase = true) }
        if (duplicateUid) {
            sendResponse(error("A card with chip ID $cardUid already exists", reqId))
            return
        }

        viewModel.addNfcCard(
            cardHolder = cardHolder,
            cardNumber = cardNumber,
            cardUid = cardUid,
            balance = balance,
            currency = currency,
            colorHex = colorHex,
            bankPassword = bankPassword
        )
        sendResponse(ok("Card for $cardHolder created", reqId))
    }

    private fun handleAddProduct(json: JSONObject, reqId: String) {
        val name = json.optString("name").trim()
        val price = json.optDouble("price", 0.0)
        val category = json.optString("category", "General").ifBlank { "General" }

        if (name.isEmpty()) {
            sendResponse(error("Product name is required", reqId))
            return
        }
        if (price <= 0.0) {
            sendResponse(error("Product price must be greater than 0", reqId))
            return
        }

        viewModel.addProduct(name, price, category)
        sendResponse(ok("Product $name added", reqId))
    }

    private fun sendResponse(json: JSONObject) {
        if (!isConnected()) return

        bridgeScope.launch {
            try {
                val data = json.toString() + "\n"
                outputStream?.write(data.toByteArray())
                outputStream?.flush()
            } catch (e: IOException) {
                Log.e(TAG, "Error writing to USB", e)
            }
        }
    }

    private fun startStateCollection() {
        stateCollectionJob?.cancel()
        stateCollectionJob = bridgeScope.launch {
            // Push live data lists so the website stays in sync without polling.
            launch {
                viewModel.allNfcCards.collectLatest { cards ->
                    sendResponse(cardsPayload(cards))
                }
            }

            launch {
                viewModel.allProducts.collectLatest { products ->
                    sendResponse(productsPayload(products))
                }
            }

            launch {
                viewModel.allTransactions.collectLatest { txs ->
                    sendResponse(transactionsPayload(txs.take(50)))
                }
            }

            launch {
                viewModel.scannedNfcUid.collectLatest { uid ->
                    if (uid != null) {
                        sendResponse(JSONObject().put("type", "NFC_TAP").put("uid", uid))
                    }
                }
            }

            launch {
                viewModel.checkoutState.collectLatest { state ->
                    val obj = JSONObject().put("type", "CHECKOUT_UPDATE")
                    when (state) {
                        is CheckoutState.Idle -> {
                            obj.put("state", "Idle")
                        }
                        is CheckoutState.Processing -> {
                            obj.put("state", "Processing")
                        }
                        is CheckoutState.Loading -> {
                            obj.put("state", "Loading")
                            val details = JSONObject()
                            details.put("provider", state.provider)
                            details.put("cardHolder", state.cardHolder)
                            obj.put("details", details)

                            // Send CARD_TAPPED event
                            sendResponse(
                                JSONObject()
                                    .put("type", "CARD_TAPPED")
                                    .put("cardHolder", state.cardHolder)
                                    .put("provider", state.provider)
                            )
                        }
                        is CheckoutState.VerificationRequired -> {
                            obj.put("state", "VerificationRequired")
                        }
                        is CheckoutState.Success -> {
                            obj.put("state", "Success")
                            val details = JSONObject()
                            details.put("transactionId", state.transaction.transactionId)
                            details.put("amount", state.transaction.amount)
                            details.put("currency", state.transaction.currency)
                            obj.put("details", details)
                        }
                        is CheckoutState.Error -> {
                            obj.put("state", "Error")
                            val details = JSONObject()
                            details.put("message", state.message)
                            obj.put("details", details)
                        }
                    }
                    sendResponse(obj)
                }
            }
        }
    }
}
