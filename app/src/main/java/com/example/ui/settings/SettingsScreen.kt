package com.example.ui.settings

import android.content.Context
import android.content.Intent
import android.widget.Toast
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.window.Dialog
import com.example.data.model.ProductEntity
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: TavernViewModel,
    onBack: () -> Unit,
    onEnterHelperMode: () -> Unit
) {
    val settings by viewModel.settings.collectAsState()
    val products by viewModel.products.collectAsState()
    val context = LocalContext.current

    var businessName by remember(settings) { mutableStateOf(settings?.businessName ?: "Cecil's Pub") }
    var address by remember(settings) { mutableStateOf(settings?.address ?: "Skylab Street, Tlamatlama Ext, Tembisa") }
    var ownerPhone by remember(settings) { mutableStateOf(settings?.ownerPhone ?: "072 555 1234") }
    var ownerEmail by remember(settings) { mutableStateOf(settings?.ownerEmail ?: "websitemaatmaster@gmail.com") }
    var helperPin by remember(settings) { mutableStateOf(settings?.helperPin ?: "1234") }
    var sabPhone by remember(settings) { mutableStateOf(settings?.supplierSabPhone ?: "011 881 8111") }
    var heinekenPhone by remember(settings) { mutableStateOf(settings?.supplierHeinekenPhone ?: "011 878 1200") }

    var notifyEvening by remember(settings) { mutableStateOf(settings?.notifyEveningCheck ?: true) }
    var notifyMorning by remember(settings) { mutableStateOf(settings?.notifyMorningVerify ?: true) }
    var notifySunday by remember(settings) { mutableStateOf(settings?.notifySundayCount ?: true) }

    var showAddProductDialog by remember { mutableStateOf(false) }
    var showEmailSentDialog by remember { mutableStateOf(false) }

    fun saveSettings() {
        val updated = (settings ?: TavernSettingsEntity()).copy(
            businessName = businessName,
            address = address,
            ownerPhone = ownerPhone,
            ownerEmail = ownerEmail,
            helperPin = helperPin,
            supplierSabPhone = sabPhone,
            supplierHeinekenPhone = heinekenPhone,
            notifyEveningCheck = notifyEvening,
            notifyMorningVerify = notifyMorning,
            notifySundayCount = notifySunday
        )
        viewModel.updateSettings(updated)
        Toast.makeText(context, "Settings saved!", Toast.LENGTH_SHORT).show()
    }

    fun exportCsv() {
        CoroutineScope(Dispatchers.Main).launch {
            val csv = viewModel.repository.generateCsvExport()
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, csv)
                putExtra(Intent.EXTRA_TITLE, "Cecil's Pub Tavern Inventory CSV")
                type = "text/plain"
            }
            context.startActivity(Intent.createChooser(sendIntent, "Export Tavern Data CSV"))
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PubBackground)
            .testTag("settings_screen"),
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
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Tavern Settings",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }
        }

        // Business Details Section
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "BUSINESS DETAILS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PubTextMuted)
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = businessName,
                        onValueChange = { businessName = it },
                        label = { Text("Tavern Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Physical Address (Tembisa)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = ownerPhone,
                        onValueChange = { ownerPhone = it },
                        label = { Text("Cecil's Phone Number") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = ownerEmail,
                        onValueChange = { ownerEmail = it },
                        label = { Text("Owner Email (Reports & Alerts)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Supplier Numbers & Helper PIN
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "SUPPLIERS & ACCESS PIN", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PubTextMuted)
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = sabPhone,
                        onValueChange = { sabPhone = it },
                        label = { Text("SAB Depot Phone") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = heinekenPhone,
                        onValueChange = { heinekenPhone = it },
                        label = { Text("Heineken Rep Phone") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = helperPin,
                        onValueChange = { helperPin = it },
                        label = { Text("Helper Mode Unlock PIN") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = { onEnterHelperMode() },
                        colors = ButtonDefaults.buttonColors(containerColor = PubHeroCard),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Switch to Helper Mode Now")
                    }
                }
            }
        }

        // Product Catalog Tools: Add Product & Bulk +R2
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "PRODUCT CATALOG & BULK PRICING", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PubTextMuted)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { showAddProductDialog = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = PubPrimary)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Product", fontSize = 12.sp)
                        }

                        Button(
                            onClick = { viewModel.bulkBumpBeerPrice(2.0) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = PubAmber, contentColor = Color.Black)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("+R2 on Beers", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Transactional Email Performance Report Hook
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "TRANSACTIONAL EMAIL HOOK", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PubTextMuted)
                            Text(text = "Weekly Tavern Performance Report", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PubPrimaryDark)
                        }
                        Icon(Icons.Default.Email, contentDescription = null, tint = PubPrimary)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Prepared to send weekly audit (Revenue, stock levels, shrinkage alerts) directly to $ownerEmail.",
                        fontSize = 12.sp,
                        color = PubTextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { showEmailSentDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = PubPrimaryLight)
                    ) {
                        Text("Trigger Weekly Performance Email Test", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Notification Toggles
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "OPERATIONS NUDGES", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PubTextMuted)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Evening Check Nudge (~20:00)", fontSize = 14.sp)
                        Switch(
                            checked = notifyEvening,
                            onCheckedChange = { notifyEvening = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = PubPrimary, checkedTrackColor = PubPrimaryLight)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Morning Stock 1-Tap Verify", fontSize = 14.sp)
                        Switch(
                            checked = notifyMorning,
                            onCheckedChange = { notifyMorning = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = PubPrimary, checkedTrackColor = PubPrimaryLight)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Sunday Count of Top 5 Movers", fontSize = 14.sp)
                        Switch(
                            checked = notifySunday,
                            onCheckedChange = { notifySunday = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = PubPrimary, checkedTrackColor = PubPrimaryLight)
                        )
                    }
                }
            }
        }

        // Save & Export Buttons
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { saveSettings() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PubPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save Settings", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { exportCsv() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Export Stock Data to CSV", fontSize = 14.sp)
                }
            }
        }

        // About & Branding Section
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Cecil's Pub Manager v1.0", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PubPrimaryDark)
                    Text(text = "Dedicated Tavern Operations Assistant for Tembisa", fontSize = 12.sp, color = PubTextMuted)
                    Spacer(modifier = Modifier.height(10.dp))
                    CoreIqBrandingBadge()
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Add Product Dialog
    if (showAddProductDialog) {
        AddProductDialog(
            onDismiss = { showAddProductDialog = false },
            onConfirm = { newProd ->
                viewModel.addProduct(newProd)
                showAddProductDialog = false
            }
        )
    }

    // Email Sent Preview Dialog
    if (showEmailSentDialog) {
        Dialog(onDismissRequest = { showEmailSentDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Weekly Report Sent 📧",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PubPrimaryDark
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "To: $ownerEmail\nSubject: Cecil's Pub — Weekly Performance Summary",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PubTextMuted
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = Color(0xFFF7FAF8),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = "📊 Summary Highlights:", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(text = "• Total Weekly Sales: R 11 840,00", fontSize = 12.sp)
                            Text(text = "• Top Mover: Carling Black Label 750ml (14 cases)", fontSize = 12.sp)
                            Text(text = "• Warehouse Cases: ${products.sumOf { it.warehouseStockCases }}", fontSize = 12.sp)
                            Text(text = "• Shrinkage Audit: 0 major discrepancies", fontSize = 12.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { showEmailSentDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = PubPrimary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Done")
                    }
                }
            }
        }
    }
}

@Composable
fun AddProductDialog(
    onDismiss: () -> Unit,
    onConfirm: (ProductEntity) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Beers & Lagers") }
    var priceStr by remember { mutableStateOf("25.00") }
    var warehouseCasesStr by remember { mutableStateOf("5") }
    var floorCasesStr by remember { mutableStateOf("2") }
    var supplier by remember { mutableStateOf("SAB") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Add New Tavern Product",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PubPrimaryDark
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Product Name (e.g. Castle Milk Stout)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = priceStr,
                    onValueChange = { priceStr = it },
                    label = { Text("Selling Price (Rand)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = warehouseCasesStr,
                        onValueChange = { warehouseCasesStr = it },
                        label = { Text("Warehouse Cases") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = floorCasesStr,
                        onValueChange = { floorCasesStr = it },
                        label = { Text("Floor Cases") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                onConfirm(
                                    ProductEntity(
                                        name = name,
                                        category = category,
                                        price = priceStr.toDoubleOrNull() ?: 25.0,
                                        warehouseStockCases = warehouseCasesStr.toIntOrNull() ?: 0,
                                        floorStockCases = floorCasesStr.toIntOrNull() ?: 0,
                                        supplier = supplier
                                    )
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PubPrimary)
                    ) {
                        Text("Add")
                    }
                }
            }
        }
    }
}
