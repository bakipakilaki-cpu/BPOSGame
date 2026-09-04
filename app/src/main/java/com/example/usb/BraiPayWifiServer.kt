package com.example.usb

import android.content.Context
import android.util.Log
import com.example.ui.BraiPayViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import org.json.JSONObject
import java.net.InetSocketAddress
import java.net.InetAddress
import java.net.NetworkInterface

class BraiPayWifiServer(
    private val context: Context,
    private val viewModel: BraiPayViewModel,
    port: Int = 8080
) : WebSocketServer(InetSocketAddress(port)) {

    private val scope = CoroutineScope(Dispatchers.IO + Job())
    private var client: WebSocket? = null

    init {
        isReuseAddr = true
    }

    override fun onOpen(conn: WebSocket, handshake: ClientHandshake) {
        Log.d("WifiServer", "New connection from ${conn.remoteSocketAddress}")
        client = conn
        
        // Start collecting state when client connects
        scope.launch {
            launch {
                viewModel.scannedNfcUid.collect { uid ->
                    if (uid != null) {
                        sendNfcTap(uid)
                    }
                }
            }
            viewModel.checkoutState.collect { state ->
                val stateName = state.javaClass.simpleName
                val details = JSONObject()
                if (state is com.example.ui.CheckoutState.Loading) {
                    details.put("provider", state.provider)
                    details.put("cardHolder", state.cardHolder)
                }
                if (state is com.example.ui.CheckoutState.Success) {
                    details.put("transactionId", state.transaction.transactionId)
                    details.put("amount", state.transaction.amount)
                }
                if (state is com.example.ui.CheckoutState.Error) {
                    details.put("message", state.message)
                }
                
                val obj = JSONObject().apply {
                    put("type", "CHECKOUT_UPDATE")
                    put("state", stateName)
                    put("details", details)
                }
                sendToClient(obj.toString())
            }
        }
    }

    override fun onClose(conn: WebSocket, code: Int, reason: String, remote: Boolean) {
        Log.d("WifiServer", "Closed connection to ${conn.remoteSocketAddress}")
        if (client == conn) client = null
    }

    override fun onMessage(conn: WebSocket, message: String) {
        Log.d("WifiServer", "Received message: $message")
        try {
            val req = JSONObject(message)
            val cmd = req.optString("cmd")

            val response = JSONObject()
            when (cmd) {
                "PING" -> {
                    response.put("type", "PONG")
                    response.put("timestamp", System.currentTimeMillis())
                }
                "GET_STATUS" -> {
                    response.put("type", "STATUS")
                    response.put("connected", true)
                    response.put("currency", viewModel.selectedCurrency.value)
                    response.put("total", viewModel.totalCartAmount.value)
                    response.put("checkoutState", viewModel.checkoutState.value.javaClass.simpleName)
                    response.put("businessName", viewModel.businessCompanyName.value)
                }
                "SET_CURRENCY" -> {
                    val curr = req.optString("currency", "RSD")
                    viewModel.selectCurrency(curr)
                    response.put("type", "OK")
                    response.put("message", "Currency set to $curr")
                }
                "PAY" -> {
                    val amount = req.optDouble("amount", 0.0)
                    viewModel.setCustomAmount(amount)
                    response.put("type", "OK")
                    response.put("message", "Ready to tap card for payment")
                }
                "CANCEL_PAY" -> {
                    viewModel.resetCheckoutState()
                    response.put("type", "OK")
                }
                "UPDATE_MONEY" -> {
                    val cardId = req.optInt("cardId", -1)
                    val amount = req.optDouble("amount", 0.0)
                    val action = req.optString("action", "ADD")
                    if (cardId != -1) {
                        viewModel.updateCardMoney(cardId, amount, action)
                        response.put("type", "OK")
                    } else {
                        response.put("type", "ERROR")
                        response.put("message", "Missing cardId")
                    }
                }
                "PAY_WITH_CARD" -> {
                    val cardId = req.optInt("cardId", -1)
                    val amount = req.optDouble("amount", 0.0)
                    val product = req.optString("productName", "Quick Pay")
                    if (cardId != -1) {
                        val card = viewModel.allNfcCards.value.find { it.id == cardId }
                        if (card != null) {
                            viewModel.setCustomAmount(amount)
                            viewModel.payWithGameCard(card)
                            response.put("type", "OK")
                        } else {
                            response.put("type", "ERROR")
                            response.put("message", "Card not found")
                        }
                    } else {
                        response.put("type", "ERROR")
                    }
                }
                "GET_CARDS" -> {
                    response.put("type", "CARDS")
                    val arr = org.json.JSONArray()
                    viewModel.allNfcCards.value.forEach { card ->
                        val c = JSONObject()
                        c.put("id", card.id)
                        c.put("cardHolder", card.cardHolder)
                        c.put("cardNumber", card.cardNumber)
                        c.put("balance", card.balance)
                        c.put("currency", card.currency)
                        c.put("colorHex", card.colorHex)
                        arr.put(c)
                    }
                    response.put("data", arr)
                }
                "GET_TRANSACTIONS" -> {
                    response.put("type", "TRANSACTIONS")
                    val limit = req.optInt("limit", 20)
                    val arr = org.json.JSONArray()
                    viewModel.allTransactions.value.take(limit).forEach { tx ->
                        val t = JSONObject()
                        t.put("id", tx.id)
                        t.put("productName", tx.productName)
                        t.put("amount", tx.amount)
                        t.put("status", tx.status)
                        t.put("paymentMethod", tx.paymentMethod)
                        t.put("timestamp", tx.timestamp)
                        arr.put(t)
                    }
                    response.put("data", arr)
                }
                else -> {
                    response.put("type", "ERROR")
                    response.put("message", "Unknown command")
                }
            }
            if (response.length() > 0) {
                sendToClient(response.toString())
            }
        } catch (e: Exception) {
            Log.e("WifiServer", "Error parsing message", e)
        }
    }

    override fun onError(conn: WebSocket?, ex: Exception) {
        Log.e("WifiServer", "Error", ex)
    }

    override fun onStart() {
        Log.d("WifiServer", "Server started successfully")
    }

    private fun sendToClient(msg: String) {
        try {
            client?.send(msg)
        } catch (e: Exception) {
            Log.e("WifiServer", "Failed to send", e)
        }
    }
    
    fun sendNfcTap(uid: String) {
        val obj = JSONObject().apply {
            put("type", "NFC_TAP")
            put("uid", uid)
        }
        sendToClient(obj.toString())
    }
}
