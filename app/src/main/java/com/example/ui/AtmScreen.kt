package com.example.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.GameNfcCard
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Serbian RSD Banknote Definition
data class RsdBanknote(
    val value: Int,
    val name: String,
    val historicalFigure: String,
    val primaryColor: Color,
    val secondaryColor: Color
)

val SERBIAN_BANKNOTES = listOf(
    RsdBanknote(200, "200 RSD", "Nadežda Petrović", Color(0xFF8D6E63), Color(0xFFD7CCC8)),
    RsdBanknote(500, "500 RSD", "Jovan Cvijić", Color(0xFF00897B), Color(0xFFB2DFDB)),
    RsdBanknote(1000, "1.000 RSD", "Đorđe Vajfert", Color(0xFFC2185B), Color(0xFFF8BBD0)),
    RsdBanknote(2000, "2.000 RSD", "Milutin Milanković", Color(0xFF5E35B1), Color(0xFFD1C4E9)),
    RsdBanknote(5000, "5.000 RSD", "Slobodan Jovanović", Color(0xFF3949AB), Color(0xFFC5CAE9))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AtmScreen(viewModel: BraiPayViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val cards by viewModel.allNfcCards.collectAsState()
    val connectedCardId by viewModel.connectedBankUserCardId.collectAsState()
    val selectedBankName by viewModel.selectedBankName.collectAsState()

    // Active Card in ATM (defaults to connected bank card or first card)
    var selectedCardId by remember {
        mutableStateOf(connectedCardId ?: cards.firstOrNull()?.id ?: 0)
    }

    LaunchedEffect(cards, connectedCardId) {
        if (selectedCardId == 0 && cards.isNotEmpty()) {
            selectedCardId = connectedCardId ?: cards.first().id
        }
    }

    val activeCard = cards.find { it.id == selectedCardId }
    val balanceInRsd = if (activeCard != null) {
        if (activeCard.currency == "RSD") activeCard.balance else activeCard.balance * 117.0
    } else 0.0

    // Withdrawal State
    var selectedAmountRsd by remember { mutableStateOf(2000.0) }
    var customAmountInput by remember { mutableStateOf("") }
    var pinInput by remember { mutableStateOf("") }
    var isDispensing by remember { mutableStateOf(false) }
    var dispensedSuccessAmount by remember { mutableStateOf<Double?>(null) }
    var showReceiptDialog by remember { mutableStateOf(false) }
    var receiptData by remember { mutableStateOf<AtmReceiptData?>(null) }
    var showCardSelectorModal by remember { mutableStateOf(false) }

    // Shutter animation for cash slot
    val cashDispenserOffset = remember { Animatable(0f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ATM Top Enclosure Banner (Realistic Bankomat Interface)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF0F172A) // Deep Navy ATM Screen
            ),
            border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF1E293B)),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Bank Logo & ATM Indicator Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF0D9488)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Rounded.LocalAtm,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column {
                            Text(
                                text = selectedBankName.ifBlank { "SRPSKI BANKOMAT" }.uppercase(),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF38BDF8),
                                letterSpacing = 0.8.sp
                            )
                            Text(
                                text = "ATM CASH DISPENSER • SRBIJA",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        }
                    }

                    // Online Status Indicator
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF065F46))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF34D399))
                        )
                        Text(
                            text = "ONLINE",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF34D399)
                        )
                    }
                }

                HorizontalDivider(color = Color(0xFF1E293B))

                // Inserted Card Panel in ATM
                if (activeCard != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1E293B).copy(alpha = 0.7f))
                            .clickable { showCardSelectorModal = true }
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                Icons.Rounded.CreditCard,
                                contentDescription = null,
                                tint = Color(0xFFFBBF24),
                                modifier = Modifier.size(28.dp)
                            )
                            Column {
                                Text(
                                    text = activeCard.cardHolder.uppercase(),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "**** **** **** ${activeCard.cardNumber.takeLast(4)}",
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color.White.copy(alpha = 0.6f)
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "STANJE NA RAČUNU",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF94A3B8)
                            )
                            Text(
                                text = "${String.format(Locale.US, "%,.2f", balanceInRsd)} RSD",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF4ADE80)
                            )
                        }
                    }
                } else {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showCardSelectorModal = true },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Rounded.AddCard, contentDescription = null, tint = Color(0xFF38BDF8))
                            Text(
                                text = "Ubaci karticu u bankomat (Izaberi karticu)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Serbian Banknotes Grid Selector (Novčanice)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "💵 Izaberite novčanice za isplatu (RSD):",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Valuta: RSD",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Banknote Chips Row
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SERBIAN_BANKNOTES.forEach { note ->
                    val isSelected = selectedAmountRsd == note.value.toDouble() && customAmountInput.isEmpty()
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedAmountRsd = note.value.toDouble()
                                customAmountInput = ""
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) note.primaryColor.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surface
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) note.primaryColor else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Banknote Mini Graphic Badge
                                Box(
                                    modifier = Modifier
                                        .size(width = 48.dp, height = 28.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(note.primaryColor),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = note.value.toString(),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                }
                                Column {
                                    Text(
                                        text = note.name,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) note.primaryColor else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = note.historicalFigure,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                }
                            }

                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    selectedAmountRsd = note.value.toDouble()
                                    customAmountInput = ""
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = note.primaryColor)
                            )
                        }
                    }
                }
            }
        }

        // Quick Preset Amounts Row
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Brzi iznosi isplate (Quick Cash):",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(500.0, 1000.0, 2000.0, 5000.0, 10000.0).forEach { preset ->
                    val isChosen = selectedAmountRsd == preset && customAmountInput.isEmpty()
                    OutlinedButton(
                        onClick = {
                            selectedAmountRsd = preset
                            customAmountInput = ""
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isChosen) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else Color.Transparent
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (isChosen) 1.5.dp else 1.dp,
                            color = if (isChosen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                        )
                    ) {
                        Text(
                            text = "${preset.toInt()}",
                            fontSize = 11.sp,
                            fontWeight = if (isChosen) FontWeight.ExtraBold else FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Custom Amount Input
        OutlinedTextField(
            value = customAmountInput,
            onValueChange = { input ->
                customAmountInput = input.filter { it.isDigit() }
                val parsed = customAmountInput.toDoubleOrNull()
                if (parsed != null && parsed > 0) {
                    selectedAmountRsd = parsed
                }
            },
            label = { Text("Drugi iznos (Custom RSD Amount)") },
            placeholder = { Text("npr. 7500 RSD") },
            leadingIcon = {
                Icon(Icons.Rounded.Payments, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            },
            trailingIcon = {
                Text("RSD", fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 12.dp))
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        // PIN Code Input (Optional security pad)
        OutlinedTextField(
            value = pinInput,
            onValueChange = { if (it.length <= 4) pinInput = it },
            label = { Text("PIN Kod kartice (Opciono / CVV)") },
            placeholder = { Text("Unesite 4-cifreni PIN") },
            leadingIcon = {
                Icon(Icons.Rounded.Key, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        // Realistic Cash Dispenser Slot Graphic
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        Icons.Rounded.PriceCheck,
                        contentDescription = null,
                        tint = if (isDispensing || dispensedSuccessAmount != null) Color(0xFF34D399) else Color.White.copy(alpha = 0.5f)
                    )
                    Text(
                        text = "OTVOR ZA ISPLATU GOTOVINE (CASH DISPENSER)",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }

                // Shutter Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(28.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF0F172A))
                        .border(
                            1.dp,
                            if (isDispensing || dispensedSuccessAmount != null) Color(0xFF10B981) else Color(0xFF334155),
                            RoundedCornerShape(6.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isDispensing) {
                        Text(
                            text = "⚙️ Brojanje i priprema novčanica...",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFBBF24)
                        )
                    } else if (dispensedSuccessAmount != null) {
                        Text(
                            text = "💵 UZMITE VAŠ NOVAC: ${String.format(Locale.US, "%,.0f", dispensedSuccessAmount)} RSD",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF34D399)
                        )
                    } else {
                        Text(
                            text = "═══ OTVOR ZATVOREN ═══",
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            color = Color.White.copy(alpha = 0.35f)
                        )
                    }
                }
            }
        }

        // Withdraw Cash Button
        Button(
            onClick = {
                if (activeCard == null) {
                    Toast.makeText(context, "Prvo izaberite karticu za isplatu!", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                if (selectedAmountRsd <= 0) {
                    Toast.makeText(context, "Izaberite iznos veći od 0 RSD!", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                isDispensing = true
                dispensedSuccessAmount = null
                TerminalSoundPlayer.playAtmDispenseSound()

                coroutineScope.launch {
                    delay(1200) // Realistic counting mechanical delay
                    viewModel.withdrawFromAtm(
                        cardId = activeCard.id,
                        rsdAmount = selectedAmountRsd,
                        pin = pinInput
                    ) { success, msg, newRsdBalance ->
                        isDispensing = false
                        if (success) {
                            dispensedSuccessAmount = selectedAmountRsd
                            receiptData = AtmReceiptData(
                                bankName = selectedBankName.ifBlank { "Raiffeisen Bank Beograd" },
                                cardHolder = activeCard.cardHolder,
                                cardNumberMasked = "**** **** **** ${activeCard.cardNumber.takeLast(4)}",
                                withdrawnRsd = selectedAmountRsd,
                                newBalanceRsd = newRsdBalance,
                                timestamp = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault()).format(Date()),
                                terminalId = "ATM-BG-${(1000..9999).random()}"
                            )
                            showReceiptDialog = true
                            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                        } else {
                            TerminalSoundPlayer.playErrorBuzzer()
                            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            },
            enabled = !isDispensing && activeCard != null && selectedAmountRsd > 0,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0D9488) // Teal ATM Action
            )
        ) {
            if (isDispensing) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Isplaćujem gotovinu...", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            } else {
                Icon(Icons.Rounded.LocalAtm, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ISPLATI ${String.format(Locale.US, "%,.0f", selectedAmountRsd)} RSD",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }

    // Modal: Select Card
    if (showCardSelectorModal) {
        Dialog(onDismissRequest = { showCardSelectorModal = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Izaberite karticu za Bankomat:",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    cards.forEach { card ->
                        val rsdEquivalent = if (card.currency == "RSD") card.balance else card.balance * 117.0
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedCardId = card.id
                                    showCardSelectorModal = false
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (card.id == selectedCardId) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(card.cardHolder, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("**** ${card.cardNumber.takeLast(4)}", fontSize = 11.sp, color = Color.Gray)
                                }
                                Text(
                                    text = "${String.format(Locale.US, "%,.2f", rsdEquivalent)} RSD",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    OutlinedButton(
                        onClick = { showCardSelectorModal = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Zatvori")
                    }
                }
            }
        }
    }

    // Modal: Printed ATM Receipt (Bankomatski Isečak)
    if (showReceiptDialog && receiptData != null) {
        val slip = receiptData!!
        Dialog(onDismissRequest = { showReceiptDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)), // Receipt Paper Color
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "================================",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "BANKOMATSKI ISEČAK (ATM SLIP)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = slip.bankName.uppercase(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "================================",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    ReceiptLine("TERMINAL ID:", slip.terminalId)
                    ReceiptLine("DATUM I VREME:", slip.timestamp)
                    ReceiptLine("KARTICA:", slip.cardNumberMasked)
                    ReceiptLine("KORISNIK:", slip.cardHolder)
                    ReceiptLine("VRSTA TRANSAKCIJE:", "ISPLATA GOTOVINE")

                    HorizontalDivider(color = Color.LightGray)

                    ReceiptLine("ISPLAĆEN IZNOS:", "${String.format(Locale.US, "%,.2f", slip.withdrawnRsd)} RSD", isBold = true)
                    ReceiptLine("PROVIZIJA (0%):", "0.00 RSD")
                    ReceiptLine("NOVO STANJE:", "${String.format(Locale.US, "%,.2f", slip.newBalanceRsd)} RSD", isBold = true)

                    Text(
                        text = "--------------------------------",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "HVALA ŠTO KORISTITE NAŠ BANKOMAT!",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { showReceiptDialog = false },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F2937))
                    ) {
                        Text("U redu (Zatvori isečak)", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun ReceiptLine(label: String, value: String, isBold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            color = Color(0xFF4B5563)
        )
        Text(
            text = value,
            fontSize = 10.sp,
            fontWeight = if (isBold) FontWeight.Black else FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = Color.Black
        )
    }
}

data class AtmReceiptData(
    val bankName: String,
    val cardHolder: String,
    val cardNumberMasked: String,
    val withdrawnRsd: Double,
    val newBalanceRsd: Double,
    val timestamp: String,
    val terminalId: String
)
