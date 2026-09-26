package com.example.ui.orders

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderItem
import com.example.data.model.TavernSettingsEntity
import com.example.ui.TavernViewModel
import com.example.ui.components.CoreIqBrandingBadge
import com.example.ui.components.CurrencyFormatter
import com.example.ui.theme.PubAmber
import com.example.ui.theme.PubBackground
import com.example.ui.theme.PubBorder
import com.example.ui.theme.PubDanger
import com.example.ui.theme.PubHeroCard
import com.example.ui.theme.PubPrimary
import com.example.ui.theme.PubPrimaryDark
import com.example.ui.theme.PubPrimaryLight
import com.example.ui.theme.PubTextMuted
import com.example.ui.theme.PubTextPrimary
import com.example.ui.theme.PubTextSecondary
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun OrdersScreen(
    viewModel: TavernViewModel
) {
    val draftOrders by viewModel.draftOrders.collectAsState()
    val ordersHistory by viewModel.orders.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val context = LocalContext.current

    var selectedSupplierTab by remember { mutableStateOf("SAB") }

    val sabDrafts = draftOrders["SAB"] ?: emptyList()
    val heinekenDrafts = draftOrders["Heineken"] ?: emptyList()

    val currentItems = if (selectedSupplierTab == "SAB") sabDrafts else heinekenDrafts
    val supplierPhone = if (selectedSupplierTab == "SAB") {
        settings?.supplierSabPhone ?: "011 881 8111"
    } else {
        settings?.supplierHeinekenPhone ?: "011 878 1200"
    }

    val totalEstimate = currentItems.sumOf { it.selectedCases * it.estimatedCasePrice }

    fun sendOrderViaWhatsApp() {
        val activeItems = currentItems.filter { it.selectedCases > 0 }
        if (activeItems.isEmpty()) {
            Toast.makeText(context, "No items selected to order", Toast.LENGTH_SHORT).show()
            return
        }

        val itemsSummary = activeItems.joinToString(", ") { "${it.selectedCases} × ${it.productName}" }
        val address = settings?.address ?: "Skylab Street, Tlamatlama Ext, Tembisa"
        val message = "Hi, Cecil's Pub here. Order: $itemsSummary. Delivery to $address."

        viewModel.saveDraftOrder(selectedSupplierTab, activeItems)

        try {
            val encodedMsg = URLEncoder.encode(message, StandardCharsets.UTF_8.toString())
            val cleanPhone = supplierPhone.replace(" ", "").replace("-", "")
            val formattedPhone = if (cleanPhone.startsWith("0")) "27" + cleanPhone.drop(1) else cleanPhone
            val url = "https://wa.me/$formattedPhone?text=$encodedMsg"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "WhatsApp message generated and saved to orders!", Toast.LENGTH_SHORT).show()
        }
    }

    fun callSupplier() {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$supplierPhone"))
        context.startActivity(intent)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PubBackground)
            .testTag("orders_screen")
    ) {
        // Header
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
                    text = "Orders & Suppliers",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                CoreIqBrandingBadge(isLight = true)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Auto-generated draft orders based on weekly run-rate.",
                fontSize = 12.sp,
                color = Color(0xFF9ECEBA)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Supplier Tabs: SAB vs HEINEKEN
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                listOf("SAB", "Heineken").forEach { supplier ->
                    val isSelected = selectedSupplierTab == supplier
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) PubAmber else Color.White.copy(alpha = 0.15f),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedSupplierTab = supplier }
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = supplier.uppercase(),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.Black else Color.White
                            )
                            val count = if (supplier == "SAB") sabDrafts.size else heinekenDrafts.size
                            Text(
                                text = "$count suggestions",
                                fontSize = 11.sp,
                                color = if (isSelected) Color.Black.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
                // Large Supplier Contact Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
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
                                text = "$selectedSupplierTab Depot",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = PubPrimaryDark
                            )
                            Text(text = "Rep: $supplierPhone", fontSize = 13.sp, color = PubTextMuted)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButton(
                                onClick = { callSupplier() },
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(PubPrimary.copy(alpha = 0.1f))
                            ) {
                                Icon(Icons.Default.Phone, contentDescription = "Call", tint = PubPrimary)
                            }
                            IconButton(
                                onClick = { sendOrderViaWhatsApp() },
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF25D366).copy(alpha = 0.15f))
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "WhatsApp", tint = Color(0xFF1E7E34))
                            }
                        }
                    }
                }
            }

            // Draft Items List
            item {
                Text(
                    text = "SUGGESTED CASES FOR $selectedSupplierTab",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PubTextMuted
                )
            }

            if (currentItems.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "✅", fontSize = 32.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "All $selectedSupplierTab stock is healthy!",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = PubPrimaryDark
                            )
                            Text(
                                text = "Nothing has fallen below reorder levels today.",
                                fontSize = 12.sp,
                                color = PubTextSecondary
                            )
                        }
                    }
                }
            } else {
                items(currentItems) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.productName,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PubTextPrimary
                                    )
                                    Text(
                                        text = item.reason,
                                        fontSize = 12.sp,
                                        color = PubDanger,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Text(
                                    text = CurrencyFormatter.formatRand(item.selectedCases * item.estimatedCasePrice),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PubPrimaryDark
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Quantity Stepper
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Wholesale ~${CurrencyFormatter.formatRand(item.estimatedCasePrice)}/cs",
                                    fontSize = 11.sp,
                                    color = PubTextMuted
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { viewModel.updateDraftQuantity(selectedSupplierTab, item.productId, -1) },
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFEAEFEA))
                                    ) {
                                        Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(16.dp))
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "${item.selectedCases} cs",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PubPrimaryDark
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    IconButton(
                                        onClick = { viewModel.updateDraftQuantity(selectedSupplierTab, item.productId, 1) },
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFEAEFEA))
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                // Total & One-Click Send via WhatsApp Button
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = PubHeroCard)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "ESTIMATED TOTAL:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9ECEBA))
                                Text(
                                    text = CurrencyFormatter.formatRand(totalEstimate),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = { sendOrderViaWhatsApp() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("send_order_whatsapp_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF25D366),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Send Order via WhatsApp", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Past Orders Log
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "RECENT SENT ORDERS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PubTextMuted)
            }

            if (ordersHistory.isEmpty()) {
                item {
                    Text(text = "No sent orders yet.", fontSize = 12.sp, color = PubTextMuted)
                }
            } else {
                items(ordersHistory.take(5)) { ord ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "${ord.supplier} Order", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text(text = "Status: ${ord.status.uppercase()}", fontSize = 11.sp, color = PubPrimaryLight)
                            }
                            Text(
                                text = CurrencyFormatter.formatRand(ord.totalEstimate),
                                fontSize = 14.sp,
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
}
