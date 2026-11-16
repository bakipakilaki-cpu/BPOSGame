package com.example.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun CasinoScreen(viewModel: BraiPayViewModel) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()
    val cards by viewModel.allNfcCards.collectAsState()
    val connectedCardId by viewModel.connectedBankUserCardId.collectAsState()

    // Resilient Card / Player Selection
    val initialCardId = if (connectedCardId > 0 && cards.any { it.id == connectedCardId }) {
        connectedCardId
    } else {
        cards.firstOrNull()?.id ?: 0
    }
    var selectedCardId by remember { mutableStateOf(initialCardId) }

    LaunchedEffect(cards, connectedCardId) {
        if ((selectedCardId <= 0 || cards.none { it.id == selectedCardId }) && cards.isNotEmpty()) {
            selectedCardId = if (connectedCardId > 0 && cards.any { it.id == connectedCardId }) {
                connectedCardId
            } else {
                cards.first().id
            }
        }
    }

    val activeCard = cards.find { it.id == selectedCardId }
    val balanceInRsd = if (activeCard != null) {
        if (activeCard.currency == "RSD") activeCard.balance else activeCard.balance * 117.0
    } else 0.0

    // Bet State
    var betAmountRsd by remember { mutableStateOf(500.0) }
    var customBetInput by remember { mutableStateOf("") }

    // Slot Machine Reels State (3 Reels)
    val symbolsPool = listOf("7️⃣", "💎", "👑", "🔔", "🍇", "🍋", "🍒")
    var reel1 by remember { mutableStateOf("7️⃣") }
    var reel2 by remember { mutableStateOf("7️⃣") }
    var reel3 by remember { mutableStateOf("7️⃣") }

    // Spin Animation State
    var isSpinning by remember { mutableStateOf(false) }
    var lastOutcomeWasWin by remember { mutableStateOf<Boolean?>(null) }
    var lastOutcomeIsJackpot by remember { mutableStateOf(false) }
    var lastOutcomeMessage by remember { mutableStateOf("") }
    var showCardSelectorModal by remember { mutableStateOf(false) }
    var showPayoutInfoModal by remember { mutableStateOf(false) }

    // Statistics Tracker
    var totalGamesPlayed by remember { mutableStateOf(0) }
    var totalWins by remember { mutableStateOf(0) }
    var totalLosses by remember { mutableStateOf(0) }
    var totalProfitLossRsd by remember { mutableStateOf(0.0) }

    // Completely Hidden Cheat Tracking (Every 3rd spin OR 3 secret logo taps = Guaranteed 777 Jackpot)
    var spinCount by remember { mutableStateOf(0) }
    var secretTapsOnLogo by remember { mutableStateOf(0) }
    var isSecretJackpotArmed by remember { mutableStateOf(false) }

    // Subtle Jackpot Glow
    val infiniteTransition = rememberInfiniteTransition(label = "slotGlow")
    val jackpotPulse by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "slotGlow"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Grand Casino Header Banner (Tapping title 3 times secretly arms 777 Jackpot!)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    secretTapsOnLogo++
                    if (secretTapsOnLogo % 3 == 0) {
                        isSecretJackpotArmed = true
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        // Secret subtle haptic only - NO exposed toast/text!
                    }
                },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF13111C)),
            border = BorderStroke(
                1.5.dp,
                Brush.horizontalGradient(listOf(Color(0xFFF59E0B), Color(0xFFEF4444), Color(0xFF8B5CF6)))
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFEAB308)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Rounded.Casino,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "LUCKY 777 SLOTS",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFFBBF24),
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "GRAND CASINO • 10X MEGA JACKPOT",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        }
                    }

                    // Payout info modal button
                    IconButton(
                        onClick = { showPayoutInfoModal = true },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Rounded.Info,
                            contentDescription = "Payout Table",
                            tint = Color(0xFFFBBF24)
                        )
                    }
                }

                HorizontalDivider(color = Color.White.copy(alpha = 0.12f))

                // Prominent Player / Card Selector Card (ALWAYS VISIBLE & NEVER LOST)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showCardSelectorModal = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1D2B)),
                    border = BorderStroke(1.dp, Color(0xFF374151))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF8B5CF6).copy(alpha = 0.25f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Rounded.Person,
                                    contentDescription = null,
                                    tint = Color(0xFFA78BFA),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = activeCard?.cardHolder ?: "Izaberite igrača",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = Color(0xFF3B82F6).copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = "Promeni ⇄",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF60A5FA),
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = if (activeCard != null) "**** ${activeCard.cardNumber.takeLast(4)}" else "Kliknite za izbor kartice",
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color.White.copy(alpha = 0.5f)
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "STANJE ZA IGRU",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.5f)
                            )
                            Text(
                                text = "${String.format(Locale.US, "%,.2f", balanceInRsd)} RSD",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF10B981)
                            )
                        }
                    }
                }
            }
        }

        // Realistic Slot Machine Cabinet (3 Reels Vegas Display)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F0E17)),
            border = BorderStroke(
                3.dp,
                if (lastOutcomeIsJackpot) {
                    Brush.radialGradient(listOf(Color(0xFFFFD700), Color(0xFFEF4444)))
                } else {
                    Brush.verticalGradient(listOf(Color(0xFFD97706), Color(0xFF78350F), Color(0xFF1E1B4B)))
                }
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Top Golden Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "★ ★ ★",
                        color = Color(0xFFFBBF24),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "CLASSIC 3-REEL SLOT MACHINE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFFBBF24),
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "★ ★ ★",
                        color = Color(0xFFFBBF24),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                // The 3 Slots (Reel 1, Reel 2, Reel 3)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF05050A))
                        .border(2.dp, Color(0xFF312E81), RoundedCornerShape(16.dp))
                        .padding(vertical = 18.dp, horizontal = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SlotReelBox(symbol = reel1, isSpinning = isSpinning, modifier = Modifier.weight(1f))
                        SlotReelBox(symbol = reel2, isSpinning = isSpinning, modifier = Modifier.weight(1f))
                        SlotReelBox(symbol = reel3, isSpinning = isSpinning, modifier = Modifier.weight(1f))
                    }

                    // Center Payline Indicator overlay
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                            .padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("▶", fontSize = 12.sp, color = Color(0xFFEF4444), fontWeight = FontWeight.Black)
                        HorizontalDivider(
                            modifier = Modifier.weight(1f).padding(horizontal = 6.dp),
                            color = Color(0xFFEF4444).copy(alpha = 0.35f),
                            thickness = 2.dp
                        )
                        Text("◀", fontSize = 12.sp, color = Color(0xFFEF4444), fontWeight = FontWeight.Black)
                    }
                }

                // Outcome Announcement Banner
                if (lastOutcomeWasWin != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (lastOutcomeIsJackpot) {
                                Color(0xFFB45309)
                            } else if (lastOutcomeWasWin == true) {
                                Color(0xFF065F46)
                            } else {
                                Color(0xFF881337)
                            }
                        ),
                        border = BorderStroke(
                            2.dp,
                            if (lastOutcomeIsJackpot) Color(0xFFFBBF24) else if (lastOutcomeWasWin == true) Color(0xFF34D399) else Color(0xFFFB7185)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (lastOutcomeIsJackpot) {
                                    "🚨🎰 777 MEGA JACKPOT (10X)! 💥"
                                } else if (lastOutcomeWasWin == true) {
                                    "🏆 POGODAK! DOBITAK!"
                                } else {
                                    "💥 VIŠE SREĆE SLEDEĆI PUT"
                                },
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = if (lastOutcomeIsJackpot) Color(0xFFFEF08A) else if (lastOutcomeWasWin == true) Color(0xFF34D399) else Color(0xFFFDA4AF)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = lastOutcomeMessage,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Bet Amount Selector
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Izaberite ulog (Bet in RSD):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Jackpot dobitak: ${String.format(Locale.US, "%,.0f", betAmountRsd * 10)} RSD",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFFBBF24)
                )
            }

            // Quick Bet Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(100.0, 500.0, 1000.0, 2000.0, 5000.0).forEach { chip ->
                    val isChipSelected = betAmountRsd == chip && customBetInput.isEmpty()
                    OutlinedButton(
                        onClick = {
                            betAmountRsd = chip
                            customBetInput = ""
                        },
                        modifier = Modifier.weight(1f).height(38.dp),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isChipSelected) Color(0xFFFBBF24).copy(alpha = 0.2f) else Color.Transparent
                        ),
                        border = BorderStroke(
                            width = if (isChipSelected) 1.5.dp else 1.dp,
                            color = if (isChipSelected) Color(0xFFFBBF24) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                        )
                    ) {
                        Text(
                            text = "${chip.toInt()}",
                            fontSize = 11.sp,
                            fontWeight = if (isChipSelected) FontWeight.Black else FontWeight.Bold
                        )
                    }
                }
            }

            // Custom Bet & All In
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = customBetInput,
                    onValueChange = { input ->
                        customBetInput = input.filter { it.isDigit() }
                        val parsed = customBetInput.toDoubleOrNull()
                        if (parsed != null && parsed > 0) {
                            betAmountRsd = parsed
                        }
                    },
                    label = { Text("Upišite ulog (RSD)") },
                    placeholder = { Text("npr. 2500") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    trailingIcon = { Text("RSD", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 8.dp)) }
                )

                Button(
                    onClick = {
                        if (balanceInRsd > 0) {
                            betAmountRsd = balanceInRsd
                            customBetInput = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.height(52.dp)
                ) {
                    Text("ALL IN", fontWeight = FontWeight.Black, fontSize = 12.sp)
                }
            }
        }

        // Action Button: SPIN SLOT MACHINE (Normal button text - cheat is 100% hidden!)
        Button(
            onClick = {
                if (activeCard == null) {
                    Toast.makeText(context, "Prvo izaberite karticu za igru!", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                if (betAmountRsd <= 0) {
                    Toast.makeText(context, "Ulog mora biti veći od 0 RSD!", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                if (balanceInRsd < betAmountRsd) {
                    TerminalSoundPlayer.playErrorBuzzer()
                    Toast.makeText(context, "Nedovoljno RSD na kartici za ovaj ulog!", Toast.LENGTH_LONG).show()
                    return@Button
                }

                // Completely hidden cheat trigger:
                // Triggers guaranteed 10x 777 Jackpot every 3rd spin OR if 3 secret taps were made!
                val willBeJackpot = ((spinCount + 1) % 3 == 0) || isSecretJackpotArmed
                spinCount++
                isSecretJackpotArmed = false

                isSpinning = true
                lastOutcomeWasWin = null
                lastOutcomeIsJackpot = false

                coroutineScope.launch {
                    try {
                        // Rapid ticking sound and randomized rolling symbols
                        val rollJob = launch {
                            for (t in 1..12) {
                                if (t <= 5) reel1 = symbolsPool.random()
                                if (t <= 8) reel2 = symbolsPool.random()
                                reel3 = symbolsPool.random()
                                TerminalSoundPlayer.playSlotSpinTick()
                                delay(110)
                            }
                        }

                        // Execute backend spin
                        viewModel.playSlotMachine(
                            cardId = activeCard.id,
                            betRsdAmount = betAmountRsd,
                            isJackpot = willBeJackpot
                        ) { success, isWin, symbols, winAmount, newBalance, message ->
                            coroutineScope.launch {
                                rollJob.cancel()

                                // Staggered realistic stopping of each reel
                                reel1 = symbols.getOrElse(0) { "7️⃣" }
                                TerminalSoundPlayer.playReelStopSound()
                                delay(350)

                                reel2 = symbols.getOrElse(1) { "7️⃣" }
                                TerminalSoundPlayer.playReelStopSound()
                                delay(450)

                                reel3 = symbols.getOrElse(2) { "7️⃣" }
                                TerminalSoundPlayer.playReelStopSound()
                                delay(200)

                                isSpinning = false

                                if (success) {
                                    lastOutcomeWasWin = isWin
                                    lastOutcomeIsJackpot = willBeJackpot || (symbols.all { it == "7️⃣" })
                                    lastOutcomeMessage = message

                                    totalGamesPlayed++
                                    if (isWin) {
                                        totalWins++
                                        totalProfitLossRsd += (winAmount - betAmountRsd)
                                        if (lastOutcomeIsJackpot) {
                                            TerminalSoundPlayer.playMegaJackpotFanfare()
                                        } else {
                                            TerminalSoundPlayer.playCasinoWinSound()
                                        }
                                    } else {
                                        totalLosses++
                                        totalProfitLossRsd -= betAmountRsd
                                        TerminalSoundPlayer.playCasinoLossSound()
                                    }
                                } else {
                                    TerminalSoundPlayer.playErrorBuzzer()
                                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    } catch (e: Exception) {
                        isSpinning = false
                        Toast.makeText(context, "Greška: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
                }
            },
            enabled = !isSpinning && activeCard != null && betAmountRsd > 0,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
        ) {
            if (isSpinning) {
                CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp), strokeWidth = 2.5.dp)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "VRTIM BUBNJEVE...",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )
            } else {
                Icon(Icons.Rounded.Casino, contentDescription = null, tint = Color.Black, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ZAVRTI SLOT (${String.format(Locale.US, "%,.0f", betAmountRsd)} RSD)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )
            }
        }

        // Live Statistics & Win Rate Tracker
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📊 STATISTIKA IGRE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Šansa: Fer slot algoritam",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF8B5CF6)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatItem("Odigrano", "$totalGamesPlayed")
                    StatItem("Pobeda", "$totalWins", Color(0xFF10B981))
                    StatItem("Poraza", "$totalLosses", Color(0xFFEF4444))
                    val winRate = if (totalGamesPlayed > 0) (totalWins.toDouble() / totalGamesPlayed * 100.0) else 50.0
                    StatItem("Procenat", "${String.format(Locale.US, "%.1f", winRate)}%")
                    StatItem(
                        "Profit",
                        "${if (totalProfitLossRsd >= 0) "+" else ""}${String.format(Locale.US, "%.0f", totalProfitLossRsd)}",
                        if (totalProfitLossRsd >= 0) Color(0xFF10B981) else Color(0xFFEF4444)
                    )
                }
            }
        }
    }

    // Modal: Select Card / Player (Scrollable & Clean)
    if (showCardSelectorModal) {
        Dialog(onDismissRequest = { showCardSelectorModal = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Izaberite igrača:",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        IconButton(onClick = { showCardSelectorModal = false }) {
                            Icon(Icons.Rounded.Close, contentDescription = "Close")
                        }
                    }

                    if (cards.isEmpty()) {
                        Text(
                            text = "Nema registrovanih kartica. Kreirajte karticu u tabu 'Cards'!",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    cards.forEach { card ->
                        val rsdEquivalent = if (card.currency == "RSD") card.balance else card.balance * 117.0
                        val isSelected = card.id == selectedCardId
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedCardId = card.id
                                    viewModel.setConnectedBankUserCardId(card.id)
                                    showCardSelectorModal = false
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(12.dp),
                            border = if (isSelected) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
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
                                        tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray
                                    )
                                    Column {
                                        Text(card.cardHolder, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("**** ${card.cardNumber.takeLast(4)}", fontSize = 11.sp, color = Color.Gray)
                                    }
                                }
                                Text(
                                    text = "${String.format(Locale.US, "%,.2f", rsdEquivalent)} RSD",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    OutlinedButton(
                        onClick = { showCardSelectorModal = false },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Zatvori")
                    }
                }
            }
        }
    }

    // Modal: Payout Table Information
    if (showPayoutInfoModal) {
        Dialog(onDismissRequest = { showPayoutInfoModal = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "🎰 TABELA ISPLATA (PAYOUTS)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFFBBF24)
                    )

                    PayoutRow("7️⃣ 7️⃣ 7️⃣", "MEGA JACKPOT", "10.0X", Color(0xFFFBBF24))
                    PayoutRow("💎 💎 💎", "DIAMOND WIN", "5.0X", Color(0xFF38BDF8))
                    PayoutRow("👑 👑 👑", "ROYAL CROWN", "3.5X", Color(0xFFA78BFA))
                    PayoutRow("🔔 🔔 🔔", "GOLDEN BELL", "3.5X", Color(0xFFFCD34D))
                    PayoutRow("🍇 🍇 🍇", "FRUIT COMBO", "2.5X", Color(0xFF34D399))
                    PayoutRow("DVA ISTA", "MATCH PAIR", "1.5X", Color(0xFFCBD5E1))

                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = { showPayoutInfoModal = false },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Zatvori", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun SlotReelBox(symbol: String, isSpinning: Boolean, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF181824)),
        border = BorderStroke(2.dp, Color(0xFF374151)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = symbol,
                fontSize = 42.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun PayoutRow(combo: String, name: String, multiplier: String, accentColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Black.copy(alpha = 0.2f))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(combo, fontSize = 16.sp)
        Text(name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text(multiplier, fontSize = 13.sp, fontWeight = FontWeight.Black, color = accentColor)
    }
}

@Composable
fun StatItem(label: String, value: String, valueColor: Color = Color.Unspecified) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 9.sp, color = Color.Gray)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = valueColor)
    }
}
