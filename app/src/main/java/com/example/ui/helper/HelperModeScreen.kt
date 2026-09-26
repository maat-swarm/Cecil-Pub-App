package com.example.ui.helper

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.TavernViewModel
import com.example.ui.components.CoreIqBrandingBadge
import com.example.ui.stock.LogDeliveryDialog
import com.example.ui.stock.PickStockDialog
import com.example.ui.theme.PubAmber
import com.example.ui.theme.PubBackground
import com.example.ui.theme.PubDanger
import com.example.ui.theme.PubHeroCard
import com.example.ui.theme.PubPrimary
import com.example.ui.theme.PubPrimaryDark
import com.example.ui.theme.PubTextMuted
import com.example.ui.theme.PubTextPrimary
import com.example.ui.theme.PubTextSecondary

@Composable
fun HelperModeScreen(
    viewModel: TavernViewModel,
    onExitHelperMode: () -> Unit
) {
    val products by viewModel.products.collectAsState()
    var showDeliveryDialog by remember { mutableStateOf(false) }
    var showPickDialog by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PubBackground)
            .testTag("helper_mode_screen")
    ) {
        // High Contrast Top Banner
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(PubHeroCard)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Helper Mode (Staff)",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = PubAmber
                    )
                    Text(
                        text = "Only Deliveries & Floor Picks enabled. Money hidden.",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
                IconButton(onClick = { showExitDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Exit Helper Mode",
                        tint = PubAmber
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Main 2 Big Action Buttons
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Pick Stock Button
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = PubAmber),
                onClick = { showPickDialog = true }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(44.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "PICK STOCK FOR FLOOR",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "Take cases from Warehouse to Fridges",
                        fontSize = 13.sp,
                        color = Color.Black.copy(alpha = 0.7f)
                    )
                }
            }

            // Log Delivery Button
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = PubPrimary),
                onClick = { showDeliveryDialog = true }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalShipping,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(44.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "LOG NEW DELIVERY",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "SAB or Heineken truck unloaded into warehouse",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            CoreIqBrandingBadge()
        }
    }

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
            onConfirm = { prodId, cases, _ ->
                viewModel.logPick(prodId, cases, isHelper = true)
                showPickDialog = false
            }
        )
    }

    // Exit Helper Mode PIN Dialog
    if (showExitDialog) {
        var pinInput by remember { mutableStateOf("") }
        var hasError by remember { mutableStateOf(false) }

        Dialog(onDismissRequest = { showExitDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Owner PIN Required",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PubPrimaryDark
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Enter Cecil's owner PIN to return to full tavern manager.",
                        fontSize = 13.sp,
                        color = PubTextSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = pinInput,
                        onValueChange = {
                            pinInput = it
                            hasError = false
                        },
                        placeholder = { Text("Enter 4-digit PIN") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        isError = hasError,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (hasError) {
                        Text(
                            text = "Incorrect PIN. Default is 1234.",
                            fontSize = 12.sp,
                            color = PubDanger,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showExitDialog = false }) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (viewModel.toggleHelperMode(pinInput)) {
                                    showExitDialog = false
                                    onExitHelperMode()
                                } else {
                                    hasError = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PubPrimary)
                        ) {
                            Text("Unlock")
                        }
                    }
                }
            }
        }
    }
}
