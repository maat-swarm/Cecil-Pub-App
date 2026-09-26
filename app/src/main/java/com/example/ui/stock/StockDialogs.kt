package com.example.ui.stock

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.ProductEntity
import com.example.ui.components.CurrencyFormatter
import com.example.ui.theme.PubAmber
import com.example.ui.theme.PubBorder
import com.example.ui.theme.PubDanger
import com.example.ui.theme.PubPrimary
import com.example.ui.theme.PubPrimaryDark
import com.example.ui.theme.PubPrimaryLight
import com.example.ui.theme.PubTextMuted
import com.example.ui.theme.PubTextPrimary
import com.example.ui.theme.PubTextSecondary

@Composable
fun LogDeliveryDialog(
    products: List<ProductEntity>,
    onDismiss: () -> Unit,
    onConfirm: (productId: Long, cases: Int, supplier: String) -> Unit
) {
    var selectedProductId by remember { mutableStateOf(products.firstOrNull()?.id ?: 0L) }
    var casesCount by remember { mutableIntStateOf(5) }
    var selectedSupplier by remember { mutableStateOf("SAB") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Log Stock Delivery",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = PubPrimaryDark
                        )
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Text(
                    text = "Adds new cases directly into warehouse storage.",
                    fontSize = 13.sp,
                    color = PubTextSecondary
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Supplier Selector
                Text(text = "SUPPLIER", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PubTextMuted)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("SAB", "Heineken", "Other").forEach { supplier ->
                        val isSelected = selectedSupplier == supplier
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) PubPrimaryDark else Color(0xFFF0F4F2),
                            modifier = Modifier.clickable { selectedSupplier = supplier }
                        ) {
                            Text(
                                text = supplier,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else PubTextPrimary,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Product Selector
                Text(text = "SELECT PRODUCT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PubTextMuted)
                Spacer(modifier = Modifier.height(6.dp))
                LazyColumn(modifier = Modifier.height(140.dp)) {
                    items(products) { prod ->
                        val isSelected = prod.id == selectedProductId
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) PubPrimaryLight.copy(alpha = 0.15f) else Color.Transparent)
                                .clickable {
                                    selectedProductId = prod.id
                                    selectedSupplier = prod.supplier
                                }
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = prod.name,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) PubPrimaryDark else PubTextPrimary
                            )
                            Text(
                                text = "${prod.warehouseStockCases} in WH",
                                fontSize = 12.sp,
                                color = PubTextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Quantity Stepper
                Text(text = "CASES RECEIVED", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PubTextMuted)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { if (casesCount > 1) casesCount-- },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEAEFEA))
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease")
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    Text(
                        text = "$casesCount cases",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = PubPrimaryDark
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                    IconButton(
                        onClick = { casesCount++ },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEAEFEA))
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Increase")
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                Button(
                    onClick = { onConfirm(selectedProductId, casesCount, selectedSupplier) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("confirm_delivery_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = PubPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Confirm Delivery (Under 5 taps)", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun PickStockDialog(
    products: List<ProductEntity>,
    onDismiss: () -> Unit,
    onConfirm: (productId: Long, cases: Int, isHelper: Boolean) -> Unit
) {
    val topMovers = products.take(5)
    var selectedProductId by remember { mutableStateOf(products.firstOrNull()?.id ?: 0L) }
    var casesToPick by remember { mutableIntStateOf(1) }
    var pickedByUser by remember { mutableStateOf("Cecil") }

    val selectedProduct = products.find { it.id == selectedProductId }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Pick Stock for Floor",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = PubPrimaryDark
                        )
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Text(
                    text = "Moves cases from Warehouse ➔ Floor fridges. Instant.",
                    fontSize = 13.sp,
                    color = PubTextSecondary
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Quick Top 5 Movers Big Buttons
                Text(text = "TOP MOVERS (1-TAP SELECT)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PubTextMuted)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    topMovers.forEach { p ->
                        val isSelected = p.id == selectedProductId
                        val shortName = p.name.split(" ").take(2).joinToString(" ")
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) PubPrimary else Color(0xFFF1F6F3),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedProductId = p.id }
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = shortName,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else PubTextPrimary,
                                    maxLines = 1
                                )
                                Text(
                                    text = "${p.warehouseStockCases} wh",
                                    fontSize = 10.sp,
                                    color = if (isSelected) Color.White.copy(alpha = 0.8f) else PubTextMuted
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Or select from full list
                Text(text = "OR ALL PRODUCTS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PubTextMuted)
                Spacer(modifier = Modifier.height(4.dp))
                LazyColumn(modifier = Modifier.height(100.dp)) {
                    items(products) { prod ->
                        val isSelected = prod.id == selectedProductId
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) PubPrimaryLight.copy(alpha = 0.15f) else Color.Transparent)
                                .clickable { selectedProductId = prod.id }
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = prod.name,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) PubPrimaryDark else PubTextPrimary
                            )
                            Text(
                                text = "${prod.warehouseStockCases} cases in WH",
                                fontSize = 12.sp,
                                color = PubTextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Case Stepper
                Text(text = "CASES TO PICK", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PubTextMuted)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { if (casesToPick > 1) casesToPick-- },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEAEFEA))
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease")
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    Text(
                        text = "$casesToPick case(s)",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = PubPrimaryDark
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                    IconButton(
                        onClick = { casesToPick++ },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEAEFEA))
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Increase")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Picked by Cecil or Helper
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "PICKED BY", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PubTextMuted)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Cecil", "Helper").forEach { user ->
                            val isSel = pickedByUser == user
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) PubPrimaryDark else Color(0xFFF0F4F2),
                                modifier = Modifier.clickable { pickedByUser = user }
                            ) {
                                Text(
                                    text = user,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.White else PubTextPrimary,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        onConfirm(selectedProductId, casesToPick, pickedByUser == "Helper")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("confirm_pick_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = PubAmber, contentColor = Color.Black),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Picked ✓ (Under 10s)", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun QuickSaleDialog(
    onDismiss: () -> Unit,
    onConfirm: (amount: Double, method: String) -> Unit
) {
    var rawInput by remember { mutableStateOf("") }
    var selectedMethod by remember { mutableStateOf("Cash") }

    val amountValue = (rawInput.toDoubleOrNull() ?: 0.0) / 100.0

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Quick Sale Keypad",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = PubPrimaryDark
                        )
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Display Amount
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = PubPrimaryDark,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 16.dp, horizontal = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "AMOUNT RECEIVED",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF9ECEBA)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = CurrencyFormatter.formatRand(amountValue),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Payment Method Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Cash", "Card", "EFT").forEach { method ->
                        val isSelected = selectedMethod == method
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) PubAmber else Color(0xFFF1F6F3),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedMethod = method }
                        ) {
                            Text(
                                text = method,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.Black else PubTextPrimary,
                                modifier = Modifier.padding(vertical = 10.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Keypad Grid
                val keys = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("C", "0", "⌫")
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    keys.forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            row.forEach { key ->
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFF6F8F7),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .clickable {
                                            when (key) {
                                                "C" -> rawInput = ""
                                                "⌫" -> if (rawInput.isNotEmpty()) rawInput = rawInput.dropLast(1)
                                                else -> if (rawInput.length < 8) rawInput += key
                                            }
                                        }
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = key,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (key == "C" || key == "⌫") PubDanger else PubTextPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        if (amountValue > 0.0) {
                            onConfirm(amountValue, selectedMethod)
                        }
                    },
                    enabled = amountValue > 0.0,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("save_quick_sale_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = PubPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Save Sale to Till", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun EodSalesDialog(
    products: List<ProductEntity>,
    onDismiss: () -> Unit,
    onConfirm: (total: Double, cash: Double, card: Double, eft: Double, items: List<Pair<Long, Int>>, source: String) -> Unit
) {
    var isSimulatingOcr by remember { mutableStateOf(false) }
    var ocrCompleted by remember { mutableStateOf(false) }

    val quantities = remember { mutableStateMapOf<Long, Int>() }

    var cashAmount by remember { mutableDoubleStateOf(1200.0) }
    var cardAmount by remember { mutableDoubleStateOf(850.0) }
    var eftAmount by remember { mutableDoubleStateOf(350.0) }

    // Pre-populate if OCR is triggered
    fun triggerSimulatedAiVision() {
        isSimulatingOcr = true
        // Simulate POS screen reading
        products.take(4).forEachIndexed { index, p ->
            quantities[p.id] = (index + 2) * 6
        }
        cashAmount = 1450.0
        cardAmount = 920.0
        eftAmount = 430.0
        isSimulatingOcr = false
        ocrCompleted = true
    }

    val totalCalculated = quantities.entries.sumOf { entry ->
        val product = products.find { it.id == entry.key }
        (product?.price ?: 0.0) * entry.value
    } + cashAmount + cardAmount + eftAmount

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "End of Day POS Closing",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = PubPrimaryDark
                        )
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Text(
                    text = "Reconciles POS closing totals & subtracts sold units from floor.",
                    fontSize = 12.sp,
                    color = PubTextSecondary
                )

                Spacer(modifier = Modifier.height(14.dp))

                // AI Vision Camera Button
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { triggerSimulatedAiVision() },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F7F5)),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(PubPrimaryLight)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = PubPrimary, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (ocrCompleted) "POS Closing Screen Scanned ✓" else "Photograph POS Closing Screen",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = PubPrimaryDark
                            )
                            Text(
                                text = "CoreIQ Vision AI extracts totals & product units automatically",
                                fontSize = 11.sp,
                                color = PubTextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(text = "UNITS SOLD (CONFIRM / EDIT)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PubTextMuted)
                Spacer(modifier = Modifier.height(6.dp))

                LazyColumn(modifier = Modifier.height(150.dp)) {
                    items(products) { prod ->
                        val qty = quantities[prod.id] ?: 0
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = prod.name, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                Text(
                                    text = "Floor: ${prod.totalFloorBottles} btls left",
                                    fontSize = 11.sp,
                                    color = PubTextMuted
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = {
                                        if (qty > 0) quantities[prod.id] = qty - 1
                                    },
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                                Text(
                                    text = "$qty",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp)
                                )
                                IconButton(
                                    onClick = { quantities[prod.id] = qty + 1 },
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Total Summary
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "TOTAL DAY SALES:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(
                        text = CurrencyFormatter.formatRand(cashAmount + cardAmount + eftAmount),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = PubPrimaryDark
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        val items = quantities.entries.filter { it.value > 0 }.map { Pair(it.key, it.value) }
                        val total = cashAmount + cardAmount + eftAmount
                        onConfirm(
                            total,
                            cashAmount,
                            cardAmount,
                            eftAmount,
                            items,
                            if (ocrCompleted) "pos_photo" else "manual"
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("confirm_eod_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = PubPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Confirm & Close Day", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
