package com.truthlens.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.truthlens.ui.model.CompanionState
import kotlin.math.*

enum class TendrilStyle {
    RADIAL_CLASSIC,      // 5 bold flowing radial curling tendrils with bud tips
    SPIRAL_FERN,         // Twin high-curling spiral tendrils + nimble side wisps
    CASCADING_WILLOW,    // Sweeping canopy tendrils cascading down both sides with leaf beads
    STARBURST_WHIP,      // 8-point energetic radiating tendrils with golden tips
    HALO_CROWN           // Intertwined looping tendril arcs with twin base leaflets
}

enum class AvatarArchetype(
    val id: String,
    val bodyWidthScale: Float,
    val bodyHeightScale: Float,
    val armLengthScale: Float,
    val armThicknessScale: Float,
    val legLengthScale: Float,
    val legThicknessScale: Float,
    val tendrilStyle: TendrilStyle
) {
    AVATAR_1(
        id = "avatar_1",
        bodyWidthScale = 1.32f,
        bodyHeightScale = 0.86f,
        armLengthScale = 0.72f,
        armThicknessScale = 1.35f,
        legLengthScale = 0.60f,
        legThicknessScale = 1.40f,
        tendrilStyle = TendrilStyle.RADIAL_CLASSIC
    ),
    AVATAR_2(
        id = "avatar_2",
        bodyWidthScale = 0.66f,
        bodyHeightScale = 1.06f,
        armLengthScale = 1.18f,
        armThicknessScale = 0.72f,
        legLengthScale = 1.12f,
        legThicknessScale = 0.75f,
        tendrilStyle = TendrilStyle.SPIRAL_FERN
    ),
    AVATAR_3(
        id = "avatar_3",
        bodyWidthScale = 0.82f,
        bodyHeightScale = 1.38f,
        armLengthScale = 1.25f,
        armThicknessScale = 0.95f,
        legLengthScale = 1.38f,
        legThicknessScale = 1.00f,
        tendrilStyle = TendrilStyle.CASCADING_WILLOW
    ),
    AVATAR_4(
        id = "avatar_4",
        bodyWidthScale = 0.86f,
        bodyHeightScale = 0.88f,
        armLengthScale = 1.78f,
        armThicknessScale = 0.88f,
        legLengthScale = 1.78f,
        legThicknessScale = 0.88f,
        tendrilStyle = TendrilStyle.STARBURST_WHIP
    ),
    AVATAR_5(
        id = "avatar_5",
        bodyWidthScale = 1.14f,
        bodyHeightScale = 1.02f,
        armLengthScale = 1.05f,
        armThicknessScale = 1.18f,
        legLengthScale = 0.92f,
        legThicknessScale = 1.22f,
        tendrilStyle = TendrilStyle.HALO_CROWN
    );

    companion object {
        fun byId(id: String): AvatarArchetype = entries.firstOrNull { it.id == id } ?: AVATAR_1
    }
}

object AvatarManager {
    var selectedArchetype by mutableStateOf(AvatarArchetype.AVATAR_1)
}

/**
 * TendrilOrganism — Iconic Upright Botanical Leaf Companion with Child-like Arms, Legs & Flowing Tendrils.
 * Keeps the tender glowing leaf aesthetic with bold strokes, expressive face on the leaf blade,
 * hand gestures while speaking, wall-leaning pose, tree-branch hanging, and celebration burst.
 */
@Composable
fun TendrilOrganism(
    state: CompanionState,
    modifier: Modifier = Modifier,
    size: Dp = 140.dp,
    archetype: AvatarArchetype = AvatarManager.selectedArchetype,
    interactiveOnTap: Boolean = true
) {
    var tapActionIndex by remember { mutableStateOf(0) }

    val infiniteTransition = rememberInfiniteTransition()

    val animPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (state) {
                    CompanionState.SPEAKING -> 1700
                    CompanionState.THINKING -> 2300
                    CompanionState.LISTENING -> 1900
                    CompanionState.COMPLETED -> 1300
                    else -> 2800
                },
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        )
    )

    val breathScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1350, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val blinkProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 3600
                0.0f at 0
                0.0f at 3300
                1.0f at 3450
                0.0f at 3600
            },
            repeatMode = RepeatMode.Restart
        )
    )

    // Interactive modes triggered by state or tapping the organism on the Chat screen:
    // tapActionIndex: 0 = normal/leaning, 1 = jumping/hopping, 2 = hanging on tree branch, 3 = celebration burst
    val isHanging = state == CompanionState.PERFORMING_TASK || (interactiveOnTap && tapActionIndex == 2)
    val isCelebrating = state == CompanionState.COMPLETED || (interactiveOnTap && tapActionIndex == 3)
    val isHopping = isCelebrating || state == CompanionState.SPEAKING || (interactiveOnTap && tapActionIndex == 1)

    val hopOffset = if (isHopping && !isHanging) {
        abs(sin(animPhase * 2f)) * 9f
    } else {
        sin(animPhase) * 2f
    }

    val primaryColor by animateColorAsState(targetValue = state.primaryColor, animationSpec = tween(400))
    val auraColor by animateColorAsState(targetValue = state.auraColor, animationSpec = tween(400))

    Box(
        modifier = if (interactiveOnTap) {
            modifier
                .size(size)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    tapActionIndex = (tapActionIndex + 1) % 4
                }
        } else {
            modifier.size(size)
        },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val center = Offset(this.size.width / 2f, (this.size.height / 2f) - hopOffset * 0.6f)
            val baseRadius = this.size.minDimension / 2f

            // 1. Soft Ambient Bioluminescent Aura
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        auraColor.copy(alpha = 0.42f),
                        auraColor.copy(alpha = 0.10f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = baseRadius * breathScale * 1.05f
                ),
                radius = baseRadius * breathScale * 1.05f,
                center = center
            )

            // 2. Celebration Burst Particles
            if (isCelebrating) {
                drawCelebrationBurst(
                    center = center,
                    radius = baseRadius * 0.92f,
                    phase = animPhase,
                    color = primaryColor
                )
            }

            // 3. Flowing Tendrils (Unique per Avatar Archetype, bold and tender)
            drawArchetypeTendrils(
                center = center,
                baseRadius = baseRadius * 0.44f,
                phase = animPhase,
                color = primaryColor,
                archetype = archetype
            )

            // 4. Overhead Sprouted Tree Branch (when hanging)
            if (isHanging) {
                drawOverheadTreeBranch(
                    center = center,
                    radius = baseRadius,
                    phase = animPhase,
                    color = primaryColor
                )
            }

            // 5. Child-like Legs & Feet (Jumping, walking, or kicking when hanging)
            drawLeafChildLegs(
                center = center,
                radius = baseRadius,
                phase = animPhase,
                color = primaryColor,
                isHopping = isHopping,
                isHanging = isHanging,
                archetype = archetype
            )

            // 6. Iconic Upright Botanical Leaf Body & Friendly Leaf Face
            drawUprightLeafBodyAndFace(
                center = center,
                radius = baseRadius,
                phase = animPhase,
                breathScale = breathScale,
                blinkProgress = blinkProgress,
                state = state,
                isCelebrating = isCelebrating,
                color = primaryColor,
                archetype = archetype
            )

            // 7. Child-like Arms & Leaf Hands (Explaining with hands, leaning on wall, or hanging)
            drawLeafChildArms(
                center = center,
                radius = baseRadius,
                phase = animPhase,
                state = state,
                color = primaryColor,
                isHanging = isHanging,
                isCelebrating = isCelebrating,
                archetype = archetype
            )
        }
    }
}

/**
 * Draws the iconic upright pointed botanical leaf (from the original Tendrils icon spec)
 * with bold tender gradient fill, central midrib vein, lateral veins, and a warm friendly face.
 */
private fun DrawScope.drawUprightLeafBodyAndFace(
    center: Offset,
    radius: Float,
    phase: Float,
    breathScale: Float,
    blinkProgress: Float,
    state: CompanionState,
    isCelebrating: Boolean,
    color: Color,
    archetype: AvatarArchetype
) {
    val leafW = radius * 0.56f * breathScale * archetype.bodyWidthScale
    val leafH = radius * 0.62f * breathScale * archetype.bodyHeightScale

    val leafBottom = Offset(center.x, center.y + leafH * 0.52f)
    val tipSway = sin(phase) * 3f
    val leafTip = Offset(center.x + tipSway, center.y - leafH * 0.72f)

    val tiltAngle = when (state) {
        CompanionState.THINKING -> sin(phase) * 5f
        CompanionState.LISTENING -> 6f
        else -> sin(phase * 0.5f) * 2f
    }

    rotate(tiltAngle, pivot = center) {
        // Nascent side budlet leaf for AVATAR_1 and AVATAR_5
        if (archetype == AvatarArchetype.AVATAR_1 || archetype == AvatarArchetype.AVATAR_5) {
            val sideLeafLeft = Path().apply {
                moveTo(leafBottom.x - 2f, leafBottom.y - leafH * 0.12f)
                cubicTo(
                    center.x - leafW * 0.95f, leafBottom.y - leafH * 0.22f,
                    center.x - leafW * 1.05f, leafBottom.y - leafH * 0.55f,
                    center.x - leafW * 0.72f, leafBottom.y - leafH * 0.68f
                )
                cubicTo(
                    center.x - leafW * 0.45f, leafBottom.y - leafH * 0.50f,
                    center.x - leafW * 0.25f, leafBottom.y - leafH * 0.25f,
                    leafBottom.x - 2f, leafBottom.y - leafH * 0.12f
                )
                close()
            }
            drawPath(sideLeafLeft, color = color.copy(alpha = 0.78f))
            drawPath(sideLeafLeft, color = Color.White.copy(alpha = 0.35f), style = Stroke(width = 1.6f))

            if (archetype == AvatarArchetype.AVATAR_5) {
                val sideLeafRight = Path().apply {
                    moveTo(leafBottom.x + 2f, leafBottom.y - leafH * 0.12f)
                    cubicTo(
                        center.x + leafW * 0.95f, leafBottom.y - leafH * 0.22f,
                        center.x + leafW * 1.05f, leafBottom.y - leafH * 0.55f,
                        center.x + leafW * 0.72f, leafBottom.y - leafH * 0.68f
                    )
                    cubicTo(
                        center.x + leafW * 0.45f, leafBottom.y - leafH * 0.50f,
                        center.x + leafW * 0.25f, leafBottom.y - leafH * 0.25f,
                        leafBottom.x + 2f, leafBottom.y - leafH * 0.12f
                    )
                    close()
                }
                drawPath(sideLeafRight, color = color.copy(alpha = 0.78f))
                drawPath(sideLeafRight, color = Color.White.copy(alpha = 0.35f), style = Stroke(width = 1.6f))
            }
        }

        // Main Upright Botanical Leaf Blade (Pointed organic leaf tip at top, rounded base at bottom)
        val leafPath = Path().apply {
            moveTo(leafBottom.x, leafBottom.y)
            // Left leaf curve sweeping up to the pointed leaf tip
            cubicTo(
                center.x - leafW * 0.85f, center.y + leafH * 0.28f,
                center.x - leafW * 0.72f, center.y - leafH * 0.38f,
                leafTip.x, leafTip.y
            )
            // Right leaf curve sweeping back down to the leaf base
            cubicTo(
                center.x + leafW * 0.72f, center.y - leafH * 0.38f,
                center.x + leafW * 0.85f, center.y + leafH * 0.28f,
                leafBottom.x, leafBottom.y
            )
            close()
        }

        // Rich tender vertical gradient fill (matches original Tendrils icon aesthetic)
        drawPath(
            path = leafPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    color,
                    color.copy(alpha = 0.82f)
                ),
                startY = leafTip.y,
                endY = leafBottom.y
            )
        )

        // Bold outer leaf stroke
        drawPath(
            path = leafPath,
            color = Color.White.copy(alpha = 0.45f),
            style = Stroke(width = 2.8f, cap = StrokeCap.Round)
        )
        drawPath(
            path = leafPath,
            color = color,
            style = Stroke(width = 1.6f, cap = StrokeCap.Round)
        )

        // Central luminous leaf midrib vein (from bottom stem up to pointed leaf tip)
        val veinPath = Path().apply {
            moveTo(leafBottom.x, leafBottom.y - leafH * 0.06f)
            quadraticBezierTo(
                center.x + tipSway * 0.4f,
                center.y - leafH * 0.15f,
                leafTip.x,
                leafTip.y + leafH * 0.08f
            )
        }
        drawPath(
            path = veinPath,
            color = Color.White.copy(alpha = 0.38f),
            style = Stroke(width = 2.2f, cap = StrokeCap.Round)
        )

        // Upper & lower lateral leaf veins
        drawLine(
            color = Color.White.copy(alpha = 0.28f),
            start = Offset(center.x, center.y - leafH * 0.28f),
            end = Offset(center.x - leafW * 0.34f, center.y - leafH * 0.42f),
            strokeWidth = 1.6f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color.White.copy(alpha = 0.28f),
            start = Offset(center.x, center.y - leafH * 0.28f),
            end = Offset(center.x + leafW * 0.34f, center.y - leafH * 0.42f),
            strokeWidth = 1.6f,
            cap = StrokeCap.Round
        )

        // --- FRIENDLY LEAF FACE (Eyes & Smile integrated naturally on the leaf blade) ---
        val eyeSpacing = (leafW * 0.26f).coerceIn(radius * 0.11f, radius * 0.20f)
        val eyeY = center.y - leafH * 0.04f
        val eyeRadius = (radius * 0.075f).coerceAtLeast(4.5f)

        if (isCelebrating) {
            // Happy joyful curved eyes (^ ^)
            val leftArc = Path().apply {
                moveTo(center.x - eyeSpacing - eyeRadius, eyeY + 2f)
                quadraticBezierTo(center.x - eyeSpacing, eyeY - eyeRadius * 1.3f, center.x - eyeSpacing + eyeRadius, eyeY + 2f)
            }
            val rightArc = Path().apply {
                moveTo(center.x + eyeSpacing - eyeRadius, eyeY + 2f)
                quadraticBezierTo(center.x + eyeSpacing, eyeY - eyeRadius * 1.3f, center.x + eyeSpacing + eyeRadius, eyeY + 2f)
            }
            drawPath(leftArc, color = Color.White, style = Stroke(width = 3.2f, cap = StrokeCap.Round))
            drawPath(rightArc, color = Color.White, style = Stroke(width = 3.2f, cap = StrokeCap.Round))
        } else {
            val openH = (1f - blinkProgress).coerceAtLeast(0.10f)
            val deepForest = Color(0xFF0A2E1D)

            // Left Eye with soft white rim & double anime sparkle
            drawOval(
                color = Color.White.copy(alpha = 0.92f),
                topLeft = Offset(center.x - eyeSpacing - eyeRadius * 1.15f, eyeY - eyeRadius * 1.2f * openH),
                size = Size(eyeRadius * 2.3f, eyeRadius * 2.4f * openH)
            )
            drawOval(
                color = deepForest,
                topLeft = Offset(center.x - eyeSpacing - eyeRadius, eyeY - eyeRadius * openH),
                size = Size(eyeRadius * 2f, eyeRadius * 2.1f * openH)
            )
            if (openH > 0.4f) {
                drawCircle(
                    color = Color.White,
                    radius = eyeRadius * 0.42f,
                    center = Offset(center.x - eyeSpacing - eyeRadius * 0.25f, eyeY - eyeRadius * 0.30f)
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.85f),
                    radius = eyeRadius * 0.20f,
                    center = Offset(center.x - eyeSpacing + eyeRadius * 0.32f, eyeY + eyeRadius * 0.25f)
                )
            }

            // Right Eye with soft white rim & double anime sparkle
            drawOval(
                color = Color.White.copy(alpha = 0.92f),
                topLeft = Offset(center.x + eyeSpacing - eyeRadius * 1.15f, eyeY - eyeRadius * 1.2f * openH),
                size = Size(eyeRadius * 2.3f, eyeRadius * 2.4f * openH)
            )
            drawOval(
                color = deepForest,
                topLeft = Offset(center.x + eyeSpacing - eyeRadius, eyeY - eyeRadius * openH),
                size = Size(eyeRadius * 2f, eyeRadius * 2.1f * openH)
            )
            if (openH > 0.4f) {
                drawCircle(
                    color = Color.White,
                    radius = eyeRadius * 0.42f,
                    center = Offset(center.x + eyeSpacing - eyeRadius * 0.25f, eyeY - eyeRadius * 0.30f)
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.85f),
                    radius = eyeRadius * 0.20f,
                    center = Offset(center.x + eyeSpacing + eyeRadius * 0.32f, eyeY + eyeRadius * 0.25f)
                )
            }
        }

        // Warm rosy cheek glow on the leaf
        drawCircle(
            color = Color(0xFFFFB2B2).copy(alpha = 0.40f),
            radius = eyeRadius * 0.75f,
            center = Offset(center.x - eyeSpacing - eyeRadius * 0.85f, eyeY + eyeRadius * 1.05f)
        )
        drawCircle(
            color = Color(0xFFFFB2B2).copy(alpha = 0.40f),
            radius = eyeRadius * 0.75f,
            center = Offset(center.x + eyeSpacing + eyeRadius * 0.85f, eyeY + eyeRadius * 1.05f)
        )

        // Friendly Smile on the leaf
        val mouthY = eyeY + eyeRadius * 1.45f
        val mouthW = eyeSpacing * 0.58f

        if (state == CompanionState.SPEAKING) {
            val openAmt = (abs(sin(phase * 4f)) * 5f) + 2.5f
            val mouthPath = Path().apply {
                moveTo(center.x - mouthW, mouthY)
                quadraticBezierTo(center.x, mouthY + openAmt + 4f, center.x + mouthW, mouthY)
                quadraticBezierTo(center.x, mouthY + openAmt * 0.3f, center.x - mouthW, mouthY)
            }
            drawPath(mouthPath, color = Color.White)
        } else {
            val smilePath = Path().apply {
                moveTo(center.x - mouthW, mouthY)
                quadraticBezierTo(center.x, mouthY + 6f, center.x + mouthW, mouthY)
            }
            drawPath(smilePath, color = Color.White, style = Stroke(width = 2.8f, cap = StrokeCap.Round))
        }
    }
}

/**
 * Draws the 5 distinct types of bold, tender flowing tendrils around the leaf organism.
 */
private fun DrawScope.drawArchetypeTendrils(
    center: Offset,
    baseRadius: Float,
    phase: Float,
    color: Color,
    archetype: AvatarArchetype
) {
    when (archetype.tendrilStyle) {
        TendrilStyle.RADIAL_CLASSIC -> {
            // 5 Bold Flowing Radial Tendrils (matches original Tendrils icon spec, bolder)
            val tendrilCount = 5
            val angleStep = (2 * PI / tendrilCount).toFloat()

            for (i in 0 until tendrilCount) {
                val baseAngle = i * angleStep - (PI / 2).toFloat()
                val wave = sin(phase + i * 1.3f) * 0.35f
                val currentAngle = baseAngle + wave * 0.4f

                val startDist = baseRadius * 0.45f
                val startPoint = Offset(
                    center.x + cos(currentAngle) * startDist,
                    center.y + sin(currentAngle) * startDist
                )

                val length = baseRadius * 1.25f + sin(phase * 1.5f + i) * (baseRadius * 0.22f)
                val cp1Dist = startDist + length * 0.48f
                val cp1Angle = currentAngle + sin(phase + i) * 0.45f
                val cp1 = Offset(
                    center.x + cos(cp1Angle) * cp1Dist,
                    center.y + sin(cp1Angle) * cp1Dist
                )

                val endAngle = currentAngle + (if (i % 2 == 0) 0.55f else -0.55f) + cos(phase + i) * 0.28f
                val endPoint = Offset(
                    center.x + cos(endAngle) * (startDist + length),
                    center.y + sin(endAngle) * (startDist + length)
                )

                val cp2 = Offset(
                    (cp1.x + endPoint.x) / 2f + cos(phase + i) * (length * 0.18f),
                    (cp1.y + endPoint.y) / 2f + sin(phase + i) * (length * 0.18f)
                )

                val tendrilPath = Path().apply {
                    moveTo(startPoint.x, startPoint.y)
                    cubicTo(cp1.x, cp1.y, cp2.x, cp2.y, endPoint.x, endPoint.y)
                }

                drawPath(
                    path = tendrilPath,
                    color = color.copy(alpha = 0.88f),
                    style = Stroke(width = 3.4f, cap = StrokeCap.Round)
                )
                drawCircle(color = color, radius = 4.2f, center = endPoint)
                drawCircle(color = Color.White.copy(alpha = 0.7f), radius = 1.8f, center = endPoint)
            }
        }

        TendrilStyle.SPIRAL_FERN -> {
            // Twin high-curling spiral fern tendrils + 4 side wisps
            for (dir in listOf(-1f, 1f)) {
                val sway = sin(phase * 1.8f + dir) * 6f
                val start = Offset(center.x + dir * baseRadius * 0.2f, center.y - baseRadius * 0.4f)
                val tip = Offset(center.x + dir * (baseRadius * 1.15f + sway), center.y - baseRadius * 1.35f)
                val path = Path().apply {
                    moveTo(start.x, start.y)
                    cubicTo(
                        start.x + dir * baseRadius * 0.9f, start.y - baseRadius * 0.5f,
                        tip.x + dir * baseRadius * 0.35f, tip.y - baseRadius * 0.3f,
                        tip.x, tip.y
                    )
                }
                drawPath(path, color = color.copy(alpha = 0.90f), style = Stroke(width = 2.8f, cap = StrokeCap.Round))
                drawCircle(color = Color.White, radius = 3.4f, center = tip)
                drawCircle(color = color.copy(alpha = 0.5f), radius = 6.5f, center = tip)
            }
            // Side fern coils
            for (side in listOf(-1f, 1f)) {
                val wispStart = Offset(center.x + side * baseRadius * 0.3f, center.y)
                val wispEnd = Offset(center.x + side * baseRadius * 1.35f, center.y - baseRadius * 0.25f + sin(phase + side) * 6f)
                val wispPath = Path().apply {
                    moveTo(wispStart.x, wispStart.y)
                    quadraticBezierTo(center.x + side * baseRadius * 0.85f, center.y - baseRadius * 0.45f, wispEnd.x, wispEnd.y)
                }
                drawPath(wispPath, color = color.copy(alpha = 0.75f), style = Stroke(width = 2.4f, cap = StrokeCap.Round))
                drawCircle(color = color, radius = 3.2f, center = wispEnd)
            }
        }

        TendrilStyle.CASCADING_WILLOW -> {
            // Tall arching canopy tendrils that cascade down both sides with glowing leaf beads
            for (side in listOf(-1f, 1f)) {
                for (strand in 0..2) {
                    val start = Offset(center.x + side * (4f + strand * 5f), center.y - baseRadius * 0.75f)
                    val sway = sin(phase * 1.3f + strand) * 5f
                    val archX = center.x + side * (baseRadius * (0.75f + strand * 0.28f)) + sway
                    val archY = center.y - baseRadius * (0.95f - strand * 0.15f)
                    val endX = archX + side * 6f
                    val endY = center.y + baseRadius * (0.45f + strand * 0.25f)

                    val path = Path().apply {
                        moveTo(start.x, start.y)
                        cubicTo(archX * 0.5f + start.x * 0.5f, archY - 12f, archX, center.y - 10f, endX, endY)
                    }
                    drawPath(path, color = color.copy(alpha = 0.85f - strand * 0.15f), style = Stroke(width = 2.8f, cap = StrokeCap.Round))
                    drawCircle(color = color, radius = 3.5f, center = Offset(endX, endY))
                    drawCircle(color = Color.White.copy(alpha = 0.65f), radius = 2.2f, center = Offset(archX, (archY + endY) * 0.5f))
                }
            }
        }

        TendrilStyle.STARBURST_WHIP -> {
            // 7 dynamic long radiating tendrils with golden/emerald tips
            val count = 7
            for (i in 0 until count) {
                val angle = (-PI * 0.85 + i * (PI * 1.7 / (count - 1))).toFloat()
                val wave = sin(phase * 2f + i) * 0.22f
                val len = baseRadius * (1.35f + (i % 2) * 0.2f)
                val start = Offset(center.x + cos(angle) * baseRadius * 0.4f, center.y + sin(angle) * baseRadius * 0.4f)
                val cp = Offset(
                    center.x + cos(angle + wave) * (len * 0.65f),
                    center.y + sin(angle + wave) * (len * 0.65f)
                )
                val tip = Offset(
                    center.x + cos(angle - wave * 0.8f) * len,
                    center.y + sin(angle - wave * 0.8f) * len
                )
                val path = Path().apply {
                    moveTo(start.x, start.y)
                    quadraticBezierTo(cp.x, cp.y, tip.x, tip.y)
                }
                drawPath(path, color = color.copy(alpha = 0.86f), style = Stroke(width = 3.0f, cap = StrokeCap.Round))
                drawCircle(color = Color(0xFFFFD54F), radius = 3.8f, center = tip)
            }
        }

        TendrilStyle.HALO_CROWN -> {
            // Intertwined botanical halo crown + 6 outward curling tendrils
            val count = 6
            for (i in 0 until count) {
                val angle = (i * (2 * PI / count)).toFloat() + sin(phase * 0.5f) * 0.15f
                val nextAngle = ((i + 1) * (2 * PI / count)).toFloat() + sin(phase * 0.5f) * 0.15f
                val r = baseRadius * 1.25f
                val p1 = Offset(center.x + cos(angle) * (baseRadius * 0.5f), center.y + sin(angle) * (baseRadius * 0.5f))
                val p2 = Offset(center.x + cos(nextAngle) * r, center.y + sin(nextAngle) * r)
                val ctrl = Offset(
                    center.x + cos((angle + nextAngle) * 0.5f) * (r * 1.25f),
                    center.y + sin((angle + nextAngle) * 0.5f) * (r * 1.25f)
                )
                val path = Path().apply {
                    moveTo(p1.x, p1.y)
                    quadraticBezierTo(ctrl.x, ctrl.y, p2.x, p2.y)
                }
                drawPath(path, color = color.copy(alpha = 0.82f), style = Stroke(width = 3.2f, cap = StrokeCap.Round))
                drawCircle(color = color, radius = 4.0f, center = p2)
                drawCircle(color = Color.White.copy(alpha = 0.8f), radius = 2.0f, center = p2)
            }
        }
    }
}

/**
 * Draws the child-like arms & leaf hands that gesture while speaking,
 * fold a hand & lean on the canvas wall, or reach up to hang on the tree branch.
 */
private fun DrawScope.drawLeafChildArms(
    center: Offset,
    radius: Float,
    phase: Float,
    state: CompanionState,
    color: Color,
    isHanging: Boolean,
    isCelebrating: Boolean,
    archetype: AvatarArchetype
) {
    val armLen = radius * 0.34f * archetype.armLengthScale
    val armThick = 3.6f * archetype.armThicknessScale
    val shoulderY = center.y + radius * 0.05f
    val shoulderSpan = radius * 0.26f * archetype.bodyWidthScale

    val shoulderLeft = Offset(center.x - shoulderSpan, shoulderY)
    val shoulderRight = Offset(center.x + shoulderSpan, shoulderY)

    if (isHanging) {
        val leftHand = Offset(center.x - radius * 0.18f, center.y - radius * 0.44f)
        val rightHand = Offset(center.x + radius * 0.18f, center.y - radius * 0.44f)
        drawCurvedChildArm(shoulderLeft, leftHand, color, armThick, isLeft = true)
        drawCurvedChildArm(shoulderRight, rightHand, color, armThick, isLeft = false)
        drawLeafHand(leftHand, -85f, color, archetype)
        drawLeafHand(rightHand, 85f, color, archetype)
        return
    }

    if (isCelebrating) {
        val leftHand = Offset(shoulderLeft.x - armLen * 0.55f, center.y - armLen * 0.85f)
        val rightHand = Offset(shoulderRight.x + armLen * 0.55f, center.y - armLen * 0.85f)
        drawCurvedChildArm(shoulderLeft, leftHand, color, armThick, isLeft = true)
        drawCurvedChildArm(shoulderRight, rightHand, color, armThick, isLeft = false)
        drawLeafHand(leftHand, -115f, color, archetype)
        drawLeafHand(rightHand, 115f, color, archetype)
        return
    }

    when (state) {
        CompanionState.SPEAKING -> {
            // Explaining with hands as it speaks
            val waveL = sin(phase * 2.5f) * armLen * 0.35f
            val waveR = cos(phase * 2.5f) * armLen * 0.35f

            val leftHand = Offset(shoulderLeft.x - armLen * 0.80f - waveL, shoulderY - waveL)
            val rightHand = Offset(shoulderRight.x + armLen * 0.80f + waveR, shoulderY - waveR)

            drawCurvedChildArm(shoulderLeft, leftHand, color, armThick, isLeft = true)
            drawCurvedChildArm(shoulderRight, rightHand, color, armThick, isLeft = false)
            drawLeafHand(leftHand, -40f + sin(phase * 2.5f) * 20f, color, archetype)
            drawLeafHand(rightHand, 40f - cos(phase * 2.5f) * 20f, color, archetype)
        }

        CompanionState.THINKING -> {
            val cheekHand = Offset(center.x - radius * 0.14f, center.y - radius * 0.02f)
            val restHand = Offset(shoulderRight.x + armLen * 0.55f, shoulderY + armLen * 0.45f)

            drawCurvedChildArm(shoulderLeft, cheekHand, color, armThick, isLeft = true)
            drawCurvedChildArm(shoulderRight, restHand, color, armThick, isLeft = false)
            drawLeafHand(cheekHand, -75f, color, archetype)
            drawLeafHand(restHand, 15f, color, archetype)
        }

        CompanionState.LISTENING -> {
            val earHand = Offset(center.x + shoulderSpan + armLen * 0.35f, center.y - radius * 0.14f)
            val restHand = Offset(shoulderLeft.x - armLen * 0.45f, shoulderY + armLen * 0.45f)

            drawCurvedChildArm(shoulderLeft, restHand, color, armThick, isLeft = true)
            drawCurvedChildArm(shoulderRight, earHand, color, armThick, isLeft = false)
            drawLeafHand(restHand, -15f, color, archetype)
            drawLeafHand(earHand, 60f, color, archetype)
        }

        else -> {
            // Long-limbed avatar folds a hand and leans playfully on the canvas wall
            val leftHand = if (archetype == AvatarArchetype.AVATAR_4) {
                Offset(center.x - radius * 0.84f, shoulderY - radius * 0.08f)
            } else {
                Offset(shoulderLeft.x - armLen * 0.55f, shoulderY + armLen * 0.38f + sin(phase) * 2.5f)
            }
            val rightHand = Offset(shoulderRight.x + armLen * 0.55f, shoulderY + armLen * 0.38f - sin(phase) * 2.5f)

            drawCurvedChildArm(shoulderLeft, leftHand, color, armThick, isLeft = true)
            drawCurvedChildArm(shoulderRight, rightHand, color, armThick, isLeft = false)
            drawLeafHand(leftHand, if (archetype == AvatarArchetype.AVATAR_4) -85f else -25f, color, archetype)
            drawLeafHand(rightHand, 25f, color, archetype)
        }
    }
}

private fun DrawScope.drawCurvedChildArm(
    start: Offset,
    end: Offset,
    color: Color,
    thickness: Float,
    isLeft: Boolean
) {
    val dir = if (isLeft) -1f else 1f
    val elbow = Offset(
        (start.x + end.x) * 0.5f + dir * 6f,
        (start.y + end.y) * 0.5f + 5f
    )
    val path = Path().apply {
        moveTo(start.x, start.y)
        quadraticBezierTo(elbow.x, elbow.y, end.x, end.y)
    }
    drawPath(path, color = color, style = Stroke(width = thickness, cap = StrokeCap.Round))
}

private fun DrawScope.drawLeafHand(position: Offset, angle: Float, color: Color, archetype: AvatarArchetype) {
    rotate(angle, pivot = position) {
        val scale = if (archetype == AvatarArchetype.AVATAR_1 || archetype == AvatarArchetype.AVATAR_5) 1.2f else 1.0f
        val handW = 12f * scale
        val handH = 16f * scale
        val path = Path().apply {
            moveTo(position.x, position.y)
            cubicTo(
                position.x - handW * 0.55f, position.y - handH * 0.35f,
                position.x - handW * 0.45f, position.y - handH,
                position.x, position.y - handH
            )
            cubicTo(
                position.x + handW * 0.45f, position.y - handH,
                position.x + handW * 0.55f, position.y - handH * 0.35f,
                position.x, position.y
            )
            close()
        }
        drawPath(path, color = color)
        drawPath(path, color = Color.White.copy(alpha = 0.45f), style = Stroke(width = 1.3f))
    }
}

/**
 * Draws the child-like legs & root feet that jump around playfully or kick when hanging.
 */
private fun DrawScope.drawLeafChildLegs(
    center: Offset,
    radius: Float,
    phase: Float,
    color: Color,
    isHopping: Boolean,
    isHanging: Boolean,
    archetype: AvatarArchetype
) {
    val legLen = radius * 0.32f * archetype.legLengthScale
    val legThick = 3.8f * archetype.legThicknessScale
    val hipSpacing = (radius * 0.14f * archetype.bodyWidthScale).coerceIn(radius * 0.08f, radius * 0.22f)
    val hipY = center.y + (radius * 0.28f * archetype.bodyHeightScale)

    val liftL = if (isHopping || isHanging) sin(phase * 2.5f).coerceAtLeast(0f) * 6f else 0f
    val liftR = if (isHopping || isHanging) cos(phase * 2.5f).coerceAtLeast(0f) * 6f else 0f

    for (isLeft in listOf(true, false)) {
        val sign = if (isLeft) -1f else 1f
        val lift = if (isLeft) liftL else liftR
        val hip = Offset(center.x + sign * hipSpacing, hipY)
        val knee = Offset(
            hip.x + sign * (if (archetype == AvatarArchetype.AVATAR_4) 7f else 3f),
            hip.y + legLen * 0.5f - lift * 0.5f
        )
        val foot = Offset(
            hip.x + sign * 2f,
            hip.y + legLen - lift
        )

        val legPath = Path().apply {
            moveTo(hip.x, hip.y)
            quadraticBezierTo(knee.x, knee.y, foot.x, foot.y)
        }
        drawPath(legPath, color = color, style = Stroke(width = legThick, cap = StrokeCap.Round))

        // Cute rounded child foot
        val footScale = if (archetype == AvatarArchetype.AVATAR_1) 1.25f else 1.0f
        val footW = 16f * footScale
        val footH = 9f * footScale
        drawOval(
            color = color,
            topLeft = Offset(foot.x - footW * 0.5f + sign * 2f, foot.y - 2f),
            size = Size(footW, footH)
        )
        drawOval(
            color = Color.White.copy(alpha = 0.35f),
            topLeft = Offset(foot.x - footW * 0.38f + sign * 2f, foot.y - 1f),
            size = Size(footW * 0.76f, footH * 0.65f),
            style = Stroke(width = 1.2f)
        )
    }
}

private fun DrawScope.drawOverheadTreeBranch(
    center: Offset,
    radius: Float,
    phase: Float,
    color: Color
) {
    val branchY = center.y - radius * 0.48f
    val path = Path().apply {
        moveTo(center.x - radius * 0.58f, branchY + 8f)
        quadraticBezierTo(center.x, branchY - 10f + sin(phase) * 2f, center.x + radius * 0.58f, branchY + 6f)
    }

    drawPath(path, color = color.copy(alpha = 0.90f), style = Stroke(width = 4.5f, cap = StrokeCap.Round))
    drawCircle(color = color, radius = 4.5f, center = Offset(center.x - radius * 0.30f, branchY - 6f))
    drawCircle(color = color, radius = 5.0f, center = Offset(center.x + radius * 0.24f, branchY - 7f))
}

private fun DrawScope.drawCelebrationBurst(
    center: Offset,
    radius: Float,
    phase: Float,
    color: Color
) {
    val particleCount = 12
    for (i in 0 until particleCount) {
        val angle = (i * (2 * PI / particleCount)).toFloat() + (phase * 0.8f)
        val dist = radius * (0.65f + abs(sin(phase * 2f + i)) * 0.35f)
        val pX = center.x + cos(angle) * dist
        val pY = center.y + sin(angle) * dist

        val particleColor = when (i % 3) {
            0 -> Color(0xFFFFD54F)
            1 -> color
            else -> Color(0xFF80CBC4)
        }

        drawCircle(
            color = particleColor.copy(alpha = 0.88f),
            radius = 3.5f + (i % 2) * 2f,
            center = Offset(pX, pY)
        )
    }
}
