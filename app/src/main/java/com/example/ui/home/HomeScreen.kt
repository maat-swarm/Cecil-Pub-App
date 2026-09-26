package com.example.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.TavernViewModel
import com.example.ui.components.AmbientHeroShimmer
import com.example.ui.components.AnimatedTillCounter
import com.example.ui.components.CoreIqBrandingBadge
import com.example.ui.components.CurrencyFormatter
import com.example.ui.stock.EodSalesDialog
import com.example.ui.stock.QuickSaleDialog
import com.example.ui.theme.PubAmber
import com.example.ui.theme.PubBackground
import com.example.ui.theme.PubBorder
import com.example.ui.theme.PubDanger
import com.example.ui.theme.PubGold
import com.example.ui.theme.PubHeroCard
import com.example.ui.theme.PubPrimary
import com.example.ui.theme.PubPrimaryDark
import com.example.ui.theme.PubPrimaryLight
import com.example.ui.theme.PubTextMuted
import com.example.ui.theme.PubTextPrimary
import com.example.ui.theme.PubTextSecondary
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: TavernViewModel,
    onNavigateToStock: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToAi: () -> Unit
) {
    val products by viewModel.products.collectAsState()
    val todayTill by viewModel.todayTill.collectAsState()
    val todaySalesCount by viewModel.todaySalesCount.collectAsState()
    val discrepancies by viewModel.discrepancies.collectAsState()
    val draftOrders by viewModel.draftOrders.collectAsState()
    val settings by viewModel.settings.collectAsState()

    var showQuickSaleDialog by remember { mutableStateOf(false) }
    var showEodDialog by remember { mutableStateOf(false) }

    // Live Clock State
    var currentTimeString by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.US)
        while (true) {
            currentTimeString = timeFormat.format(Date())
            delay(10000)
        }
    }

    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when {
        hour < 12 -> "Good morning, Cecil ☀️"
        hour < 17 -> "Good afternoon, Cecil 🍺"
        else -> "Good evening, Cecil 🌙"
    }

    val totalWarehouseCases = products.sumOf { it.warehouseStockCases }
    val totalFloorCases = products.sumOf { it.floorStockCases }
    val lowStockCount = products.count { it.isLowStock }

    // Infinite red pulse for low stock alert
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PubBackground)
            .padding(horizontal = 16.dp)
            .testTag("home_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            // Header: Greeting + Live Clock
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = greeting,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PubPrimaryDark
                        )
                    )
                    Text(
                        text = "${settings?.address ?: "Skylab St, Tembisa"} • $currentTimeString",
                        style = MaterialTheme.typography.bodyMedium.copy(color = PubTextMuted)
                    )
                }
                CoreIqBrandingBadge()
            }
        }

        // HERO CARD: Today's Till (#111F1A)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp)),
                colors = CardDefaults.cardColors(containerColor = PubHeroCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    AmbientHeroShimmer(modifier = Modifier.matchParentSize())

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "TODAY'S TILL",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp,
                                color = Color(0xFF9ECEBA)
                            )
                            Surface(
                                color = PubAmber.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "$todaySalesCount sales",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = PubAmber,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Animated Till Counter
                        AnimatedTillCounter(targetValue = todayTill)

                        Spacer(modifier = Modifier.height(18.dp))

                        // Action Buttons: [Quick Sale] & [End of Day]
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { showQuickSaleDialog = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("quick_sale_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PubAmber,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.PointOfSale, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "Quick Sale", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }

                            OutlinedButton(
                                onClick = { showEodDialog = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("end_of_day_button"),
                                border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                                    brush = androidx.compose.ui.graphics.SolidColor(Color.White.copy(alpha = 0.6f))
                                ),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(text = "End of Day", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }

        // Live Tiles: Warehouse / Floor / Low-stock
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Warehouse tile
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToStock() },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Icon(
                            imageVector = Icons.Default.LocalShipping,
                            contentDescription = null,
                            tint = PubPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$totalWarehouseCases",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = PubTextPrimary
                        )
                        Text(
                            text = "Warehouse",
                            fontSize = 12.sp,
                            color = PubTextSecondary
                        )
                    }
                }

                // Floor tile
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToStock() },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = null,
                            tint = PubPrimaryLight,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$totalFloorCases",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = PubTextPrimary
                        )
                        Text(
                            text = "Floor Fridges",
                            fontSize = 12.sp,
                            color = PubTextSecondary
                        )
                    }
                }

                // Low Stock tile
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToStock() },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (lowStockCount > 0) Color(0xFFFDE8E8) else Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (lowStockCount > 0) PubDanger else PubTextMuted,
                            modifier = Modifier
                                .size(20.dp)
                                .alpha(if (lowStockCount > 0) pulseAlpha else 1f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$lowStockCount",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (lowStockCount > 0) PubDanger else PubTextPrimary
                        )
                        Text(
                            text = "Low Stock",
                            fontSize = 12.sp,
                            color = if (lowStockCount > 0) PubDanger else PubTextSecondary
                        )
                    }
                }
            }
        }

        // SHRINKAGE / DISCREPANCY ALERT (Owner's Trust Feature)
        if (discrepancies.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToStock() },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF0F0)),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(PubDanger.copy(alpha = 0.5f))
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(PubDanger.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = PubDanger)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Shrinkage Discrepancy (${discrepancies.size})",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = PubDanger
                            )
                            Text(
                                text = "${discrepancies.first().productName}: ~${discrepancies.first().missingUnits} bottles missing after pick by ${discrepancies.first().lastPickBy}.",
                                fontSize = 12.sp,
                                color = PubTextPrimary
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = PubDanger,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Order Day Nudge & Draft Status
        item {
            val totalDraftCases = draftOrders.values.flatten().sumOf { it.selectedCases }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToOrders() },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(PubPrimary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.LocalShipping, contentDescription = null, tint = PubPrimary)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Next Order Day: Monday (SAB)",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = PubTextPrimary
                        )
                        Text(
                            text = if (totalDraftCases > 0) "Draft ready with $totalDraftCases suggested cases. Tap to review." else "Stock is healthy for the weekend crowd.",
                            fontSize = 12.sp,
                            color = PubTextSecondary
                        )
                    }
                    Button(
                        onClick = { onNavigateToOrders() },
                        colors = ButtonDefaults.buttonColors(containerColor = PubPrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Draft", fontSize = 12.sp)
                    }
                }
            }
        }

        // Weekly Streak Line
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8F4)),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(PubPrimaryLight.copy(alpha = 0.3f))
                )
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "💪", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "5-Day Logging Streak",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = PubPrimaryDark
                        )
                        Text(
                            text = "You logged 5 days straight this week — your numbers are true.",
                            fontSize = 12.sp,
                            color = PubTextSecondary
                        )
                    }
                }
            }
        }

        // Ask AI Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToAi() },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = PubPrimaryDark),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "💬", fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Ask Cecil's Operations Partner",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "\"How many Black Labels do I have?\" or \"What must I order?\"",
                            fontSize = 12.sp,
                            color = Color(0xFF9ECEBA)
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = PubAmber
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // Quick Sale Keypad Dialog
    if (showQuickSaleDialog) {
        QuickSaleDialog(
            onDismiss = { showQuickSaleDialog = false },
            onConfirm = { amount, method ->
                viewModel.logQuickSale(amount, method)
                showQuickSaleDialog = false
            }
        )
    }

    // End of Day Dialog
    if (showEodDialog) {
        EodSalesDialog(
            products = products,
            onDismiss = { showEodDialog = false },
            onConfirm = { total, cash, card, eft, items, source ->
                viewModel.logEodSale(total, cash, card, eft, items, source)
                showEodDialog = false
            }
        )
    }
}
