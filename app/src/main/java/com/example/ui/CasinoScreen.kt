package com.example.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

enum class CasinoGameMode {
    ROULETTE_RED_BLACK, // Crveno ili Crno (50/50)
    COIN_FLIP_HEADS_TAILS // Glava ili Pismo (50/50)
}

@Composable
fun CasinoScreen(viewModel: BraiPayViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val cards by viewModel.allNfcCards.collectAsState()
    val connectedCardId by viewModel.connectedBankUserCardId.collectAsState()

    // Card selection
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

    // Casino Game State
    var gameMode by remember { mutableStateOf(CasinoGameMode.ROULETTE_RED_BLACK) }
    var betAmountRsd by remember { mutableStateOf(500.0) }
    var customBetInput by remember { mutableStateOf("") }
    
    // User Choice: true = Red / Heads, false = Black / Tails
    var userChoiceIsPrimary by remember { mutableStateOf(true) }

    // Game Running State
    var isSpinning by remember { mutableStateOf(false) }
    var lastOutcomeIsPrimary by remember { mutableStateOf<Boolean?>(null) }
    var lastOutcomeWasWin by remember { mutableStateOf<Boolean?>(null) }
    var lastOutcomeMessage by remember { mutableStateOf("") }
    var showCardSelectorModal by remember { mutableStateOf(false) }

    // Statistics Tracker
    var totalGamesPlayed by remember { mutableStateOf(0) }
    var totalWins by remember { mutableStateOf(0) }
    var totalLosses by remember { mutableStateOf(0) }
    var totalProfitLossRsd by remember { mutableStateOf(0.0) }
    var currentStreak by remember { mutableStateOf(0) }

    // Visual Animation States
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Spin / Rotation Animation
    val rotationAngle = remember { Animatable(0f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // VIP Casino Header Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF13111C)),
            border = androidx.compose.foundation.BorderStroke(
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
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFEAB308)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Rounded.Casino,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "GRAND CASINO 50/50",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFFBBF24),
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "100% FER ŠANSE • DOUBLE OR NOTHING",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        }
                    }

                    // 50/50 Provably Fair Badge
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFF8B5CF6).copy(alpha = 0.25f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF8B5CF6))
                    ) {
                        Text(
                            text = "50% : 50%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFA78BFA),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                // Active Card Chip Selector in Casino
                if (activeCard != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1F1D2B))
                            .clickable { showCardSelectorModal = true }
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Rounded.AccountBalanceWallet, contentDescription = null, tint = Color(0xFFFBBF24))
                            Column {
                                Text(
                                    text = activeCard.cardHolder,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Klikni za promenu kartice",
                                    fontSize = 10.sp,
                                    color = Color.White.copy(alpha = 0.5f)
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("STANJE U KASINU", fontSize = 8.sp, color = Color.White.copy(alpha = 0.6f))
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

        // Game Mode Tabs: Roulette (Red/Black) vs Coin Toss (Heads/Tails)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { gameMode = CasinoGameMode.ROULETTE_RED_BLACK },
                modifier = Modifier.weight(1f).height(42.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (gameMode == CasinoGameMode.ROULETTE_RED_BLACK) Color(0xFFE11D48) else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Icon(Icons.Rounded.Colorize, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Crveno / Crno", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { gameMode = CasinoGameMode.COIN_FLIP_HEADS_TAILS },
                modifier = Modifier.weight(1f).height(42.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (gameMode == CasinoGameMode.COIN_FLIP_HEADS_TAILS) Color(0xFFD97706) else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Icon(Icons.Rounded.MonetizationOn, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Glava / Pismo", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Interactive Gaming Arena (The Wheel / The Coin)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF1E293B))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (gameMode == CasinoGameMode.ROULETTE_RED_BLACK) {
                    Text(
                        text = "EVROPSKI RULET (50/50 ŠANSA)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8),
                        letterSpacing = 1.sp
                    )

                    // Spinning Roulette Visual
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .clip(CircleShape)
                            .border(6.dp, Color(0xFFEAB308), CircleShape)
                            .rotate(rotationAngle.value),
                        contentAlignment = Alignment.Center
                    ) {
                        // Half Red, Half Black Circle
                        Row(modifier = Modifier.fillMaxSize()) {
                            Box(modifier = Modifier.fillMaxHeight().weight(1f).background(Color(0xFFDC2626)))
                            Box(modifier = Modifier.fillMaxHeight().weight(1f).background(Color(0xFF18181B)))
                        }
                        // Center Roulette Ball / Needle
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFBBF24))
                                .border(2.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("50", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color.Black)
                        }
                    }

                    // Pick: RED vs BLACK
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // RED OPTION
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { userChoiceIsPrimary = true },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (userChoiceIsPrimary) Color(0xFFDC2626) else Color(0xFF7F1D1D).copy(alpha = 0.5f)
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (userChoiceIsPrimary) 3.dp else 1.dp,
                                color = if (userChoiceIsPrimary) Color(0xFFFBBF24) else Color.Transparent
                            )
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("🔴 CRVENO", fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color.White)
                                Text("Isplata 2x (100%)", fontSize = 10.sp, color = Color.White.copy(alpha = 0.8f))
                            }
                        }

                        // BLACK OPTION
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { userChoiceIsPrimary = false },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (!userChoiceIsPrimary) Color(0xFF27272A) else Color(0xFF18181B).copy(alpha = 0.5f)
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (!userChoiceIsPrimary) 3.dp else 1.dp,
                                color = if (!userChoiceIsPrimary) Color(0xFFFBBF24) else Color.Transparent
                            )
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("⚫ CRNO", fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color.White)
                                Text("Isplata 2x (100%)", fontSize = 10.sp, color = Color.White.copy(alpha = 0.8f))
                            }
                        }
                    }
                } else {
                    // Coin Flip Mode (Glava ili Pismo)
                    Text(
                        text = "ZLATNI NOVČIĆ (GLAVA ILI PISMO)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8),
                        letterSpacing = 1.sp
                    )

                    // Flipping Coin Visual
                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(Color(0xFFFDE047), Color(0xFFD97706), Color(0xFF78350F))
                                )
                            )
                            .border(4.dp, Color(0xFFFEF08A), CircleShape)
                            .rotate(rotationAngle.value),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (userChoiceIsPrimary) "🪙" else "👑",
                                fontSize = 34.sp
                            )
                            Text(
                                text = if (userChoiceIsPrimary) "GLAVA" else "PISMO",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF451A03)
                            )
                        }
                    }

                    // Pick: GLAVA vs PISMO
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { userChoiceIsPrimary = true },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (userChoiceIsPrimary) Color(0xFFD97706) else Color(0xFF78350F).copy(alpha = 0.4f)
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (userChoiceIsPrimary) 3.dp else 1.dp,
                                color = if (userChoiceIsPrimary) Color(0xFFFBBF24) else Color.Transparent
                            )
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("🪙 GLAVA", fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color.White)
                                Text("Isplata 2x", fontSize = 10.sp, color = Color.White.copy(alpha = 0.8f))
                            }
                        }

                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { userChoiceIsPrimary = false },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (!userChoiceIsPrimary) Color(0xFF4F46E5) else Color(0xFF312E81).copy(alpha = 0.4f)
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (!userChoiceIsPrimary) 3.dp else 1.dp,
                                color = if (!userChoiceIsPrimary) Color(0xFFFBBF24) else Color.Transparent
                            )
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("👑 PISMO", fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color.White)
                                Text("Isplata 2x", fontSize = 10.sp, color = Color.White.copy(alpha = 0.8f))
                            }
                        }
                    }
                }

                // Outcome Announcement Banner
                if (lastOutcomeWasWin != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (lastOutcomeWasWin == true) Color(0xFF065F46) else Color(0xFF881337)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (lastOutcomeWasWin == true) Color(0xFF34D399) else Color(0xFFFB7185)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (lastOutcomeWasWin == true) "🏆 DUPLI DOBITAK! (2X WIN)" else "💥 VIŠE SREĆE SLEDEĆI PUT",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = if (lastOutcomeWasWin == true) Color(0xFF34D399) else Color(0xFFFDA4AF)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = lastOutcomeMessage,
                                fontSize = 11.sp,
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
                    text = "Izaberite ulog (Bet Amount in RSD):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Potencijalni dobitak: ${String.format(Locale.US, "%,.0f", betAmountRsd * 2)} RSD",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF10B981)
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
                        modifier = Modifier.weight(1f).height(36.dp),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isChipSelected) Color(0xFFFBBF24).copy(alpha = 0.2f) else Color.Transparent
                        ),
                        border = androidx.compose.foundation.BorderStroke(
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

            // All In & Custom Bet Row
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
                    label = { Text("Drugi ulog (RSD)") },
                    placeholder = { Text("Upiši ulog") },
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

        // Action Button: PLAY (SPIN / FLIP)
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

                isSpinning = true
                lastOutcomeWasWin = null

                coroutineScope.launch {
                    // Play spin mechanical animation
                    launch {
                        rotationAngle.animateTo(
                            targetValue = rotationAngle.value + 1080f,
                            animationSpec = tween(durationMillis = 1400, easing = FastOutSlowInEasing)
                        )
                    }

                    // Ticking suspense sounds
                    for (i in 1..4) {
                        TerminalSoundPlayer.playSineWave(900.0, 30, 0.4)
                        delay(250)
                    }

                    // Execute Provably Fair 50/50 Logic
                    viewModel.playCasino5050(
                        cardId = activeCard.id,
                        betRsdAmount = betAmountRsd,
                        choiceIsRedOrHeads = userChoiceIsPrimary
                    ) { success, isWin, outcomePrimary, newBalance, message ->
                        isSpinning = false
                        if (success) {
                            lastOutcomeIsPrimary = outcomePrimary
                            lastOutcomeWasWin = isWin
                            lastOutcomeMessage = message

                            totalGamesPlayed++
                            if (isWin) {
                                totalWins++
                                currentStreak = if (currentStreak >= 0) currentStreak + 1 else 1
                                totalProfitLossRsd += betAmountRsd
                                TerminalSoundPlayer.playCasinoWinSound()
                            } else {
                                totalLosses++
                                currentStreak = if (currentStreak <= 0) currentStreak - 1 else -1
                                totalProfitLossRsd -= betAmountRsd
                                TerminalSoundPlayer.playCasinoLossSound()
                            }
                        } else {
                            TerminalSoundPlayer.playErrorBuzzer()
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            },
            enabled = !isSpinning && activeCard != null && betAmountRsd > 0,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFF59E0B) // Gold Casino Play
            )
        ) {
            if (isSpinning) {
                CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                Spacer(modifier = Modifier.width(10.dp))
                Text("VRTIM (50/50)...", fontSize = 15.sp, fontWeight = FontWeight.Black, color = Color.Black)
            } else {
                Icon(Icons.Rounded.Casino, contentDescription = null, tint = Color.Black, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (gameMode == CasinoGameMode.ROULETTE_RED_BLACK) {
                        "ZAVRTI RULET (ULOG ${String.format(Locale.US, "%,.0f", betAmountRsd)} RSD)"
                    } else {
                        "BACI NOVČIĆ (ULOG ${String.format(Locale.US, "%,.0f", betAmountRsd)} RSD)"
                    },
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
                        text = "📊 STATISTIKA IGRE (50/50 PROVERA)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Šansa: tačno 50%",
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

    // Modal: Select Card
    if (showCardSelectorModal) {
        Dialog(onDismissRequest = { showCardSelectorModal = false }) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Izaberite karticu za Kazino:",
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
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
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
}

@Composable
fun StatItem(label: String, value: String, valueColor: Color = Color.Unspecified) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 9.sp, color = Color.Gray)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = valueColor)
    }
}
