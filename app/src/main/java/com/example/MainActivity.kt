package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.TavernViewModel
import com.example.ui.ai.AiChatScreen
import com.example.ui.components.CoreIqBrandingBadge
import com.example.ui.components.SuccessBurstOverlay
import com.example.ui.helper.HelperModeScreen
import com.example.ui.home.HomeScreen
import com.example.ui.orders.OrdersScreen
import com.example.ui.settings.SettingsScreen
import com.example.ui.splash.SplashScreen
import com.example.ui.stock.StockScreen
import com.example.ui.theme.CecilPubTheme
import com.example.ui.theme.PubAmber
import com.example.ui.theme.PubDanger
import com.example.ui.theme.PubPrimary
import com.example.ui.theme.PubPrimaryDark
import com.example.ui.theme.PubPrimaryLight
import com.example.ui.theme.PubTextMuted
import com.example.ui.theme.PubTextSecondary

enum class AppTab {
    HOME,
    STOCK,
    ORDERS,
    AI
}

class MainActivity : ComponentActivity() {

    private val viewModel: TavernViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CecilPubTheme {
                MainAppContainer(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContainer(viewModel: TavernViewModel) {
    var isSplashDone by remember { mutableStateOf(false) }
    var currentTab by remember { mutableStateOf(AppTab.HOME) }
    var isSettingsOpen by remember { mutableStateOf(false) }

    val isHelperMode by viewModel.isHelperMode.collectAsState()
    val burstMessage by viewModel.burstMessage.collectAsState()
    val products by viewModel.products.collectAsState()
    val discrepancies by viewModel.discrepancies.collectAsState()

    val lowStockCount = products.count { it.isLowStock }

    if (!isSplashDone) {
        SplashScreen(onSplashComplete = { isSplashDone = true })
        return
    }

    if (isHelperMode) {
        HelperModeScreen(
            viewModel = viewModel,
            onExitHelperMode = { viewModel.exitHelperMode() }
        )
        burstMessage?.let { msg ->
            SuccessBurstOverlay(text = msg, isVisible = true, onDismiss = { viewModel.dismissBurst() })
        }
        return
    }

    if (isSettingsOpen) {
        BackHandler { isSettingsOpen = false }
        SettingsScreen(
            viewModel = viewModel,
            onBack = { isSettingsOpen = false },
            onEnterHelperMode = {
                viewModel.toggleHelperMode("1234")
                isSettingsOpen = false
            }
        )
        burstMessage?.let { msg ->
            SuccessBurstOverlay(text = msg, isVisible = true, onDismiss = { viewModel.dismissBurst() })
        }
        return
    }

    BackHandler(enabled = currentTab != AppTab.HOME) {
        currentTab = AppTab.HOME
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Cecil's Pub",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        CoreIqBrandingBadge(isLight = true)
                    }
                },
                actions = {
                    IconButton(
                        onClick = { isSettingsOpen = true },
                        modifier = Modifier.testTag("settings_top_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = PubPrimaryDark
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                // Home Tab
                NavigationBarItem(
                    selected = currentTab == AppTab.HOME,
                    onClick = { currentTab = AppTab.HOME },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PubPrimary,
                        selectedTextColor = PubPrimary,
                        indicatorColor = PubPrimaryLight.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.testTag("nav_tab_home")
                )

                // Stock Tab
                NavigationBarItem(
                    selected = currentTab == AppTab.STOCK,
                    onClick = { currentTab = AppTab.STOCK },
                    icon = {
                        if (lowStockCount > 0 || discrepancies.isNotEmpty()) {
                            BadgedBox(
                                badge = {
                                    Badge(containerColor = PubDanger) {
                                        Text("${lowStockCount + discrepancies.size}")
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Storefront, contentDescription = "Stock")
                            }
                        } else {
                            Icon(Icons.Default.Storefront, contentDescription = "Stock")
                        }
                    },
                    label = { Text("Stock", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PubPrimary,
                        selectedTextColor = PubPrimary,
                        indicatorColor = PubPrimaryLight.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.testTag("nav_tab_stock")
                )

                // Orders Tab
                NavigationBarItem(
                    selected = currentTab == AppTab.ORDERS,
                    onClick = { currentTab = AppTab.ORDERS },
                    icon = { Icon(Icons.Default.LocalShipping, contentDescription = "Orders") },
                    label = { Text("Orders", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PubPrimary,
                        selectedTextColor = PubPrimary,
                        indicatorColor = PubPrimaryLight.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.testTag("nav_tab_orders")
                )

                // AI Tab
                NavigationBarItem(
                    selected = currentTab == AppTab.AI,
                    onClick = { currentTab = AppTab.AI },
                    icon = { Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "AI") },
                    label = { Text("AI Partner", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PubPrimary,
                        selectedTextColor = PubPrimary,
                        indicatorColor = PubPrimaryLight.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.testTag("nav_tab_ai")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = {
                    (fadeIn(animationSpec = androidx.compose.animation.core.tween(200)) +
                            slideInHorizontally(animationSpec = androidx.compose.animation.core.tween(200))) togetherWith
                            (fadeOut(animationSpec = androidx.compose.animation.core.tween(200)) +
                                    slideOutHorizontally(animationSpec = androidx.compose.animation.core.tween(200)))
                },
                label = "tabTransition"
            ) { targetTab ->
                when (targetTab) {
                    AppTab.HOME -> HomeScreen(
                        viewModel = viewModel,
                        onNavigateToStock = { currentTab = AppTab.STOCK },
                        onNavigateToOrders = { currentTab = AppTab.ORDERS },
                        onNavigateToAi = { currentTab = AppTab.AI }
                    )
                    AppTab.STOCK -> StockScreen(viewModel = viewModel)
                    AppTab.ORDERS -> OrdersScreen(viewModel = viewModel)
                    AppTab.AI -> AiChatScreen(viewModel = viewModel)
                }
            }

            // Success Burst Overlay
            burstMessage?.let { msg ->
                SuccessBurstOverlay(
                    text = msg,
                    isVisible = true,
                    onDismiss = { viewModel.dismissBurst() }
                )
            }
        }
    }
}
