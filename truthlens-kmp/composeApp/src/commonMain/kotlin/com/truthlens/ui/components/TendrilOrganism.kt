package com.truthlens.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.truthlens.ui.model.CompanionState
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * TendrilOrganism — Living Botanical Companion Visual
 *
 * In resting/idle state: A serene central sprout/bud with tender leaves and stem.
 * In active states (Speaking, Thinking, Listening, Task): Sprouts flowing organic
 * tendrils that wave, unfurl, and weave gracefully, reflecting the companion's
 * inner thought and speech processes while maintaining the current state colors.
 */
@Composable
fun TendrilOrganism(
    state: CompanionState,
    modifier: Modifier = Modifier,
    size: Dp = 140.dp
) {
    val isAnimated = state == CompanionState.LISTENING ||
            state == CompanionState.THINKING ||
            state == CompanionState.SPEAKING ||
            state == CompanionState.PERFORMING_TASK

    val infiniteTransition = rememberInfiniteTransition()

    // Smooth continuous phase angle for organic flowing movement
    val wavePhase by if (isAnimated) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = (2 * PI).toFloat(),
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = when (state) {
                        CompanionState.SPEAKING -> 2400
                        CompanionState.THINKING -> 3200
                        CompanionState.LISTENING -> 2000
                        CompanionState.PERFORMING_TASK -> 1800
                        else -> 3000
                    },
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Restart
            )
        )
    } else {
        remember { mutableStateOf(0f) }
    }

    // Sprout extension factor (0.0 = resting bud, 1.0 = fully sprouted tendrils)
    val sproutFactor by animateFloatAsState(
        targetValue = if (isAnimated) 1.0f else 0.0f,
        animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing)
    )

    // Breathing pulse for the aura
    val pulseScale by if (isAnimated) {
        infiniteTransition.animateFloat(
            initialValue = 0.94f,
            targetValue = 1.06f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    } else {
        remember { mutableStateOf(1.0f) }
    }

    val primaryColor by animateColorAsState(targetValue = state.primaryColor, animationSpec = tween(450))
    val auraColor by animateColorAsState(targetValue = state.auraColor, animationSpec = tween(450))

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val radius = this.size.minDimension / 2f

            // 1. Soft Ambient Glowing Aura
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        auraColor.copy(alpha = (auraColor.alpha * 1.5f).coerceAtMost(0.45f)),
                        auraColor.copy(alpha = 0.05f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = radius * pulseScale
                ),
                radius = radius * pulseScale,
                center = center
            )

            // 2. Flowing Sprouting Tendrils (when active or transitioning)
            if (sproutFactor > 0.01f) {
                drawFlowingTendrils(
                    center = center,
                    baseRadius = radius * 0.45f,
                    phase = wavePhase,
                    growth = sproutFactor,
                    color = primaryColor,
                    state = state
                )
            }

            // 3. Central Seedling / Leaf / Bud
            drawCentralBud(
                center = center,
                size = radius * 0.55f,
                color = primaryColor,
                growth = sproutFactor,
                phase = wavePhase,
                state = state
            )
        }
    }
}

/**
 * Draws the central living seedling/bud/leaf.
 */
private fun DrawScope.drawCentralBud(
    center: Offset,
    size: Float,
    color: Color,
    growth: Float,
    phase: Float,
    state: CompanionState
) {
    val stemBottom = Offset(center.x, center.y + size * 0.85f)
    val budTip = Offset(
        center.x + if (growth > 0) sin(phase) * 3f else 0f,
        center.y - size * (0.95f + growth * 0.1f)
    )

    // Gentle stem
    val stemPath = Path().apply {
        moveTo(stemBottom.x, stemBottom.y)
        quadraticBezierTo(
            center.x - size * 0.08f,
            center.y + size * 0.35f,
            center.x,
            center.y
        )
    }
    drawPath(
        path = stemPath,
        color = color.copy(alpha = 0.85f),
        style = Stroke(width = 3.5f, cap = StrokeCap.Round)
    )

    // Main Leaf/Bud Body (Bézier leaf shape)
    val leafPath = Path().apply {
        moveTo(center.x, center.y + size * 0.2f)
        // Left leaf curve
        cubicTo(
            center.x - size * 0.55f,
            center.y - size * 0.1f,
            center.x - size * 0.45f,
            center.y - size * 0.65f,
            budTip.x,
            budTip.y
        )
        // Right leaf curve
        cubicTo(
            center.x + size * 0.45f,
            center.y - size * 0.65f,
            center.x + size * 0.55f,
            center.y - size * 0.1f,
            center.x,
            center.y + size * 0.2f
        )
        close()
    }

    // Leaf Fill with organic gradient
    drawPath(
        path = leafPath,
        brush = Brush.verticalGradient(
            colors = listOf(color, color.copy(alpha = 0.75f)),
            startY = budTip.y,
            endY = center.y + size * 0.2f
        )
    )

    // Inner delicate Leaf Vein
    val veinPath = Path().apply {
        moveTo(center.x, center.y + size * 0.15f)
        quadraticBezierTo(
            center.x + if (growth > 0) sin(phase) * 2f else 0f,
            center.y - size * 0.35f,
            budTip.x,
            budTip.y
        )
    }
    drawPath(
        path = veinPath,
        color = Color.White.copy(alpha = 0.45f),
        style = Stroke(width = 2.0f, cap = StrokeCap.Round)
    )

    // Nascent side leaf / budlet on the right
    val sideLeaf = Path().apply {
        moveTo(center.x + 2f, center.y + size * 0.05f)
        cubicTo(
            center.x + size * 0.4f,
            center.y - size * 0.05f,
            center.x + size * 0.5f,
            center.y - size * 0.3f,
            center.x + size * 0.35f,
            center.y - size * 0.45f
        )
        cubicTo(
            center.x + size * 0.2f,
            center.y - size * 0.3f,
            center.x + size * 0.1f,
            center.y - size * 0.1f,
            center.x + 2f,
            center.y + size * 0.05f
        )
        close()
    }
    drawPath(
        path = sideLeaf,
        color = color.copy(alpha = 0.85f)
    )
}

/**
 * Draws the flowing tendrils that sprout outward from the bud when active.
 */
private fun DrawScope.drawFlowingTendrils(
    center: Offset,
    baseRadius: Float,
    phase: Float,
    growth: Float,
    color: Color,
    state: CompanionState
) {
    // 5 sprouting tendrils placed radially around the organism
    val tendrilCount = 5
    val angleStep = (2 * PI / tendrilCount).toFloat()

    for (i in 0 until tendrilCount) {
        val baseAngle = i * angleStep - (PI / 2).toFloat()
        // Wave offset creates natural asynchronous curling
        val wave = sin(phase + i * 1.3f) * 0.35f
        val currentAngle = baseAngle + wave * 0.4f

        val startDist = baseRadius * 0.5f
        val startPoint = Offset(
            center.x + cos(currentAngle) * startDist,
            center.y + sin(currentAngle) * startDist
        )

        val length = (baseRadius * 1.3f + sin(phase * 1.5f + i) * (baseRadius * 0.25f)) * growth

        // Control point 1: initial outward sprout
        val cp1Dist = startDist + length * 0.45f
        val cp1Angle = currentAngle + sin(phase + i) * 0.5f
        val cp1 = Offset(
            center.x + cos(cp1Angle) * cp1Dist,
            center.y + sin(cp1Angle) * cp1Dist
        )

        // Control point 2 & End point: curling spiral tip
        val endAngle = currentAngle + (if (i % 2 == 0) 0.6f else -0.6f) * growth + cos(phase + i) * 0.3f
        val endPoint = Offset(
            center.x + cos(endAngle) * (startDist + length),
            center.y + sin(endAngle) * (startDist + length)
        )

        val cp2 = Offset(
            (cp1.x + endPoint.x) / 2f + cos(phase + i) * (length * 0.2f),
            (cp1.y + endPoint.y) / 2f + sin(phase + i) * (length * 0.2f)
        )

        val tendrilPath = Path().apply {
            moveTo(startPoint.x, startPoint.y)
            cubicTo(cp1.x, cp1.y, cp2.x, cp2.y, endPoint.x, endPoint.y)
        }

        // Draw tendril vine stroke (tapers softly with opacity)
        drawPath(
            path = tendrilPath,
            brush = Brush.radialGradient(
                colors = listOf(
                    color.copy(alpha = 0.9f * growth),
                    color.copy(alpha = 0.4f * growth)
                ),
                center = center,
                radius = baseRadius * 2.2f
            ),
            style = Stroke(
                width = (2.8f * growth).coerceAtLeast(1.2f),
                cap = StrokeCap.Round
            )
        )

        // Delicate curling leaf bud at the tip of each tendril
        drawCircle(
            color = color.copy(alpha = 0.95f * growth),
            radius = (3.2f * growth).coerceAtLeast(1.5f),
            center = endPoint
        )
    }
}
