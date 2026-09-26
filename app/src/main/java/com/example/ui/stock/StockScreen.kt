package com.example.ui.stock

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProductEntity
import com.example.ui.TavernViewModel
import com.example.ui.components.CoreIqBrandingBadge
import com.example.ui.components.CurrencyFormatter
import com.example.ui.components.DualStockBar
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StockScreen(
    viewModel: TavernViewModel
) {
    val products by viewModel.products.collectAsState()
    val discrepancies by viewModel.discrepancies.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    val collapsedCategories = remember { mutableStateMapOf<String, Boolean>() }

    var showDeliveryDialog by remember { mutableStateOf(false) }
    var showPickDialog by remember { mutableStateOf(false) }
    var showEodDialog by remember { mutableStateOf(false) }

    val totalWarehouseCases = products.sumOf { it.warehouseStockCases }
    val totalFloorCases = products.sumOf { it.floorStockCases }
    val lowStockCount = products.count { it.isLowStock }

    val infiniteTransition = rememberInfiniteTransition(label = "pulseStock")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlphaStock"
    )

    val filteredProducts = products.filter {
        searchQuery.isBlank() || it.name.contains(searchQuery, ignoreCase = true) || it.category.contains(searchQuery, ignoreCase = true)
    }

    val groupedProducts = filteredProducts.groupBy { it.category }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PubBackground)
            .testTag("stock_screen")
    ) {
        // Top Header
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
                    text = "Stock Operations",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                CoreIqBrandingBadge(isLight = true)
            }
            Spacer(modifier = Modifier.height(12.dp))

            // Animated Summary Strip (Three Live Tiles)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color.White.copy(alpha = 0.12f),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(text = "WAREHOUSE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9ECEBA))
                        Text(text = "$totalWarehouseCases cases", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color.White.copy(alpha = 0.12f),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(text = "FLOOR", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9ECEBA))
                        Text(text = "$totalFloorCases cases", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (lowStockCount > 0) PubDanger.copy(alpha = 0.35f) else Color.White.copy(alpha = 0.12f),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "LOW STOCK",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (lowStockCount > 0) Color(0xFFFFB4B4) else Color(0xFF9ECEBA),
                            modifier = Modifier.alpha(if (lowStockCount > 0) pulseAlpha else 1f)
                        )
                        Text(
                            text = "$lowStockCount items",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (lowStockCount > 0) Color(0xFFFF6B6B) else Color.White
                        )
                    }
                }
            }
        }

        // Three Big Action Buttons: [Log Delivery] [Pick Stock] [Log Sales]
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { showDeliveryDialog = true },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("action_log_delivery"),
                colors = ButtonDefaults.buttonColors(containerColor = PubPrimary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Delivery", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { showPickDialog = true },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("action_pick_stock"),
                colors = ButtonDefaults.buttonColors(containerColor = PubAmber, contentColor = Color.Black),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.ShoppingBag, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Pick", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { showEodDialog = true },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("action_log_sales"),
                colors = ButtonDefaults.buttonColors(containerColor = PubHeroCard),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.PointOfSale, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Sales", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search beers, spirits, ciders...", fontSize = 14.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = PubTextMuted) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = PubPrimary,
                unfocusedIndicatorColor = PubBorder
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp)
                .testTag("stock_search_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Product List or Empty State
        if (products.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "🍺", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Add your first beer",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PubPrimaryDark
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Start fresh or load the standard Tembisa tavern favorite pack.",
                        fontSize = 13.sp,
                        color = PubTextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.loadStarterPack() },
                        colors = ButtonDefaults.buttonColors(containerColor = PubPrimary)
                    ) {
                        Text("Load Tembisa Favorites Pack")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // SHRINKAGE INSIGHT SECTION
                if (discrepancies.isNotEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF4F4)),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = androidx.compose.ui.graphics.SolidColor(PubDanger.copy(alpha = 0.5f))
                            )
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Warning, contentDescription = null, tint = PubDanger, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "SHRINKAGE DISCREPANCIES (${discrepancies.size})",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PubDanger
                                        )
                                    }
                                    Text(text = "Picks vs POS Sales", fontSize = 11.sp, color = PubTextMuted)
                                }
                                Spacer(modifier = Modifier.height(8.dp))

                                discrepancies.forEach { disc ->
                                    val timeStr = SimpleDateFormat("HH:mm", Locale.US).format(Date(disc.lastPickTimestamp))
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(text = disc.productName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PubTextPrimary)
                                            Text(
                                                text = "Last pick by ${disc.lastPickBy} at $timeStr",
                                                fontSize = 11.sp,
                                                color = PubTextMuted
                                            )
                                        }
                                        Surface(
                                            color = PubDanger,
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = "-${disc.missingUnits} bottles",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Grouped Categories
                groupedProducts.forEach { (category, prods) ->
                    val isCollapsed = collapsedCategories[category] == true

                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    collapsedCategories[category] = !isCollapsed
                                }
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$category (${prods.size})",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = PubPrimaryDark
                            )
                            Icon(
                                imageVector = if (isCollapsed) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                                contentDescription = null,
                                tint = PubTextMuted
                            )
                        }
                    }

                    if (!isCollapsed) {
                        items(prods) { product ->
                            ProductStockCard(product = product)
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }

    // Dialogs
    if (showDeliveryDialog) {
        LogDeliveryDialog(
            products = products,
            onDismiss = { showDeliveryDialog = false },
            onConfirm = { prodId, cases, supplier ->
                viewModel.logDelivery(prodId, cases, supplier)
                showDeliveryDialog = false
            }
        )
    }

    if (showPickDialog) {
        PickStockDialog(
            products = products,
            onDismiss = { showPickDialog = false },
            onConfirm = { prodId, cases, isHelper ->
                viewModel.logPick(prodId, cases, isHelper)
                showPickDialog = false
            }
        )
    }

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

@Composable
fun ProductStockCard(product: ProductEntity) {
    val isLow = product.isLowStock
    val isNearReorder = (product.warehouseStockCases + product.floorStockCases) <= (product.reorderLevelCases + 1)

    val accentColor = when {
        isLow -> PubDanger
        isNearReorder -> PubAmber
        else -> PubPrimaryLight
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("product_stock_card_${product.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Left Accent Bar (Severity indicator)
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .height(130.dp)
                    .background(accentColor)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(14.dp)
            ) {
                // Name & Price
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = product.name,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = PubTextPrimary
                        )
                        Text(
                            text = "${product.supplier} • ${product.caseSizeUnits}s",
                            fontSize = 11.sp,
                            color = PubTextMuted
                        )
                    }
                    Text(
                        text = CurrencyFormatter.formatRand(product.price),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = PubPrimaryDark
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Dual Animated Stock Bars
                DualStockBar(
                    warehouseCases = product.warehouseStockCases,
                    floorCases = product.floorStockCases,
                    floorLoose = product.floorStockLoose,
                    reorderLevel = product.reorderLevelCases,
                    confidence = product.stockConfidence
                )
            }
        }
    }
}
