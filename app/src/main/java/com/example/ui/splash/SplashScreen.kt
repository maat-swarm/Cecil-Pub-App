package com.example.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CoreIqBrandingBadge
import com.example.ui.theme.PubAmber
import com.example.ui.theme.PubGold
import com.example.ui.theme.PubPrimaryDark
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashComplete: () -> Unit
) {
    val liquidFillAnim = remember { Animatable(0f) }
    val foamExpandAnim = remember { Animatable(0f) }
    val logoScaleAnim = remember { Animatable(0.6f) }
    val textFadeAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Step 1: Liquid fills up the beer mug (0 to 1)
        liquidFillAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing)
        )
        // Step 2: Foam puffs up at top
        foamExpandAnim.animateTo(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
        )
        // Step 3: Logo scales in with soft bounce + text fades in
        logoScaleAnim.animateTo(
            targetValue = 1.05f,
            animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f)
        )
        logoScaleAnim.animateTo(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = 0.8f, stiffness = 300f)
        )
        textFadeAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 400)
        )
        // Total duration ~ 2.5s
        delay(600)
        onSplashComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PubPrimaryDark)
            .testTag("splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated Beer Mug Logo
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .scale(logoScaleAnim.value),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    val mugLeft = w * 0.22f
                    val mugTop = h * 0.22f
                    val mugWidth = w * 0.44f
                    val mugHeight = h * 0.58f

                    // Glass Handle (outer and inner arc)
                    val handlePath = Path().apply {
                        moveTo(mugLeft + mugWidth, mugTop + mugHeight * 0.2f)
                        cubicTo(
                            w * 0.88f, mugTop + mugHeight * 0.2f,
                            w * 0.88f, mugTop + mugHeight * 0.75f,
                            mugLeft + mugWidth, mugTop + mugHeight * 0.75f
                        )
                    }
                    drawPath(
                        path = handlePath,
                        color = Color.White.copy(alpha = 0.85f),
                        style = Stroke(width = 8f)
                    )

                    // Amber Liquid filling
                    val fillPercent = liquidFillAnim.value
                    if (fillPercent > 0.05f) {
                        val liquidHeight = (mugHeight - 8f) * fillPercent
                        val liquidTop = (mugTop + mugHeight - 4f) - liquidHeight

                        val liquidRect = Path().apply {
                            addRoundRect(
                                RoundRect(
                                    left = mugLeft + 6f,
                                    top = liquidTop,
                                    right = mugLeft + mugWidth - 6f,
                                    bottom = mugTop + mugHeight - 6f,
                                    bottomLeftCornerRadius = CornerRadius(16f, 16f),
                                    bottomRightCornerRadius = CornerRadius(16f, 16f)
                                )
                            )
                        }
                        drawPath(path = liquidRect, color = PubAmber)

                        // Internal bubbles
                        if (fillPercent > 0.4f) {
                            val bubbleColor = PubGold.copy(alpha = 0.8f)
                            drawCircle(bubbleColor, radius = 3.5f, center = Offset(mugLeft + mugWidth * 0.35f, liquidTop + liquidHeight * 0.6f))
                            drawCircle(bubbleColor, radius = 2.5f, center = Offset(mugLeft + mugWidth * 0.65f, liquidTop + liquidHeight * 0.4f))
                            drawCircle(bubbleColor, radius = 4f, center = Offset(mugLeft + mugWidth * 0.5f, liquidTop + liquidHeight * 0.75f))
                        }
                    }

                    // Foam head at top when fill is near complete
                    val foamFactor = foamExpandAnim.value
                    if (foamFactor > 0.01f) {
                        val foamTop = mugTop - 10f * foamFactor
                        val foamColor = Color.White

                        // Frothy foam bubbles
                        drawCircle(foamColor, radius = 14f * foamFactor, center = Offset(mugLeft + mugWidth * 0.25f, foamTop + 6f))
                        drawCircle(foamColor, radius = 17f * foamFactor, center = Offset(mugLeft + mugWidth * 0.52f, foamTop + 4f))
                        drawCircle(foamColor, radius = 15f * foamFactor, center = Offset(mugLeft + mugWidth * 0.78f, foamTop + 7f))
                        drawCircle(foamColor, radius = 11f * foamFactor, center = Offset(mugLeft + mugWidth * 0.94f, foamTop + 14f))
                    }

                    // Glass Mug Outer Outline
                    drawRoundRect(
                        color = Color.White,
                        topLeft = Offset(mugLeft, mugTop),
                        size = Size(mugWidth, mugHeight),
                        cornerRadius = CornerRadius(16f, 16f),
                        style = Stroke(width = 8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // App Title & Tagline
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.alpha(textFadeAnim.value)
            ) {
                Text(
                    text = "Cecil's Pub Manager",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = (-0.3).sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Tavern Operations Assistant • Tembisa",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF9ECEBA)
                )
            }
        }

        // Powered by CoreIQ bottom badge
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .alpha(textFadeAnim.value)
                .height(80.dp),
            contentAlignment = Alignment.Center
        ) {
            CoreIqBrandingBadge(isLight = true)
        }
    }
}
