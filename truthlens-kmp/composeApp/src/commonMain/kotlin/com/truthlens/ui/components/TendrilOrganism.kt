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
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.truthlens.ui.model.CompanionState
import kotlin.math.*

enum class LeafBodyShape {
    ROUND_HEART_LOTUS,    // Plump wide heart-lotus leaf with rounded cheeks
    TALL_SPEAR_BLADE,     // Tall slender spear/bamboo leaf blade
    THREE_LOBE_CROWN,     // 3-pointed maple/oak crown leaf with wing lobes
    COMPACT_SEED_POD,     // Compact small seed-leaf body with huge long limbs
    MONSTERA_SHIELD       // Broad 5-point scalloped monstera/palm shield leaf
}

enum class TendrilStyle {
    EVENT_SPROUT_BLOOM,   // No tendrils when idle! Sprouts blooming lotus tendrils only on events (speaking/thinking/tap)
    GRAPPLING_HOOKS,      // Curved J-hook & anchor-clasp tendrils
    RARE_HELIX_COILS,     // Rare multi-loop corkscrew spiral spring tendrils
    EVENT_WHIP_HOOKS,     // Dormant when idle! Sprouts long whip-hook tendrils on events
    RARE_LANTERN_HOOKS    // Rare arching crescent-hook tendrils suspending glowing bioluminescent lantern buds
}

enum class AvatarArchetype(
    val id: String,
    val bodyShape: LeafBodyShape,
    val bodyWidthScale: Float,
    val bodyHeightScale: Float,
    val armLengthScale: Float,
    val armThicknessScale: Float,
    val handScale: Float,
    val legLengthScale: Float,
    val legThicknessScale: Float,
    val footScale: Float,
    val tendrilStyle: TendrilStyle,
    val sproutsOnlyOnEvent: Boolean
) {
    // 1. Plump wide heart-lotus leaf, BIG arms & legs, NO tendrils when idle (sprouts only on events!)
    AVATAR_1(
        id = "avatar_1",
        bodyShape = LeafBodyShape.ROUND_HEART_LOTUS,
        bodyWidthScale = 1.28f,
        bodyHeightScale = 0.90f,
        armLengthScale = 0.95f,
        armThicknessScale = 1.65f,
        handScale = 1.45f,
        legLengthScale = 0.82f,
        legThicknessScale = 1.75f,
        footScale = 1.45f,
        tendrilStyle = TendrilStyle.EVENT_SPROUT_BLOOM,
        sproutsOnlyOnEvent = true
    ),
    // 2. Tall slender spear-blade leaf, nimble limbs, Grappling Hook tendrils
    AVATAR_2(
        id = "avatar_2",
        bodyShape = LeafBodyShape.TALL_SPEAR_BLADE,
        bodyWidthScale = 0.64f,
        bodyHeightScale = 1.26f,
        armLengthScale = 1.15f,
        armThicknessScale = 0.80f,
        handScale = 0.90f,
        legLengthScale = 1.22f,
        legThicknessScale = 0.82f,
        footScale = 0.92f,
        tendrilStyle = TendrilStyle.GRAPPLING_HOOKS,
        sproutsOnlyOnEvent = false
    ),
    // 3. Three-lobed crown/maple leaf, athletic limbs, Rare Corkscrew Helix Coils
    AVATAR_3(
        id = "avatar_3",
        bodyShape = LeafBodyShape.THREE_LOBE_CROWN,
        bodyWidthScale = 1.12f,
        bodyHeightScale = 1.05f,
        armLengthScale = 1.10f,
        armThicknessScale = 1.05f,
        handScale = 1.08f,
        legLengthScale = 1.12f,
        legThicknessScale = 1.08f,
        footScale = 1.08f,
        tendrilStyle = TendrilStyle.RARE_HELIX_COILS,
        sproutsOnlyOnEvent = false
    ),
    // 4. Compact small seed-leaf body, HUGE long arms & long legs, Event-only Whip-Hook tendrils
    AVATAR_4(
        id = "avatar_4",
        bodyShape = LeafBodyShape.COMPACT_SEED_POD,
        bodyWidthScale = 0.72f,
        bodyHeightScale = 0.74f,
        armLengthScale = 1.85f,
        armThicknessScale = 1.25f,
        handScale = 1.38f,
        legLengthScale = 1.85f,
        legThicknessScale = 1.25f,
        footScale = 1.35f,
        tendrilStyle = TendrilStyle.EVENT_WHIP_HOOKS,
        sproutsOnlyOnEvent = true
    ),
    // 5. Broad scalloped Monstera/Palm Shield leaf, BIG brawny arms & legs, Rare Crescent-Hook Lantern tendrils
    AVATAR_5(
        id = "avatar_5",
        bodyShape = LeafBodyShape.MONSTERA_SHIELD,
        bodyWidthScale = 1.22f,
        bodyHeightScale = 1.12f,
        armLengthScale = 1.25f,
        armThicknessScale = 1.70f,
        handScale = 1.50f,
        legLengthScale = 1.15f,
        legThicknessScale = 1.70f,
        footScale = 1.50f,
        tendrilStyle = TendrilStyle.RARE_LANTERN_HOOKS,
        sproutsOnlyOnEvent = false
    );

    companion object {
        fun byId(id: String): AvatarArchetype = entries.firstOrNull { it.id == id } ?: AVATAR_1
    }
}

object AvatarManager {
    var selectedArchetype by mutableStateOf(AvatarArchetype.AVATAR_1)
}

/**
 * TendrilOrganism — 5 Radically Distinct Botanical Leaf Companions.
 * Zoomed-in inside the existing Canvas bounds so the character and animations appear larger and bolder
 * without increasing layout height.
 */
@Composable
fun TendrilOrganism(
    state: CompanionState,
    modifier: Modifier = Modifier,
    size: Dp = 140.dp,
    archetype: AvatarArchetype = AvatarManager.selectedArchetype,
    interactiveOnTap: Boolean = true,
    zoomFactor: Float = 1.32f
) {
    var tapActionIndex by remember { mutableStateOf(0) }

    val infiniteTransition = rememberInfiniteTransition()

    val animPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (state) {
                    CompanionState.SPEAKING -> 1600
                    CompanionState.THINKING -> 2200
                    CompanionState.LISTENING -> 1850
                    CompanionState.COMPLETED -> 1250
                    else -> 2700
                },
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        )
    )

    val breathScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.05f,
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
    // tapActionIndex: 0 = normal/leaning, 1 = jumping & sprouting tendrils, 2 = hanging on tree branch, 3 = celebration burst
    val isHanging = state == CompanionState.PERFORMING_TASK || (interactiveOnTap && tapActionIndex == 2)
    val isCelebrating = state == CompanionState.COMPLETED || (interactiveOnTap && tapActionIndex == 3)
    val isHopping = isCelebrating || state == CompanionState.SPEAKING || (interactiveOnTap && tapActionIndex == 1)
    val isEventActive = state != CompanionState.IDLE || tapActionIndex != 0

    // Smoothly animate event-sprouting tendrils from 0f (hidden/bud) to 1f (full bloom/whip)
    val sproutScale by animateFloatAsState(
        targetValue = if (!archetype.sproutsOnlyOnEvent || isEventActive) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    val hopOffset = if (isHopping && !isHanging) {
        abs(sin(animPhase * 2f)) * 8.5f
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
            val canvasCenter = Offset(this.size.width / 2f, this.size.height / 2f)
            val center = Offset(canvasCenter.x, canvasCenter.y - hopOffset * 0.55f)
            val baseRadius = this.size.minDimension / 2f

            // Zoom inside the fixed Canvas bounds so the character and animations are larger and closer
            withTransform({
                scale(scaleX = zoomFactor, scaleY = zoomFactor, pivot = canvasCenter)
            }) {
                // 1. Soft Ambient Bioluminescent Aura
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            auraColor.copy(alpha = 0.42f),
                            auraColor.copy(alpha = 0.12f),
                            Color.Transparent
                        ),
                        center = center,
                        radius = baseRadius * breathScale * 0.90f
                    ),
                    radius = baseRadius * breathScale * 0.90f,
                    center = center
                )

                // 2. Celebration Burst Particles
                if (isCelebrating) {
                    drawCelebrationBurst(
                        center = center,
                        radius = baseRadius * 0.78f,
                        phase = animPhase,
                        color = primaryColor
                    )
                }

                // 3. Distinct Tendrils (Hooks, Rare Coils, Lanterns, or Event-Only Sprouting Tendrils)
                drawArchetypeTendrils(
                    center = center,
                    baseRadius = baseRadius * 0.48f,
                    phase = animPhase,
                    color = primaryColor,
                    archetype = archetype,
                    sproutScale = sproutScale
                )

                // 4. Overhead Sprouted Tree Branch (when hanging)
                if (isHanging) {
                    drawOverheadTreeBranch(
                        center = center,
                        radius = baseRadius * 0.85f,
                        phase = animPhase,
                        color = primaryColor
                    )
                }

                // 5. Child-like Legs & Feet (Big sturdy legs for AVATAR_1/5, long lanky legs for AVATAR_4)
                drawLeafChildLegs(
                    center = center,
                    radius = baseRadius * 0.82f,
                    phase = animPhase,
                    color = primaryColor,
                    isHopping = isHopping,
                    isHanging = isHanging,
                    archetype = archetype
                )

                // 6. Distinct Botanical Leaf Body Shape & Friendly Leaf Face
                drawDistinctLeafBodyAndFace(
                    center = center,
                    radius = baseRadius * 0.88f,
                    phase = animPhase,
                    breathScale = breathScale,
                    blinkProgress = blinkProgress,
                    state = state,
                    isCelebrating = isCelebrating,
                    color = primaryColor,
                    archetype = archetype
                )

                // 7. Child-like Arms & Leaf Hands (Big arms for AVATAR_1/5, long wall-leaning arms for AVATAR_4)
                drawLeafChildArms(
                    center = center,
                    radius = baseRadius * 0.84f,
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
}

/**
 * Draws 5 unmistakably different botanical leaf silhouettes (Round Heart-Lotus, Tall Spear Blade,
 * 3-Lobed Maple Crown, Compact Seed Pod, and Scalloped Monstera Shield) with expressive faces.
 */
private fun DrawScope.drawDistinctLeafBodyAndFace(
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
    val leafW = radius * 0.54f * breathScale * archetype.bodyWidthScale
    val leafH = radius * 0.58f * breathScale * archetype.bodyHeightScale

    val leafBottom = Offset(center.x, center.y + leafH * 0.50f)
    val tipSway = sin(phase) * 3f
    val leafTip = Offset(center.x + tipSway, center.y - leafH * 0.72f)

    val tiltAngle = when (state) {
        CompanionState.THINKING -> sin(phase) * 5f
        CompanionState.LISTENING -> 6f
        else -> sin(phase * 0.5f) * 2f
    }

    rotate(tiltAngle, pivot = center) {
        val leafPath = Path()

        when (archetype.bodyShape) {
            LeafBodyShape.ROUND_HEART_LOTUS -> {
                // Wide, plump, chubby heart-lotus leaf with broad rounded cheeks and cute curled apex
                leafPath.apply {
                    moveTo(leafBottom.x, leafBottom.y)
                    cubicTo(
                        center.x - leafW * 1.15f, center.y + leafH * 0.42f,
                        center.x - leafW * 1.10f, center.y - leafH * 0.48f,
                        leafTip.x, leafTip.y + leafH * 0.08f
                    )
                    cubicTo(
                        center.x + leafW * 1.10f, center.y - leafH * 0.48f,
                        center.x + leafW * 1.15f, center.y + leafH * 0.42f,
                        leafBottom.x, leafBottom.y
                    )
                    close()
                }
            }

            LeafBodyShape.TALL_SPEAR_BLADE -> {
                // Tall, sleek, angular bamboo/spear leaf blade with crisp tapered point
                leafPath.apply {
                    moveTo(leafBottom.x, leafBottom.y + leafH * 0.08f)
                    quadraticBezierTo(
                        center.x - leafW * 0.82f, center.y + leafH * 0.12f,
                        center.x - leafW * 0.55f, center.y - leafH * 0.25f
                    )
                    quadraticBezierTo(
                        center.x - leafW * 0.28f, center.y - leafH * 0.55f,
                        leafTip.x, leafTip.y - leafH * 0.12f
                    )
                    quadraticBezierTo(
                        center.x + leafW * 0.28f, center.y - leafH * 0.55f,
                        center.x + leafW * 0.55f, center.y - leafH * 0.25f
                    )
                    quadraticBezierTo(
                        center.x + leafW * 0.82f, center.y + leafH * 0.12f,
                        leafBottom.x, leafBottom.y + leafH * 0.08f
                    )
                    close()
                }
            }

            LeafBodyShape.THREE_LOBE_CROWN -> {
                // 3-pointed maple/oak crown leaf with distinct left & right side wing lobes
                val leftLobeTip = Offset(center.x - leafW * 0.92f, center.y - leafH * 0.22f)
                val rightLobeTip = Offset(center.x + leafW * 0.92f, center.y - leafH * 0.22f)
                val leftNotch = Offset(center.x - leafW * 0.42f, center.y - leafH * 0.28f)
                val rightNotch = Offset(center.x + leafW * 0.42f, center.y - leafH * 0.28f)

                leafPath.apply {
                    moveTo(leafBottom.x, leafBottom.y)
                    // Lower left curve out to left wing lobe
                    quadraticBezierTo(
                        center.x - leafW * 0.85f, center.y + leafH * 0.30f,
                        leftLobeTip.x, leftLobeTip.y
                    )
                    // Upper edge of left lobe into notch
                    quadraticBezierTo(
                        center.x - leafW * 0.65f, center.y - leafH * 0.36f,
                        leftNotch.x, leftNotch.y
                    )
                    // Center crown peak
                    quadraticBezierTo(
                        center.x - leafW * 0.32f, center.y - leafH * 0.58f,
                        leafTip.x, leafTip.y - leafH * 0.06f
                    )
                    quadraticBezierTo(
                        center.x + leafW * 0.32f, center.y - leafH * 0.58f,
                        rightNotch.x, rightNotch.y
                    )
                    // Right wing lobe
                    quadraticBezierTo(
                        center.x + leafW * 0.65f, center.y - leafH * 0.36f,
                        rightLobeTip.x, rightLobeTip.y
                    )
                    // Lower right curve back to stem base
                    quadraticBezierTo(
                        center.x + leafW * 0.85f, center.y + leafH * 0.30f,
                        leafBottom.x, leafBottom.y
                    )
                    close()
                }
            }

            LeafBodyShape.COMPACT_SEED_POD -> {
                // Compact, cute acorn/seed-sprout body with a twin top sprout leaflet
                leafPath.apply {
                    moveTo(leafBottom.x, leafBottom.y)
                    cubicTo(
                        center.x - leafW * 0.95f, center.y + leafH * 0.35f,
                        center.x - leafW * 0.85f, center.y - leafH * 0.45f,
                        leafTip.x, leafTip.y + leafH * 0.12f
                    )
                    cubicTo(
                        center.x + leafW * 0.85f, center.y - leafH * 0.45f,
                        center.x + leafW * 0.95f, center.y + leafH * 0.35f,
                        leafBottom.x, leafBottom.y
                    )
                    close()
                }
            }

            LeafBodyShape.MONSTERA_SHIELD -> {
                // Broad scalloped 5-ridge Monstera/Palm Shield leaf with side indentations
                leafPath.apply {
                    moveTo(leafBottom.x, leafBottom.y + leafH * 0.04f)
                    // Lower left lobe
                    quadraticBezierTo(
                        center.x - leafW * 0.98f, center.y + leafH * 0.36f,
                        center.x - leafW * 0.88f, center.y + leafH * 0.04f
                    )
                    // Left fenestration notch
                    quadraticBezierTo(
                        center.x - leafW * 0.52f, center.y - leafH * 0.02f,
                        center.x - leafW * 0.92f, center.y - leafH * 0.26f
                    )
                    // Upper left curve to peak
                    quadraticBezierTo(
                        center.x - leafW * 0.65f, center.y - leafH * 0.62f,
                        leafTip.x, leafTip.y
                    )
                    // Upper right curve from peak
                    quadraticBezierTo(
                        center.x + leafW * 0.65f, center.y - leafH * 0.62f,
                        center.x + leafW * 0.92f, center.y - leafH * 0.26f
                    )
                    // Right fenestration notch
                    quadraticBezierTo(
                        center.x + leafW * 0.52f, center.y - leafH * 0.02f,
                        center.x + leafW * 0.88f, center.y + leafH * 0.04f
                    )
                    // Lower right lobe
                    quadraticBezierTo(
                        center.x + leafW * 0.98f, center.y + leafH * 0.36f,
                        leafBottom.x, leafBottom.y + leafH * 0.04f
                    )
                    close()
                }
            }
        }

        // Rich tender vertical gradient fill
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

        // Bold outer leaf contour
        drawPath(
            path = leafPath,
            color = Color.White.copy(alpha = 0.48f),
            style = Stroke(width = 2.8f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
        drawPath(
            path = leafPath,
            color = color,
            style = Stroke(width = 1.6f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Central luminous leaf midrib vein
        val veinPath = Path().apply {
            moveTo(leafBottom.x, leafBottom.y - leafH * 0.06f)
            quadraticBezierTo(
                center.x + tipSway * 0.4f,
                center.y - leafH * 0.15f,
                leafTip.x,
                leafTip.y + leafH * 0.12f
            )
        }
        drawPath(
            path = veinPath,
            color = Color.White.copy(alpha = 0.40f),
            style = Stroke(width = 2.2f, cap = StrokeCap.Round)
        )

        // Lateral veins
        drawLine(
            color = Color.White.copy(alpha = 0.30f),
            start = Offset(center.x, center.y - leafH * 0.24f),
            end = Offset(center.x - leafW * 0.36f, center.y - leafH * 0.38f),
            strokeWidth = 1.6f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color.White.copy(alpha = 0.30f),
            start = Offset(center.x, center.y - leafH * 0.24f),
            end = Offset(center.x + leafW * 0.36f, center.y - leafH * 0.38f),
            strokeWidth = 1.6f,
            cap = StrokeCap.Round
        )

        // --- FRIENDLY LEAF FACE ---
        val eyeSpacing = (leafW * 0.27f).coerceIn(radius * 0.11f, radius * 0.21f)
        val eyeY = center.y - leafH * 0.03f
        val eyeRadius = (radius * 0.078f).coerceAtLeast(4.5f)

        if (isCelebrating) {
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

            // Left Eye
            drawOval(
                color = Color.White.copy(alpha = 0.94f),
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

            // Right Eye
            drawOval(
                color = Color.White.copy(alpha = 0.94f),
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

        // Rosy cheek glow
        drawCircle(
            color = Color(0xFFFFB2B2).copy(alpha = 0.42f),
            radius = eyeRadius * 0.78f,
            center = Offset(center.x - eyeSpacing - eyeRadius * 0.85f, eyeY + eyeRadius * 1.05f)
        )
        drawCircle(
            color = Color(0xFFFFB2B2).copy(alpha = 0.42f),
            radius = eyeRadius * 0.78f,
            center = Offset(center.x + eyeSpacing + eyeRadius * 0.85f, eyeY + eyeRadius * 1.05f)
        )

        // Friendly Smile
        val mouthY = eyeY + eyeRadius * 1.45f
        val mouthW = eyeSpacing * 0.58f

        if (state == CompanionState.SPEAKING) {
            val openAmt = (abs(sin(phase * 4f)) * 5.5f) + 2.5f
            val mouthPath = Path().apply {
                moveTo(center.x - mouthW, mouthY)
                quadraticBezierTo(center.x, mouthY + openAmt + 4f, center.x + mouthW, mouthY)
                quadraticBezierTo(center.x, mouthY + openAmt * 0.3f, center.x - mouthW, mouthY)
            }
            drawPath(mouthPath, color = Color.White)
        } else {
            val smilePath = Path().apply {
                moveTo(center.x - mouthW, mouthY)
                quadraticBezierTo(center.x, mouthY + 6.5f, center.x + mouthW, mouthY)
            }
            drawPath(smilePath, color = Color.White, style = Stroke(width = 2.8f, cap = StrokeCap.Round))
        }
    }
}

/**
 * Draws the 5 distinct tendril types:
 * - EVENT_SPROUT_BLOOM: Dormant when idle; sprouts blooming lotus tendrils on events
 * - GRAPPLING_HOOKS: Curved J-hooks & barbed botanical anchor hooks
 * - RARE_HELIX_COILS: Multi-loop corkscrew spiral spring tendrils
 * - EVENT_WHIP_HOOKS: Tiny buds when idle; sprouts long whip-hooks on events
 * - RARE_LANTERN_HOOKS: Arching crescent hooks suspending glowing bioluminescent lantern pods
 */
private fun DrawScope.drawArchetypeTendrils(
    center: Offset,
    baseRadius: Float,
    phase: Float,
    color: Color,
    archetype: AvatarArchetype,
    sproutScale: Float
) {
    when (archetype.tendrilStyle) {
        TendrilStyle.EVENT_SPROUT_BLOOM -> {
            // AVATAR_1 has NO tendrils when idle; they sprout dynamically when an event happens!
            if (sproutScale <= 0.04f) return

            val tendrilCount = 6
            val angleStep = (2 * PI / tendrilCount).toFloat()
            for (i in 0 until tendrilCount) {
                val angle = i * angleStep - (PI / 2).toFloat() + sin(phase + i) * 0.25f
                val start = Offset(
                    center.x + cos(angle) * baseRadius * 0.45f,
                    center.y + sin(angle) * baseRadius * 0.45f
                )
                val len = baseRadius * 1.18f * sproutScale
                val ctrl = Offset(
                    center.x + cos(angle + 0.4f) * (baseRadius * 0.45f + len * 0.6f),
                    center.y + sin(angle + 0.4f) * (baseRadius * 0.45f + len * 0.6f)
                )
                val tip = Offset(
                    center.x + cos(angle - 0.2f) * (baseRadius * 0.45f + len),
                    center.y + sin(angle - 0.2f) * (baseRadius * 0.45f + len)
                )
                val path = Path().apply {
                    moveTo(start.x, start.y)
                    quadraticBezierTo(ctrl.x, ctrl.y, tip.x, tip.y)
                }
                drawPath(path, color = color.copy(alpha = 0.88f * sproutScale), style = Stroke(width = 3.8f, cap = StrokeCap.Round))
                // Blooming flower bud at tip
                drawCircle(color = Color(0xFFFFD54F).copy(alpha = sproutScale), radius = 5.0f * sproutScale, center = tip)
                drawCircle(color = Color.White.copy(alpha = 0.85f * sproutScale), radius = 2.4f * sproutScale, center = tip)
            }
        }

        TendrilStyle.GRAPPLING_HOOKS -> {
            // 4 Bold Grappling J-Hook & Anchor-Hook Tendrils
            for (side in listOf(-1f, 1f)) {
                for (tier in 0..1) {
                    val yOff = if (tier == 0) -baseRadius * 0.35f else baseRadius * 0.10f
                    val reach = baseRadius * (1.28f - tier * 0.16f)
                    val sway = sin(phase * 1.6f + tier + side) * 5f

                    val start = Offset(center.x + side * baseRadius * 0.25f, center.y + yOff)
                    val shaftEnd = Offset(
                        center.x + side * reach + sway,
                        center.y + yOff - baseRadius * (0.45f - tier * 0.35f)
                    )
                    // Sharp curved J-Hook claw at the end of each tendril
                    val hookCurve = Offset(
                        shaftEnd.x + side * baseRadius * 0.24f,
                        shaftEnd.y - baseRadius * 0.28f
                    )
                    val hookBarb = Offset(
                        shaftEnd.x - side * baseRadius * 0.14f,
                        shaftEnd.y - baseRadius * 0.32f
                    )

                    val hookPath = Path().apply {
                        moveTo(start.x, start.y)
                        quadraticBezierTo(
                            center.x + side * reach * 0.65f,
                            start.y - baseRadius * 0.35f,
                            shaftEnd.x,
                            shaftEnd.y
                        )
                        // Hook bend and pointed barb
                        quadraticBezierTo(hookCurve.x, hookCurve.y, hookBarb.x, hookBarb.y)
                    }
                    drawPath(
                        path = hookPath,
                        color = color.copy(alpha = 0.92f),
                        style = Stroke(width = 3.4f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )
                    // Barb tip accent
                    drawCircle(color = Color.White, radius = 2.6f, center = hookBarb)
                }
            }
        }

        TendrilStyle.RARE_HELIX_COILS -> {
            // Rare Corkscrew Spiral Spring Tendrils (multi-loop coiled botanical springs)
            val directions = listOf(
                 -0.75f * PI.toFloat(),
                -0.25f * PI.toFloat(),
                0.15f * PI.toFloat(),
                0.85f * PI.toFloat()
            )
            directions.forEachIndexed { idx, baseAngle ->
                val coilPath = Path()
                val steps = 28
                val maxDist = baseRadius * 1.38f
                val startDist = baseRadius * 0.35f
                val perpAngle = baseAngle + (PI / 2f).toFloat()

                for (s in 0..steps) {
                    val t = s.toFloat() / steps
                    val dist = startDist + t * (maxDist - startDist)
                    // 3 full spiral loops along the tendril axis
                    val loopRadius = baseRadius * 0.16f * sin(t * PI.toFloat())
                    val coilOffset = sin(t * 6f * PI.toFloat() + phase * 2f + idx) * loopRadius

                    val px = center.x + cos(baseAngle) * dist + cos(perpAngle) * coilOffset
                    val py = center.y + sin(baseAngle) * dist + sin(perpAngle) * coilOffset

                    if (s == 0) coilPath.moveTo(px, py) else coilPath.lineTo(px, py)
                }
                drawPath(
                    path = coilPath,
                    color = color.copy(alpha = 0.88f),
                    style = Stroke(width = 2.8f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
                val tipX = center.x + cos(baseAngle) * maxDist
                val tipY = center.y + sin(baseAngle) * maxDist
                drawCircle(color = color, radius = 4.2f, center = Offset(tipX, tipY))
                drawCircle(color = Color.White, radius = 2.0f, center = Offset(tipX, tipY))
            }
        }

        TendrilStyle.EVENT_WHIP_HOOKS -> {
            // AVATAR_4: Dormant tiny side buds when idle; sprouts long whip-hooks when event occurs!
            if (sproutScale <= 0.05f) {
                // Draw tiny dormant shoulder buds when idle
                drawCircle(color = color.copy(alpha = 0.7f), radius = 3.2f, center = Offset(center.x - baseRadius * 0.42f, center.y - baseRadius * 0.15f))
                drawCircle(color = color.copy(alpha = 0.7f), radius = 3.2f, center = Offset(center.x + baseRadius * 0.42f, center.y - baseRadius * 0.15f))
                return
            }

            val count = 6
            for (i in 0 until count) {
                val angle = (-PI * 0.85 + i * (PI * 1.7 / (count - 1))).toFloat()
                val wave = sin(phase * 2.2f + i) * 0.25f
                val len = baseRadius * 1.45f * sproutScale
                val start = Offset(center.x + cos(angle) * baseRadius * 0.35f, center.y + sin(angle) * baseRadius * 0.35f)
                val cp = Offset(
                    center.x + cos(angle + wave) * (len * 0.65f),
                    center.y + sin(angle + wave) * (len * 0.65f)
                )
                val tip = Offset(
                    center.x + cos(angle - wave * 0.7f) * len,
                    center.y + sin(angle - wave * 0.7f) * len
                )
                val hookTip = Offset(
                    tip.x + cos(angle + 1.8f) * (baseRadius * 0.22f * sproutScale),
                    tip.y + sin(angle + 1.8f) * (baseRadius * 0.22f * sproutScale)
                )
                val path = Path().apply {
                    moveTo(start.x, start.y)
                    quadraticBezierTo(cp.x, cp.y, tip.x, tip.y)
                    lineTo(hookTip.x, hookTip.y)
                }
                drawPath(path, color = color.copy(alpha = 0.90f * sproutScale), style = Stroke(width = 3.2f, cap = StrokeCap.Round))
                drawCircle(color = Color(0xFFFFD54F).copy(alpha = sproutScale), radius = 3.8f * sproutScale, center = hookTip)
            }
        }

        TendrilStyle.RARE_LANTERN_HOOKS -> {
            // AVATAR_5: Rare Arching Crescent-Hook Vines suspending glowing bioluminescent lantern orbs
            for (side in listOf(-1f, 1f)) {
                val sway = sin(phase * 1.4f + side) * 4f
                val start = Offset(center.x + side * baseRadius * 0.30f, center.y - baseRadius * 0.45f)
                val archPeak = Offset(center.x + side * baseRadius * 1.15f + sway, center.y - baseRadius * 1.18f)
                val hookEnd = Offset(center.x + side * baseRadius * 1.32f + sway, center.y - baseRadius * 0.55f)
                val hookInward = Offset(center.x + side * baseRadius * 1.05f + sway, center.y - baseRadius * 0.42f)

                val vinePath = Path().apply {
                    moveTo(start.x, start.y)
                    quadraticBezierTo(archPeak.x, archPeak.y, hookEnd.x, hookEnd.y)
                    quadraticBezierTo(
                        hookEnd.x - side * baseRadius * 0.05f,
                        hookEnd.y + baseRadius * 0.20f,
                        hookInward.x,
                        hookInward.y
                    )
                }
                drawPath(vinePath, color = color.copy(alpha = 0.92f), style = Stroke(width = 3.8f, cap = StrokeCap.Round))

                // Suspended glowing lantern pod hanging from the crescent hook
                val lanternCenter = Offset(hookInward.x, hookInward.y + baseRadius * 0.18f)
                drawCircle(
                    color = Color(0xFFFFD54F).copy(alpha = 0.38f),
                    radius = 9.5f,
                    center = lanternCenter
                )
                drawCircle(
                    color = Color(0xFFFFD54F),
                    radius = 5.2f,
                    center = lanternCenter
                )
                drawCircle(
                    color = Color.White,
                    radius = 2.4f,
                    center = lanternCenter
                )

                // Lower secondary barbed hook spur
                val lowStart = Offset(center.x + side * baseRadius * 0.48f, center.y + baseRadius * 0.08f)
                val lowTip = Offset(center.x + side * baseRadius * 1.22f, center.y + baseRadius * 0.32f - sway * 0.5f)
                val lowHook = Offset(lowTip.x - side * baseRadius * 0.18f, lowTip.y - baseRadius * 0.22f)
                val lowPath = Path().apply {
                    moveTo(lowStart.x, lowStart.y)
                    quadraticBezierTo(center.x + side * baseRadius * 0.95f, lowTip.y + 8f, lowTip.x, lowTip.y)
                    lineTo(lowHook.x, lowHook.y)
                }
                drawPath(lowPath, color = color.copy(alpha = 0.82f), style = Stroke(width = 3.2f, cap = StrokeCap.Round))
            }
        }
    }
}

/**
 * Draws the child-like arms & leaf hands (with big brawny arms/hands for AVATAR_1 & AVATAR_5,
 * and extra-long wall-leaning arms for AVATAR_4).
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
    val armLen = radius * 0.35f * archetype.armLengthScale
    val armThick = 3.8f * archetype.armThicknessScale
    val shoulderY = center.y + radius * 0.04f
    val shoulderSpan = radius * 0.25f * archetype.bodyWidthScale

    val shoulderLeft = Offset(center.x - shoulderSpan, shoulderY)
    val shoulderRight = Offset(center.x + shoulderSpan, shoulderY)

    if (isHanging) {
        val leftHand = Offset(center.x - radius * 0.18f, center.y - radius * 0.46f)
        val rightHand = Offset(center.x + radius * 0.18f, center.y - radius * 0.46f)
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
            val leftHand = if (archetype == AvatarArchetype.AVATAR_4) {
                Offset(center.x - radius * 0.78f, shoulderY - radius * 0.06f)
            } else {
                Offset(shoulderLeft.x - armLen * 0.62f, shoulderY + armLen * 0.36f + sin(phase) * 2.5f)
            }
            val rightHand = Offset(shoulderRight.x + armLen * 0.62f, shoulderY + armLen * 0.36f - sin(phase) * 2.5f)

            drawCurvedChildArm(shoulderLeft, leftHand, color, armThick, isLeft = true)
            drawCurvedChildArm(shoulderRight, rightHand, color, armThick, isLeft = false)
            drawLeafHand(leftHand, if (archetype == AvatarArchetype.AVATAR_4) -85f else -28f, color, archetype)
            drawLeafHand(rightHand, 28f, color, archetype)
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
        (start.x + end.x) * 0.5f + dir * 7f,
        (start.y + end.y) * 0.5f + 5f
    )
    val path = Path().apply {
        moveTo(start.x, start.y)
        quadraticBezierTo(elbow.x, elbow.y, end.x, end.y)
    }
    drawPath(path, color = Color.White.copy(alpha = 0.35f), style = Stroke(width = thickness + 1.8f, cap = StrokeCap.Round))
    drawPath(path, color = color, style = Stroke(width = thickness, cap = StrokeCap.Round))
}

private fun DrawScope.drawLeafHand(position: Offset, angle: Float, color: Color, archetype: AvatarArchetype) {
    rotate(angle, pivot = position) {
        val scale = archetype.handScale
        val handW = 13f * scale
        val handH = 17f * scale
        val path = Path().apply {
            moveTo(position.x, position.y)
            cubicTo(
                position.x - handW * 0.60f, position.y - handH * 0.35f,
                position.x - handW * 0.48f, position.y - handH,
                position.x, position.y - handH
            )
            cubicTo(
                position.x + handW * 0.48f, position.y - handH,
                position.x + handW * 0.60f, position.y - handH * 0.35f,
                position.x, position.y
            )
            close()
        }
        drawPath(path, color = color)
        drawPath(path, color = Color.White.copy(alpha = 0.50f), style = Stroke(width = 1.5f))
    }
}

/**
 * Draws the child-like legs & root feet (with big sturdy legs & feet for AVATAR_1 & AVATAR_5,
 * and extra-long legs for AVATAR_4).
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
    val legThick = 4.0f * archetype.legThicknessScale
    val hipSpacing = (radius * 0.15f * archetype.bodyWidthScale).coerceIn(radius * 0.09f, radius * 0.24f)
    val hipY = center.y + (radius * 0.25f * archetype.bodyHeightScale)

    val liftL = if (isHopping || isHanging) sin(phase * 2.5f).coerceAtLeast(0f) * 6.5f else 0f
    val liftR = if (isHopping || isHanging) cos(phase * 2.5f).coerceAtLeast(0f) * 6.5f else 0f

    for (isLeft in listOf(true, false)) {
        val sign = if (isLeft) -1f else 1f
        val lift = if (isLeft) liftL else liftR
        val hip = Offset(center.x + sign * hipSpacing, hipY)
        val knee = Offset(
            hip.x + sign * (if (archetype == AvatarArchetype.AVATAR_4) 7.5f else 3.5f),
            hip.y + legLen * 0.5f - lift * 0.5f
        )
        val foot = Offset(
            hip.x + sign * 2.5f,
            hip.y + legLen - lift
        )

        val legPath = Path().apply {
            moveTo(hip.x, hip.y)
            quadraticBezierTo(knee.x, knee.y, foot.x, foot.y)
        }
        drawPath(legPath, color = Color.White.copy(alpha = 0.35f), style = Stroke(width = legThick + 1.8f, cap = StrokeCap.Round))
        drawPath(legPath, color = color, style = Stroke(width = legThick, cap = StrokeCap.Round))

        val footScale = archetype.footScale
        val footW = 16.5f * footScale
        val footH = 9.5f * footScale
        drawOval(
            color = color,
            topLeft = Offset(foot.x - footW * 0.5f + sign * 2f, foot.y - 2f),
            size = Size(footW, footH)
        )
        drawOval(
            color = Color.White.copy(alpha = 0.42f),
            topLeft = Offset(foot.x - footW * 0.38f + sign * 2f, foot.y - 1f),
            size = Size(footW * 0.76f, footH * 0.65f),
            style = Stroke(width = 1.3f)
        )
    }
}

private fun DrawScope.drawOverheadTreeBranch(
    center: Offset,
    radius: Float,
    phase: Float,
    color: Color
) {
    val branchY = center.y - radius * 0.52f
    val path = Path().apply {
        moveTo(center.x - radius * 0.62f, branchY + 8f)
        quadraticBezierTo(center.x, branchY - 10f + sin(phase) * 2f, center.x + radius * 0.62f, branchY + 6f)
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
