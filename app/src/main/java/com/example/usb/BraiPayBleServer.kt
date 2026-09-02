package com.example.usb

import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.content.Context
import android.os.ParcelUuid
import android.util.Log
import com.example.ui.BraiPayViewModel
import kotlinx.coroutines.*
import org.json.JSONObject
import java.util.UUID

@SuppressLint("MissingPermission")
class BraiPayBleServer(private val context: Context, private val viewModel: BraiPayViewModel) {

    private val bluetoothManager: BluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
    private var bluetoothGattServer: BluetoothGattServer? = null
    private var advertiser: BluetoothLeAdvertiser? = null

    private var connectedDevice: BluetoothDevice? = null
    
    // BLE Service and Characteristic UUIDs for BraiPay
    private val SERVICE_UUID = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb")
    private val CHAR_TX_UUID = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb") // App sends to Web
    private val CHAR_RX_UUID = UUID.fromString("0000ffe2-0000-1000-8000-00805f9b34fb") // Web sends to App

    private var txCharacteristic: BluetoothGattCharacteristic? = null
    
    private val scope = CoroutineScope(Dispatchers.IO + Job())
    private var isStarted = false
    
    // Buffer for incoming BLE data (since MTU is small)
    private var rxBuffer = StringBuilder()

    // Reuse the logic from BraiPayUsbBridge for handling JSON commands
    // In a real app we'd refactor this into a shared CommandProcessor
    
    fun start() {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            Log.e("BLEServer", "Bluetooth not enabled or not supported")
            return
        }
        if (isStarted) return
        isStarted = true

        bluetoothGattServer = bluetoothManager.openGattServer(context, gattServerCallback)
        
        val service = BluetoothGattService(SERVICE_UUID, BluetoothGattService.SERVICE_TYPE_PRIMARY)
        
        txCharacteristic = BluetoothGattCharacteristic(CHAR_TX_UUID, 
            BluetoothGattCharacteristic.PROPERTY_NOTIFY or BluetoothGattCharacteristic.PROPERTY_READ,
            BluetoothGattCharacteristic.PERMISSION_READ)
            
        val rxCharacteristic = BluetoothGattCharacteristic(CHAR_RX_UUID,
            BluetoothGattCharacteristic.PROPERTY_WRITE or BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE,
            BluetoothGattCharacteristic.PERMISSION_WRITE)
            
        // Client Characteristic Configuration Descriptor (CCCD)
        val cccd = BluetoothGattDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"), BluetoothGattDescriptor.PERMISSION_READ or BluetoothGattDescriptor.PERMISSION_WRITE)
        txCharacteristic?.addDescriptor(cccd)

        service.addCharacteristic(txCharacteristic)
        service.addCharacteristic(rxCharacteristic)
        
        bluetoothGattServer?.addService(service)
        
        startAdvertising()
        observeViewModel()
    }

    private fun startAdvertising() {
        advertiser = bluetoothAdapter?.bluetoothLeAdvertiser
        if (advertiser == null) return

        val settings = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
            .setConnectable(true)
            .setTimeout(0)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
            .build()

        val data = AdvertiseData.Builder()
            .setIncludeDeviceName(true)
            .addServiceUuid(ParcelUuid(SERVICE_UUID))
            .build()

        advertiser?.startAdvertising(settings, data, advertiseCallback)
    }

    private val advertiseCallback = object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
            Log.d("BLEServer", "Advertising started")
        }
        override fun onStartFailure(errorCode: Int) {
            Log.e("BLEServer", "Advertising failed: \$errorCode")
        }
    }

    fun stop() {
        isStarted = false
        advertiser?.stopAdvertising(advertiseCallback)
        connectedDevice?.let { bluetoothGattServer?.cancelConnection(it) }
        bluetoothGattServer?.close()
        scope.cancel()
    }

    private val gattServerCallback = object : BluetoothGattServerCallback() {
        override fun onConnectionStateChange(device: BluetoothDevice?, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                connectedDevice = device
                Log.d("BLEServer", "Device connected: \${device?.address}")
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                if (connectedDevice?.address == device?.address) {
                    connectedDevice = null
                    rxBuffer.clear()
                }
                Log.d("BLEServer", "Device disconnected: \${device?.address}")
            }
        }
        
        override fun onCharacteristicWriteRequest(device: BluetoothDevice?, requestId: Int, characteristic: BluetoothGattCharacteristic?, preparedWrite: Boolean, responseNeeded: Boolean, offset: Int, value: ByteArray?) {
            if (responseNeeded) {
                bluetoothGattServer?.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, value)
            }
            if (characteristic?.uuid == CHAR_RX_UUID && value != null) {
                val chunk = String(value, Charsets.UTF_8)
                rxBuffer.append(chunk)
                
                // If chunk ends with newline, we process it
                if (chunk.endsWith("\n")) {
                    val fullCommand = rxBuffer.toString().trim()
                    rxBuffer.clear()
                    if (fullCommand.isNotEmpty()) {
                        processCommand(fullCommand)
                    }
                }
            }
        }

        override fun onDescriptorWriteRequest(device: BluetoothDevice?, requestId: Int, descriptor: BluetoothGattDescriptor?, preparedWrite: Boolean, responseNeeded: Boolean, offset: Int, value: ByteArray?) {
            if (responseNeeded) {
                bluetoothGattServer?.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, value)
            }
        }
    }
    
    private fun processCommand(cmdString: String) {
        scope.launch {
            try {
                val json = JSONObject(cmdString)
                val cmd = json.optString("cmd")
                val reqId = json.optString("reqId", "")
                
                // Handle basic commands to prove it works
                when (cmd) {
                    "PING" -> sendResponse(JSONObject().put("type", "PONG").put("timestamp", System.currentTimeMillis()), reqId)
                    "GET_STATUS" -> {
                        val state = viewModel.checkoutState.value
                        val stateStr = state.javaClass.simpleName
                        val currency = viewModel.selectedCurrency.value
                        val total = viewModel.totalCartAmount.value
                        sendResponse(JSONObject()
                            .put("type", "STATUS")
                            .put("connected", true)
                            .put("currency", currency)
                            .put("total", total)
                            .put("checkoutState", stateStr), reqId)
                    }
                    // For full functionality, we should use the same command processor as USB Bridge
                    // But we'll just redirect to a shared function or re-implement here for MVP
                    else -> sendResponse(JSONObject().put("type", "ERROR").put("message", "Command forwarded to USB Bridge logic not fully implemented yet in BLE"), reqId)
                }
            } catch (e: Exception) {
                Log.e("BLEServer", "Error parsing command", e)
            }
        }
    }

    fun sendResponse(json: JSONObject, reqId: String? = null) {
        if (!isStarted || connectedDevice == null) return
        if (!reqId.isNullOrEmpty()) json.put("reqId", reqId)
        val data = (json.toString() + "\n").toByteArray(Charsets.UTF_8)
        
        // Split data into chunks if it's larger than MTU (assume 20 bytes standard if MTU not negotiated, but we'll try 500)
        val chunkSize = 500 
        var offset = 0
        while (offset < data.size) {
            val end = Math.min(offset + chunkSize, data.size)
            val chunk = data.copyOfRange(offset, end)
            txCharacteristic?.value = chunk
            bluetoothGattServer?.notifyCharacteristicChanged(connectedDevice, txCharacteristic, false)
            offset = end
            Thread.sleep(10) // Small delay to prevent overflow
        }
    }

    private fun observeViewModel() {
        scope.launch {
            viewModel.checkoutState.collect { state ->
                val stateName = state.javaClass.simpleName
                sendResponse(JSONObject().put("type", "CHECKOUT_UPDATE").put("state", stateName))
            }
        }
        scope.launch {
            viewModel.scannedNfcUid.collect { uid ->
                if (uid != null) {
                    sendResponse(JSONObject().put("type", "NFC_TAP").put("uid", uid))
                }
            }
        }
    }
}
