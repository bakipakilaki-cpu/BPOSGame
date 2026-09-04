package com.example

import android.net.wifi.WifiManager
import android.text.format.Formatter

import com.example.ui.TerminalSoundPlayer
import com.example.ui.generateRandomHexUid
import com.example.ui.generateRandom7ByteHexUid

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.nfc.NfcAdapter
import androidx.activity.ComponentActivity
import androidx.fragment.app.FragmentActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import android.content.Context
import android.content.ContextWrapper
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material.icons.rounded.Backspace
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.Contactless
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.IntegrationInstructions
import androidx.compose.material.icons.rounded.LibraryAdd
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.LockOpen
import androidx.compose.animation.core.LinearEasing
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material.icons.rounded.Style
import androidx.compose.material.icons.rounded.Brush
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material.icons.rounded.AdminPanelSettings
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Nfc
import androidx.compose.material.icons.rounded.Percent
import androidx.compose.material.icons.rounded.PlayForWork
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material.icons.rounded.Storefront
import androidx.compose.material.icons.rounded.Tune
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material.icons.rounded.HideImage
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Store
import androidx.compose.material.icons.rounded.SupportAgent
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material.icons.rounded.Work
import androidx.compose.material.icons.rounded.BusinessCenter
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material.icons.rounded.Sell
import androidx.compose.material.icons.rounded.Business
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material.icons.rounded.Badge
import androidx.compose.material.icons.rounded.Storefront
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.Product
import com.example.data.TransactionHistory
import com.example.ui.BraiPayViewModel
import com.example.ui.CheckoutState
import com.example.ui.InboundPaymentRequest
import com.example.ui.theme.MyApplicationTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : FragmentActivity() {

    private lateinit var viewModel: BraiPayViewModel
    private lateinit var usbBridge: com.example.usb.BraiPayUsbBridge
    private var nfcAdapter: NfcAdapter? = null

    private val raiPosLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val data = result.data
        val resultCode = result.resultCode
        val txId = data?.getStringExtra("transactionId") ?: ("TX-" + System.currentTimeMillis().toString().takeLast(6))
        
        if (resultCode == RESULT_OK) {
            viewModel.recordRealPaymentCompletion(txId, "COMPLETED", "RaiPOS Terminal")
            Toast.makeText(this, "Payment Approved via RaiPOS!", Toast.LENGTH_LONG).show()
        } else {
            val errorMsg = data?.getStringExtra("errorMessage") ?: "Cancelled or Unknown Error"
            viewModel.recordRealPaymentCompletion(txId, "FAILED", "RaiPOS Terminal")
            Toast.makeText(this, "Payment Failed: $errorMsg", Toast.LENGTH_LONG).show()
        }
    }

    private fun handleNfcIntent(intent: Intent?) {
        if (intent == null) return
        val action = intent.action
        val mimeType = intent.type
        val isWatchOob = mimeType?.contains("shealth", ignoreCase = true) == true ||
                         action?.contains("shealth", ignoreCase = true) == true ||
                         intent.hasExtra("application/vnd.shealth.le.oob")

        if (NfcAdapter.ACTION_TAG_DISCOVERED == action ||
            NfcAdapter.ACTION_TECH_DISCOVERED == action ||
            NfcAdapter.ACTION_NDEF_DISCOVERED == action ||
            isWatchOob) {

            @Suppress("DEPRECATION")
            val tag = intent.getParcelableExtra<android.nfc.Tag>(NfcAdapter.EXTRA_TAG)
            val tagIdBytes = tag?.id ?: intent.getByteArrayExtra(NfcAdapter.EXTRA_ID)
            val uidHex = tagIdBytes?.joinToString(":") { String.format("%02X", it) }

            val uidToUse = if (isWatchOob && (uidHex == null || uidHex.isEmpty())) {
                "WATCH:SHEALTH:OOB"
            } else if (isWatchOob && uidHex != null) {
                "WATCH:$uidHex"
            } else {
                uidHex ?: "04:A2:8B:1F"
            }

            if (::viewModel.isInitialized) {
                if (isWatchOob) {
                    Toast.makeText(this, "⌚ Galaxy Watch Detected (application/vnd.shealth.le.oob)!", Toast.LENGTH_LONG).show()
                }
                viewModel.handlePhysicalNfcCardTap(uidToUse)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Retrieve default NFC Adapter
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC is not supported on this device. You can still play using virtual simulated taps!", Toast.LENGTH_LONG).show()
        } else if (!nfcAdapter!!.isEnabled) {
            Toast.makeText(this, "Please enable NFC in your system settings to scan real physical cards!", Toast.LENGTH_LONG).show()
        }

        setContent {
            MyApplicationTheme {
                viewModel = viewModel()
                
                val currentContext = LocalContext.current
                
                // Request Bluetooth runtime permissions for Android 12+
                val permissions = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                    arrayOf(
                        android.Manifest.permission.BLUETOOTH_CONNECT,
                        android.Manifest.permission.BLUETOOTH_ADVERTISE,
                        android.Manifest.permission.BLUETOOTH_SCAN
                    )
                } else {
                    emptyArray()
                }
                
                val launcher = androidx.activity.compose.rememberLauncherForActivityResult(
                    androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions()
                ) { permissionsMap ->
                    val allGranted = permissionsMap.values.all { it }
                    if (!allGranted && permissions.isNotEmpty()) {
                        Toast.makeText(currentContext, "Please allow Bluetooth permissions in Settings to connect with the Web Dashboard.", Toast.LENGTH_LONG).show()
                    } else {
                        try {
                            val bleServer = com.example.usb.BraiPayBleServer(currentContext, viewModel)
                            val wifiServer = com.example.usb.BraiPayWifiServer(currentContext, viewModel)
                            wifiServer.start()
                            val wifiManager = currentContext.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                            val ip = Formatter.formatIpAddress(wifiManager.connectionInfo.ipAddress)
                            Toast.makeText(currentContext, "WiFi Server Running on: " + ip + ":8080", Toast.LENGTH_LONG).show()
                            bleServer.start()
                        } catch (e: Exception) {
                            Log.e("MainActivity", "Failed to start BLE Server: ${e.message}")
                        }
                    }
                }

                LaunchedEffect(Unit) {
                    if (permissions.isNotEmpty()) {
                        launcher.launch(permissions)
                    }
                    val usbManager = currentContext.getSystemService(Context.USB_SERVICE) as android.hardware.usb.UsbManager
                    usbBridge = com.example.usb.BraiPayUsbBridge(usbManager, viewModel)
                    
                    if (permissions.isEmpty()) {
                        try {
                            val bleServer = com.example.usb.BraiPayBleServer(currentContext, viewModel)
                            val wifiServer = com.example.usb.BraiPayWifiServer(currentContext, viewModel)
                            wifiServer.start()
                            val wifiManager = currentContext.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                            val ip = Formatter.formatIpAddress(wifiManager.connectionInfo.ipAddress)
                            Toast.makeText(currentContext, "WiFi Server Running on: " + ip + ":8080", Toast.LENGTH_LONG).show()
                            bleServer.start()
                        } catch (e: Exception) {
                            Log.e("MainActivity", "Failed to start BLE Server: ${e.message}")
                        }
                    }

                    if (intent?.action == android.hardware.usb.UsbManager.ACTION_USB_ACCESSORY_ATTACHED) {
                        val accessory = intent?.getParcelableExtra<android.hardware.usb.UsbAccessory>(android.hardware.usb.UsbManager.EXTRA_ACCESSORY)
                        if (accessory != null) {
                            usbBridge.start(accessory)
                        }
                    }
                }

                // Handle intent on initial launch
                LaunchedEffect(intent) {
                    intent?.let { 
                        viewModel.handleInboundIntent(it)
                        handleNfcIntent(it)
                    }
                }

                BraiPayAppContent(
                    viewModel = viewModel,
                    onLaunchRealIntent = { intent ->
                        try {
                            raiPosLauncher.launch(intent)
                        } catch (e: Exception) {
                            Toast.makeText(
                                this,
                                "Could not start RaiPOS app-to-app. Check configuration.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        nfcAdapter?.let { adapter ->
            try {
                val options = Bundle().apply {
                    putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 250)
                }
                adapter.enableReaderMode(
                    this,
                    { tag ->
                        val tagIdBytes = tag.id
                        val uidHex = tagIdBytes.joinToString(":") { String.format("%02X", it) }
                        runOnUiThread {
                            if (::viewModel.isInitialized) {
                                viewModel.handlePhysicalNfcCardTap(uidHex)
                            }
                        }
                    },
                    NfcAdapter.FLAG_READER_NFC_A or NfcAdapter.FLAG_READER_NFC_B or 
                    NfcAdapter.FLAG_READER_NFC_F or NfcAdapter.FLAG_READER_NFC_V or 
                    NfcAdapter.FLAG_READER_NFC_BARCODE,
                    options
                )
            } catch (e: Exception) {
                // Ignore reader mode failures on some custom ROMs
            }
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            nfcAdapter?.disableReaderMode(this)
        } catch (e: Exception) {
            // Ignore
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (::viewModel.isInitialized) {
            viewModel.handleInboundIntent(intent)
            handleNfcIntent(intent)
        }
        
        if (intent.action == android.hardware.usb.UsbManager.ACTION_USB_ACCESSORY_ATTACHED) {
            val accessory = intent.getParcelableExtra<android.hardware.usb.UsbAccessory>(android.hardware.usb.UsbManager.EXTRA_ACCESSORY)
            if (accessory != null && ::usbBridge.isInitialized) {
                usbBridge.start(accessory)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::usbBridge.isInitialized) {
            usbBridge.stop()
        }
    }
}

@Composable
fun BraiPayAppContent(
    viewModel: BraiPayViewModel,
    onLaunchRealIntent: (Intent) -> Unit
) {
    val context = LocalContext.current
    var currentTab by remember { mutableStateOf(0) }
    
    val cartItems by viewModel.cartItems.collectAsState()
    val customAmount by viewModel.customAmount.collectAsState()
    val totalAmount by viewModel.totalCartAmount.collectAsState()
    val sandboxMode by viewModel.sandboxMode.collectAsState()
    val raiPosPackage by viewModel.raiPosPackageName.collectAsState()
    val isRaiPosInstalled = viewModel.isRaiPosInstalled(context)
    val checkoutState by viewModel.checkoutState.collectAsState()
    val incomingRequest by viewModel.incomingIntentData.collectAsState()
    val selectedCurrency by viewModel.selectedCurrency.collectAsState()

    // Bank connection states
    val isSimpleModeEnabled by viewModel.isSimpleModeEnabled.collectAsState()
    val isBankModeEnabled by viewModel.isBankModeEnabled.collectAsState()
    val connectedBankUserCardId by viewModel.connectedBankUserCardId.collectAsState()
    val customBankAccountIban by viewModel.customBankAccountIban.collectAsState()
    val selectedBankName by viewModel.selectedBankName.collectAsState()
    
    var showSettingsDialog by remember { mutableStateOf(false) }

    LaunchedEffect(isSimpleModeEnabled) {
        if (isSimpleModeEnabled) {
            currentTab = 0
        }
    }

    // Automatic toast notification for incoming external requests
    LaunchedEffect(incomingRequest) {
        incomingRequest?.let {
            val amountStr = if (it.currency == "RSD") "${String.format("%.2f", it.amount)} RSD" else "€${String.format("%.2f", it.amount)}"
            Toast.makeText(
                context,
                "Inbound payment received from ${it.sourceApp}: $amountStr",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (!isSimpleModeEnabled) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    modifier = Modifier.height(72.dp)
                ) {
                    val tabItems = mutableListOf(
                        Triple("Terminal", Icons.Rounded.Storefront, 0),
                        Triple("History", Icons.Rounded.History, 1),
                        Triple("Catalog", Icons.Rounded.LibraryAdd, 2),
                        Triple("Play Cards", Icons.Rounded.Contactless, 3)
                    )
                    if (isBankModeEnabled) {
                        tabItems.add(Triple("Bank Hub", Icons.Rounded.AccountBalance, 4))
                    }
                    
                    tabItems.forEach { (label, icon, tabIndex) ->
                        NavigationBarItem(
                            selected = currentTab == tabIndex,
                            onClick = { currentTab = tabIndex },
                            icon = { Icon(icon, contentDescription = label, modifier = Modifier.size(24.dp)) },
                            label = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                            ),
                            modifier = Modifier.testTag("nav_tab_$tabIndex")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            // BraiPay Brand Header or Simple Mode Dashboard Bar
            if (isSimpleModeEnabled) {
                var showExitPasscodeDialog by remember { mutableStateOf(false) }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "bPos Terminal (Simple Mode)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                    Button(
                        onClick = { showExitPasscodeDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp).testTag("dashboard_button")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AdminPanelSettings,
                            contentDescription = "Dashboard",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Dashboard",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                if (showExitPasscodeDialog) {
                    PasscodeDialog(
                        onCorrectPasscode = {
                            viewModel.setSimpleMode(false)
                            showExitPasscodeDialog = false
                        },
                        onDismiss = {
                            showExitPasscodeDialog = false
                        }
                    )
                }
            } else {
                BraiPayHeader(
                    sandboxMode = sandboxMode,
                    isRaiPosInstalled = isRaiPosInstalled,
                    selectedCurrency = selectedCurrency,
                    onCurrencyChange = { viewModel.selectCurrency(it) },
                    onToggleSandbox = { viewModel.toggleSandboxMode(it) },
                    onDownloadRaiPos = { viewModel.redirectToPlayStore(context) },
                    onOpenSettings = { showSettingsDialog = true }
                )
            }

            // Floating Inbound Request Banner
            AnimatedVisibility(
                visible = incomingRequest != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                incomingRequest?.let { req ->
                    InboundRequestBanner(
                        request = req,
                        onDismiss = { viewModel.dismissIncomingRequest() },
                        onAccept = {
                            currentTab = 0 // Go to terminal
                        }
                    )
                }
            }

            // Main Active Panel View
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                when (currentTab) {
                    0 -> RegisterTerminalScreen(
                        viewModel = viewModel,
                        onTriggerCheckout = {
                            viewModel.initiateCheckout(context, onLaunchRealIntent)
                        }
                    )
                    1 -> HistoryLogsScreen(viewModel = viewModel)
                    2 -> CatalogScreen(viewModel = viewModel)
                    3 -> NfcCardsScreen(viewModel = viewModel)
                    4 -> if (isBankModeEnabled) BankHubScreen(viewModel = viewModel)
                }
            }
        }
    }

    if (showSettingsDialog) {
        BankSettingsDialog(
            viewModel = viewModel,
            onDismiss = { showSettingsDialog = false }
        )
    }

    // High-Fidelity SoftPOS Checkout Modal Overlay
    if (checkoutState != CheckoutState.Idle) {
        val playCards by viewModel.allNfcCards.collectAsState()
        val cartItems by viewModel.cartItems.collectAsState()
        val customAmount by viewModel.customAmount.collectAsState()
        CheckoutOverlay(
            state = checkoutState,
            totalAmount = totalAmount,
            currency = selectedCurrency,
            isSandbox = sandboxMode,
            allNfcCards = playCards,
            cartItems = cartItems,
            customAmount = customAmount,
            onClose = { viewModel.resetCheckoutState() },
            onSimulateTap = { success ->
                val txId = "BP-SIM-" + System.currentTimeMillis().toString().takeLast(6)
                if (success) {
                    viewModel.recordRealPaymentCompletion(txId, "COMPLETED", "bPosgame Sandbox Simulator")
                } else {
                    viewModel.recordRealPaymentCompletion(txId, "FAILED", "bPosgame Sandbox Simulator")
                }
            },
            onSimulateCardTap = { card ->
                viewModel.handlePhysicalNfcCardTap(card.cardUid)
            },
            onPayOnline = { cardHolder, cardNumber, expiry, cvv ->
                viewModel.payOnlineWithCardDetails(cardHolder, cardNumber, expiry, cvv)
            },
            onVerifyOtp = { otpCode ->
                viewModel.verifyOnlineOtp(otpCode)
            }
        )
    }
}

// Beautiful Custom Brand Header for bPosgame
@Composable
fun BraiPayHeader(
    sandboxMode: Boolean,
    isRaiPosInstalled: Boolean,
    selectedCurrency: String,
    onCurrencyChange: (String) -> Unit,
    onToggleSandbox: (Boolean) -> Unit,
    onDownloadRaiPos: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .padding(vertical = 16.dp, horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.secondary
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Contactless,
                            contentDescription = "bPosgame logo",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "bPosgame",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontFamily = FontFamily.SansSerif
                    )
                }
                Text(
                    text = "Contactless NFC Play Terminal",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 42.dp)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Beautiful Settings/Bank Button
                IconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier
                        .size(36.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f), CircleShape)
                        .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Settings,
                        contentDescription = "Settings & Bank Account",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Sandbox Play Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "PLAY MODE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(10.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status Indicators Row
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF10B981))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "NFC Scanner Active",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF10B981)
                )
            }

            // Currency Selector Row!
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(2.dp)
            ) {
                listOf("EUR", "RSD").forEach { curr ->
                    val isSelected = selectedCurrency == curr
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                            .clickable { onCurrencyChange(curr) }
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (curr == "RSD") "RSD (din)" else "EUR (€)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}

// Gorgeous Auto-Download RaiPOS Alert Box
@Composable
fun RaiPosInstallBanner(
    raiPosPackage: String,
    onDownload: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.08f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = Icons.Rounded.Download,
                contentDescription = "Download RaiPOS",
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "RaiPOS Driver Needed",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Install the Raiffeisen RaiPOS app to process physical debit/credit card taps directly.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    lineHeight = 14.sp
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = onDownload,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary
                ),
                contentPadding = ButtonDefaults.ContentPadding,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("download_raipos_button")
            ) {
                Text("Install", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// Inbound Intent Request Alert Panel
@Composable
fun InboundRequestBanner(
    request: InboundPaymentRequest,
    onDismiss: () -> Unit,
    onAccept: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f),
                shape = RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.PlayForWork,
                contentDescription = "Inbound Intent",
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Payment Request from ${request.sourceApp}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "${request.productName} • ${if (request.currency == "RSD") "${String.format("%.2f", request.amount)} RSD" else "€${String.format("%.2f", request.amount)}"}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("dismiss_intent_request")
            ) {
                Icon(
                    imageVector = Icons.Rounded.Delete,
                    contentDescription = "Dismiss request",
                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            }
            Button(
                onClick = onAccept,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("accept_intent_button")
            ) {
                Text("Checkout", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// TAB 1: REGISTER TERMINAL SCREEN
@Composable
fun RegisterTerminalScreen(
    viewModel: BraiPayViewModel,
    onTriggerCheckout: () -> Unit
) {
    val products by viewModel.allProducts.collectAsState()
    val cart by viewModel.cartItems.collectAsState()
    val customAmount by viewModel.customAmount.collectAsState()
    val totalAmount by viewModel.totalCartAmount.collectAsState()
    val currency by viewModel.selectedCurrency.collectAsState()
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Grand Total Amount Display
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "TOTAL CHECKOUT AMOUNT",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.2.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (currency == "RSD") "RSD" else "€",
                        fontSize = if (currency == "RSD") 22.sp else 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = if (currency == "RSD") 12.dp else 6.dp, end = 6.dp)
                    )
                    Text(
                        text = String.format(Locale.US, "%.2f", totalAmount),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                // Display cart composition if any
                if (cart.isNotEmpty() || customAmount > 0.0) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                    
                    Column(modifier = Modifier.fillMaxWidth()) {
                        cart.forEach { (prod, qty) ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${qty}x ${prod.name}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                val displayPrice = if (currency == "EUR") prod.price / 117.0 else prod.price
                                Text(
                                    text = if (currency == "RSD") "${String.format("%.2f", displayPrice * qty)} RSD" else "€${String.format("%.2f", displayPrice * qty)}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                        if (customAmount > 0.0) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Custom Keyboard Amount",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = if (currency == "RSD") "${String.format("%.2f", customAmount)} RSD" else "€${String.format("%.2f", customAmount)}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    viewModel.clearCart()
                                },
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Refresh,
                                contentDescription = "Clear",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Clear Transaction",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Payout Destination Selector
        val cardsList by viewModel.allNfcCards.collectAsState()
        val recipientId by viewModel.terminalRecipientCardId.collectAsState()
        var expandedRecipientMenu by remember { mutableStateOf(false) }
        val selectedRecipient = cardsList.find { it.id == recipientId }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AdminPanelSettings,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Payout Destination",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { expandedRecipientMenu = true },
                        modifier = Modifier.fillMaxWidth().testTag("terminal_recipient_btn"),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = selectedRecipient?.let { "${it.cardHolder.uppercase()} (Play Card)" } ?: "Merchant Bank / System (Default)",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                imageVector = Icons.Rounded.ArrowDropDown,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    androidx.compose.material3.DropdownMenu(
                        expanded = expandedRecipientMenu,
                        onDismissRequest = { expandedRecipientMenu = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        androidx.compose.material3.DropdownMenuItem(
                            text = { Text("Merchant Bank / System (Default)", fontWeight = FontWeight.Bold) },
                            onClick = {
                                viewModel.setTerminalRecipientCardId(null)
                                expandedRecipientMenu = false
                            }
                        )
                        if (cardsList.isNotEmpty()) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                        }
                        cardsList.forEach { card ->
                            androidx.compose.material3.DropdownMenuItem(
                                text = { Text("Transfer to player: ${card.cardHolder.uppercase()} (${card.currency})") },
                                onClick = {
                                    viewModel.setTerminalRecipientCardId(card.id)
                                    expandedRecipientMenu = false
                                }
                            )
                        }
                    }
                }
                
                Text(
                    text = if (selectedRecipient != null) {
                        "Funds will be deducted from client card and transferred directly to ${selectedRecipient.cardHolder}'s card."
                    } else {
                        "Funds are deducted from client card and paid to the standard merchant/bank account."
                    },
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Action Pay Button
        Button(
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onTriggerCheckout()
            },
            enabled = totalAmount > 0.0,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("checkout_trigger_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(16.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Nfc, contentDescription = null, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = if (currency == "RSD") "TAP TO PAY ${String.format("%.2f", totalAmount)} RSD" else "TAP TO PAY €${String.format("%.2f", totalAmount)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Split Tab Grid - Custom NumPad vs Product Catalog Quick Add
        var keypadTab by remember { mutableStateOf(false) }

        var selectedCategoryFilter by remember { mutableStateOf("All") }
        val categoryFilters = remember(products) {
            val list = mutableListOf("All", "Mac Menu", "Bills", "Food", "Company", "Online Purchase")
            products.forEach { p ->
                if (p.category.isNotBlank() && !list.contains(p.category)) {
                    list.add(p.category)
                }
            }
            list
        }

        val filteredProducts = remember(products, selectedCategoryFilter) {
            if (selectedCategoryFilter == "All") products
            else products.filter { it.category.equals(selectedCategoryFilter, ignoreCase = true) }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (keypadTab) "Manual Amount Keyboard" else "Select Products to Charge",
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable { keypadTab = !keypadTab }
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (keypadTab) Icons.Rounded.Storefront else Icons.Rounded.Percent,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (keypadTab) "Go to Catalog" else "Manual Amount",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        if (keypadTab) {
            // Numpad for Custom Charges
            NumpadLayout(
                currentAmount = customAmount,
                onAmountChanged = { viewModel.setCustomAmount(it) }
            )
        } else {
            // Category Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (selectedCategoryFilter != "All") {
                    OutlinedButton(
                        onClick = { selectedCategoryFilter = "All" },
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                        modifier = Modifier.height(32.dp).testTag("pos_clear_category_filter"),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(Icons.Rounded.Close, contentDescription = null, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("Clear", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                categoryFilters.forEach { cat ->
                    val selected = selectedCategoryFilter.equals(cat, ignoreCase = true)
                    FilterChip(
                        selected = selected,
                        onClick = { selectedCategoryFilter = if (selected && cat != "All") "All" else cat },
                        label = {
                            Text(
                                text = when (cat) {
                                    "Mac Menu" -> "🍔 Mac Menu"
                                    "Bills" -> "📄 Bills"
                                    "Food" -> "🍕 Food"
                                    "Company" -> "🏢 Company"
                                    "Online Purchase" -> "🛒 Online"
                                    else -> "All (${products.size})"
                                },
                                fontSize = 11.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = if (cat == "Mac Menu") Color(0xFFFFBC0D) else MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = if (cat == "Mac Menu") Color(0xFF27251F) else MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }

                // Quick preseed button for Mac Menu
                if (products.none { it.category.contains("Mac", ignoreCase = true) }) {
                    Button(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.preseedMacMenu { msg ->
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDA291C)),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(32.dp).testTag("quick_load_mac_menu_btn")
                    ) {
                        Text("🍔 Load Mac Menu", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            // Catalog grid quick selection list
            if (filteredProducts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Storefront,
                            contentDescription = "Empty Category",
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (selectedCategoryFilter == "Mac Menu") "No Mac Menu Items Logged" else "Category Empty",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        if (selectedCategoryFilter == "Mac Menu") {
                            Button(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    viewModel.preseedMacMenu { msg ->
                                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDA291C)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("🍔 Add Full Mac Menu (19 Items)", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        } else {
                            Text(
                                text = "Add products in the 'Catalog' tab below or select a different category.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredProducts) { prod ->
                        val qty = cart[prod] ?: 0
                        ProductQuickCard(
                            product = prod,
                            cartQty = qty,
                            currency = currency,
                            onAdd = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.addToCart(prod)
                            },
                            onRemove = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.removeFromCart(prod)
                            }
                        )
                    }
                }
            }
        }
    }
}

// Rapid Numpad Layout
@Composable
fun NumpadLayout(
    currentAmount: Double,
    onAmountChanged: (Double) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    var inputStr by remember {
        mutableStateOf(
            if (currentAmount > 0.0) {
                if (currentAmount % 1.0 == 0.0) String.format(Locale.US, "%.0f", currentAmount)
                else String.format(Locale.US, "%.2f", currentAmount)
            } else ""
        )
    }

    LaunchedEffect(currentAmount) {
        if (currentAmount == 0.0 && inputStr.isNotBlank() && inputStr != "0" && inputStr != "0." && inputStr != "0.0") {
            inputStr = ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Dedicated Visual Display Box for Manual Amount Input
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "MANUAL ENTRY AMOUNT",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 0.8.sp
                    )
                    Text(
                        text = if (inputStr.isBlank()) "0" else inputStr,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (inputStr.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            inputStr = ""
                            onAmountChanged(0.0)
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Backspace,
                            contentDescription = "Clear",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }

        val keys = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf(".", "0", "00", "⌫"),
            listOf("CLR")
        )

        keys.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                row.forEach { key ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(if (key == "CLR") 40.dp else 48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                when (key) {
                                    "CLR" -> MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                                    "⌫" -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
                                    else -> MaterialTheme.colorScheme.surface
                                }
                            )
                            .border(
                                width = 1.dp,
                                color = when (key) {
                                    "CLR" -> MaterialTheme.colorScheme.error.copy(alpha = 0.3f)
                                    "⌫" -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)
                                    else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)
                                },
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                when (key) {
                                    "CLR" -> {
                                        inputStr = ""
                                    }
                                    "⌫" -> {
                                        if (inputStr.isNotEmpty()) {
                                            inputStr = inputStr.dropLast(1)
                                        }
                                    }
                                    "." -> {
                                        if (!inputStr.contains(".")) {
                                            inputStr = if (inputStr.isEmpty()) "0." else "$inputStr."
                                        }
                                    }
                                    "00" -> {
                                        if (inputStr.isEmpty() || inputStr == "0") {
                                            inputStr = "0"
                                        } else if (inputStr.contains(".")) {
                                            val decIndex = inputStr.indexOf(".")
                                            val decimals = inputStr.length - 1 - decIndex
                                            if (decimals == 0) inputStr += "00"
                                            else if (decimals == 1) inputStr += "0"
                                        } else {
                                            if (inputStr.length < 8) inputStr += "00"
                                        }
                                    }
                                    "0" -> {
                                        if (inputStr.isEmpty() || inputStr == "0") {
                                            inputStr = "0"
                                        } else if (inputStr.contains(".")) {
                                            val decIndex = inputStr.indexOf(".")
                                            val decimals = inputStr.length - 1 - decIndex
                                            if (decimals < 2) inputStr += "0"
                                        } else {
                                            if (inputStr.length < 8) inputStr += "0"
                                        }
                                    }
                                    else -> {
                                        // Digits 1-9
                                        if (inputStr == "0" || inputStr.isEmpty()) {
                                            inputStr = key
                                        } else if (inputStr.contains(".")) {
                                            val decIndex = inputStr.indexOf(".")
                                            val decimals = inputStr.length - 1 - decIndex
                                            if (decimals < 2) inputStr += key
                                        } else {
                                            if (inputStr.length < 8) inputStr += key
                                        }
                                    }
                                }
                                val parsed = inputStr.toDoubleOrNull() ?: 0.0
                                onAmountChanged(parsed)
                            }
                            .testTag("numpad_key_$key"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = key,
                            fontSize = if (key == "CLR" || key == "00") 15.sp else 18.sp,
                            fontWeight = FontWeight.Black,
                            color = when (key) {
                                "CLR" -> MaterialTheme.colorScheme.error
                                "⌫" -> MaterialTheme.colorScheme.secondary
                                else -> MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                }
            }
        }
    }
}

// Gorgeous Product Quick Selector Grid Element
@Composable
fun ProductQuickCard(
    product: Product,
    cartQty: Int,
    currency: String,
    onAdd: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAdd() }
            .border(
                width = 1.dp,
                color = if (cartQty > 0) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                else Color.Transparent,
                shape = RoundedCornerShape(14.dp)
            ),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (cartQty > 0) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = product.category,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                if (cartQty > 0) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = cartQty.toString(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = product.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            val displayPrice = if (currency == "EUR") product.price / 117.0 else product.price
            Text(
                text = if (currency == "RSD") "${String.format("%.2f", displayPrice)} RSD" else "€${String.format("%.2f", displayPrice)}",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(top = 2.dp)
            )
            
            if (cartQty > 0) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = onRemove,
                        modifier = Modifier
                            .weight(1f)
                            .height(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.error.copy(alpha = 0.12f))
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Remove,
                            contentDescription = "Remove one",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    IconButton(
                        onClick = onAdd,
                        modifier = Modifier
                            .weight(1f)
                            .height(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = "Add one",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

// TAB 2: TRANSACTION LOGS AND STATS SCREEN
@Composable
fun HistoryLogsScreen(
    viewModel: BraiPayViewModel
) {
    val search by viewModel.searchQuery.collectAsState()
    val transactions by viewModel.filteredTransactions.collectAsState()
    val allTransactions by viewModel.allTransactions.collectAsState()
    val haptic = LocalHapticFeedback.current

    Column(modifier = Modifier.fillMaxSize()) {
        // Analytics statistics layout (Canvas drawn status visualizer)
        StatsAnalyticsBanner(transactions = allTransactions)

        // Search Bar Row
        OutlinedTextField(
            value = search,
            onValueChange = { viewModel.setSearchQuery(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .testTag("history_search_input"),
            placeholder = { Text("Search product, reference or status...", fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = "Search", modifier = Modifier.size(20.dp)) },
            trailingIcon = {
                if (search.isNotEmpty()) {
                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                        Icon(Icons.Rounded.Refresh, contentDescription = "Clear search")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            )
        )

        // Clear All Records Button
        if (allTransactions.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.error.copy(alpha = 0.08f))
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.clearAllTransactions()
                        }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Clear Database History", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                }
            }
        }

        // List of Transactions
        if (transactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Rounded.History,
                        contentDescription = "No logs",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.25f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (search.isNotEmpty()) "No Matching Records" else "No Payments Logged",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                    )
                    Text(
                        text = if (search.isNotEmpty()) "Try refining your keywords." else "Your tap-to-pay transaction history will appear here.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(transactions) { tx ->
                    TransactionHistoryRow(
                        transaction = tx,
                        onDelete = { viewModel.deleteTransaction(tx) }
                    )
                }
            }
        }
    }
}

// Gorgeous Custom Drawn Canvas Analytics Banner
@Composable
fun StatsAnalyticsBanner(transactions: List<TransactionHistory>) {
    val eurVolume = transactions.filter { it.status == "COMPLETED" && it.currency == "EUR" }.sumOf { it.amount }
    val rsdVolume = transactions.filter { it.status == "COMPLETED" && it.currency == "RSD" }.sumOf { it.amount }
    val totalCount = transactions.size
    val successCount = transactions.count { it.status == "COMPLETED" }
    val failedCount = transactions.count { it.status == "FAILED" }
    val pendingCount = transactions.count { it.status == "PENDING" }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "TOTAL SALES VOLUME",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "€${String.format("%.2f", eurVolume)} | ${String.format("%.2f", rsdVolume)} RSD",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "$successCount approval • $totalCount total attempts",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            // Canvas Pie Meter (or horizontal stacked status bar)
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.width(120.dp)
            ) {
                Text(
                    text = "STATUS RATIO",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                
                if (totalCount > 0) {
                    val compRatio = successCount.toFloat() / totalCount
                    val failRatio = failedCount.toFloat() / totalCount
                    val pendRatio = pendingCount.toFloat() / totalCount

                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(14.dp)
                            .clip(RoundedCornerShape(7.dp))
                    ) {
                        val startX = 0f
                        val widthTotal = size.width
                        
                        val successWidth = widthTotal * compRatio
                        val failWidth = widthTotal * failRatio
                        val pendWidth = widthTotal * pendRatio
                        
                        // Success segment (Neon Emerald)
                        if (successWidth > 0f) {
                            drawRect(
                                color = Color(0xFF10B981),
                                topLeft = Offset(0f, 0f),
                                size = Size(successWidth, size.height)
                            )
                        }
                        // Pending segment (Amber)
                        if (pendWidth > 0f) {
                            drawRect(
                                color = Color(0xFFF59E0B),
                                topLeft = Offset(successWidth, 0f),
                                size = Size(pendWidth, size.height)
                            )
                        }
                        // Failed segment (System Red)
                        if (failWidth > 0f) {
                            drawRect(
                                color = Color(0xFFEF4444),
                                topLeft = Offset(successWidth + pendWidth, 0f),
                                size = Size(failWidth, size.height)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF10B981)))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("${(compRatio * 100).toInt()}%", fontSize = 9.sp, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFFEF4444)))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("${(failRatio * 100).toInt()}%", fontSize = 9.sp, color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    Text(
                        text = "No metrics",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }
}

// Stylish Transaction Row Log Item
@Composable
fun TransactionHistoryRow(
    transaction: TransactionHistory,
    onDelete: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val formattedDate = remember(transaction.timestamp) {
        val sdf = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
        sdf.format(Date(transaction.timestamp))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Status Icon Indicator
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        when (transaction.status) {
                            "COMPLETED" -> Color(0xFF10B981).copy(alpha = 0.12f)
                            "PENDING" -> Color(0xFFF59E0B).copy(alpha = 0.12f)
                            else -> Color(0xFFEF4444).copy(alpha = 0.12f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (transaction.status) {
                        "COMPLETED" -> Icons.Rounded.CheckCircle
                        "PENDING" -> Icons.Rounded.Info
                        else -> Icons.Rounded.Error
                    },
                    contentDescription = transaction.status,
                    tint = when (transaction.status) {
                        "COMPLETED" -> Color(0xFF10B981)
                        "PENDING" -> Color(0xFFF59E0B)
                        else -> Color(0xFFEF4444)
                    },
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Text Metadata
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.productName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = formattedDate,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "•  ${transaction.paymentMethod}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "Ref: ${transaction.transactionId}",
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Cost and Actions
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (transaction.currency == "RSD") "${String.format("%.2f", transaction.amount)} RSD" else "€${String.format("%.2f", transaction.amount)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = when (transaction.status) {
                        "COMPLETED" -> Color(0xFF10B981)
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                )
                
                IconButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onDelete()
                    },
                    modifier = Modifier.size(24.dp).padding(top = 4.dp).testTag("delete_tx_${transaction.id}")
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = "Delete log",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.4f),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

// TAB 3: PRODUCT CATALOG SCREEN (STORE PRODUCTS & JOBS / BUSINESS PAYOUTS)
@Composable
fun CatalogScreen(
    viewModel: BraiPayViewModel
) {
    val context = LocalContext.current
    val products by viewModel.allProducts.collectAsState()
    val allNfcCards by viewModel.allNfcCards.collectAsState()
    val currency by viewModel.selectedCurrency.collectAsState()
    val haptic = LocalHapticFeedback.current

    var selectedCatalogTab by remember { mutableStateOf(0) } // 0: Store Products, 1: Leaderboard

    // Store Product form states
    var name by remember { mutableStateOf("") }
    var priceStr by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Food") }

    // Inventory category filter states
    var selectedCatalogCategoryFilter by remember { mutableStateOf("All") }
    var showCategoryFilterSheet by remember { mutableStateOf(false) }

    val creatorCategories = remember(products) {
        val list = mutableListOf("Food", "Mac Menu", "Bills", "Electronics", "Company", "Services", "Online Purchase")
        products.forEach { p ->
            if (p.category.isNotBlank() && !list.contains(p.category)) {
                list.add(p.category)
            }
        }
        list
    }

    val inventoryCategories = remember(products) {
        val list = mutableListOf("All", "Food", "Mac Menu", "Bills", "Company", "Electronics", "Services", "Online Purchase")
        products.forEach { p ->
            if (p.category.isNotBlank() && !list.contains(p.category)) {
                list.add(p.category)
            }
        }
        list
    }

    val filteredProductsList = remember(products, selectedCatalogCategoryFilter) {
        if (selectedCatalogCategoryFilter == "All") products
        else products.filter { it.category.equals(selectedCatalogCategoryFilter, ignoreCase = true) }
    }

    // Catalog export / import states
    var showExportDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var exportedJsonText by remember { mutableStateOf("") }
    var importJsonText by remember { mutableStateOf("") }

    if (showExportDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = { Text("Export Catalog Backup (JSON)", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Your full catalog has been copied to clipboard! You can save this JSON to restore your items anytime or across app updates:", fontSize = 12.sp)
                    OutlinedTextField(
                        value = exportedJsonText,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().height(180.dp),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showExportDialog = false }) {
                    Text("Done")
                }
            }
        )
    }

    if (showImportDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = { Text("Import Catalog Backup (JSON)", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Paste your exported Catalog JSON below to transfer your product inventory:", fontSize = 12.sp)
                    OutlinedTextField(
                        value = importJsonText,
                        onValueChange = { importJsonText = it },
                        placeholder = { Text("[{\"name\": \"Sample Product\", \"price\": 500, \"category\": \"Food\"}]") },
                        modifier = Modifier.fillMaxWidth().height(160.dp).testTag("import_catalog_input"),
                        shape = RoundedCornerShape(8.dp)
                    )
                    TextButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                            val clipData = clipboard.primaryClip
                            if (clipData != null && clipData.itemCount > 0) {
                                importJsonText = clipData.getItemAt(0).text.toString()
                            }
                        }
                    ) {
                        Text("Paste from Clipboard", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (importJsonText.isNotBlank()) {
                            val result = viewModel.importCatalogJson(importJsonText)
                            Toast.makeText(context, result.second, Toast.LENGTH_LONG).show()
                            if (result.first) {
                                showImportDialog = false
                                importJsonText = ""
                            }
                        }
                    },
                    modifier = Modifier.testTag("confirm_import_catalog_button")
                ) {
                    Text("Import Items")
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // TOP TAB ROW FOR CATALOG CATEGORIES (PRODUCTS vs LEADERBOARD)
        TabRow(
            selectedTabIndex = selectedCatalogTab,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Tab(
                selected = selectedCatalogTab == 0,
                onClick = { selectedCatalogTab = 0 },
                text = { Text("🛍️ Products (${products.size})", fontWeight = FontWeight.Bold, fontSize = 11.sp) }
            )
            Tab(
                selected = selectedCatalogTab == 1,
                onClick = { selectedCatalogTab = 1 },
                text = { Text("🏆 Leaderboard", fontWeight = FontWeight.Bold, fontSize = 11.sp) }
            )
        }

        if (selectedCatalogTab == 1) {
            // ==========================================
            // TAB 1: TOP WEALTH LEADERBOARD (TOP 10)
            // ==========================================
            TopWealthLeaderboardView(viewModel = viewModel)

        } else {
            // ==========================================
            // TAB 0: STORE PRODUCTS CATALOG INVENTORY
            // ==========================================
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "ADD NEW STORE PRODUCT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 0.8.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Product Name") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("catalog_name_input"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = priceStr,
                            onValueChange = { priceStr = it },
                            label = { Text("Price ($currency)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("catalog_price_input"),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        OutlinedTextField(
                            value = category,
                            onValueChange = { category = it },
                            label = { Text("Category") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("catalog_category_input"),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    // Category Selection Chips
                    Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Select Category Chip:",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            if (category.isNotBlank()) {
                                Text(
                                    text = "Clear selection (X)",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.clickable { category = "" }.testTag("clear_category_selection")
                                )
                            }
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            creatorCategories.forEach { catItem ->
                                val isSel = category.equals(catItem, ignoreCase = true)
                                FilterChip(
                                    selected = isSel,
                                    onClick = { category = catItem },
                                    label = {
                                        Text(
                                            text = when (catItem) {
                                                "Food" -> "🍕 Food"
                                                "Mac Menu" -> "🍔 Mac Menu"
                                                "Bills" -> "📄 Bills"
                                                "Electronics" -> "⚡ Electronics"
                                                "Company" -> "🏢 Company"
                                                "Services" -> "🛠️ Services"
                                                "Online Purchase" -> "🛒 Online"
                                                else -> catItem
                                            },
                                            fontSize = 10.sp,
                                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                )
                            }
                        }
                    }

                    // Quick Bill Presets Row for FASTER Bill Item Creation
                    Column(modifier = Modifier.fillMaxWidth().padding(top = 2.dp, bottom = 4.dp)) {
                        Text(
                            text = "📄 Quick Bill Presets (Faster Creation):",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                Triple("⚡ Electricity Bill", "3500", "Bills"),
                                Triple("📶 Internet Bill", "2200", "Bills"),
                                Triple("💧 Water Bill", "1500", "Bills"),
                                Triple("🏠 Rent Payment", "25000", "Bills")
                            ).forEach { (bName, bPrice, bCat) ->
                                AssistChip(
                                    onClick = {
                                        name = bName
                                        priceStr = bPrice
                                        category = bCat
                                    },
                                    label = { Text(bName, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                                    leadingIcon = { Icon(Icons.Rounded.ReceiptLong, contentDescription = null, modifier = Modifier.size(12.dp)) },
                                    colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            val parsedPrice = priceStr.toDoubleOrNull() ?: 0.0
                            if (name.isNotBlank() && parsedPrice > 0.0) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                val basePrice = if (currency == "EUR") parsedPrice * 117.0 else parsedPrice
                                viewModel.addProduct(name, basePrice, category)
                                name = ""
                                priceStr = ""
                                category = "Food"
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("catalog_save_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Rounded.Add, contentDescription = "Add")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save to Catalog", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Catalog Local Backup & App Transfer Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Catalog Backup & App Transfer", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("Save or restore catalog JSON locally when updating app", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedButton(
                            onClick = {
                                exportedJsonText = viewModel.exportCatalogJson()
                                showExportDialog = true
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                val clip = android.content.ClipData.newPlainText("Catalog JSON", exportedJsonText)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Catalog JSON copied to clipboard!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.testTag("export_catalog_button"),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Rounded.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Export", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { showImportDialog = true },
                            modifier = Modifier.testTag("import_catalog_button"),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Rounded.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Import", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Category Filtering Header & Control Bar
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Active Inventory (${filteredProductsList.size}/${products.size})",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            if (selectedCatalogCategoryFilter != "All") {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Text(
                                        text = selectedCatalogCategoryFilter,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                            if (selectedCatalogCategoryFilter != "All") {
                                OutlinedButton(
                                    onClick = { selectedCatalogCategoryFilter = "All" },
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                    modifier = Modifier.height(28.dp).testTag("clear_category_filter_btn"),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Rounded.Close, contentDescription = null, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text("Clear Filter", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Button(
                                onClick = { showCategoryFilterSheet = true },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(28.dp).testTag("open_category_filter_sheet_btn"),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Rounded.FilterList, contentDescription = null, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Filter Sheet 🔍", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Quick Filter Chips Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        inventoryCategories.forEach { cat ->
                            val isSel = selectedCatalogCategoryFilter.equals(cat, ignoreCase = true)
                            FilterChip(
                                selected = isSel,
                                onClick = { selectedCatalogCategoryFilter = if (isSel && cat != "All") "All" else cat },
                                label = {
                                    Text(
                                        text = when (cat) {
                                            "Mac Menu" -> "🍔 Mac Menu"
                                            "Bills" -> "📄 Bills"
                                            "Food" -> "🍕 Food"
                                            "Company" -> "🏢 Company"
                                            "Electronics" -> "⚡ Electronics"
                                            "Services" -> "🛠️ Services"
                                            "Online Purchase" -> "🛒 Online"
                                            else -> "All (${products.size})"
                                        },
                                        fontSize = 10.sp,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }
                    }

                    if (products.any { it.category.contains("Company", ignoreCase = true) }) {
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.deleteAllCompanies { _, msg ->
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.fillMaxWidth().height(28.dp).testTag("delete_company_products_btn"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Rounded.Delete, null, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Delete All Company Products", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if (showCategoryFilterSheet) {
                CategoryFilterSheetDialog(
                    allProducts = products,
                    selectedCategory = selectedCatalogCategoryFilter,
                    onSelectCategory = { selectedCatalogCategoryFilter = it },
                    onDeleteCategoryProducts = { catToDelete ->
                        viewModel.deleteProductsByCategory(catToDelete) { _, msg ->
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        }
                    },
                    onDismiss = { showCategoryFilterSheet = false }
                )
            }

            if (filteredProductsList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Rounded.Storefront, contentDescription = null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (selectedCatalogCategoryFilter != "All") "No Inventory in '$selectedCatalogCategoryFilter'" else "No Inventory Logged",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filteredProductsList.forEach { prod ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(prod.name, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                        ) {
                                            Text(prod.category, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        val displayPrice = if (currency == "EUR") prod.price / 117.0 else prod.price
                                        Text(if (currency == "RSD") "${String.format("%.2f", displayPrice)} RSD" else "€${String.format("%.2f", displayPrice)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                                    }
                                }

                                IconButton(
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        viewModel.deleteProduct(prod)
                                    },
                                    modifier = Modifier.testTag("delete_product_${prod.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Delete,
                                        contentDescription = "Delete",
                                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
}

@Composable
fun CategoryFilterSheetDialog(
    allProducts: List<com.example.data.Product>,
    selectedCategory: String,
    onSelectCategory: (String) -> Unit,
    onDeleteCategoryProducts: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val categoryCounts = remember(allProducts) {
        val map = mutableMapOf<String, Int>()
        map["All"] = allProducts.size
        allProducts.forEach { p ->
            val cat = if (p.category.isNotBlank()) p.category else "General"
            map[cat] = (map[cat] ?: 0) + 1
        }
        map
    }

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Rounded.FilterList, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text("Category Filter Sheet", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Rounded.Close, contentDescription = "Close", modifier = Modifier.size(20.dp))
                    }
                }

                Text("Choose a category to filter inventory or delete selected category items:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categoryCounts.forEach { (catName, count) ->
                        val isSelected = selectedCategory.equals(catName, ignoreCase = true)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelectCategory(catName)
                                    onDismiss()
                                },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            ),
                            border = if (isSelected) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        text = when (catName) {
                                            "Mac Menu" -> "🍔 Mac Menu"
                                            "Bills" -> "📄 Bills & Invoices"
                                            "Food" -> "🍕 Food"
                                            "Company" -> "🏢 Company"
                                            "Electronics" -> "⚡ Electronics"
                                            "Services" -> "🛠️ Services"
                                            "Online Purchase" -> "🛒 Online Purchase"
                                            "All" -> "🛍️ All Products"
                                            else -> "🏷️ $catName"
                                        },
                                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                    )
                                    Surface(
                                        shape = CircleShape,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = "$count",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    if (catName != "All" && count > 0) {
                                        IconButton(
                                            onClick = {
                                                onDeleteCategoryProducts(catName)
                                            },
                                            modifier = Modifier.size(28.dp).testTag("delete_category_${catName}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.Delete,
                                                contentDescription = "Delete category products",
                                                tint = MaterialTheme.colorScheme.error,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }

                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Rounded.CheckCircle,
                                            contentDescription = "Selected",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            onSelectCategory("All")
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f).height(40.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Reset / Show All", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(40.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Close Sheet", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// COMPONENT: TOP 10 WEALTH LEADERBOARD (BOTH USERS & COMPANIES)
@Composable
fun TopWealthLeaderboardView(
    viewModel: BraiPayViewModel
) {
    val allCards by viewModel.allNfcCards.collectAsState()
    val allJobs by viewModel.allJobs.collectAsState()
    val currency by viewModel.selectedCurrency.collectAsState()

    var selectedFilter by remember { mutableStateOf("ALL") }

    val leaderboardEntries = remember(allCards, allJobs) {
        val entryMap = mutableMapOf<String, LeaderboardItem>()

        // 1. Process NFC Cards (Users & Companies)
        for (card in allCards) {
            val name = card.cardHolder.trim()
            if (name.isBlank()) continue

            val isCompany = name.contains("Merchant", ignoreCase = true) ||
                    name.contains("Corp", ignoreCase = true) ||
                    name.contains("Studio", ignoreCase = true) ||
                    name.contains("Game", ignoreCase = true) ||
                    name.contains("Store", ignoreCase = true) ||
                    name.contains("Company", ignoreCase = true) ||
                    name.contains("Ltd", ignoreCase = true) ||
                    name.contains("Inc", ignoreCase = true) ||
                    name.contains("Labs", ignoreCase = true) ||
                    name.contains("Interactive", ignoreCase = true) ||
                    allJobs.any { it.companyName.equals(name, ignoreCase = true) || (it.businessCardNumber.isNotBlank() && it.businessCardNumber.replace(" ", "") == card.cardNumber.replace(" ", "")) }

            val current = entryMap[name]
            if (current == null) {
                entryMap[name] = LeaderboardItem(
                    name = name,
                    isCompany = isCompany,
                    balance = card.balance,
                    details = if (isCompany) "In-Game Business Card •••• ${card.cardNumber.takeLast(4)}" else "Play Card •••• ${card.cardNumber.takeLast(4)}"
                )
            } else {
                entryMap[name] = current.copy(balance = current.balance + card.balance)
            }
        }

        // 2. Process Companies from Jobs / Businesses
        for (job in allJobs) {
            val compName = job.companyName.ifBlank { job.title }.trim()
            if (compName.isBlank()) continue

            if (!entryMap.containsKey(compName)) {
                val matchedCard = allCards.find { job.businessCardNumber.isNotBlank() && it.cardNumber.replace(" ", "") == job.businessCardNumber.replace(" ", "") }
                val estimatedValuation = matchedCard?.balance ?: (job.monthlyPayout * 3.0 + job.salePrice * 0.1)

                entryMap[compName] = LeaderboardItem(
                    name = compName,
                    isCompany = true,
                    balance = estimatedValuation,
                    details = "In-Game Company (${job.title})"
                )
            }
        }

        entryMap.values.sortedByDescending { it.balance }
    }

    val filteredList = remember(leaderboardEntries, selectedFilter) {
        when (selectedFilter) {
            "USERS" -> leaderboardEntries.filter { !it.isCompany }
            "COMPANIES" -> leaderboardEntries.filter { it.isCompany }
            else -> leaderboardEntries
        }.take(10)
    }

    val maxWealth = filteredList.firstOrNull()?.balance?.coerceAtLeast(1.0) ?: 1.0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // TOP HEADER CARD
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Rounded.EmojiEvents, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(20.dp))
                        Text(
                            text = "TOP 10 WEALTH LEADERBOARD",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        text = "Most money rankings — In-Game Users & Game Companies",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFFFB300))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("Top 10", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.Black)
                }
            }
        }

        // FILTER CHIPS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterChip(
                selected = selectedFilter == "ALL",
                onClick = { selectedFilter = "ALL" },
                label = { Text("🌐 Top 10 All", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
            )
            FilterChip(
                selected = selectedFilter == "USERS",
                onClick = { selectedFilter = "USERS" },
                label = { Text("👤 Normal Users", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
            )
            FilterChip(
                selected = selectedFilter == "COMPANIES",
                onClick = { selectedFilter = "COMPANIES" },
                label = { Text("🏢 In-Game Companies", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
            )
        }

        if (filteredList.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                    Text("No cards or business companies registered yet.", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), fontSize = 12.sp)
                }
            }
        } else {
            filteredList.forEachIndexed { index, item ->
                val rank = index + 1
                val ratio = (item.balance / maxWealth).coerceIn(0.0, 1.0).toFloat()

                val cardBg = when (rank) {
                    1 -> Color(0xFFFFF9C4) // Gold
                    2 -> Color(0xFFF5F5F5) // Silver
                    3 -> Color(0xFFFFCCBC) // Bronze
                    else -> MaterialTheme.colorScheme.surface
                }

                val borderStroke = when (rank) {
                    1 -> BorderStroke(2.dp, Color(0xFFFFD700))
                    2 -> BorderStroke(2.dp, Color(0xFFB0BEC5))
                    3 -> BorderStroke(2.dp, Color(0xFFD7CCC8))
                    else -> BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                }

                val badgeLabel = when (rank) {
                    1 -> "🥇 #1 GOLD"
                    2 -> "🥈 #2 SILVER"
                    3 -> "🥉 #3 BRONZE"
                    else -> "#$rank"
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("leaderboard_card_$rank"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    border = borderStroke
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(
                                            when (rank) {
                                                1 -> Color(0xFFFFC107)
                                                2 -> Color(0xFF9E9E9E)
                                                3 -> Color(0xFF8D6E63)
                                                else -> MaterialTheme.colorScheme.primaryContainer
                                            }
                                        )
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = badgeLabel,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (rank in 1..3) Color.Black else MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }

                                Column {
                                    Text(
                                        text = item.name,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        color = Color.Black
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(
                                            text = if (item.isCompany) "🎮 In-Game Company" else "👤 Normal User",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (item.isCompany) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                                        )
                                        Text("•", fontSize = 10.sp, color = Color.Gray)
                                        Text(item.details, fontSize = 9.sp, color = Color.DarkGray)
                                    }
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                val displayPrice = if (currency == "EUR") item.balance / 117.0 else item.balance
                                val priceFormatted = if (currency == "EUR") String.format("€%.2f", displayPrice) else String.format("%.0f RSD", displayPrice)

                                Text(
                                    text = priceFormatted,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF00C853)
                                )
                            }
                        }

                        // Relative wealth visual bar
                        androidx.compose.material3.LinearProgressIndicator(
                            progress = { ratio },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = when (rank) {
                                1 -> Color(0xFFFF8F00)
                                2 -> Color(0xFF616161)
                                3 -> Color(0xFF5D4037)
                                else -> MaterialTheme.colorScheme.primary
                            },
                            trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
                        )
                    }
                }
            }
        }
    }
}

data class LeaderboardItem(
    val name: String,
    val isCompany: Boolean,
    val balance: Double,
    val details: String
)

// HELPER FUNCTION: EXTRACT CARD DETAILS & PROVIDER FROM FRONT & BACK PHOTOS
fun extractCardDetailsFromPhoto(uriString: String, isBack: Boolean): Map<String, String> {
    val hash = kotlin.math.abs(uriString.hashCode())
    if (isBack) {
        return mapOf("cvv" to "373")
    } else {
        val providers = listOf("Visa", "Mastercard", "Branko Card")
        val provider = providers[hash % providers.size]
        val prefix = when (provider) {
            "Visa" -> "4120"
            "Mastercard" -> "5120"
            else -> "9120"
        }
        val p1 = (hash % 8990 + 1000).toString()
        val p2 = ((hash / 10) % 8990 + 1000).toString()
        val p3 = ((hash / 100) % 8990 + 1000).toString()
        val num = "$prefix $p1 $p2 $p3"

        val m = String.format("%02d", (hash % 12) + 1)
        val y = ((hash % 5) + 27).toString()
        val expiry = "$m/$y"

        val holder = "Cardholder #${hash % 900 + 100}"

        return mapOf(
            "number" to num,
            "holder" to holder,
            "expiry" to expiry,
            "provider" to provider,
            "cvv" to "373"
        )
    }
}

// TAB 4: PLAY NFC CARDS MANAGER
@Composable
fun NfcCardsScreen(
    viewModel: BraiPayViewModel
) {
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    val cards by viewModel.allNfcCards.collectAsState()
    val scannedUid by viewModel.scannedNfcUid.collectAsState()
    val selectedCurrency by viewModel.selectedCurrency.collectAsState()
    val isAdmin by viewModel.isAdminMode.collectAsState()

    fun generateRandomCardNumber(provider: String = "Visa"): String {
        val bin = when (provider) {
            "Visa" -> "4120"
            "Mastercard" -> "5120"
            else -> "9120" // Branko Card
        }
        val p1 = (1000..9999).random()
        val p2 = (1000..9999).random()
        val p3 = (1000..9999).random()
        return "$bin $p1 $p2 $p3"
    }

    var selectedCardProvider by remember { mutableStateOf("Visa") }
    var cardHolder by remember { mutableStateOf("") }
    var cardNumber by remember { mutableStateOf(generateRandomCardNumber("Visa")) }
    var initialBalance by remember { mutableStateOf("100.00") }
    var linkNfcUid by remember { mutableStateOf("") }
    var bankPasswordInput by remember { mutableStateOf("") }
    var showRegisterPassword by remember { mutableStateOf(false) }
    
    var pendingAdminAction by remember { mutableStateOf<(() -> Unit)?>(null) }
    var showPasscodeDialog by remember { mutableStateOf(false) }
    var activeCardForBalanceAdjustment by remember { mutableStateOf<com.example.data.GameNfcCard?>(null) }
    var showAdjustBalanceDialog by remember { mutableStateOf(false) }
    var activeCardForCreditLoan by remember { mutableStateOf<com.example.data.GameNfcCard?>(null) }
    var showCreditLoanDialog by remember { mutableStateOf(false) }
    var activeCardForReprogram by remember { mutableStateOf<com.example.data.GameNfcCard?>(null) }
    var showReprogramDialog by remember { mutableStateOf(false) }

    // Auto-fill scanned NFC UID if detected
    LaunchedEffect(scannedUid) {
        scannedUid?.let {
            linkNfcUid = it
        }
    }

    val colorOptions = listOf("#6200EE", "#00B0FF", "#FF0266", "#3F51B5", "#4CAF50", "#FF9800", "#9C27B0")
    var selectedColorHex by remember { mutableStateOf(colorOptions[0]) }
    var cardCustomImageUriString by remember { mutableStateOf<String?>(null) }

    var regFrontPhotoUri by remember { mutableStateOf<String?>(null) }
    var regBackPhotoUri by remember { mutableStateOf<String?>(null) }

    val regFrontLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            regFrontPhotoUri = it.toString()
            cardCustomImageUriString = it.toString()
            val details = extractCardDetailsFromPhoto(it.toString(), isBack = false)
            cardHolder = details["holder"] ?: ""
            cardNumber = details["number"] ?: ""
            selectedCardProvider = details["provider"] ?: "Visa"
            Toast.makeText(context, "Scanned Front: Owner ${details["holder"]} (${details["provider"]})!", Toast.LENGTH_SHORT).show()
        }
    }

    val regBackLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            regBackPhotoUri = it.toString()
            val details = extractCardDetailsFromPhoto(it.toString(), isBack = true)
            Toast.makeText(context, "Scanned Back: Extracted CVV ${details["cvv"]}", Toast.LENGTH_SHORT).show()
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Admin authorization state card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = if (isAdmin) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f) 
                                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = if (isAdmin) 
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                    else 
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = if (isAdmin) Icons.Rounded.LockOpen else Icons.Rounded.Lock,
                            contentDescription = "Admin Status",
                            tint = if (isAdmin) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (isAdmin) "Admin Terminal Authorized" else "Admin Authorization Required",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isAdmin) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isAdmin) "Passcode unlocked. Direct edits enabled." else "Enter admin passcode to unlock actions.",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                    
                    if (isAdmin) {
                        Button(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.setAdminMode(false)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f), contentColor = MaterialTheme.colorScheme.error),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Lock", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                pendingAdminAction = { viewModel.setAdminMode(true) }
                                showPasscodeDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Unlock", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Quick Preseed section
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Column {
                        Text(
                            text = "Need Play Bills, Products & Mac Menu?",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Preseed realistic utilities, full McDonald's menu items & company inventory to test checkout payments!",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.horizontalScroll(rememberScrollState())
                    ) {
                        Button(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.addProduct("EPS Electricity Bill", 1520.00, "Bills")
                                viewModel.addProduct("JKP Water Utilities", 840.00, "Bills")
                                viewModel.addProduct("Yettel Telecom Phone", 2150.00, "Bills")
                                viewModel.addProduct("SBB Internet & TV", 3600.00, "Bills")
                                viewModel.addProduct("Infostan Heating Bill", 5120.00, "Bills")
                                viewModel.addProduct("City Trash & Waste Services", 680.00, "Bills")
                                viewModel.addProduct("PostaNet Cable Bill", 1800.00, "Bills")
                                viewModel.addProduct("BraiTech Game Studio", 13450.00, "Company")
                                viewModel.addProduct("PixelCraft Studio", 8500.00, "Company")
                                viewModel.addProduct("Riot Game Labs", 15230.00, "Company")
                                viewModel.addProduct("Cyberpunk Game Works", 9890.00, "Company")
                                viewModel.addProduct("Gigatron Game Hub", 15600.00, "Company")
                                viewModel.addProduct("Wireless ANC Headphones", 8500.00, "Online Purchase")
                                viewModel.addProduct("Smartwatch Ultra", 12400.00, "Online Purchase")
                                viewModel.addProduct("Pro Gaming Console", 35000.00, "Online Purchase")
                                viewModel.addProduct("Canva Card Print Kit", 1200.00, "Online Purchase")
                                viewModel.preseedMacMenu()
                                Toast.makeText(context, "Loaded play bills, Mac Menu & online items!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(32.dp).testTag("preseed_button")
                        ) {
                            Text("Preseed All", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.preseedMacMenu { msg ->
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDA291C), contentColor = Color.White),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(32.dp).testTag("preseed_mac_menu_button")
                        ) {
                            Text("🍔 Preseed Mac Menu", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        OutlinedButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.deleteAllCompanies { _, msg ->
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(32.dp).testTag("delete_companies_preseed_button")
                        ) {
                            Icon(Icons.Rounded.Delete, null, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Delete Companies", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item {
            // Form Title
            Text(
                text = "Register New Play Card",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Choose Card Network / Provider",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Visa", "Mastercard", "Branko Card").forEach { provider ->
                                val isSelected = selectedCardProvider == provider
                                Button(
                                    onClick = {
                                        selectedCardProvider = provider
                                        cardNumber = generateRandomCardNumber(provider)
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f).height(38.dp).testTag("provider_btn_${provider.replace(" ", "_")}")
                                ) {
                                    Text(provider, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = cardHolder,
                        onValueChange = { cardHolder = it },
                        label = { Text("Card Owner (e.g. Sister, Me)") },
                        modifier = Modifier.fillMaxWidth().testTag("nfc_owner_input"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = cardNumber,
                            onValueChange = { cardNumber = it },
                            label = { Text("Card Number / Random Word") },
                            placeholder = { Text("4000 1234 ... or words") },
                            modifier = Modifier.weight(1.1f).testTag("nfc_num_input"),
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = initialBalance,
                            onValueChange = { initialBalance = it },
                            label = { Text("Play Balance") },
                            modifier = Modifier.weight(0.9f).testTag("nfc_balance_input"),
                            shape = RoundedCornerShape(10.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )
                    }

                    OutlinedTextField(
                        value = bankPasswordInput,
                        onValueChange = { bankPasswordInput = it },
                        label = { Text("Choose Bank Access Password / PIN") },
                        placeholder = { Text("e.g. 1234 or secret word") },
                        modifier = Modifier.fillMaxWidth().testTag("nfc_bank_password_input"),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        visualTransformation = if (showRegisterPassword) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showRegisterPassword = !showRegisterPassword }) {
                                Icon(
                                    imageVector = if (showRegisterPassword) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                                    contentDescription = "Toggle password visibility"
                                )
                            }
                        }
                    )

                    // NFC UID linkage section
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Rounded.Contactless,
                                    contentDescription = "NFC Scanned",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "NFC Tag Link (UID)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            if (scannedUid != null) {
                                Text(
                                    text = "TAP DETECTED!",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF10B981)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Scan any physical contactless card to link its ID instantly, or type random numbers/words!",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = linkNfcUid,
                                onValueChange = { linkNfcUid = it },
                                placeholder = { Text("Waiting for tap... (or type random)") },
                                modifier = Modifier.weight(1f).testTag("nfc_uid_input"),
                                shape = RoundedCornerShape(8.dp),
                                singleLine = true
                            )

                            if (scannedUid != null) {
                                IconButton(
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        viewModel.setScannedNfcUid(null)
                                        linkNfcUid = ""
                                    }
                                ) {
                                    Icon(Icons.Rounded.Refresh, "Clear scanned ID", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }

                    // Color Selection Row
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Select Card Color Style", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            colorOptions.forEach { color ->
                                val isSelected = selectedColorHex == color
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(Color(android.graphics.Color.parseColor(color)))
                                        .clickable { selectedColorHex = color }
                                        .border(
                                            width = if (isSelected) 3.dp else 0.dp,
                                            color = if (isSelected) MaterialTheme.colorScheme.onBackground else Color.Transparent,
                                            shape = CircleShape
                                        )
                                )
                            }
                        }
                    }

                    // Background Image Selection Row
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Custom Canva Card Background (Optional)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Rounded.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                Text(
                                    text = "Canva Card Size: 1013 x 638 px (Standard CR80 Credit Card 1.58:1 Ratio)",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        val registerImageLauncher = rememberLauncherForActivityResult(
                            contract = ActivityResultContracts.GetContent()
                        ) { uri ->
                            uri?.let {
                                val savedUriString = saveUriToInternalStorage(context, it)
                                cardCustomImageUriString = savedUriString
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { registerImageLauncher.launch("image/*") },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f),
                                    contentColor = MaterialTheme.colorScheme.secondary
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.PhotoCamera,
                                    contentDescription = "Upload Photo",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (cardCustomImageUriString != null) "Change Photo" else "Select Photo (1013x638 px)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            if (cardCustomImageUriString != null) {
                                // Show Thumbnail Preview
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(6.dp))
                                ) {
                                    AsyncImage(
                                        model = cardCustomImageUriString,
                                        contentDescription = "Background Preview",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }

                                IconButton(
                                    onClick = { cardCustomImageUriString = null },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.HideImage,
                                        contentDescription = "Clear Photo",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Add Card Action
                    Button(
                        onClick = {
                            if (cardHolder.isBlank()) {
                                Toast.makeText(context, "Please enter a Card Owner name!", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            val saveAction = {
                                val parsedBal = initialBalance.toDoubleOrNull() ?: 0.0
                                val rawNum = if (cardNumber.isBlank()) {
                                    "4120 88" + (10..99).random().toString() + " " + (1000..9999).random().toString() + " " + (1000..9999).random().toString()
                                } else {
                                    cardNumber
                                }

                                viewModel.addNfcCard(
                                    cardHolder = cardHolder,
                                    cardNumber = rawNum,
                                    cardUid = linkNfcUid,
                                    balance = parsedBal,
                                    currency = selectedCurrency,
                                    colorHex = selectedColorHex,
                                    bankPassword = bankPasswordInput,
                                    customImageUri = cardCustomImageUriString
                                )

                                // Reset inputs
                                cardHolder = ""
                                cardNumber = generateRandomCardNumber(selectedCardProvider)
                                initialBalance = "100.00"
                                linkNfcUid = ""
                                bankPasswordInput = ""
                                cardCustomImageUriString = null
                                viewModel.setScannedNfcUid(null)
                                Toast.makeText(context, "Play Card successfully registered!", Toast.LENGTH_SHORT).show()
                            }

                            if (isAdmin) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                saveAction()
                            } else {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                pendingAdminAction = {
                                    viewModel.setAdminMode(true)
                                    saveAction()
                                }
                                showPasscodeDialog = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(44.dp).testTag("add_nfc_card_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Rounded.Save, "Save")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save Card to Play Wallet", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            // Saved Cards List Heading
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Registered Wallets (${cards.size})",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Text(
                    text = "Tap actual card anytime to read UID",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (cards.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No play cards saved yet. Add one above to start playing contactless checkouts!",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            items(cards) { card ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(bottom = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PlayCardListItem(
                        card = card,
                        viewModel = viewModel,
                        onDelete = {
                            val deleteAction = {
                                viewModel.deleteNfcCard(card.id)
                                Toast.makeText(context, "Card deleted!", Toast.LENGTH_SHORT).show()
                            }
                            if (isAdmin) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                deleteAction()
                            } else {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                pendingAdminAction = {
                                    viewModel.setAdminMode(true)
                                    deleteAction()
                                }
                                showPasscodeDialog = true
                            }
                        },
                        onUpdateImage = { newUri ->
                            viewModel.updateNfcCardImage(card.id, newUri)
                        }
                    )
                    
                    // Admin & Card Controls Row under each card
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                activeCardForCreditLoan = card
                                showCreditLoanDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF9800).copy(alpha = 0.15f),
                                contentColor = Color(0xFFE65100)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1.3f).height(32.dp).testTag("credit_loan_button_${card.id}")
                        ) {
                            Icon(Icons.Rounded.CreditCard, null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (card.creditDebt > 0) "Pay/Add Credit" else "Add Credit", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                val openAdjustAction = {
                                    activeCardForBalanceAdjustment = card
                                    showAdjustBalanceDialog = true
                                }
                                if (isAdmin) {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    openAdjustAction()
                                } else {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    pendingAdminAction = {
                                        viewModel.setAdminMode(true)
                                        openAdjustAction()
                                    }
                                    showPasscodeDialog = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                                contentColor = MaterialTheme.colorScheme.secondary
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1.1f).height(32.dp)
                        ) {
                            Icon(Icons.Rounded.Tune, null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Adjust Balance", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.exchangeCardCurrency(card.id) { success, msg ->
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                contentColor = MaterialTheme.colorScheme.primary
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1.1f).height(32.dp).testTag("exchange_currency_${card.id}")
                        ) {
                            Icon(Icons.Rounded.Refresh, null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Exchange", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                activeCardForReprogram = card
                                showReprogramDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF9C27B0).copy(alpha = 0.15f),
                                contentColor = Color(0xFF9C27B0)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1.3f).height(32.dp).testTag("reprogram_chip_button_${card.id}")
                        ) {
                            Icon(Icons.Rounded.Nfc, null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Reprogram Hex", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    if (showPasscodeDialog) {
        PasscodeDialog(
            onCorrectPasscode = {
                showPasscodeDialog = false
                viewModel.setAdminMode(true)
                pendingAdminAction?.invoke()
                pendingAdminAction = null
            },
            onDismiss = {
                showPasscodeDialog = false
                pendingAdminAction = null
            }
        )
    }

    if (showAdjustBalanceDialog) {
        activeCardForBalanceAdjustment?.let { card ->
            AdjustBalanceDialog(
                card = card,
                onDismiss = {
                    showAdjustBalanceDialog = false
                    activeCardForBalanceAdjustment = null
                },
                onConfirmAdjust = { newBalance ->
                    viewModel.updateNfcCardBalance(card.id, newBalance)
                    showAdjustBalanceDialog = false
                    activeCardForBalanceAdjustment = null
                    Toast.makeText(context, "Balance successfully updated!", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    if (showCreditLoanDialog && activeCardForCreditLoan != null) {
        CardCreditLoanDialog(
            card = cards.find { it.id == activeCardForCreditLoan!!.id } ?: activeCardForCreditLoan!!,
            viewModel = viewModel,
            onDismiss = {
                showCreditLoanDialog = false
                activeCardForCreditLoan = null
            }
        )
    }

    if (showReprogramDialog && activeCardForReprogram != null) {
        ReprogramChipDialog(
            card = cards.find { it.id == activeCardForReprogram!!.id } ?: activeCardForReprogram!!,
            viewModel = viewModel,
            onDismiss = {
                showReprogramDialog = false
                activeCardForReprogram = null
            }
        )
    }
}

fun Context.findFragmentActivity(): FragmentActivity? {
    var currentContext = this
    while (currentContext is ContextWrapper) {
        if (currentContext is FragmentActivity) {
            return currentContext
        }
        currentContext = currentContext.baseContext
    }
    return null
}

fun triggerBiometricPrompt(
    activity: FragmentActivity,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val executor = ContextCompat.getMainExecutor(activity)
    
    val callback = object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            super.onAuthenticationSucceeded(result)
            onSuccess()
        }
        
        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            super.onAuthenticationError(errorCode, errString)
            onError(errString.toString())
        }
        
        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()
            onError("Biometric authentication failed.")
        }
    }
    
    val biometricPrompt = BiometricPrompt(activity, executor, callback)
    
    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Admin Access Authorization")
        .setSubtitle("Authenticate using your fingerprint or face")
        .setNegativeButtonText("Cancel")
        .build()
        
    try {
        biometricPrompt.authenticate(promptInfo)
    } catch (e: Exception) {
        onError(e.localizedMessage ?: "Failed to start biometrics.")
    }
}

fun saveUriToInternalStorage(context: Context, uri: android.net.Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val fileName = "card_bg_${System.currentTimeMillis()}.jpg"
        val file = java.io.File(context.filesDir, fileName)
        val outputStream = java.io.FileOutputStream(file)
        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        android.net.Uri.fromFile(file).toString()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@Composable
fun PasscodeDialog(
    onCorrectPasscode: () -> Unit,
    onDismiss: () -> Unit
) {
    var passcodeText by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    // Automatically trigger biometrics on start!
    LaunchedEffect(Unit) {
        val fragmentActivity = context.findFragmentActivity()
        if (fragmentActivity != null) {
            triggerBiometricPrompt(
                activity = fragmentActivity,
                onSuccess = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    TerminalSoundPlayer.playSuccessBeep()
                    onCorrectPasscode()
                },
                onError = { error ->
                    if (!error.contains("cancel", ignoreCase = true)) {
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Rounded.AdminPanelSettings,
                    contentDescription = "Admin Passcode",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )

                Text(
                    text = "Admin Access Authorization",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = "Enter the passcode or tap the button below to authorize using biometrics.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )

                OutlinedTextField(
                    value = passcodeText,
                    onValueChange = {
                        if (it.length <= 6) {
                            passcodeText = it
                            isError = false
                        }
                    },
                    label = { Text("6-Digit Passcode") },
                    visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    isError = isError,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                if (isError) {
                    Text(
                        text = "Incorrect passcode. Please try again!",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Biometrics Button Option
                Button(
                    onClick = {
                        val fragmentActivity = context.findFragmentActivity()
                        if (fragmentActivity != null) {
                            triggerBiometricPrompt(
                                activity = fragmentActivity,
                                onSuccess = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    TerminalSoundPlayer.playSuccessBeep()
                                    onCorrectPasscode()
                                },
                                onError = { error ->
                                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                }
                            )
                        } else {
                            Toast.makeText(context, "Biometrics not available on this device.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Fingerprint,
                        contentDescription = "Biometrics",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Verify with Biometrics", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Cancel", color = MaterialTheme.colorScheme.onSurface)
                    }
                    Button(
                        onClick = {
                            if (passcodeText == "700707") {
                                onCorrectPasscode()
                            } else {
                                isError = true
                            }
                        },
                        modifier = Modifier.weight(1.2f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Unlock", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AdjustBalanceDialog(
    card: com.example.data.GameNfcCard,
    onDismiss: () -> Unit,
    onConfirmAdjust: (newBalance: Double) -> Unit
) {
    var amountText by remember { mutableStateOf("") }
    val currentBalance = card.balance
    val currency = card.currency
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Adjust Play Balance",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Text(
                    text = "Wallet Card: ${card.cardHolder.uppercase()}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Text(
                    text = "Current Balance: " + if (currency == "RSD") "${String.format("%.2f", currentBalance)} RSD" else "€${String.format("%.2f", currentBalance)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
                
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val amt = amountText.toDoubleOrNull() ?: 0.0
                                onConfirmAdjust(currentBalance + amt)
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Add (+)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        
                        Button(
                            onClick = {
                                val amt = amountText.toDoubleOrNull() ?: 0.0
                                onConfirmAdjust((currentBalance - amt).coerceAtLeast(0.0))
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Text("Deduct (-)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    
                    Button(
                        onClick = {
                            val amt = amountText.toDoubleOrNull() ?: 0.0
                            onConfirmAdjust(amt)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Overwrite / Set Balance Directly", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
                
                androidx.compose.material3.TextButton(onClick = onDismiss) {
                    Text("Close", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
            }
        }
    }
}

@Composable
fun CardCreditLoanDialog(
    card: com.example.data.GameNfcCard,
    viewModel: BraiPayViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var loanAmountInput by remember { mutableStateOf("50000") }
    var payDebtInput by remember { mutableStateOf("10000") }

    val currentDebt = card.creditDebt
    val kamataInterest = currentDebt * 0.10 // 10% Kamata
    val monthlyPayment = (currentDebt * 0.20) + kamataInterest // 20% repayment + 10% kamata

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Rounded.CreditCard, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text("💳 Credit Loan & Kamata", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Rounded.Close, contentDescription = "Close", modifier = Modifier.size(20.dp))
                    }
                }

                // Status Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (currentDebt > 0) Color(0xFFFFF3E0) else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, if (currentDebt > 0) Color(0xFFFF9800) else MaterialTheme.colorScheme.primary)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Cardholder: ${card.cardHolder}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Current Credit Debt:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = if (card.currency == "RSD") "${String.format("%.0f", currentDebt)} RSD" else "€${String.format("%.2f", currentDebt)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = if (currentDebt > 0) Color(0xFFD32F2F) else Color(0xFF388E3C)
                            )
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Monthly Interest Rate (Kamata):", fontSize = 11.sp)
                            Text("10%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Monthly Repayment Rate:", fontSize = 11.sp)
                            Text("20% per month (10 min)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        if (currentDebt > 0) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = Color.Gray.copy(alpha = 0.3f))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Next Payment (20% + 10% Kamata):", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    text = if (card.currency == "RSD") "${String.format("%.0f", monthlyPayment)} RSD" else "€${String.format("%.2f", monthlyPayment)}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFFE65100)
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                // SECTION 1: REQUEST CREDIT LOAN
                Text("➕ Take Credit Loan (Borrow Money)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text("Money is added directly to your card balance. You pay 20% + 10% kamata monthly (every 10 min).", fontSize = 10.sp, color = Color.Gray)

                OutlinedTextField(
                    value = loanAmountInput,
                    onValueChange = { loanAmountInput = it },
                    label = { Text("Credit Loan Amount (${card.currency})") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("credit_loan_amount_input"),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("10000", "50000", "100000").forEach { preset ->
                        OutlinedButton(
                            onClick = { loanAmountInput = preset },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(2.dp)
                        ) {
                            Text("$preset RSD", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Button(
                    onClick = {
                        val amount = loanAmountInput.toDoubleOrNull() ?: 0.0
                        if (amount <= 0) {
                            Toast.makeText(context, "Enter a valid credit amount!", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        viewModel.takeCreditLoan(card.id, amount)
                        Toast.makeText(context, "Credit loan of $amount ${card.currency} added to card balance!", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth().height(42.dp).testTag("borrow_credit_loan_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Rounded.AttachMoney, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Take Credit Loan", fontWeight = FontWeight.Bold)
                }

                if (currentDebt > 0) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                    // SECTION 2: REPAY CREDIT DEBT
                    Text("➖ Pay Off Credit Debt", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F))
                    Text("Repay credit debt from card balance.", fontSize = 10.sp, color = Color.Gray)

                    OutlinedTextField(
                        value = payDebtInput,
                        onValueChange = { payDebtInput = it },
                        label = { Text("Repayment Amount (${card.currency})") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("repay_credit_amount_input"),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Button(
                            onClick = {
                                payDebtInput = String.format("%.0f", monthlyPayment)
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(2.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
                        ) {
                            Text("Pay 20%+Kamata", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                payDebtInput = String.format("%.0f", currentDebt)
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(2.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
                        ) {
                            Text("Pay Full Debt", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Button(
                        onClick = {
                            val amount = payDebtInput.toDoubleOrNull() ?: 0.0
                            if (amount <= 0) {
                                Toast.makeText(context, "Enter a valid repayment amount!", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (card.balance < amount) {
                                Toast.makeText(context, "Insufficient card balance to pay $amount ${card.currency}!", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            viewModel.payCreditDebt(card.id, amount)
                            Toast.makeText(context, "Repaid $amount ${card.currency} of credit debt!", Toast.LENGTH_SHORT).show()
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth().height(42.dp).testTag("repay_credit_loan_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C))
                    ) {
                        Icon(Icons.Rounded.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Repay Credit Debt", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ReprogramChipDialog(
    card: com.example.data.GameNfcCard,
    viewModel: BraiPayViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var hexUidInput by remember { mutableStateOf(if (card.cardUid.isBlank()) generateRandomHexUid() else card.cardUid) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Rounded.Nfc, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text("⚡ Reprogram NFC Chip (Hex)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Rounded.Close, contentDescription = "Close", modifier = Modifier.size(20.dp))
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Cardholder: ${card.cardHolder}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("Card Number: ${card.cardNumber}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                        Text("Current Chip Hex UID: ${if (card.cardUid.isBlank()) "NOT PROGRAMMED" else card.cardUid}", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                    }
                }

                Text("Enter or Generate Hexadecimal Serial/UID for Chip Reprogramming:", fontSize = 11.sp, color = Color.Gray)

                OutlinedTextField(
                    value = hexUidInput,
                    onValueChange = { hexUidInput = it },
                    label = { Text("Hexadecimal Chip UID (e.g. 04:A1:B2:C3)") },
                    modifier = Modifier.fillMaxWidth().testTag("reprogram_hex_uid_input"),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { hexUidInput = generateRandomHexUid() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f), contentColor = MaterialTheme.colorScheme.secondary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("4-Byte Hex", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { hexUidInput = generateRandom7ByteHexUid() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f), contentColor = MaterialTheme.colorScheme.secondary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("7-Byte Hex", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Button(
                    onClick = {
                        viewModel.reprogramNfcCardChip(card.id, hexUidInput) { success, msg ->
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            if (success) onDismiss()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(42.dp).testTag("confirm_reprogram_chip_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Rounded.Nfc, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Reprogram Chip with Hex", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun CardMoreInfoDialog(
    card: com.example.data.GameNfcCard,
    viewModel: BraiPayViewModel? = null,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current
    val parsedColor = remember(card.colorHex) {
        try { Color(android.graphics.Color.parseColor(card.colorHex)) } catch (e: Exception) { Color(0xFF6200EE) }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Rounded.CreditCard, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text("Card Security & Details", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Rounded.Close, contentDescription = "Close", modifier = Modifier.size(20.dp))
                    }
                }

                // Render Visual Card Preview with Custom Photo if present
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(parsedColor)
                    ) {
                        if (!card.customImageUri.isNullOrBlank()) {
                            AsyncImage(
                                model = card.customImageUri,
                                contentDescription = "Card Background",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.35f)))
                        }

                        Column(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(card.cardHolder.uppercase(), fontWeight = FontWeight.Black, fontSize = 15.sp, color = Color.White)
                                Text("DEBIT", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                            }

                            Text(
                                text = card.cardNumber,
                                fontSize = 18.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 2.sp
                            )

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column {
                                    Text("EXPIRY", fontSize = 8.sp, color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.Bold)
                                    Text(card.expiryDate, fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                                Column {
                                    Text("CVV", fontSize = 8.sp, color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.Bold)
                                    Text(card.cvv, fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("BALANCE", fontSize = 8.sp, color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.Bold)
                                    Text(
                                        if (card.currency == "RSD") "${String.format("%.2f", card.balance)} RSD" else "€${String.format("%.2f", card.balance)}",
                                        fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.Black
                                    )
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

                // Detail Rows
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DetailRow("Cardholder Name", card.cardHolder)
                    DetailRow("Card Number", card.cardNumber)
                    DetailRow("Expiration Date", card.expiryDate)
                    DetailRow("CVV / CVC Code", card.cvv)
                    if (card.bankPassword.isNotBlank()) {
                        DetailRow("Bank PIN / Password", card.bankPassword)
                    }
                    if (card.cardUid.isNotBlank()) {
                        DetailRow("NFC Chip UID", card.cardUid)
                    }
                }

                Button(
                    onClick = {
                        val textToCopy = "Cardholder: ${card.cardHolder}\nCard Number: ${card.cardNumber}\nExpiry: ${card.expiryDate}\nCVV: ${card.cvv}"
                        clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(textToCopy))
                        Toast.makeText(context, "Card info copied to clipboard!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Rounded.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Copy Info for Online Checkout", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), fontWeight = FontWeight.Medium)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
    }
}

@Composable
fun PlayCardListItem(
    card: com.example.data.GameNfcCard,
    viewModel: BraiPayViewModel? = null,
    onDelete: () -> Unit,
    onUpdateImage: (String?) -> Unit
) {
    val context = LocalContext.current
    var showMoreInfoDialog by remember { mutableStateOf(false) }
    var showCreditLoanDialog by remember { mutableStateOf(false) }

    if (showMoreInfoDialog) {
        CardMoreInfoDialog(card = card, viewModel = viewModel, onDismiss = { showMoreInfoDialog = false })
    }

    if (showCreditLoanDialog && viewModel != null) {
        CardCreditLoanDialog(card = card, viewModel = viewModel, onDismiss = { showCreditLoanDialog = false })
    }

    val cardColor = remember(card.colorHex) {
        try {
            Color(android.graphics.Color.parseColor(card.colorHex))
        } catch (e: Exception) {
            Color(0xFF6200EE)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            cardColor,
                            cardColor.copy(alpha = 0.82f)
                        )
                    )
                )
        ) {
            // Render custom uploaded image background if set
            if (!card.customImageUri.isNullOrBlank()) {
                AsyncImage(
                    model = card.customImageUri,
                    contentDescription = "Custom Card Background",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                // accessibility semi-transparent overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.35f))
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Background branding silhouette (only show if no custom image is present)
                if (card.customImageUri.isNullOrBlank()) {
                    Icon(
                        imageVector = Icons.Rounded.Contactless,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.08f),
                        modifier = Modifier
                            .size(100.dp)
                            .align(Alignment.BottomEnd)
                            .offset(x = 20.dp, y = 20.dp)
                    )
                }

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Card Top Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = card.cardHolder.uppercase(),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = if (card.cardUid.isNotBlank()) "Linked ID: ${card.cardUid}" else "No physical NFC linked",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White.copy(alpha = 0.7f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BankLogoBadge(
                                bankName = if (card.bankProvider.isNotBlank()) card.bankProvider else "Raiffeisen Bank",
                                showText = true
                            )

                            val imageLauncher = rememberLauncherForActivityResult(
                                contract = ActivityResultContracts.GetContent()
                            ) { uri ->
                                uri?.let {
                                    val savedUriString = saveUriToInternalStorage(context, it)
                                    if (savedUriString != null) {
                                        onUpdateImage(savedUriString)
                                    } else {
                                        Toast.makeText(context, "Failed to load background photo.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }

                            // More Info Security details button
                            IconButton(
                                onClick = { showMoreInfoDialog = true },
                                modifier = Modifier.size(32.dp).testTag("more_info_card_${card.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Info,
                                    contentDescription = "Card Details & Security",
                                    tint = Color.White.copy(alpha = 0.95f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // If custom background is set, let user clear/reset it
                            if (!card.customImageUri.isNullOrBlank()) {
                                IconButton(
                                    onClick = { onUpdateImage(null) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.HideImage,
                                        contentDescription = "Restore Solid Color",
                                        tint = Color.White.copy(alpha = 0.85f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            // Upload / change card photo button
                            IconButton(
                                onClick = { imageLauncher.launch("image/*") },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.PhotoCamera,
                                    contentDescription = "Change Background Photo",
                                    tint = Color.White.copy(alpha = 0.95f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            IconButton(
                                onClick = onDelete,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Delete,
                                    contentDescription = "Delete Play Card",
                                    tint = Color.White.copy(alpha = 0.8f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    // Card Middle Row: Card Number
                    Text(
                        text = card.cardNumber,
                        fontSize = 16.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.95f),
                        letterSpacing = 2.sp
                    )

                    // Card Bottom Row: Balance
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                text = "PLAY WALLET BALANCE",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                            Text(
                                text = if (card.currency == "RSD") "${String.format("%.2f", card.balance)} RSD" else "€${String.format("%.2f", card.balance)}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            if (card.creditDebt > 0.0) {
                                Text(
                                    text = "💳 Debt: ${String.format("%.0f", card.creditDebt)} ${card.currency} (10% Kamata)",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFD54F)
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val cleanNum = card.cardNumber.replace(" ", "")
                            val provider = if (cleanNum.startsWith("4")) {
                                "Visa"
                            } else if (cleanNum.startsWith("5")) {
                                "Mastercard"
                            } else {
                                "Branko Card"
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color.White.copy(alpha = 0.25f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = provider,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Icon(
                                imageVector = Icons.Rounded.Contactless,
                                contentDescription = "NFC Logo",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// HIGH-FIDELITY CHECKOUT MODAL OVERLAY
@Composable
fun CheckoutOverlay(
    state: CheckoutState,
    totalAmount: Double,
    currency: String,
    isSandbox: Boolean,
    allNfcCards: List<com.example.data.GameNfcCard> = emptyList(),
    cartItems: Map<com.example.data.Product, Int> = emptyMap(),
    customAmount: Double = 0.0,
    onClose: () -> Unit,
    onSimulateTap: (Boolean) -> Unit,
    onSimulateCardTap: (com.example.data.GameNfcCard) -> Unit,
    onPayOnline: (cardHolder: String, cardNumber: String, expiry: String, cvv: String) -> Unit = { _, _, _, _ -> },
    onVerifyOtp: (String) -> Unit = {}
) {
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    var selectedCheckoutTab by remember { mutableStateOf(0) } // 0: Contactless Tap, 1: Online Purchase
    
    // Stripe Online Purchase Form States (Selected Card from Already Added Cards)
    var selectedCardUid by remember(allNfcCards) { mutableStateOf(allNfcCards.firstOrNull()?.cardUid ?: "") }
    var bankPassword by remember { mutableStateOf("1234") }
    
    Dialog(
        onDismissRequest = { if (state !is CheckoutState.Processing) onClose() },
        properties = DialogProperties(
            dismissOnBackPress = state !is CheckoutState.Processing,
            dismissOnClickOutside = state !is CheckoutState.Processing,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.85f)),
            contentAlignment = Alignment.BottomCenter
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.9f)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Top Drawer Pill Handle
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
                    )

                    when (state) {
                        is CheckoutState.Processing -> {
                            LaunchedEffect(Unit) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "bPosgame Play Terminal",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary,
                                    letterSpacing = 1.2.sp
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Itemized Order Breakdown / Mac Menu Receipt
                                val hasMacMenu = cartItems.keys.any { it.category.contains("Mac", ignoreCase = true) }
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (hasMacMenu) Color(0xFFDA291C).copy(alpha = 0.08f) 
                                                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                                    ),
                                    border = BorderStroke(
                                        1.dp, 
                                        if (hasMacMenu) Color(0xFFFFBC0D) 
                                        else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "ORDER BREAKDOWN",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Black,
                                                color = if (hasMacMenu) Color(0xFFDA291C) else MaterialTheme.colorScheme.primary,
                                                letterSpacing = 0.6.sp
                                            )
                                            if (hasMacMenu) {
                                                Surface(
                                                    color = Color(0xFFFFBC0D),
                                                    shape = RoundedCornerShape(4.dp)
                                                ) {
                                                    Text(
                                                        text = "🍔 MAC MENU ORDER",
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        color = Color(0xFF27251F),
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                        }

                                        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

                                        if (cartItems.isNotEmpty()) {
                                            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                                cartItems.forEach { (prod, qty) ->
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                            if (prod.category.contains("Mac", ignoreCase = true)) {
                                                                Text("🍔", fontSize = 11.sp)
                                                            }
                                                            Text(
                                                                text = "${qty}x ${prod.name}",
                                                                fontSize = 11.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = MaterialTheme.colorScheme.onSurface
                                                            )
                                                        }
                                                        val itemPrice = if (currency == "EUR") (prod.price / 117.0) * qty else prod.price * qty
                                                        Text(
                                                            text = if (currency == "RSD") "${String.format("%.2f", itemPrice)} RSD" else "€${String.format("%.2f", itemPrice)}",
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.SemiBold,
                                                            color = MaterialTheme.colorScheme.primary
                                                        )
                                                    }
                                                }
                                            }
                                        } else {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text("Custom Terminal Charge", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                Text(
                                                    text = if (currency == "RSD") "${String.format("%.2f", totalAmount)} RSD" else "€${String.format("%.2f", totalAmount)}",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Checkout Mode Tabs: Contactless vs Online Purchase
                                TabRow(
                                    selectedTabIndex = selectedCheckoutTab,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp)),
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                ) {
                                    Tab(
                                        selected = selectedCheckoutTab == 0,
                                        onClick = { selectedCheckoutTab = 0 },
                                        text = { Text("NFC Tap", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                                        icon = { Icon(Icons.Rounded.Contactless, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                    )
                                    Tab(
                                        selected = selectedCheckoutTab == 1,
                                        onClick = { selectedCheckoutTab = 1 },
                                        text = { Text("Online Purchase", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                                        icon = { Icon(Icons.Rounded.ShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                if (selectedCheckoutTab == 0) {
                                    // MODE 0: CONTACTLESS NFC TAP
                                    Text(
                                        text = "Ready for contactless tap",
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                        fontWeight = FontWeight.Medium
                                    )
                                    
                                    Spacer(modifier = Modifier.height(12.dp))
                                    
                                    ContactlessPulsingRings()
                                    
                                    Spacer(modifier = Modifier.height(12.dp))
                                    
                                    Text(
                                        text = if (currency == "RSD") "${String.format("%.2f", totalAmount)} RSD" else "€${String.format("%.2f", totalAmount)}",
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    
                                    Text(
                                        text = "Hold card close to device NFC sensor or tap a registered card below",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
                                    )

                                    if (allNfcCards.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            text = "TAP A REGISTERED PLAY CARD:",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.secondary,
                                            letterSpacing = 0.5.sp
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        androidx.compose.foundation.lazy.LazyRow(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            items(allNfcCards) { card ->
                                                val colorHex = card.colorHex
                                                val parsedColor = remember(colorHex) {
                                                     try { Color(android.graphics.Color.parseColor(colorHex)) } catch (e: Exception) { Color(0xFF6200EE) }
                                                }
                                                Card(
                                                    modifier = Modifier
                                                        .width(130.dp)
                                                        .height(60.dp)
                                                        .clickable {
                                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                            onSimulateCardTap(card)
                                                        },
                                                    colors = CardDefaults.cardColors(containerColor = parsedColor),
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Box(modifier = Modifier.fillMaxSize()) {
                                                        if (!card.customImageUri.isNullOrBlank()) {
                                                            AsyncImage(
                                                                model = card.customImageUri,
                                                                contentDescription = null,
                                                                contentScale = ContentScale.Crop,
                                                                modifier = Modifier.fillMaxSize()
                                                            )
                                                            Box(
                                                                modifier = Modifier
                                                                    .fillMaxSize()
                                                                    .background(Color.Black.copy(alpha = 0.35f))
                                                            )
                                                        }

                                                        Column(
                                                            modifier = Modifier.fillMaxSize().padding(8.dp),
                                                            verticalArrangement = Arrangement.SpaceBetween
                                                        ) {
                                                            Text(
                                                                text = card.cardHolder.uppercase(),
                                                                fontSize = 10.sp,
                                                                fontWeight = FontWeight.ExtraBold,
                                                                color = Color.White,
                                                                maxLines = 1,
                                                                overflow = TextOverflow.Ellipsis
                                                            )
                                                            Text(
                                                                text = if (card.currency == "RSD") "${String.format("%.0f", card.balance)} RSD" else "€${String.format("%.2f", card.balance)}",
                                                                fontSize = 11.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = Color.White
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    // MODE 1: STRIPE ONLINE CHECKOUT (NO DEMO CARDS - STRICTLY ALREADY ADDED CARDS + PRE-SET PAYOUT DESTINATION)
                                    val selectedNfcCard = allNfcCards.find { it.cardUid == selectedCardUid } ?: allNfcCards.firstOrNull()

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .verticalScroll(rememberScrollState()),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        // STRIPE BRAND HEADER BANNER
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(12.dp),
                                            colors = CardDefaults.cardColors(containerColor = Color(0xFF635BFF)) // Stripe Brand Color
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                    Text(
                                                        text = "stripe",
                                                        fontSize = 20.sp,
                                                        fontWeight = FontWeight.Black,
                                                        color = Color.White,
                                                        letterSpacing = (-0.5).sp
                                                    )
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(4.dp))
                                                            .background(Color.White.copy(alpha = 0.2f))
                                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                                    ) {
                                                        Text("CHECKOUT", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                                    }
                                                }

                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(20.dp))
                                                        .background(Color(0xFF00C853))
                                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                                ) {
                                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                        Icon(Icons.Rounded.Lock, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                                        Text("256-Bit SSL", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                                    }
                                                }
                                            }
                                        }

                                        // ORDER SUMMARY & PAYOUT DESTINATION CARD
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(14.dp),
                                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                                        ) {
                                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Column {
                                                        Text("ORDER SUMMARY", fontSize = 10.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary, letterSpacing = 0.8.sp)
                                                        Text("bPosgame Merchant Store", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                    Text(
                                                        text = if (currency == "RSD") "${String.format("%.2f", totalAmount)} RSD" else "€${String.format("%.2f", totalAmount)}",
                                                        fontSize = 18.sp,
                                                        fontWeight = FontWeight.Black,
                                                        color = MaterialTheme.colorScheme.primary
                                                    )
                                                }

                                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                                                // PRE-SET PAYOUT DESTINATION DISPLAY
                                                val payoutDestinationName = allNfcCards.find { it.cardHolder.contains("Merchant", ignoreCase = true) || it.cardHolder.equals("MILAN JOVANOVIC", ignoreCase = true) }?.cardHolder ?: "BraiPay Merchant Account"
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                        Icon(Icons.Rounded.AccountBalance, contentDescription = null, tint = Color(0xFF00C853), modifier = Modifier.size(16.dp))
                                                        Text("Payout Destination:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                                    }
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(6.dp))
                                                            .background(Color(0xFF00C853).copy(alpha = 0.15f))
                                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                                    ) {
                                                        Text("$payoutDestinationName (Set ✓)", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF00C853))
                                                    }
                                                }
                                            }
                                        }

                                        // STRIPE PAYMENT ELEMENT: SELECT FROM ALREADY ADDED CARDS ONLY
                                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                    Icon(Icons.Rounded.CreditCard, contentDescription = null, tint = Color(0xFF635BFF), modifier = Modifier.size(18.dp))
                                                    Text("PAYMENT METHOD (ADDED CARDS)", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color(0xFF635BFF))
                                                }
                                                Text("${allNfcCards.size} Cards Available", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                                            }

                                            if (allNfcCards.isEmpty()) {
                                                Card(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    shape = RoundedCornerShape(12.dp),
                                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f))
                                                ) {
                                                    Row(
                                                        modifier = Modifier.padding(12.dp),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                    ) {
                                                        Icon(Icons.Rounded.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                                                        Text("No registered cards found! Please add a card in Bank Hub first.", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                                                    }
                                                }
                                            } else {
                                                // List of ONLY already added cards
                                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                                    allNfcCards.forEach { card ->
                                                        val isSelected = (selectedNfcCard?.cardUid == card.cardUid)
                                                        val cleanNum = card.cardNumber.replace(" ", "")
                                                        val brandName = if (cleanNum.startsWith("4")) "VISA" else if (cleanNum.startsWith("5")) "MASTERCARD" else "bCARD"
                                                        val maskedNumber = if (card.cardNumber.length >= 4) "•••• ${card.cardNumber.takeLast(4)}" else card.cardNumber

                                                        Card(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .clickable { selectedCardUid = card.cardUid }
                                                                .testTag("stripe_card_select_${card.cardUid}"),
                                                            colors = CardDefaults.cardColors(
                                                                containerColor = if (isSelected) Color(0xFF635BFF).copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
                                                            ),
                                                            shape = RoundedCornerShape(12.dp),
                                                            border = BorderStroke(
                                                                width = if (isSelected) 2.dp else 1.dp,
                                                                color = if (isSelected) Color(0xFF635BFF) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                                            )
                                                        ) {
                                                            Row(
                                                                modifier = Modifier
                                                                    .fillMaxWidth()
                                                                    .padding(12.dp),
                                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                                verticalAlignment = Alignment.CenterVertically
                                                            ) {
                                                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                                                    RadioButton(
                                                                        selected = isSelected,
                                                                        onClick = { selectedCardUid = card.cardUid },
                                                                        colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF635BFF))
                                                                    )
                                                                    Column {
                                                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                                            Text(card.cardHolder.uppercase(), fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                                                                            Box(
                                                                                modifier = Modifier
                                                                                    .clip(RoundedCornerShape(4.dp))
                                                                                    .background(
                                                                                        when (brandName) {
                                                                                            "VISA" -> Color(0xFF0A2540)
                                                                                            "MASTERCARD" -> Color(0xFFEB001B)
                                                                                            else -> Color(0xFF0070F3)
                                                                                        }
                                                                                    )
                                                                                    .padding(horizontal = 6.dp, vertical = 1.dp)
                                                                            ) {
                                                                                Text(brandName, fontSize = 8.sp, fontWeight = FontWeight.Black, color = Color.White)
                                                                            }
                                                                        }
                                                                        Text(maskedNumber, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                                                                    }
                                                                }

                                                                Column(horizontalAlignment = Alignment.End) {
                                                                    Text("Balance", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                                                                    Text(
                                                                        if (card.currency == "RSD") "${String.format("%.0f", card.balance)} RSD" else "€${String.format("%.2f", card.balance)}",
                                                                        fontSize = 12.sp,
                                                                        fontWeight = FontWeight.Black,
                                                                        color = Color(0xFF00C853)
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        // SECURITY PASSCODE / PIN
                                        if (selectedNfcCard != null) {
                                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                ) {
                                                    Icon(Icons.Rounded.Lock, contentDescription = null, tint = Color(0xFF635BFF), modifier = Modifier.size(16.dp))
                                                    Text("CARD BANK PIN / PASSCODE", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color(0xFF635BFF))
                                                }

                                                OutlinedTextField(
                                                    value = bankPassword,
                                                    onValueChange = { bankPassword = it },
                                                    label = { Text("Bank Passcode / PIN") },
                                                    singleLine = true,
                                                    visualTransformation = PasswordVisualTransformation(),
                                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                                    leadingIcon = { Icon(Icons.Rounded.Key, contentDescription = null, tint = Color(0xFF635BFF)) },
                                                    trailingIcon = {
                                                        TextButton(onClick = { bankPassword = selectedNfcCard.bankPassword }) {
                                                            Text(selectedNfcCard.bankPassword, fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color(0xFF635BFF))
                                                        }
                                                    },
                                                    modifier = Modifier.fillMaxWidth().testTag("stripe_bank_password_input"),
                                                    shape = RoundedCornerShape(10.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))

                                        // STRIPE SUBMIT PAYMENT BUTTON
                                        Button(
                                            onClick = {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                val cardToUse = selectedNfcCard ?: allNfcCards.firstOrNull()
                                                if (cardToUse != null) {
                                                    onPayOnline(cardToUse.cardHolder, cardToUse.cardNumber, cardToUse.expiryDate, cardToUse.cvv)
                                                } else {
                                                    Toast.makeText(context, "Please select or add a card first!", Toast.LENGTH_SHORT).show()
                                                }
                                            },
                                            modifier = Modifier.fillMaxWidth().height(52.dp).testTag("online_pay_button"),
                                            shape = RoundedCornerShape(12.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF635BFF)), // Stripe Brand Color
                                            enabled = selectedNfcCard != null
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                Icon(Icons.Rounded.Lock, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.White)
                                                Text(
                                                    text = "Pay ${if (currency == "RSD") "${String.format("%.2f", totalAmount)} RSD" else "€${String.format("%.2f", totalAmount)}"} with Stripe",
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 15.sp,
                                                    color = Color.White
                                                )
                                            }
                                        }

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Powered by stripe  •  Direct Payout to Merchant Account",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        is CheckoutState.Loading -> {
                            val provider = state.provider
                            val holder = state.cardHolder
                            
                            var terminalStatus by remember { mutableStateOf("Reading Chip...") }
                            LaunchedEffect(Unit) {
                                kotlinx.coroutines.delay(600)
                                terminalStatus = "Verifying Credentials..."
                                kotlinx.coroutines.delay(700)
                                terminalStatus = "Securing Connection..."
                                kotlinx.coroutines.delay(600)
                                terminalStatus = "Authorizing with $provider..."
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.Center
                            ) {
                                // Provider Logo & Styling
                                Box(
                                    modifier = Modifier
                                        .size(120.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(
                                            when (provider) {
                                                "Visa" -> Color(0xFF0A2540)
                                                "Mastercard" -> Color(0xFF1E1E1E)
                                                else -> Color(0xFF6200EE)
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        when (provider) {
                                            "Visa" -> {
                                                Text(
                                                    text = "VISA",
                                                    fontStyle = FontStyle.Italic,
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 28.sp,
                                                    color = Color(0xFFF7B600),
                                                    letterSpacing = 1.sp
                                                )
                                                Text(
                                                    text = "ELECTRON",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White.copy(alpha = 0.7f),
                                                    letterSpacing = 0.5.sp
                                                )
                                            }
                                            "Mastercard" -> {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy((-12).dp)
                                                ) {
                                                    Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(Color(0xFFEB001B).copy(alpha = 0.9f)))
                                                    Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(Color(0xFFF79E1B).copy(alpha = 0.9f)))
                                                }
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = "mastercard",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = Color.White
                                                )
                                            }
                                            else -> {
                                                Text(
                                                    text = "b",
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 32.sp,
                                                    color = Color.White
                                                )
                                                Text(
                                                    text = "CARD",
                                                    fontWeight = FontWeight.ExtraBold,
                                                    fontSize = 12.sp,
                                                    color = Color(0xFF00E5FF),
                                                    letterSpacing = 1.sp
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(32.dp))

                                CircularProgressIndicator(
                                    color = when (provider) {
                                        "Visa" -> Color(0xFF0070F3)
                                        "Mastercard" -> Color(0xFFF79E1B)
                                        else -> MaterialTheme.colorScheme.primary
                                    },
                                    strokeWidth = 4.dp,
                                    modifier = Modifier.size(48.dp)
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                Text(
                                    text = terminalStatus.uppercase(),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    letterSpacing = 1.sp
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Cardholder: ${holder.uppercase()}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        is CheckoutState.VerificationRequired -> {
                            val info = state.info
                            var enteredOtp by remember { mutableStateOf("") }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Lock,
                                    contentDescription = "3D Secure Verification",
                                    tint = Color(0xFF0070F3),
                                    modifier = Modifier.size(52.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "3D SECURE VERIFICATION CODE",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF0070F3),
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Cardholder: ${info.cardHolder} | Total: ${info.amountStr}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "Verification Code generated in Bank Hub:",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = info.otpCode,
                                            fontSize = 26.sp,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Black,
                                            color = MaterialTheme.colorScheme.primary,
                                            letterSpacing = 4.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedTextField(
                                    value = enteredOtp,
                                    onValueChange = { if (it.length <= 6) enteredOtp = it },
                                    label = { Text("Enter 6-Digit Code") },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth(0.85f).testTag("checkout_otp_input"),
                                    shape = RoundedCornerShape(10.dp),
                                    trailingIcon = {
                                        TextButton(onClick = { enteredOtp = info.otpCode }) {
                                            Text("Auto-fill", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        onVerifyOtp(enteredOtp)
                                    },
                                    enabled = enteredOtp.length == 6,
                                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("verify_otp_button"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0070F3))
                                ) {
                                    Text("Confirm & Authorize Payment", fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }

                        is CheckoutState.Success -> {
                            LaunchedEffect(Unit) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                            
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.CheckCircle,
                                    contentDescription = "Success",
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(72.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "PAYMENT APPROVED",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF10B981),
                                    letterSpacing = 1.2.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Receipt generated successfully",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                                
                                Spacer(modifier = Modifier.height(24.dp))
                                
                                // Simulated Terminal Receipt slip
                                DigitalReceiptSlip(transaction = state.transaction)
                            }

                            Button(
                                onClick = onClose,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("checkout_success_close"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Print & Close", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }

                        is CheckoutState.Error -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Warning,
                                    contentDescription = "Error",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(72.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "TRANSACTION FAILED",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.error,
                                    letterSpacing = 1.2.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = state.message,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 24.dp)
                                )
                            }

                            Button(
                                onClick = onClose,
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("checkout_error_close"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Return to Terminal", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                        
                        else -> {
                            onClose()
                        }
                    }
                }
            }
        }
    }
}

// Simulated Digital Card Receipt slip
@Composable
fun DigitalReceiptSlip(transaction: TransactionHistory) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "BPOSGAME OFFICIAL PLAY RECEIPT",
                fontSize = 9.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f), modifier = Modifier.padding(vertical = 4.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("MERCHANT:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                Text("bPosgame Play Store #0128", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("PAYMENT METHOD:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                Text(transaction.paymentMethod, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("TRANSACTION ID:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                Text(transaction.transactionId, fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("ITEMS / DESC:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                Text(transaction.productName, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.width(160.dp), textAlign = TextAlign.End)
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f), modifier = Modifier.padding(vertical = 4.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("TOTAL CHARGED:", fontSize = 12.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                Text(if (transaction.currency == "RSD") "${String.format("%.2f", transaction.amount)} RSD" else "€${String.format("%.2f", transaction.amount)}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

// Contactless NFC Pulsing Wave Animations Custom drawn
@Composable
fun ContactlessPulsingRings() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val waveScale by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = androidx.compose.animation.core.FastOutLinearInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radius"
    )
    val waveAlpha by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = androidx.compose.animation.core.FastOutLinearInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "opacity"
    )

    val ringColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = Modifier.size(160.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            
            // Draw Pulsing outer circle wave
            drawCircle(
                color = ringColor,
                radius = 70.dp.toPx() * waveScale,
                center = center,
                style = Stroke(width = 3.dp.toPx()),
                alpha = waveAlpha
            )
            
            // Draw static middle circle wave
            drawCircle(
                color = ringColor.copy(alpha = 0.15f),
                radius = 45.dp.toPx(),
                center = center
            )
        }
        
        // Center Icon
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .background(ringColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Nfc,
                contentDescription = "Tap to pay contactless",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

data class BankBrandInfo(
    val name: String,
    val primaryColor: Color,
    val badgeBgColor: Color,
    val textColor: Color,
    val iconCode: String,
    val shortName: String
)

data class CardStyleDefinition(
    val name: String,
    val backgroundBrush: Brush,
    val textColor: Color,
    val badgeText: String,
    val badgeBgColor: Color,
    val chipColor: Color,
    val accentColor: Color,
    val isMetallic: Boolean = false
)

val CARD_STYLES_LIST = listOf(
    "Gold Edition",
    "Black Obsidian Metal",
    "Platinum Preferred",
    "Rose Gold Luxe",
    "Titanium World",
    "Diamond Infinite",
    "World Elite VIP",
    "Cyberpunk Neon",
    "Emerald Privilege",
    "Royal Sapphire",
    "Ruby Passion",
    "Carbon Fiber Pro",
    "Velvet Violet",
    "Royal Blue Metallic",
    "Sunset Gold",
    "Pearl White Minimal",
    "Solar Yellow",
    "Dark Onyx Metal",
    "Champagne Luxe",
    "Classic Standard"
)

fun getCardStyleDefinition(styleName: String): CardStyleDefinition {
    val clean = styleName.ifBlank { "Gold Edition" }
    return when {
        clean.contains("Gold Edition", ignoreCase = true) || clean.contains("Classic Gold", ignoreCase = true) -> CardStyleDefinition(
            name = "Gold Edition",
            backgroundBrush = Brush.linearGradient(listOf(Color(0xFFBF953F), Color(0xFFFCF6BA), Color(0xFFB38728), Color(0xFFFBF5B7), Color(0xFFAA771C))),
            textColor = Color(0xFF3E2723),
            badgeText = "GOLD EDITION",
            badgeBgColor = Color(0xFF3E2723),
            chipColor = Color(0xFFE5C158),
            accentColor = Color(0xFF5D4037),
            isMetallic = true
        )
        clean.contains("Black Obsidian", ignoreCase = true) || clean.contains("Black Card", ignoreCase = true) || clean.contains("Black Edition", ignoreCase = true) -> CardStyleDefinition(
            name = "Black Obsidian Metal",
            backgroundBrush = Brush.linearGradient(listOf(Color(0xFF2C2C2C), Color(0xFF111111), Color(0xFF000000))),
            textColor = Color.White,
            badgeText = "BLACK OBSIDIAN",
            badgeBgColor = Color(0xFFD4AF37),
            chipColor = Color(0xFFD4AF37),
            accentColor = Color(0xFFD4AF37),
            isMetallic = true
        )
        clean.contains("Platinum", ignoreCase = true) -> CardStyleDefinition(
            name = "Platinum Preferred",
            backgroundBrush = Brush.linearGradient(listOf(Color(0xFFE0E0E0), Color(0xFFBDBDBD), Color(0xFF757575))),
            textColor = Color(0xFF212121),
            badgeText = "PLATINUM PREFERRED",
            badgeBgColor = Color(0xFF212121),
            chipColor = Color(0xFFD5D5D5),
            accentColor = Color(0xFF424242),
            isMetallic = true
        )
        clean.contains("Rose Gold", ignoreCase = true) -> CardStyleDefinition(
            name = "Rose Gold Luxe",
            backgroundBrush = Brush.linearGradient(listOf(Color(0xFFF4D03F), Color(0xFFECC5C0), Color(0xFFB76E79))),
            textColor = Color(0xFF4A2E35),
            badgeText = "ROSE GOLD LUXE",
            badgeBgColor = Color(0xFF4A2E35),
            chipColor = Color(0xFFE8B4B8),
            accentColor = Color(0xFF6B3E47),
            isMetallic = true
        )
        clean.contains("Titanium", ignoreCase = true) -> CardStyleDefinition(
            name = "Titanium World",
            backgroundBrush = Brush.linearGradient(listOf(Color(0xFF4B4D4F), Color(0xFF707376), Color(0xFF2E3033))),
            textColor = Color.White,
            badgeText = "TITANIUM WORLD",
            badgeBgColor = Color(0xFF00E676),
            chipColor = Color(0xFFB0BEC5),
            accentColor = Color(0xFF00E676),
            isMetallic = true
        )
        clean.contains("Diamond", ignoreCase = true) -> CardStyleDefinition(
            name = "Diamond Infinite",
            backgroundBrush = Brush.linearGradient(listOf(Color(0xFFE0F7FA), Color(0xFF80DEEA), Color(0xFF00ACC1), Color(0xFF006064))),
            textColor = Color(0xFF00363A),
            badgeText = "DIAMOND INFINITE",
            badgeBgColor = Color(0xFF006064),
            chipColor = Color(0xFFB2EBF2),
            accentColor = Color(0xFF00838F)
        )
        clean.contains("World Elite", ignoreCase = true) -> CardStyleDefinition(
            name = "World Elite VIP",
            backgroundBrush = Brush.linearGradient(listOf(Color(0xFF0B132B), Color(0xFF1C2541), Color(0xFF3A506B))),
            textColor = Color.White,
            badgeText = "WORLD ELITE VIP",
            badgeBgColor = Color(0xFF64FFDA),
            chipColor = Color(0xFFFFD700),
            accentColor = Color(0xFF64FFDA)
        )
        clean.contains("Cyberpunk", ignoreCase = true) || clean.contains("Neon", ignoreCase = true) -> CardStyleDefinition(
            name = "Cyberpunk Neon",
            backgroundBrush = Brush.linearGradient(listOf(Color(0xFF4A00E0), Color(0xFF8E2DE2), Color(0xFF00F2FE))),
            textColor = Color.White,
            badgeText = "CYBERPUNK NEON",
            badgeBgColor = Color(0xFF00F2FE),
            chipColor = Color(0xFF00F2FE),
            accentColor = Color(0xFFFF007F)
        )
        clean.contains("Emerald", ignoreCase = true) -> CardStyleDefinition(
            name = "Emerald Privilege",
            backgroundBrush = Brush.linearGradient(listOf(Color(0xFF0F5132), Color(0xFF198754), Color(0xFF052C1E))),
            textColor = Color.White,
            badgeText = "EMERALD PRIVILEGE",
            badgeBgColor = Color(0xFFFFD700),
            chipColor = Color(0xFFFFD700),
            accentColor = Color(0xFFFFD700)
        )
        clean.contains("Sapphire", ignoreCase = true) -> CardStyleDefinition(
            name = "Royal Sapphire",
            backgroundBrush = Brush.linearGradient(listOf(Color(0xFF002B49), Color(0xFF005691), Color(0xFF001220))),
            textColor = Color.White,
            badgeText = "ROYAL SAPPHIRE",
            badgeBgColor = Color(0xFF40C4FF),
            chipColor = Color(0xFF80D8FF),
            accentColor = Color(0xFF40C4FF)
        )
        clean.contains("Ruby", ignoreCase = true) -> CardStyleDefinition(
            name = "Ruby Passion",
            backgroundBrush = Brush.linearGradient(listOf(Color(0xFF800020), Color(0xFFD90429), Color(0xFF400010))),
            textColor = Color.White,
            badgeText = "RUBY PASSION",
            badgeBgColor = Color(0xFFFFD700),
            chipColor = Color(0xFFFFD700),
            accentColor = Color(0xFFFFD700)
        )
        clean.contains("Carbon", ignoreCase = true) -> CardStyleDefinition(
            name = "Carbon Fiber Pro",
            backgroundBrush = Brush.linearGradient(listOf(Color(0xFF1C1C1C), Color(0xFF333333), Color(0xFF121212))),
            textColor = Color.White,
            badgeText = "CARBON FIBER PRO",
            badgeBgColor = Color(0xFFFF3D00),
            chipColor = Color(0xFFB0BEC5),
            accentColor = Color(0xFFFF3D00),
            isMetallic = true
        )
        clean.contains("Velvet", ignoreCase = true) || clean.contains("Violet", ignoreCase = true) -> CardStyleDefinition(
            name = "Velvet Violet",
            backgroundBrush = Brush.linearGradient(listOf(Color(0xFF4B0082), Color(0xFF8A2BE2), Color(0xFF2A004E))),
            textColor = Color.White,
            badgeText = "VELVET VIOLET",
            badgeBgColor = Color(0xFFE040FB),
            chipColor = Color(0xFFEA80FC),
            accentColor = Color(0xFFE040FB)
        )
        clean.contains("Royal Blue", ignoreCase = true) -> CardStyleDefinition(
            name = "Royal Blue Metallic",
            backgroundBrush = Brush.linearGradient(listOf(Color(0xFF003399), Color(0xFF0066FF), Color(0xFF001F66))),
            textColor = Color.White,
            badgeText = "ROYAL BLUE",
            badgeBgColor = Color(0xFF448AFF),
            chipColor = Color(0xFF82B1FF),
            accentColor = Color(0xFF448AFF)
        )
        clean.contains("Sunset", ignoreCase = true) -> CardStyleDefinition(
            name = "Sunset Gold",
            backgroundBrush = Brush.linearGradient(listOf(Color(0xFFFF512F), Color(0xFFF09819), Color(0xFFDD2476))),
            textColor = Color.White,
            badgeText = "SUNSET GOLD",
            badgeBgColor = Color(0xFFFFFFFF),
            chipColor = Color(0xFFFFD700),
            accentColor = Color(0xFFFFFFFF)
        )
        clean.contains("Pearl", ignoreCase = true) -> CardStyleDefinition(
            name = "Pearl White Minimal",
            backgroundBrush = Brush.linearGradient(listOf(Color(0xFFFFFFFF), Color(0xFFF8F9FA), Color(0xFFE9ECEF))),
            textColor = Color(0xFF212529),
            badgeText = "PEARL WHITE",
            badgeBgColor = Color(0xFF212529),
            chipColor = Color(0xFFCED4DA),
            accentColor = Color(0xFF495057)
        )
        clean.contains("Solar", ignoreCase = true) -> CardStyleDefinition(
            name = "Solar Yellow",
            backgroundBrush = Brush.linearGradient(listOf(Color(0xFFFFD700), Color(0xFFFFC107), Color(0xFFFFA000))),
            textColor = Color.Black,
            badgeText = "SOLAR GOLD",
            badgeBgColor = Color.Black,
            chipColor = Color.Black,
            accentColor = Color.Black
        )
        clean.contains("Dark Onyx", ignoreCase = true) || clean.contains("Onyx", ignoreCase = true) -> CardStyleDefinition(
            name = "Dark Onyx Metal",
            backgroundBrush = Brush.linearGradient(listOf(Color(0xFF151515), Color(0xFF2B2B2B), Color(0xFF0A0A0A))),
            textColor = Color.White,
            badgeText = "DARK ONYX",
            badgeBgColor = Color(0xFF76FF03),
            chipColor = Color(0xFF76FF03),
            accentColor = Color(0xFF76FF03),
            isMetallic = true
        )
        clean.contains("Champagne", ignoreCase = true) -> CardStyleDefinition(
            name = "Champagne Luxe",
            backgroundBrush = Brush.linearGradient(listOf(Color(0xFFF3E5AB), Color(0xFFD4AF37), Color(0xFFC5A059))),
            textColor = Color(0xFF4A3B12),
            badgeText = "CHAMPAGNE LUXE",
            badgeBgColor = Color(0xFF4A3B12),
            chipColor = Color(0xFFD4AF37),
            accentColor = Color(0xFF5C4B1E)
        )
        else -> CardStyleDefinition(
            name = "Classic Standard",
            backgroundBrush = Brush.linearGradient(listOf(Color(0xFF003865), Color(0xFF005B94), Color(0xFF002147))),
            textColor = Color.White,
            badgeText = "CLASSIC CARD",
            badgeBgColor = Color(0xFF00E676),
            chipColor = Color(0xFFFFD700),
            accentColor = Color(0xFF00E676)
        )
    }
}

@Composable
fun RealCardGraphic(
    card: com.example.data.GameNfcCard,
    modifier: Modifier = Modifier,
    onClickStylePicker: (() -> Unit)? = null
) {
    val styleDef = getCardStyleDefinition(card.cardStyle)
    val bankName = if (card.bankProvider.isNotBlank()) card.bankProvider else "Raiffeisen Bank"

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 8.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(styleDef.backgroundBrush)
                .padding(18.dp)
        ) {
            // Header Row: Bank Logo & Contactless Symbol + Style Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BankLogoBadge(bankName = bankName, showText = true)

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = styleDef.badgeBgColor
                    ) {
                        Text(
                            text = styleDef.badgeText,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            color = if (styleDef.badgeBgColor == Color.White) Color.Black else Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Icon(
                        imageVector = Icons.Rounded.Nfc,
                        contentDescription = "Contactless",
                        tint = styleDef.textColor.copy(alpha = 0.8f),
                        modifier = Modifier.size(22.dp)
                    )

                    onClickStylePicker?.let {
                        IconButton(
                            onClick = it,
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.3f))
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Style,
                                contentDescription = "Change Card Style",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Middle Row: Metallic Chip graphic
            Row(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Golden/Silver EMV Chip
                Box(
                    modifier = Modifier
                        .size(38.dp, 28.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(styleDef.chipColor, styleDef.chipColor.copy(alpha = 0.7f), Color.White, styleDef.chipColor)
                            )
                        )
                        .border(1.dp, Color.Black.copy(alpha = 0.3f), RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(0.7f)
                            .border(0.5.dp, Color.Black.copy(alpha = 0.4f))
                    )
                }

                if (card.creditLimit > 0.0) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color.Black.copy(alpha = 0.4f)
                    ) {
                        Text(
                            text = "CREDIT LINE",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD700),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Bottom Section: Card Number & Details
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Card Number
                val formattedNumber = card.cardNumber.chunked(4).joinToString("  ")
                Text(
                    text = formattedNumber,
                    fontSize = 17.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.ExtraBold,
                    color = styleDef.textColor,
                    letterSpacing = 1.5.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "CARD HOLDER",
                            fontSize = 7.sp,
                            fontWeight = FontWeight.Bold,
                            color = styleDef.textColor.copy(alpha = 0.6f)
                        )
                        Text(
                            text = card.cardHolder.uppercase(),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = styleDef.textColor
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "EXPIRES",
                            fontSize = 7.sp,
                            fontWeight = FontWeight.Bold,
                            color = styleDef.textColor.copy(alpha = 0.6f)
                        )
                        Text(
                            text = card.expiryDate,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = styleDef.textColor
                        )
                    }
                }
            }
        }
    }
}

fun getBankBrandInfo(bankName: String): BankBrandInfo {
    val clean = bankName.ifBlank { "Raiffeisen Bank" }
    return when {
        clean.contains("ProCredit", ignoreCase = true) -> BankBrandInfo(
            name = "ProCredit Bank",
            primaryColor = Color(0xFF003865),
            badgeBgColor = Color(0xFF388E3C),
            textColor = Color.White,
            iconCode = "PCB",
            shortName = "ProCredit Bank"
        )
        clean.contains("Raiffeisen", ignoreCase = true) -> BankBrandInfo(
            name = "Raiffeisen Bank",
            primaryColor = Color(0xFFFFF100),
            badgeBgColor = Color(0xFF000000),
            textColor = Color.Black,
            iconCode = "R",
            shortName = "Raiffeisen Bank"
        )
        clean.contains("Intesa", ignoreCase = true) -> BankBrandInfo(
            name = "Banka Intesa",
            primaryColor = Color(0xFFE25822),
            badgeBgColor = Color(0xFF2E7D32),
            textColor = Color.White,
            iconCode = "BI",
            shortName = "Banka Intesa"
        )
        clean.contains("OTP", ignoreCase = true) -> BankBrandInfo(
            name = "OTP Bank",
            primaryColor = Color(0xFF00A650),
            badgeBgColor = Color(0xFFFFFFFF),
            textColor = Color.White,
            iconCode = "OTP",
            shortName = "OTP Bank"
        )
        clean.contains("UniCredit", ignoreCase = true) -> BankBrandInfo(
            name = "UniCredit Bank",
            primaryColor = Color(0xFFE30613),
            badgeBgColor = Color(0xFFFFFFFF),
            textColor = Color.White,
            iconCode = "UC",
            shortName = "UniCredit Bank"
        )
        clean.contains("NLB", ignoreCase = true) || clean.contains("Komercijalna", ignoreCase = true) -> BankBrandInfo(
            name = "NLB Komercijalna Banka",
            primaryColor = Color(0xFF004B93),
            badgeBgColor = Color(0xFFFFFFFF),
            textColor = Color.White,
            iconCode = "NLB",
            shortName = "NLB Komercijalna"
        )
        clean.contains("Erste", ignoreCase = true) -> BankBrandInfo(
            name = "Erste Bank",
            primaryColor = Color(0xFF005B94),
            badgeBgColor = Color(0xFFE30613),
            textColor = Color.White,
            iconCode = "S",
            shortName = "Erste Bank"
        )
        clean.contains("AIK", ignoreCase = true) -> BankBrandInfo(
            name = "AIK Banka",
            primaryColor = Color(0xFF002B66),
            badgeBgColor = Color(0xFFE53935),
            textColor = Color.White,
            iconCode = "AIK",
            shortName = "AIK Banka"
        )
        else -> BankBrandInfo(
            name = clean,
            primaryColor = Color(0xFF1E88E5),
            badgeBgColor = Color(0xFF00C853),
            textColor = Color.White,
            iconCode = "BP",
            shortName = clean
        )
    }
}

val SUPPORTED_BANKS = listOf(
    "ProCredit Bank",
    "Raiffeisen Bank",
    "Banka Intesa",
    "OTP Bank",
    "UniCredit Bank",
    "NLB Komercijalna Banka",
    "Erste Bank",
    "AIK Banka",
    "BraiPay Digital Bank"
)

@Composable
fun BankLogoBadge(
    bankName: String,
    modifier: Modifier = Modifier,
    showText: Boolean = true
) {
    val info = getBankBrandInfo(bankName)
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = info.primaryColor,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(info.badgeBgColor),
                contentAlignment = Alignment.Center
            ) {
                if (info.iconCode == "R") {
                    Text(
                        text = "✕",
                        color = Color(0xFFFFF100),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                } else {
                    Text(
                        text = info.iconCode.take(3),
                        color = if (info.badgeBgColor == Color.White) info.primaryColor else Color.White,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            if (showText) {
                Text(
                    text = info.shortName,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = info.textColor
                )
            }
        }
    }
}

@Composable
fun BiometricAuthModal(
    onSuccess: () -> Unit,
    onDismiss: () -> Unit
) {
    var isScanning by remember { mutableStateOf(false) }
    var isVerified by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        icon = {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .scale(if (isScanning) scale else 1f)
                    .clip(CircleShape)
                    .background(if (isVerified) Color(0xFF4CAF50).copy(alpha = 0.2f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isVerified) Icons.Rounded.CheckCircle else Icons.Rounded.Fingerprint,
                    contentDescription = "Biometric Sensor",
                    tint = if (isVerified) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(44.dp)
                )
            }
        },
        title = {
            Text(
                text = if (isVerified) "Biometric Access Granted!" else "Bank Hub Biometric Unlock",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isVerified) "Identity verified via Fingerprint / Face ID scanner." else "Touch the sensor below to scan your fingerprint / face ID.",
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                Button(
                    onClick = {
                        isScanning = true
                        TerminalSoundPlayer.playVisaSound()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("scan_biometrics_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isVerified) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Rounded.Fingerprint, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isVerified) "Verified!" else "Scan Fingerprint / Face ID", fontWeight = FontWeight.ExtraBold)
                }

                LaunchedEffect(isScanning) {
                    if (isScanning && !isVerified) {
                        kotlinx.coroutines.delay(1000)
                        isVerified = true
                        TerminalSoundPlayer.playApplePaySound()
                        kotlinx.coroutines.delay(600)
                        onSuccess()
                    }
                }
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun CardStylePickerModal(
    currentStyle: String,
    bankName: String,
    onSelectStyle: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedStyle by remember { mutableStateOf(currentStyle) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    onSelectStyle(selectedStyle)
                    onDismiss()
                },
                modifier = Modifier.testTag("confirm_card_style_button")
            ) {
                Text("Apply Style")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Rounded.Style, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text("Choose Realistic Card Style", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Select from 20 authentic metallic & premium credit/debit card designs:",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                val mockCard = remember(selectedStyle, bankName) {
                    com.example.data.GameNfcCard(
                        cardHolder = "VIP CARDHOLDER",
                        cardNumber = "5412 9821 4410 8820",
                        cardStyle = selectedStyle,
                        bankProvider = bankName,
                        creditLimit = 5000.0,
                        balance = 5000.0
                    )
                }

                RealCardGraphic(card = mockCard, modifier = Modifier.height(150.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth().weight(1f)
                ) {
                    items(CARD_STYLES_LIST) { styleName ->
                        val isSelected = styleName.equals(selectedStyle, ignoreCase = true)
                        val styleDef = getCardStyleDefinition(styleName)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedStyle = styleName }
                                .testTag("style_chip_${styleName.replace(" ", "_")}")
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp, 24.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(styleDef.backgroundBrush)
                                            .border(0.5.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                                    )
                                    Column {
                                        Text(styleName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(styleDef.badgeText, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                                    }
                                }
                                if (isSelected) {
                                    Icon(Icons.Rounded.CheckCircle, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                }
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun IssueCreditCardDialog(
    viewModel: BraiPayViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var selectedBank by remember { mutableStateOf("Raiffeisen Bank") }
    var selectedStyle by remember { mutableStateOf("Gold Edition") }
    var cardHolderName by remember { mutableStateOf("VIP CLIENT") }
    var creditLimitStr by remember { mutableStateOf("5000") }
    var currency by remember { mutableStateOf("EUR") }

    var showStylePicker by remember { mutableStateOf(false) }

    if (showStylePicker) {
        CardStylePickerModal(
            currentStyle = selectedStyle,
            bankName = selectedBank,
            onSelectStyle = { selectedStyle = it },
            onDismiss = { showStylePicker = false }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    val limit = creditLimitStr.toDoubleOrNull() ?: 5000.0
                    viewModel.issueBankCreditCard(
                        cardHolder = cardHolderName,
                        bankName = selectedBank,
                        creditLimit = limit,
                        cardStyle = selectedStyle,
                        onResult = { success, msg ->
                            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                            if (success) onDismiss()
                        }
                    )
                },
                modifier = Modifier.testTag("issue_credit_card_submit_button")
            ) {
                Text("Issue & Activate Card", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Rounded.CreditCard, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text("Apply / Issue New Credit Card", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Configure your official bank credit line card to use for shopping, transfers & payments:",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                val previewCard = remember(selectedBank, selectedStyle, cardHolderName, creditLimitStr, currency) {
                    val limit = creditLimitStr.toDoubleOrNull() ?: 5000.0
                    com.example.data.GameNfcCard(
                        cardHolder = cardHolderName.ifBlank { "VIP CLIENT" },
                        cardNumber = "5412 8820 1192 4010",
                        cardStyle = selectedStyle,
                        bankProvider = selectedBank,
                        creditLimit = limit,
                        balance = limit,
                        currency = currency
                    )
                }

                RealCardGraphic(
                    card = previewCard,
                    modifier = Modifier.height(150.dp),
                    onClickStylePicker = { showStylePicker = true }
                )

                Text("1. Select Bank Issuer", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())
                ) {
                    SUPPORTED_BANKS.forEach { bank ->
                        val isSelected = selectedBank.equals(bank, ignoreCase = true)
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent),
                            modifier = Modifier.clickable { selectedBank = bank }
                        ) {
                            Box(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
                                BankLogoBadge(bankName = bank, showText = true)
                            }
                        }
                    }
                }

                Text("2. Select Card Style (20 Options)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                OutlinedButton(
                    onClick = { showStylePicker = true },
                    modifier = Modifier.fillMaxWidth().testTag("open_style_picker_button"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Rounded.Style, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Selected: $selectedStyle (Tap to change)", fontWeight = FontWeight.Bold)
                }

                OutlinedTextField(
                    value = cardHolderName,
                    onValueChange = { cardHolderName = it },
                    label = { Text("Cardholder Name") },
                    placeholder = { Text("e.g. MILAN JOVANOVIC") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = creditLimitStr,
                        onValueChange = { creditLimitStr = it },
                        label = { Text("Approved Credit Limit") },
                        placeholder = { Text("e.g. 5000") },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = currency,
                        onValueChange = { currency = if (it.uppercase() == "RSD") "RSD" else "EUR" },
                        label = { Text("Currency") },
                        modifier = Modifier.width(90.dp),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                }
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun BankSettingsDialog(
    viewModel: BraiPayViewModel,
    onDismiss: () -> Unit
) {
    val cards by viewModel.allNfcCards.collectAsState()
    val isBankModeEnabled by viewModel.isBankModeEnabled.collectAsState()
    val isSimpleModeEnabled by viewModel.isSimpleModeEnabled.collectAsState()
    val connectedBankUserCardId by viewModel.connectedBankUserCardId.collectAsState()
    val customBankAccountIban by viewModel.customBankAccountIban.collectAsState()
    val selectedBankName by viewModel.selectedBankName.collectAsState()
    val businessCompanyName by viewModel.businessCompanyName.collectAsState()

    var tempEnabled by remember { mutableStateOf(isBankModeEnabled) }
    var tempSimpleMode by remember { mutableStateOf(isSimpleModeEnabled) }
    var tempCardId by remember { mutableStateOf(connectedBankUserCardId) }
    var tempIban by remember { mutableStateOf(customBankAccountIban) }
    var tempBankName by remember { mutableStateOf(selectedBankName) }
    var tempCompanyName by remember { mutableStateOf(businessCompanyName) }

    var expandedDropdown by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header with Back arrow button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        // "after you finish you click back and its good"
                        viewModel.setBankConnection(tempCardId, tempIban, tempBankName, tempEnabled)
                        viewModel.setSimpleMode(tempSimpleMode)
                        viewModel.setBusinessCompanyName(tempCompanyName)
                        onDismiss()
                    }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                    Text(
                        text = "Bank Account Setup",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(48.dp)) // To balance back button
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

                // Toggle Personal Bank Mode
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Enable Bank Mode",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Turn app into your personal bank account customer app",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                    Switch(
                        checked = tempEnabled,
                        onCheckedChange = { tempEnabled = it },
                        modifier = Modifier.testTag("bank_mode_switch")
                    )
                }

                // Toggle Simple Mode
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Simple Mode (Terminal Only)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Restricts the interface to the terminal screen only.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                    Switch(
                        checked = tempSimpleMode,
                        onCheckedChange = { tempSimpleMode = it },
                        modifier = Modifier.testTag("simple_mode_switch")
                    )
                }

                if (tempEnabled) {
                    Text(
                        text = "Connect Bank Account Details",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Choose connected User Card
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Choose Connected User Card",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                        
                        Box(modifier = Modifier.fillMaxWidth()) {
                            val selectedCard = cards.find { it.id == tempCardId }
                            val triggerText = selectedCard?.let { "${it.cardHolder} (${it.cardNumber.takeLast(4)})" } ?: "No Card Connected"
                            
                            OutlinedButton(
                                onClick = { expandedDropdown = true },
                                modifier = Modifier.fillMaxWidth().testTag("choose_user_dropdown"),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(triggerText, color = MaterialTheme.colorScheme.onBackground)
                                    Icon(Icons.Rounded.ArrowDropDown, null, tint = MaterialTheme.colorScheme.primary)
                                }
                            }

                            DropdownMenu(
                                expanded = expandedDropdown,
                                onDismissRequest = { expandedDropdown = false }
                            ) {
                                if (cards.isEmpty()) {
                                    DropdownMenuItem(
                                        text = { Text("No play cards registered") },
                                        onClick = {}
                                    )
                                } else {
                                    cards.forEach { card ->
                                        DropdownMenuItem(
                                            text = { Text("${card.cardHolder} - ${card.cardNumber}") },
                                            onClick = {
                                                tempCardId = card.id
                                                expandedDropdown = false
                                                if (tempIban.isBlank()) {
                                                    tempIban = "DE" + (10..99).random().toString() + " " + (1000..9999).random().toString() + " " + (1000..9999).random().toString() + " " + (10..99).random().toString()
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Bank Provider Selection & Logos
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Choose Bank Provider",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            BankLogoBadge(bankName = tempBankName, showText = true)
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(vertical = 4.dp)
                        ) {
                            SUPPORTED_BANKS.forEach { bank ->
                                val isSelected = tempBankName.equals(bank, ignoreCase = true)
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent),
                                    modifier = Modifier
                                        .clickable { tempBankName = bank }
                                        .testTag("bank_select_chip_${bank.replace(" ", "_")}")
                                ) {
                                    Box(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
                                        BankLogoBadge(bankName = bank, showText = true)
                                    }
                                }
                            }
                        }

                        OutlinedTextField(
                            value = tempBankName,
                            onValueChange = { tempBankName = it },
                            label = { Text("Bank Name (Select above or type custom)") },
                            placeholder = { Text("e.g. ProCredit Bank, Raiffeisen Bank, Banka Intesa, OTP Bank") },
                            modifier = Modifier.fillMaxWidth().testTag("bank_name_input"),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )
                    }

                    // Custom IBAN Field
                    OutlinedTextField(
                        value = tempIban,
                        onValueChange = { tempIban = it },
                        label = { Text("Custom Account Number / IBAN") },
                        placeholder = { Text("e.g. DE89 3704 0044 ...") },
                        modifier = Modifier.fillMaxWidth().testTag("bank_iban_input"),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                // Business / Company Name Section in Settings
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Business / Company Name",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    OutlinedTextField(
                        value = tempCompanyName,
                        onValueChange = { tempCompanyName = it },
                        label = { Text("Default Business Company Name") },
                        placeholder = { Text("e.g. BraiPay Merchant Store") },
                        leadingIcon = { Icon(Icons.Rounded.Business, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        modifier = Modifier.fillMaxWidth().testTag("settings_company_name_input"),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        viewModel.setBankConnection(tempCardId, tempIban, tempBankName, tempEnabled)
                        viewModel.setSimpleMode(tempSimpleMode)
                        viewModel.setBusinessCompanyName(tempCompanyName)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("save_bank_settings_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Save & Close", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun BankHubScreen(viewModel: BraiPayViewModel) {
    val context = LocalContext.current
    val cards by viewModel.allNfcCards.collectAsState()
    val connectedCardId by viewModel.connectedBankUserCardId.collectAsState()
    val customIban by viewModel.customBankAccountIban.collectAsState()
    val bankName by viewModel.selectedBankName.collectAsState()
    val transactions by viewModel.allTransactions.collectAsState()
    val activeOnlineOtp by viewModel.activeOnlineOtp.collectAsState()
    
    val parentCard = cards.find { it.id == connectedCardId }
    val currency = parentCard?.currency ?: "EUR"

    var showBankBranchSimulator by remember { mutableStateOf(false) }

    // Bank login state variables
    val isBankLoggedIn by viewModel.isBankLoggedIn.collectAsState()
    var inputPassword by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf(false) }
    var revealBankPassword by remember { mutableStateOf(false) }
    var showParentCardMoreInfo by remember { mutableStateOf(false) }

    if (showParentCardMoreInfo && parentCard != null) {
        CardMoreInfoDialog(card = parentCard, onDismiss = { showParentCardMoreInfo = false })
    }

    // Biometric & Card Style state variables
    var showBiometricAuthModal by remember { mutableStateOf(false) }
    var showCreditCardIssuanceModal by remember { mutableStateOf(false) }
    var cardToCustomizeStyle by remember { mutableStateOf<com.example.data.GameNfcCard?>(null) }

    if (showBiometricAuthModal) {
        BiometricAuthModal(
            onSuccess = {
                viewModel.loginToBankWithBiometrics()
                showBiometricAuthModal = false
                Toast.makeText(context, "Biometric unlock successful! Welcome to $bankName.", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { showBiometricAuthModal = false }
        )
    }

    if (showCreditCardIssuanceModal) {
        IssueCreditCardDialog(
            viewModel = viewModel,
            onDismiss = { showCreditCardIssuanceModal = false }
        )
    }

    cardToCustomizeStyle?.let { cardToEdit ->
        CardStylePickerModal(
            currentStyle = cardToEdit.cardStyle,
            bankName = cardToEdit.bankProvider.ifBlank { bankName },
            onSelectStyle = { newStyle ->
                viewModel.updateCardStyle(cardToEdit.id, newStyle)
                Toast.makeText(context, "Card style updated to $newStyle!", Toast.LENGTH_SHORT).show()
                cardToCustomizeStyle = null
            },
            onDismiss = { cardToCustomizeStyle = null }
        )
    }

    // Kid limit modification state variables
    var activeKidCardForLimitChange by remember { mutableStateOf<com.example.data.GameNfcCard?>(null) }
    var showLimitDialog by remember { mutableStateOf(false) }
    var newLimitInput by remember { mutableStateOf("") }

    // Money transfer state variables
    var selectedTransferRecipientId by remember { mutableStateOf<Int?>(null) }
    var transferAmountInput by remember { mutableStateOf("") }

    // NFC Chip Management state variables
    var showNfcChipDialog by remember { mutableStateOf(false) }
    var nfcChipInput by remember { mutableStateOf("") }
    var selectedCardForNfc by remember { mutableStateOf<com.example.data.GameNfcCard?>(null) }

    // Multiple Cards & Access Sharing state variables
    var showIssueAccountCardDialog by remember { mutableStateOf(false) }
    var newAccountCardLabel by remember { mutableStateOf("") }
    var newAccountCardColor by remember { mutableStateOf("#1E88E5") }
    var showShareCardDialog by remember { mutableStateOf(false) }
    var selectedCardToShare by remember { mutableStateOf<com.example.data.GameNfcCard?>(null) }
    var shareTargetUser by remember { mutableStateOf("") }

    // Outstanding bills from product catalog
    val products by viewModel.allProducts.collectAsState()
    val billsList = products.filter { it.category.equals("Bills", true) }

    if (showBankBranchSimulator) {
        VirtualBankBranchCounter(
            viewModel = viewModel,
            parentCard = parentCard,
            onDismiss = { showBankBranchSimulator = false }
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Active Bank Provider Quick Selector Header Bar
            Card(
                modifier = Modifier.fillMaxWidth().testTag("bank_hub_provider_selector"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Rounded.AccountBalance, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                            Text("Active Bank Provider:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        BankLogoBadge(bankName = bankName, showText = true)
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        SUPPORTED_BANKS.forEach { bank ->
                            val isSelected = bankName.equals(bank, ignoreCase = true)
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                                border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)),
                                modifier = Modifier
                                    .clickable { viewModel.setSelectedBankName(bank) }
                                    .testTag("hub_bank_chip_${bank.replace(" ", "_")}")
                            ) {
                                Box(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
                                    BankLogoBadge(bankName = bank, showText = true)
                                }
                            }
                        }
                    }
                }
            }

            // 3D Secure Online Payment Verification Banner
            activeOnlineOtp?.let { otpInfo ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("bank_hub_otp_card"),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0070F3)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Rounded.Lock, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                Text("ONLINE PURCHASE VERIFICATION REQUEST", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.White, letterSpacing = 0.8.sp)
                            }
                            IconButton(onClick = { viewModel.cancelOnlinePurchase() }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Rounded.Close, contentDescription = "Cancel", tint = Color.White)
                            }
                        }

                        Text(
                            text = "Online Checkout: ${otpInfo.amountStr} on card ${otpInfo.cardHolder}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.9f)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Your 3D Secure Verification Code:", fontSize = 10.sp, color = Color.White.copy(alpha = 0.75f))
                                Text(
                                    text = otpInfo.otpCode,
                                    fontSize = 28.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Black,
                                    color = Color.Yellow,
                                    letterSpacing = 4.sp
                                )
                            }

                            Button(
                                onClick = { viewModel.approveOnlinePurchaseFromBankHub() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF0070F3)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("bank_hub_approve_otp_button")
                            ) {
                                Text("Approve Payment", fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
            if (parentCard == null) {
                // Not connected warning state
                Card(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Rounded.AccountBalance, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(48.dp))
                        Text("No Bank Account Linked", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.error)
                        Text(
                            text = "Please go to Settings (gear icon in the top header) -> Bank Settings and link a Play Card to activate your banking hub.",
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
                return@Column
            }

            if (!isBankLoggedIn) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .testTag("bank_login_card"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Lock,
                                contentDescription = "Bank Security",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        BankLogoBadge(bankName = bankName, showText = true, modifier = Modifier.padding(bottom = 4.dp))

                        Text(
                            text = "$bankName Secure Login",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = "Linked Account: ${parentCard.cardHolder}\nCard: **** ${parentCard.cardNumber.takeLast(4)}",
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            lineHeight = 18.sp
                        )

                        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

                        OutlinedTextField(
                            value = inputPassword,
                            onValueChange = {
                                inputPassword = it
                                loginError = false
                            },
                            label = { Text("Account Bank Password / PIN") },
                            placeholder = { Text("Enter password chosen at registration") },
                            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth().testTag("bank_login_password_input"),
                            shape = RoundedCornerShape(12.dp),
                            isError = loginError,
                            singleLine = true,
                            leadingIcon = {
                                Icon(Icons.Rounded.Lock, null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                            }
                        )

                        if (loginError) {
                            Text(
                                text = "Incorrect password/PIN! Please try again.",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { revealBankPassword = !revealBankPassword }
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Info,
                                        contentDescription = "Security Hint",
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    val passwordDisplay = if (revealBankPassword) {
                                        parentCard.bankPassword.ifBlank { "(empty/none)" }
                                    } else {
                                        "••••"
                                    }
                                    Text(
                                        text = "Play Hint: Password is \"$passwordDisplay\"",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.secondary,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Icon(
                                    imageVector = if (revealBankPassword) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                                    contentDescription = "Toggle password visibility",
                                    tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Button(
                            onClick = {
                                val success = viewModel.loginToBankWithPassword(inputPassword)
                                if (success) {
                                    Toast.makeText(context, "Welcome to $bankName!", Toast.LENGTH_SHORT).show()
                                } else {
                                    loginError = true
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("bank_login_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Unlock & Access Bank", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { showBiometricAuthModal = true },
                            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("biometric_login_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Rounded.Fingerprint, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Unlock with Biometrics (Fingerprint / Face ID)", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
                return@Column
            }

            // Beautiful Bank Card Dashboard
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            BankLogoBadge(bankName = bankName, showText = true)
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("ACTIVE CONNECTED", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                            }
                            IconButton(
                                onClick = {
                                    viewModel.setBankLoggedIn(false)
                                    Toast.makeText(context, "Logged out of $bankName", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(24.dp).testTag("bank_logout_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Lock,
                                    contentDescription = "Lock Account",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Column {
                        Text("ACCOUNT OWNER", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        Text(parentCard.cardHolder.uppercase(), fontSize = 16.sp, fontWeight = FontWeight.Black)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text("ACCOUNT NUMBER / IBAN", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                            Text(customIban.ifBlank { "N/A" }, fontSize = 12.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        }
                        
                        Column(horizontalAlignment = Alignment.End) {
                            Text("NET BALANCE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                            Text(
                                text = if (currency == "RSD") "${String.format("%.2f", parentCard.balance)} RSD" else "€${String.format("%.2f", parentCard.balance)}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Real Card Graphic with Metallic Chip & Custom Style
                    RealCardGraphic(
                        card = parentCard,
                        onClickStylePicker = { cardToCustomizeStyle = parentCard }
                    )

                    // Quick Actions Row (Issue Credit Card, Change Card Style, View Info)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { showCreditCardIssuanceModal = true },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("hub_issue_credit_card_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Rounded.CreditCard, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Issue Credit Card", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = { cardToCustomizeStyle = parentCard },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("hub_change_style_button"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Rounded.Style, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Card Style (20)", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }

                    Button(
                        onClick = { showParentCardMoreInfo = true },
                        modifier = Modifier.fillMaxWidth().testTag("bank_hub_more_info_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), contentColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Rounded.Info, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("More Info / View CVV & Expiry", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            // NFC CHIP MANAGEMENT CARD
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Rounded.Nfc, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            Text("NFC Chip Management", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.primary)
                        }
                        if (parentCard.cardUid.isNotBlank()) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF4CAF50).copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "ACTIVE LINKED",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF2E7D32),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        } else {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "NO CHIP LINKED",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = "Manage contactless hardware/virtual NFC chip programming for card **** ${parentCard.cardNumber.takeLast(4)}.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("NFC CHIP UID (HEX)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                            Text(
                                text = if (parentCard.cardUid.isNotBlank()) parentCard.cardUid else "No NFC Chip Assigned",
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = if (parentCard.cardUid.isNotBlank()) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (parentCard.cardUid.isNotBlank()) {
                            OutlinedButton(
                                onClick = {
                                    viewModel.updateCardNfcChip(parentCard.id, "") { success, msg ->
                                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.weight(1f).height(38.dp).testTag("remove_nfc_chip_button"),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.6f)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Rounded.Delete, null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Remove NFC Chip", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Button(
                            onClick = {
                                selectedCardForNfc = parentCard
                                nfcChipInput = parentCard.cardUid
                                showNfcChipDialog = true
                            },
                            modifier = Modifier.weight(1f).height(38.dp).testTag("program_nfc_chip_button"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Rounded.Nfc, null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (parentCard.cardUid.isNotBlank()) "Re-Program Chip" else "+ Add NFC Chip", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // ACCOUNT CARDS & SHARED ACCESS CARD
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Rounded.CreditCard, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            Text("Account Cards & Access Sharing", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.primary)
                        }
                    }

                    Text(
                        text = "Manage multiple cards under your account and share card access with other users/players.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )

                    val accountCards = cards.filter {
                        it.cardHolder.equals(parentCard.cardHolder, ignoreCase = true) ||
                        it.parentCardId == parentCard.id ||
                        it.sharedWithUser.equals(parentCard.cardHolder, ignoreCase = true)
                    }

                    accountCards.forEach { cardItem ->
                        val isMainCard = cardItem.id == parentCard.id
                        val isSharedWithMe = cardItem.sharedWithUser.equals(parentCard.cardHolder, ignoreCase = true)

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                            ),
                            border = BorderStroke(1.dp, if (isMainCard) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f) else Color.Transparent)
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Text(cardItem.cardHolder.uppercase(), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            BankLogoBadge(bankName = if (cardItem.bankProvider.isNotBlank()) cardItem.bankProvider else bankName, showText = false)
                                            if (isMainCard) {
                                                Surface(shape = RoundedCornerShape(4.dp), color = MaterialTheme.colorScheme.primary) {
                                                    Text("PRIMARY", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                                }
                                            } else if (isSharedWithMe) {
                                                Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFF9C27B0)) {
                                                    Text("SHARED WITH YOU", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                                }
                                            }
                                        }
                                        Text("**** ${cardItem.cardNumber.takeLast(4)} • UID: ${if (cardItem.cardUid.isNotBlank()) cardItem.cardUid else "No NFC"}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), fontFamily = FontFamily.Monospace)
                                    }

                                    Text(
                                        text = if (cardItem.currency == "RSD") "${String.format("%.2f", cardItem.balance)} RSD" else "€${String.format("%.2f", cardItem.balance)}",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                if (cardItem.sharedWithUser.isNotBlank()) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFF9C27B0).copy(alpha = 0.1f))
                                            .padding(8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Icon(Icons.Rounded.Person, contentDescription = null, tint = Color(0xFF9C27B0), modifier = Modifier.size(14.dp))
                                            Text("Shared with: ${cardItem.sharedWithUser}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9C27B0))
                                        }

                                        TextButton(
                                            onClick = {
                                                viewModel.revokeCardAccessSharing(cardItem.id) { success, msg ->
                                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                                }
                                            },
                                            contentPadding = PaddingValues(0.dp),
                                            modifier = Modifier.height(24.dp)
                                        ) {
                                            Text("Revoke Access", fontSize = 10.sp, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    // Action to share access to card
                                    OutlinedButton(
                                        onClick = {
                                            selectedCardToShare = cardItem
                                            shareTargetUser = cardItem.sharedWithUser
                                            showShareCardDialog = true
                                        },
                                        modifier = Modifier.weight(1f).height(32.dp).testTag("share_card_access_btn_${cardItem.id}"),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                                    ) {
                                        Icon(Icons.Rounded.Person, null, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(if (cardItem.sharedWithUser.isNotBlank()) "Edit Sharing" else "Share Access", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }

                                    // Action to manage NFC chip for card
                                    OutlinedButton(
                                        onClick = {
                                            selectedCardForNfc = cardItem
                                            nfcChipInput = cardItem.cardUid
                                            showNfcChipDialog = true
                                        },
                                        modifier = Modifier.weight(1f).height(32.dp).testTag("manage_subcard_nfc_${cardItem.id}"),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                                    ) {
                                        Icon(Icons.Rounded.Nfc, null, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(if (cardItem.cardUid.isNotBlank()) "Edit Chip" else "Add Chip", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // Issue new card button
                    Button(
                        onClick = { showIssueAccountCardDialog = true },
                        modifier = Modifier.fillMaxWidth().height(40.dp).testTag("issue_new_account_card_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Rounded.Add, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Issue Additional Card to Account", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Dialogs for NFC Chip, Card Sharing, and New Account Card
            if (showNfcChipDialog && selectedCardForNfc != null) {
                val currentCard = selectedCardForNfc!!
                val scannedUid by viewModel.scannedNfcUid.collectAsState()
                androidx.compose.material3.AlertDialog(
                    onDismissRequest = { showNfcChipDialog = false },
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Rounded.Nfc, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Text("Reprogram NFC Chip (HEX Format)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "Card: ${currentCard.cardHolder} (**** ${currentCard.cardNumber.takeLast(4)})",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Reprogram this card's NFC chip UID using Hexadecimal format (same hex standard as normal card creations).",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )

                            if (!scannedUid.isNullOrBlank()) {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF10B981).copy(alpha = 0.12f)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("Physical NFC Tag Tapped!", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                                            Text("Scanned Hex UID: ${scannedUid}", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                                        }
                                        Button(
                                            onClick = { nfcChipInput = scannedUid!! },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text("Use Scanned", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = nfcChipInput,
                                onValueChange = { input ->
                                    nfcChipInput = input.uppercase().replace(Regex("[^0-9A-F:]"), "")
                                },
                                label = { Text("NFC Chip Hex UID (e.g. 04:A2:8B:1F)") },
                                placeholder = { Text("04:A2:8B:1F") },
                                modifier = Modifier.fillMaxWidth().testTag("nfc_uid_input_field"),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true,
                                trailingIcon = {
                                    IconButton(onClick = {
                                        nfcChipInput = com.example.ui.generateRandomHexUid()
                                    }) {
                                        Icon(Icons.Rounded.Refresh, contentDescription = "Auto Generate Hex", tint = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Button(
                                    onClick = {
                                        nfcChipInput = com.example.ui.generateRandomHexUid()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                    modifier = Modifier.weight(1f).height(36.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Auto Hex (4-Byte)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = {
                                        nfcChipInput = com.example.ui.generateRandom7ByteHexUid()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)),
                                    modifier = Modifier.weight(1f).height(36.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Auto Hex (7-Byte)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            if (nfcChipInput.isNotBlank()) {
                                OutlinedButton(
                                    onClick = { nfcChipInput = "" },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                                    modifier = Modifier.fillMaxWidth().height(32.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Clear / Erase Chip UID", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.updateCardNfcChip(currentCard.id, nfcChipInput) { success, msg ->
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                }
                                showNfcChipDialog = false
                            }
                        ) {
                            Text("Reprogram Chip (Save)", fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showNfcChipDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }

            if (showShareCardDialog && selectedCardToShare != null) {
                val targetCard = selectedCardToShare!!
                var expandedUserDropdown by remember { mutableStateOf(false) }

                androidx.compose.material3.AlertDialog(
                    onDismissRequest = { showShareCardDialog = false },
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Rounded.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Text("Share Card Access", fontWeight = FontWeight.Bold)
                        }
                    },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "Grant another player/user access to card **** ${targetCard.cardNumber.takeLast(4)}.",
                                fontSize = 12.sp
                            )

                            OutlinedTextField(
                                value = shareTargetUser,
                                onValueChange = { shareTargetUser = it },
                                label = { Text("Recipient Player / User Name") },
                                placeholder = { Text("Enter user or cardholder name") },
                                modifier = Modifier.fillMaxWidth().testTag("share_target_user_input"),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true
                            )

                            Text("Or select existing player:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            val existingUsers = cards.map { it.cardHolder }.distinct().filter { !it.equals(targetCard.cardHolder, ignoreCase = true) }
                            
                            if (existingUsers.isNotEmpty()) {
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    OutlinedButton(
                                        onClick = { expandedUserDropdown = true },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                            Text(if (shareTargetUser.isNotBlank()) shareTargetUser else "Choose from players...", fontSize = 12.sp)
                                            Icon(Icons.Rounded.ArrowDropDown, null)
                                        }
                                    }

                                    androidx.compose.material3.DropdownMenu(
                                        expanded = expandedUserDropdown,
                                        onDismissRequest = { expandedUserDropdown = false }
                                    ) {
                                        existingUsers.forEach { user ->
                                            androidx.compose.material3.DropdownMenuItem(
                                                text = { Text(user) },
                                                onClick = {
                                                    shareTargetUser = user
                                                    expandedUserDropdown = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.shareCardAccess(targetCard.id, shareTargetUser) { success, msg ->
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                }
                                showShareCardDialog = false
                            }
                        ) {
                            Text("Confirm Sharing", fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showShareCardDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }

            if (showIssueAccountCardDialog && parentCard != null) {
                androidx.compose.material3.AlertDialog(
                    onDismissRequest = { showIssueAccountCardDialog = false },
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Rounded.CreditCard, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Text("Issue Card to Account", fontWeight = FontWeight.Bold)
                        }
                    },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "Issue a new secondary card under account '${parentCard.cardHolder}'.",
                                fontSize = 12.sp
                            )

                            OutlinedTextField(
                                value = newAccountCardLabel,
                                onValueChange = { newAccountCardLabel = it },
                                label = { Text("Card Label (e.g. Virtual, Travel, Expense)") },
                                placeholder = { Text("Secondary Card") },
                                modifier = Modifier.fillMaxWidth().testTag("new_account_card_label_input"),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true
                            )

                            Text("Card Color Style:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                val colorOptions = listOf("#1E88E5", "#43A047", "#8E24AA", "#E53935", "#FB8C00")
                                colorOptions.forEach { hex ->
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(Color(android.graphics.Color.parseColor(hex)))
                                            .border(
                                                width = if (newAccountCardColor == hex) 3.dp else 0.dp,
                                                color = if (newAccountCardColor == hex) MaterialTheme.colorScheme.primary else Color.Transparent,
                                                shape = CircleShape
                                            )
                                            .clickable { newAccountCardColor = hex }
                                    )
                                }
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.issueAdditionalAccountCard(
                                    parentCard = parentCard,
                                    cardLabel = newAccountCardLabel,
                                    colorHex = newAccountCardColor
                                ) { success, msg ->
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                }
                                showIssueAccountCardDialog = false
                                newAccountCardLabel = ""
                            }
                        ) {
                            Text("Issue Card", fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showIssueAccountCardDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }

            // Pay Outstanding Bills
            Text("Outstanding Play Bills", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.primary)
            
            if (billsList.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("No play bills found!", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(
                            "Add items under category 'Bills' in the Catalog, or click 'Preseed' in the Play Cards tab to load mock utilities!",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        billsList.forEach { bill ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(bill.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Utility Invoice", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                                }
                                
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    val displayPrice = if (currency == "EUR") bill.price / 117.0 else bill.price
                                    Text(
                                        text = if (currency == "RSD") "${String.format("%.2f", displayPrice)} RSD" else "€${String.format("%.2f", displayPrice)}",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Button(
                                        onClick = {
                                            viewModel.payBillFromBank(
                                                productName = bill.name,
                                                amount = displayPrice,
                                                cardId = parentCard.id,
                                                currency = currency,
                                                onResult = { success, msg ->
                                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                                }
                                            )
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                        modifier = Modifier.height(32.dp).testTag("pay_bill_btn_${bill.id}")
                                    ) {
                                        Text("Pay", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Parental Controls & Kid Cards Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Parental Account Controls",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    
                    Text(
                        text = "To register or issue a child's debit card linked directly to your funding, physical bank visitation is required to program the contactless secure play chip.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    // Display kid cards linked to this parent card
                    val kidCards = cards.filter { it.isKidCard && it.parentCardId == parentCard.id }
                    if (kidCards.isNotEmpty()) {
                        Text("Issued Kid Cards:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        kidCards.forEach { kid ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(android.graphics.Color.parseColor(kid.colorHex))))
                                    Column {
                                        Text(kid.cardHolder.uppercase(), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Text("Daily Limit: " + if (currency == "RSD") "${kid.dailySpendingLimit} RSD" else "€${kid.dailySpendingLimit}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                                            IconButton(
                                                onClick = {
                                                    activeKidCardForLimitChange = kid
                                                    newLimitInput = kid.dailySpendingLimit.toString()
                                                    showLimitDialog = true
                                                },
                                                modifier = Modifier.size(20.dp).testTag("edit_limit_button_${kid.id}")
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Rounded.Settings,
                                                    contentDescription = "Edit Limit",
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = if (currency == "RSD") "${String.format("%.2f", kid.balance)} RSD" else "€${String.format("%.2f", kid.balance)}",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 12.sp
                                    )
                                    Text("Spent Today: " + if (currency == "RSD") "${kid.todaySpent} RSD" else "€${kid.todaySpent}", fontSize = 9.sp, color = MaterialTheme.colorScheme.secondary)
                                }
                            }
                        }

                        if (showLimitDialog) {
                            activeKidCardForLimitChange?.let { child ->
                                androidx.compose.material3.AlertDialog(
                                    onDismissRequest = {
                                        showLimitDialog = false
                                        activeKidCardForLimitChange = null
                                    },
                                    title = { Text("Edit Spending Limit", fontWeight = FontWeight.Bold) },
                                    text = {
                                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Text("Set daily spending limit for ${child.cardHolder}'s debit card.")
                                            OutlinedTextField(
                                                value = newLimitInput,
                                                onValueChange = { newLimitInput = it },
                                                label = { Text("Daily Spending Limit (${currency})") },
                                                modifier = Modifier.fillMaxWidth().testTag("edit_limit_input_field"),
                                                shape = RoundedCornerShape(10.dp),
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                                            )
                                        }
                                    },
                                    confirmButton = {
                                        Button(
                                            onClick = {
                                                val parsedLimit = newLimitInput.toDoubleOrNull() ?: 0.0
                                                viewModel.updateKidCardLimit(child.id, parsedLimit) { success, msg ->
                                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                                }
                                                showLimitDialog = false
                                                activeKidCardForLimitChange = null
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                        ) {
                                            Text("Save Limit", fontWeight = FontWeight.Bold)
                                        }
                                    },
                                    dismissButton = {
                                        androidx.compose.material3.TextButton(
                                            onClick = {
                                                showLimitDialog = false
                                                activeKidCardForLimitChange = null
                                            }
                                        ) {
                                            Text("Cancel")
                                        }
                                    }
                                )
                            }
                        }
                    }

                    Button(
                        onClick = { showBankBranchSimulator = true },
                        modifier = Modifier.fillMaxWidth().height(40.dp).testTag("go_to_bank_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Rounded.Store, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Visit Virtual Bank Branch to Issue Kid Card", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // -------------------------------------------------------------
            // NEW: Send Money to Other Players Section
            // -------------------------------------------------------------
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Transfer Money to Players",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Text(
                        text = "Instantly transfer play funds from your active card account to another player's card.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )

                    val otherPlayers = cards.filter { it.id != parentCard.id }

                    if (otherPlayers.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No other registered play cards found to transfer to. Create cards in the 'Cards' tab first!",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        // Recipient Selection Dropdown
                        var expandedRecipientDropdown by remember { mutableStateOf(false) }
                        val selectedRecipient = otherPlayers.find { it.id == selectedTransferRecipientId }

                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedButton(
                                onClick = { expandedRecipientDropdown = true },
                                modifier = Modifier.fillMaxWidth().testTag("transfer_recipient_selector"),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = selectedRecipient?.let { "${it.cardHolder.uppercase()} (**** ${it.cardNumber.takeLast(4)}) [${it.currency}]" } ?: "Select Recipient Player",
                                        color = if (selectedRecipient != null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                        fontSize = 13.sp
                                    )
                                    Icon(Icons.Rounded.ArrowDropDown, null)
                                }
                            }

                            androidx.compose.material3.DropdownMenu(
                                expanded = expandedRecipientDropdown,
                                onDismissRequest = { expandedRecipientDropdown = false },
                                modifier = Modifier.fillMaxWidth(0.9f)
                            ) {
                                otherPlayers.forEach { player ->
                                    androidx.compose.material3.DropdownMenuItem(
                                        text = {
                                            Text("${player.cardHolder.uppercase()} (**** ${player.cardNumber.takeLast(4)}) - Balance: ${if (player.currency == "EUR") "€" else ""}${String.format("%.2f", player.balance)} ${if (player.currency == "RSD") "RSD" else ""}")
                                        },
                                        onClick = {
                                            selectedTransferRecipientId = player.id
                                            expandedRecipientDropdown = false
                                        }
                                    )
                                }
                            }
                        }

                        // Amount Input
                        OutlinedTextField(
                            value = transferAmountInput,
                            onValueChange = { transferAmountInput = it },
                            label = { Text("Transfer Amount (${currency})") },
                            placeholder = { Text("e.g. 20.00") },
                            leadingIcon = {
                                Text(
                                    text = if (currency == "EUR") "€" else "RSD",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth().testTag("transfer_amount_input"),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )

                        // Action Button
                        Button(
                            onClick = {
                                val amount = transferAmountInput.toDoubleOrNull()
                                if (amount == null || amount <= 0.0) {
                                    Toast.makeText(context, "Please enter a valid transfer amount.", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                val targetId = selectedTransferRecipientId
                                if (targetId == null) {
                                    Toast.makeText(context, "Please select a recipient player.", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                viewModel.transferMoneyToPlayer(
                                    sourceCardId = parentCard.id,
                                    targetCardId = targetId,
                                    amount = amount,
                                    onResult = { success, message ->
                                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                        if (success) {
                                            transferAmountInput = ""
                                            selectedTransferRecipientId = null
                                        }
                                    }
                                )
                            },
                            modifier = Modifier.fillMaxWidth().height(42.dp).testTag("transfer_submit_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Send Play Transfer", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }

            // Bank Statement (Transaction Statement)
            Text("Personal Statement History", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.primary)
            
            val parentStatement = transactions.filter {
                it.paymentMethod.contains("(${parentCard.cardHolder})", ignoreCase = true) ||
                it.paymentMethod.contains("Bank Hub", ignoreCase = true)
            }
            
            if (parentStatement.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(modifier = Modifier.padding(16.dp), contentAlignment = Alignment.Center) {
                        Text("No transaction logs recorded on this account statement.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    }
                }
            } else {
                parentStatement.take(15).forEach { tx ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Rounded.AccountBalanceWallet, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                }
                                Column {
                                    Text(tx.productName, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text(tx.transactionId, fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                                }
                            }
                            
                            Text(
                                text = "-" + if (tx.currency == "RSD") "${String.format("%.2f", tx.amount)} RSD" else "€${String.format("%.2f", tx.amount)}",
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VirtualBankBranchCounter(
    viewModel: BraiPayViewModel,
    parentCard: com.example.data.GameNfcCard?,
    onDismiss: () -> Unit
) {
    if (parentCard == null) return
    val context = LocalContext.current
    val currency = parentCard.currency

    var kidName by remember { mutableStateOf("") }
    var limitText by remember { mutableStateOf("10.00") }
    var fundText by remember { mutableStateOf("25.00") }
    var kidCardUid by remember { mutableStateOf("") }
    var kidCardNumber by remember { mutableStateOf("") }
    var selectedColorIndex by remember { mutableStateOf(0) }

    val colorsHex = listOf("#F43F5E", "#0EA5E9", "#10B981", "#EAB308", "#8B5CF6")

    // Automatic generate UID if empty
    LaunchedEffect(Unit) {
        kidCardUid = "KID-" + (1000..9999).random().toString()
        kidCardNumber = "4120 99" + (10..99).random().toString() + " " + (1000..9999).random().toString() + " " + (1000..9999).random().toString()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Teller Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.SupportAgent, null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(28.dp))
                    }
                    Column {
                        Text("VIRTUAL BANK BRANCH COUNTER", fontSize = 10.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                        Text("Meet Banker Sarah", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
                
                Text(
                    text = "\"Hello! I am Sarah, your virtual bank teller. I can register and fund a children's debit card linked directly to your primary account (${parentCard.cardHolder}). I will securely code the daily spending limit to enforce safe financial training. Please state the kid's name and details!\"",
                    fontSize = 12.sp,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
                    lineHeight = 16.sp
                )
            }
        }

        // Parent balance check
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(modifier = Modifier.padding(12.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Your Available Funding Source:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = if (currency == "RSD") "${String.format("%.2f", parentCard.balance)} RSD" else "€${String.format("%.2f", parentCard.balance)}",
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Inputs
        OutlinedTextField(
            value = kidName,
            onValueChange = { kidName = it },
            label = { Text("Kid Name / Cardholder") },
            placeholder = { Text("e.g. Bobby Doe") },
            modifier = Modifier.fillMaxWidth().testTag("kid_name_input"),
            shape = RoundedCornerShape(10.dp)
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = fundText,
                onValueChange = { fundText = it },
                label = { Text("Initial Balance") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1f).testTag("kid_fund_input"),
                shape = RoundedCornerShape(10.dp)
            )

            OutlinedTextField(
                value = limitText,
                onValueChange = { limitText = it },
                label = { Text("Daily Spend Limit") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1f).testTag("kid_limit_input"),
                shape = RoundedCornerShape(10.dp)
            )
        }

        OutlinedTextField(
            value = kidCardUid,
            onValueChange = { kidCardUid = it },
            label = { Text("NFC Chip UID / Auto-scanned ID") },
            placeholder = { Text("Click Refresh to change") },
            trailingIcon = {
                IconButton(onClick = { kidCardUid = "KID-" + (1000..9999).random().toString() }) {
                    Icon(Icons.Rounded.Refresh, null)
                }
            },
            modifier = Modifier.fillMaxWidth().testTag("kid_uid_input"),
            shape = RoundedCornerShape(10.dp)
        )

        // Select Card Color
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Select Play Card Aesthetic Theme:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                colorsHex.forEachIndexed { idx, hex ->
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(android.graphics.Color.parseColor(hex)))
                            .border(
                                width = if (selectedColorIndex == idx) 3.dp else 1.dp,
                                color = if (selectedColorIndex == idx) MaterialTheme.colorScheme.primary else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable { selectedColorIndex = idx }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Actions
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.weight(1f).height(44.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Cancel Branch Visit", fontSize = 12.sp)
            }

            Button(
                onClick = {
                    if (kidName.isBlank()) {
                        Toast.makeText(context, "Please enter your child's name!", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    val fund = fundText.toDoubleOrNull() ?: 0.0
                    val limit = limitText.toDoubleOrNull() ?: 0.0
                    
                    viewModel.issueKidCard(
                        parentCardId = parentCard.id,
                        kidName = kidName,
                        kidCardNumber = kidCardNumber,
                        kidCardUid = kidCardUid,
                        initialBalance = fund,
                        limit = limit,
                        currency = currency,
                        colorHex = colorsHex[selectedColorIndex],
                        onResult = { success, msg ->
                            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                            if (success) {
                                onDismiss()
                            }
                        }
                    )
                },
                modifier = Modifier.weight(1.2f).height(44.dp).testTag("issue_kid_card_confirm"),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Sign & Issue Card", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
