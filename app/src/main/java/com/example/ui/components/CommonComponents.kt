package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PubAmber
import com.example.ui.theme.PubDanger
import com.example.ui.theme.PubGold
import com.example.ui.theme.PubPrimary
import com.example.ui.theme.PubPrimaryDark
import com.example.ui.theme.PubPrimaryLight
import com.example.ui.theme.PubTextMuted
import com.example.ui.theme.PubTextSecondary

@Composable
fun CoreIqBrandingBadge(
    modifier: Modifier = Modifier,
    isLight: Boolean = false
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(if (isLight) PubGold else PubPrimaryLight)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = "POWERED BY COREIQ",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp,
            color = if (isLight) Color.White.copy(alpha = 0.75f) else PubTextMuted
        )
    }
}

@Composable
fun AnimatedTillCounter(
    targetValue: Double,
    modifier: Modifier = Modifier
) {
    var animatedValue by remember { mutableFloatStateOf(0f) }
    val animatedFloat by animateFloatAsState(
        targetValue = targetValue.toFloat(),
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "tillCounter"
    )

    Text(
        text = CurrencyFormatter.formatRand(animatedFloat.toDouble()),
        style = MaterialTheme.typography.displayLarge.copy(
            color = Color.White,
            fontWeight = FontWeight.Bold
        ),
        modifier = modifier.testTag("till_counter_text")
    )
}

@Composable
fun AmbientHeroShimmer(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "heroShimmer")
    val xOffset by infiniteTransition.animateFloat(
        initialValue = -500f,
        targetValue = 1200f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerOffset"
    )

    Canvas(modifier = modifier) {
        val brush = Brush.linearGradient(
            colors = listOf(
                Color.Transparent,
                Color.White.copy(alpha = 0.04f),
                Color.Transparent
            ),
            start = Offset(xOffset, 0f),
            end = Offset(xOffset + 350f, size.height)
        )
        drawRect(brush = brush)
    }
}

@Composable
fun DualStockBar(
    warehouseCases: Int,
    floorCases: Int,
    floorLoose: Int,
    reorderLevel: Int,
    confidence: String,
    modifier: Modifier = Modifier
) {
    val maxCases = maxOf(8, (warehouseCases + floorCases + 2))
    val warehouseRatio = (warehouseCases.toFloat() / maxCases).coerceIn(0f, 1f)
    val floorRatio = (floorCases.toFloat() / maxCases).coerceIn(0f, 1f)

    val animatedWarehouse by animateFloatAsState(
        targetValue = warehouseRatio,
        animationSpec = tween(500),
        label = "whRatio"
    )
    val animatedFloor by animateFloatAsState(
        targetValue = floorRatio,
        animationSpec = tween(500),
        label = "flRatio"
    )

    val isLow = (warehouseCases + floorCases) <= reorderLevel
    val barColor = when {
        isLow -> PubDanger
        (warehouseCases + floorCases) <= (reorderLevel + 1) -> PubAmber
        else -> PubPrimaryLight
    }

    Column(modifier = modifier) {
        // Warehouse Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "WAREHOUSE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = PubTextMuted
            )
            Text(
                text = "$warehouseCases cases",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color(0xFFE5ECE8))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedWarehouse)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(PubPrimary)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Floor Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "FLOOR (FRIDGES)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = PubTextMuted
                )
                if (confidence == "estimated") {
                    Spacer(modifier = Modifier.width(4.dp))
                    Surface(
                        color = PubAmber.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "≈ Est.",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = PubAmber,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
            }
            Text(
                text = CurrencyFormatter.formatCasesAndLoose(floorCases, floorLoose),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isLow) PubDanger else MaterialTheme.colorScheme.onSurface
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color(0xFFE5ECE8))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedFloor)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(barColor)
            )
        }
    }
}

@Composable
fun SuccessBurstOverlay(
    text: String,
    isVisible: Boolean,
    onDismiss: () -> Unit
) {
    if (isVisible) {
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(1600)
            onDismiss()
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            val scaleAnim = remember { Animatable(0.4f) }
            LaunchedEffect(Unit) {
                scaleAnim.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(350, easing = FastOutSlowInEasing)
                )
            }

            Surface(
                modifier = Modifier
                    .scale(scaleAnim.value)
                    .padding(32.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 16.dp
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 40.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(PubPrimaryLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Success",
                            tint = Color.White,
                            modifier = Modifier.size(44.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                    Text(
                        text = text,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = PubPrimaryDark
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Updated live in Tavern registry",
                        fontSize = 13.sp,
                        color = PubTextSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun PressableCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White,
    content: @Composable () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "cardPressScale"
    )

    Card(
        modifier = modifier
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                        onClick()
                    }
                )
            },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        content()
    }
}
