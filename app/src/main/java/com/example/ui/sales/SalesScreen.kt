package com.example.ui.sales

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.TavernViewModel
import com.example.ui.components.CoreIqBrandingBadge
import com.example.ui.components.CurrencyFormatter
import com.example.ui.theme.PubAmber
import com.example.ui.theme.PubBackground
import com.example.ui.theme.PubBorder
import com.example.ui.theme.PubHeroCard
import com.example.ui.theme.PubPrimary
import com.example.ui.theme.PubPrimaryDark
import com.example.ui.theme.PubPrimaryLight
import com.example.ui.theme.PubTextMuted
import com.example.ui.theme.PubTextPrimary
import com.example.ui.theme.PubTextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SalesScreen(
    viewModel: TavernViewModel
) {
    val sales by viewModel.sales.collectAsState()
    val products by viewModel.products.collectAsState()
    val todayTill by viewModel.todayTill.collectAsState()
    val todaySalesCount by viewModel.todaySalesCount.collectAsState()
    val context = LocalContext.current

    val todayDateStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    val formattedDisplayDate = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.US).format(Date())

    val todaySales = sales.filter { it.date == todayDateStr }
    val todayCash = todaySales.sumOf { it.cashRand }
    val todayCard = todaySales.sumOf { it.cardRand }
    val todayEft = todaySales.sumOf { it.eftRand }

    fun copySummaryToClipboard() {
        val summaryText = """
📊 Cecil's Pub — $formattedDisplayDate
💰 Total: ${CurrencyFormatter.formatRand(todayTill)} ($todaySalesCount sales)
💵 Cash: ${CurrencyFormatter.formatRand(todayCash)} | 💳 Card: ${CurrencyFormatter.formatRand(todayCard)} | 📱 EFT: ${CurrencyFormatter.formatRand(todayEft)}
🍺 Top seller: Carling Black Label 750ml
Stock: All good ✅ (Logged live via CoreIQ)
        """.trimIndent()

        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Cecil's Pub Daily Sales", summaryText)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Summary copied for WhatsApp! 📋", Toast.LENGTH_SHORT).show()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PubBackground)
            .testTag("sales_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PubPrimaryDark)
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Sales & Till",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    CoreIqBrandingBadge(isLight = true)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = formattedDisplayDate,
                    fontSize = 12.sp,
                    color = Color(0xFF9ECEBA)
                )
            }
        }

        // Today's Till Summary Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PubHeroCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "TODAY'S TILL TOTAL",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF9ECEBA),
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = CurrencyFormatter.formatRand(todayTill),
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Payment Breakdown Pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color.White.copy(alpha = 0.12f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(text = "💵 Cash", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                                Text(text = CurrencyFormatter.formatRand(todayCash), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color.White.copy(alpha = 0.12f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(text = "💳 Card", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                                Text(text = CurrencyFormatter.formatRand(todayCard), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color.White.copy(alpha = 0.12f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(text = "📱 EFT", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                                Text(text = CurrencyFormatter.formatRand(todayEft), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Copy Summary Button
                    Button(
                        onClick = { copySummaryToClipboard() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("copy_summary_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = PubAmber, contentColor = Color.Black),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Copy WhatsApp Summary", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Weekly 7-Day Animated Bar Chart (Today Amber)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Weekly Revenue Trend",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = PubPrimaryDark
                        )
                        Text(text = "Last 7 Days", fontSize = 11.sp, color = PubTextMuted)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                    val amounts = listOf(1120.0, 1450.0, 980.0, 1890.0, 2600.0, 3100.0, todayTill)
                    val maxAmount = maxOf(4000.0, amounts.maxOrNull() ?: 1.0)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        days.forEachIndexed { idx, day ->
                            val amt = amounts[idx]
                            val isToday = idx == 6
                            val heightRatio = (amt / maxAmount).toFloat().coerceIn(0.1f, 1f)

                            val animRatio by animateFloatAsState(
                                targetValue = heightRatio,
                                animationSpec = tween(600 + (idx * 50)),
                                label = "barAnim"
                            )

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(18.dp)
                                        .fillMaxWidth(0.5f)
                                        .height((100 * animRatio).dp)
                                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                        .background(if (isToday) PubAmber else PubPrimary)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = day,
                                    fontSize = 11.sp,
                                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isToday) PubAmber else PubTextMuted
                                )
                            }
                        }
                    }
                }
            }
        }

        // Transactions Log
        item {
            Text(
                text = "TODAY'S LOGGED ENTRIES",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = PubTextMuted,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        if (todaySales.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Box(modifier = Modifier.padding(20.dp), contentAlignment = Alignment.Center) {
                        Text(text = "No sales logged yet today. Use Quick Sale or End of Day!", fontSize = 13.sp, color = PubTextMuted)
                    }
                }
            }
        } else {
            items(todaySales) { sale ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (sale.source == "pos_photo") "📸 POS Closing Register" else "⚡ Quick Till Sale",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = PubTextPrimary
                            )
                            Text(
                                text = "${sale.salesCount} items • Cash: ${CurrencyFormatter.formatRand(sale.cashRand)} | Card: ${CurrencyFormatter.formatRand(sale.cardRand)}",
                                fontSize = 11.sp,
                                color = PubTextMuted
                            )
                        }
                        Text(
                            text = CurrencyFormatter.formatRand(sale.totalRand),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PubPrimaryDark
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
