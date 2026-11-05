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
    FIDDLEHEAD_COIL,     // Curled spiral fern fiddleheads on crown + rosy cheek foliage
    SPIRAL_ANTENNAE,      // Twin delicate coiled twining vines with active antennae reach
    WEEPING_WILLOW,       // Cascading sweeping willow curls draping down both sides like a cape
    STARBURST_TENDRILS,   // 5 radiating bouncy starburst vine shoots
    GINKGO_CANOPY         // Wide tiered fan fronds layered like an ancient bonsai umbrella
}

enum class AvatarArchetype(
    val id: String,
    val displayName: String,
    val description: String,
    val tag: String,
    val bodyWidthScale: Float,
    val bodyHeightScale: Float,
    val armLengthScale: Float,
    val armThicknessScale: Float,
    val legLengthScale: Float,
    val legThicknessScale: Float,
    val tendrilStyle: TendrilStyle
) {
    CHUBBY_SPROUT(
        id = "chubby",
        displayName = "Puff-Sprout",
        description = "Chubby, roly-poly dumpling sprout with curled fiddlehead coils and stubby paws",
        tag = "Chubby",
        bodyWidthScale = 1.30f,
        bodyHeightScale = 0.88f,
        armLengthScale = 0.70f,
        armThicknessScale = 1.35f,
        legLengthScale = 0.55f,
        legThicknessScale = 1.40f,
        tendrilStyle = TendrilStyle.FIDDLEHEAD_COIL
    ),
    SKINNY_TWIGLET(
        id = "skinny",
        displayName = "Vine-Wisp",
        description = "Slender, nimble, agile silhouette with long flexible twining vine arms and spiral fern antennae",
        tag = "Skinny",
        bodyWidthScale = 0.68f,
        bodyHeightScale = 1.05f,
        armLengthScale = 1.35f,
        armThicknessScale = 0.70f,
        legLengthScale = 1.15f,
        legThicknessScale = 0.75f,
        tendrilStyle = TendrilStyle.SPIRAL_ANTENNAE
    ),
    TALL_WILLOW(
        id = "tall",
        displayName = "Arbor-Elder",
        description = "Stately elongated bough torso with cascading weeping willow tendrils draping like a leafy cloak",
        tag = "Tall",
        bodyWidthScale = 0.82f,
        bodyHeightScale = 1.45f,
        armLengthScale = 1.30f,
        armThicknessScale = 0.95f,
        legLengthScale = 1.40f,
        legThicknessScale = 1.00f,
        tendrilStyle = TendrilStyle.WEEPING_WILLOW
    ),
    LONG_STRIDER(
        id = "strider",
        displayName = "Branch-Strider",
        description = "Extra-long elastic leafy arms and springy stilt legs that climb, hang, and lean playfully",
        tag = "Long Limbs",
        bodyWidthScale = 0.88f,
        bodyHeightScale = 0.92f,
        armLengthScale = 1.80f,
        armThicknessScale = 0.85f,
        legLengthScale = 1.85f,
        legThicknessScale = 0.85f,
        tendrilStyle = TendrilStyle.STARBURST_TENDRILS
    ),
    GINKGO_SAGE(
        id = "sage",
        displayName = "Bonsai-Sage",
        description = "Broad grounded rooted stance with multi-tiered scalloped Ginkgo biloba frond umbrella crown",
        tag = "Bonsai Sage",
        bodyWidthScale = 1.12f,
        bodyHeightScale = 1.05f,
        armLengthScale = 1.05f,
        armThicknessScale = 1.15f,
        legLengthScale = 0.90f,
        legThicknessScale = 1.25f,
        tendrilStyle = TendrilStyle.GINKGO_CANOPY
    );

    companion object {
        fun byId(id: String): AvatarArchetype = entries.firstOrNull { it.id == id } ?: CHUBBY_SPROUT
    }
}

object AvatarManager {
    var selectedArchetype by mutableStateOf(AvatarArchetype.CHUBBY_SPROUT)
    fun cycleNext() {
        val nextIdx = (selectedArchetype.ordinal + 1) % AvatarArchetype.entries.size
        selectedArchetype = AvatarArchetype.entries[nextIdx]
    }
}

/**
 * TendrilOrganism — 5 Customizable Botanical Avatar Archetypes:
 * 1. Chubby Sprout (Dumpling body, roly-poly, stubby root paws, fiddlehead coils)
 * 2. Skinny Twiglet (Slender, agile whip arms, twin spiral fern antennae)
 * 3. Tall Willow (Stately elongated torso, cascading weeping willow leafy hair/cloak)
 * 4. Long-Limbed Strider (Extra-long elastic leafy arms & springy stilt legs, hanging & climbing)
 * 5. Bonsai Sage (Broad rooted bonsai stance, wide Ginkgo leaf fan hands, tiered umbrella fronds)
 */
@Composable
fun TendrilOrganism(
    state: CompanionState,
    modifier: Modifier = Modifier,
    size: Dp = 140.dp,
    archetype: AvatarArchetype = AvatarManager.selectedArchetype
) {
    var isInteractiveTapped by remember { mutableStateOf(false) }
    var tapCounter by remember { mutableStateOf(0) }

    val infiniteTransition = rememberInfiniteTransition()

    // Smooth continuous phase for breathing, arm waving, and tendrils
    val animPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (state) {
                    CompanionState.SPEAKING -> 1800
                    CompanionState.THINKING -> 2400
                    CompanionState.LISTENING -> 2000
                    CompanionState.COMPLETED -> 1400
                    else -> 3000
                },
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        )
    )

    // Gentle breathing pulse
    val breathScale by infiniteTransition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Natural eye blink timer
    val blinkProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 3500
                0.0f at 0
                0.0f at 3200
                1.0f at 3350
                0.0f at 3500
            },
            repeatMode = RepeatMode.Restart
        )
    )

    // Cute bouncy hopping
    val isHopping = state == CompanionState.COMPLETED ||
            state == CompanionState.SPEAKING ||
            isInteractiveTapped

    val hopOffset = if (isHopping) {
        abs(sin(animPhase * 2f)) * 10f
    } else {
        sin(animPhase) * 2f
    }

    val primaryColor by animateColorAsState(targetValue = state.primaryColor, animationSpec = tween(400))
    val auraColor by animateColorAsState(targetValue = state.auraColor, animationSpec = tween(400))

    Box(
        modifier = modifier
            .size(size)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                tapCounter++
                isInteractiveTapped = !isInteractiveTapped
                AvatarManager.cycleNext()
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val center = Offset(this.size.width / 2f, (this.size.height / 2f) - hopOffset)
            val baseRadius = this.size.minDimension / 2f

            // 1. Soft Bioluminescent Radial Aura
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        auraColor.copy(alpha = 0.40f),
                        auraColor.copy(alpha = 0.10f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = baseRadius * breathScale * 1.05f
                ),
                radius = baseRadius * breathScale * 1.05f,
                center = center
            )

            // 2. Celebration Sparkle Burst
            if (state == CompanionState.COMPLETED || (tapCounter % 3 == 0 && tapCounter > 0)) {
                drawCelebrationBurst(
                    center = center,
                    radius = baseRadius * 0.9f,
                    phase = animPhase,
                    color = primaryColor
                )
            }

            // 3. Overhead Tree Branch (companion can hang playfully)
            val showBranch = state == CompanionState.COMPLETED || tapCounter % 2 == 1
            if (showBranch) {
                drawOverheadTreeBranch(
                    center = center,
                    radius = baseRadius,
                    phase = animPhase,
                    color = primaryColor
                )
            }

            // 4. Legs / Rootlet Paws (Styled per Archetype)
            drawCustomPaws(
                center = center,
                radius = baseRadius,
                phase = animPhase,
                color = primaryColor,
                isHopping = isHopping,
                isHanging = showBranch,
                archetype = archetype
            )

            // 5. Body (Styled per Archetype)
            drawCustomBody(
                center = center,
                radius = baseRadius,
                color = primaryColor,
                auraColor = auraColor,
                breathScale = breathScale,
                archetype = archetype
            )

            // 6. Scalloped Leaf-Mask Face with Big Friendly Anime Eyes & Smile
            drawScallopedLeafFace(
                center = center,
                radius = baseRadius,
                phase = animPhase,
                state = state,
                color = primaryColor,
                auraColor = auraColor,
                blinkProgress = blinkProgress,
                archetype = archetype
            )

            // 7. Arms & Leaf Hands (Styled per Archetype)
            drawCustomArms(
                center = center,
                radius = baseRadius,
                phase = animPhase,
                state = state,
                color = primaryColor,
                isHanging = showBranch,
                archetype = archetype
            )

            // 8. Crown Sprout & Distinct Tendrils
            drawCustomCrownSproutAndTendrils(
                center = center,
                radius = baseRadius,
                phase = animPhase,
                state = state,
                color = primaryColor,
                archetype = archetype
            )
        }
    }
}

/**
 * 1. Morphological Body Drawing per Archetype
 */
private fun DrawScope.drawCustomBody(
    center: Offset,
    radius: Float,
    color: Color,
    auraColor: Color,
    breathScale: Float,
    archetype: AvatarArchetype
) {
    val bodyW = radius * 0.78f * breathScale * archetype.bodyWidthScale
    val bodyH = radius * 0.80f * breathScale * archetype.bodyHeightScale
    val bodyCenter = Offset(center.x, center.y + (radius * 0.04f * archetype.bodyHeightScale))

    val bodyPath = Path().apply {
        addOval(
            androidx.compose.ui.geometry.Rect(
                left = bodyCenter.x - bodyW * 0.5f,
                top = bodyCenter.y - bodyH * 0.5f,
                right = bodyCenter.x + bodyW * 0.5f,
                bottom = bodyCenter.y + bodyH * 0.5f
            )
        )
    }

    drawPath(
        path = bodyPath,
        brush = Brush.radialGradient(
            colors = listOf(
                color.copy(alpha = 0.24f),
                color.copy(alpha = 0.12f),
                auraColor.copy(alpha = 0.05f)
            ),
            center = bodyCenter,
            radius = bodyW * 0.6f
        )
    )

    drawPath(
        path = bodyPath,
        color = color.copy(alpha = 0.80f),
        style = Stroke(width = 3.2f, cap = StrokeCap.Round)
    )
}

/**
 * 2. Scalloped Leaf-Mask Face with Big Friendly Anime Eyes and Happy Smile
 */
private fun DrawScope.drawScallopedLeafFace(
    center: Offset,
    radius: Float,
    phase: Float,
    state: CompanionState,
    color: Color,
    auraColor: Color,
    blinkProgress: Float,
    archetype: AvatarArchetype
) {
    val faceCenter = Offset(center.x, center.y - (radius * 0.02f * archetype.bodyHeightScale))
    val maskW = radius * 0.64f * (0.9f + archetype.bodyWidthScale * 0.1f)
    val maskH = radius * 0.56f * (0.9f + archetype.bodyHeightScale * 0.1f)

    val tiltAngle = when (state) {
        CompanionState.THINKING -> sin(phase) * 6f
        CompanionState.LISTENING -> 8f
        else -> sin(phase * 0.6f) * 2f
    }

    rotate(tiltAngle, pivot = faceCenter) {
        val leafMaskPath = Path().apply {
            moveTo(faceCenter.x, faceCenter.y - maskH * 0.52f)
            cubicTo(
                faceCenter.x - maskW * 0.35f, faceCenter.y - maskH * 0.62f,
                faceCenter.x - maskW * 0.56f, faceCenter.y - maskH * 0.25f,
                faceCenter.x - maskW * 0.52f, faceCenter.y + maskH * 0.12f
            )
            cubicTo(
                faceCenter.x - maskW * 0.48f, faceCenter.y + maskH * 0.48f,
                faceCenter.x - maskW * 0.22f, faceCenter.y + maskH * 0.52f,
                faceCenter.x, faceCenter.y + maskH * 0.48f
            )
            cubicTo(
                faceCenter.x + maskW * 0.22f, faceCenter.y + maskH * 0.52f,
                faceCenter.x + maskW * 0.48f, faceCenter.y + maskH * 0.48f,
                faceCenter.x + maskW * 0.52f, faceCenter.y + maskH * 0.12f
            )
            cubicTo(
                faceCenter.x + maskW * 0.56f, faceCenter.y - maskH * 0.25f,
                faceCenter.x + maskW * 0.35f, faceCenter.y - maskH * 0.62f,
                faceCenter.x, faceCenter.y - maskH * 0.52f
            )
            close()
        }

        drawPath(
            path = leafMaskPath,
            brush = Brush.radialGradient(
                colors = listOf(
                    color.copy(alpha = 0.32f),
                    color.copy(alpha = 0.18f),
                    auraColor.copy(alpha = 0.08f)
                ),
                center = faceCenter,
                radius = maskW * 0.65f
            )
        )

        drawPath(
            path = leafMaskPath,
            color = color.copy(alpha = 0.90f),
            style = Stroke(width = 3f, cap = StrokeCap.Round)
        )

        // Veins
        drawLine(
            color = color.copy(alpha = 0.40f),
            start = Offset(faceCenter.x, faceCenter.y - maskH * 0.42f),
            end = Offset(faceCenter.x, faceCenter.y + maskH * 0.25f),
            strokeWidth = 2f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color.copy(alpha = 0.25f),
            start = Offset(faceCenter.x, faceCenter.y - maskH * 0.15f),
            end = Offset(faceCenter.x - maskW * 0.32f, faceCenter.y - maskH * 0.28f),
            strokeWidth = 1.4f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color.copy(alpha = 0.25f),
            start = Offset(faceCenter.x, faceCenter.y - maskH * 0.15f),
            end = Offset(faceCenter.x + maskW * 0.32f, faceCenter.y - maskH * 0.28f),
            strokeWidth = 1.4f,
            cap = StrokeCap.Round
        )

        // Anime Eyes
        val eyeSpacing = maskW * 0.26f
        val eyeY = faceCenter.y - maskH * 0.04f
        val eyeRadius = maskW * 0.12f

        if (state == CompanionState.COMPLETED) {
            val leftEyeArc = Path().apply {
                moveTo(faceCenter.x - eyeSpacing - eyeRadius, eyeY + 2f)
                quadraticBezierTo(faceCenter.x - eyeSpacing, eyeY - eyeRadius * 1.2f, faceCenter.x - eyeSpacing + eyeRadius, eyeY + 2f)
            }
            val rightEyeArc = Path().apply {
                moveTo(faceCenter.x + eyeSpacing - eyeRadius, eyeY + 2f)
                quadraticBezierTo(faceCenter.x + eyeSpacing, eyeY - eyeRadius * 1.2f, faceCenter.x + eyeSpacing + eyeRadius, eyeY + 2f)
            }
            drawPath(leftEyeArc, color = color, style = Stroke(width = 4f, cap = StrokeCap.Round))
            drawPath(rightEyeArc, color = color, style = Stroke(width = 4f, cap = StrokeCap.Round))
        } else {
            val openH = (1f - blinkProgress).coerceAtLeast(0.08f)

            // Left Eye
            drawOval(
                color = color,
                topLeft = Offset(faceCenter.x - eyeSpacing - eyeRadius, eyeY - (eyeRadius * openH)),
                size = Size(eyeRadius * 2f, eyeRadius * 2.2f * openH)
            )
            if (openH > 0.4f) {
                drawCircle(color = Color.White, radius = eyeRadius * 0.42f, center = Offset(faceCenter.x - eyeSpacing - eyeRadius * 0.25f, eyeY - eyeRadius * 0.30f))
                drawCircle(color = Color.White.copy(alpha = 0.85f), radius = eyeRadius * 0.20f, center = Offset(faceCenter.x - eyeSpacing + eyeRadius * 0.35f, eyeY + eyeRadius * 0.25f))
            }

            // Right Eye
            drawOval(
                color = color,
                topLeft = Offset(faceCenter.x + eyeSpacing - eyeRadius, eyeY - (eyeRadius * openH)),
                size = Size(eyeRadius * 2f, eyeRadius * 2.2f * openH)
            )
            if (openH > 0.4f) {
                drawCircle(color = Color.White, radius = eyeRadius * 0.42f, center = Offset(faceCenter.x + eyeSpacing - eyeRadius * 0.25f, eyeY - eyeRadius * 0.30f))
                drawCircle(color = Color.White.copy(alpha = 0.85f), radius = eyeRadius * 0.20f, center = Offset(faceCenter.x + eyeSpacing + eyeRadius * 0.35f, eyeY + eyeRadius * 0.25f))
            }
        }

        // Smile
        val mouthY = faceCenter.y + maskH * 0.18f
        val mouthW = maskW * 0.14f

        if (state == CompanionState.SPEAKING) {
            val mouthOpen = (abs(sin(phase * 4f)) * 5f) + 2.5f
            val mouthPath = Path().apply {
                moveTo(faceCenter.x - mouthW, mouthY)
                quadraticBezierTo(faceCenter.x, mouthY + mouthOpen + 4f, faceCenter.x + mouthW, mouthY)
                quadraticBezierTo(faceCenter.x, mouthY + mouthOpen * 0.4f, faceCenter.x - mouthW, mouthY)
            }
            drawPath(mouthPath, color = color.copy(alpha = 0.90f))
        } else {
            val mouthPath = Path().apply {
                moveTo(faceCenter.x - mouthW, mouthY)
                quadraticBezierTo(faceCenter.x, mouthY + 5.5f, faceCenter.x + mouthW, mouthY)
            }
            drawPath(mouthPath, color = color.copy(alpha = 0.90f), style = Stroke(width = 2.8f, cap = StrokeCap.Round))
        }

        // Cheeks
        drawCircle(
            color = Color(0xFFFF8A80).copy(alpha = 0.28f),
            radius = maskW * 0.09f,
            center = Offset(faceCenter.x - eyeSpacing - maskW * 0.10f, eyeY + maskH * 0.18f)
        )
        drawCircle(
            color = Color(0xFFFF8A80).copy(alpha = 0.28f),
            radius = maskW * 0.09f,
            center = Offset(faceCenter.x + eyeSpacing + maskW * 0.10f, eyeY + maskH * 0.18f)
        )
    }
}

/**
 * 3. Morphological Arms Drawing per Archetype (Chubby, Skinny, Tall, Long Limbs, Bonsai)
 */
private fun DrawScope.drawCustomArms(
    center: Offset,
    radius: Float,
    phase: Float,
    state: CompanionState,
    color: Color,
    isHanging: Boolean,
    archetype: AvatarArchetype
) {
    val armLen = radius * 0.36f * archetype.armLengthScale
    val armThick = 3.6f * archetype.armThicknessScale
    val shoulderY = center.y + (radius * 0.04f * archetype.bodyHeightScale)
    val shoulderSpan = radius * 0.32f * archetype.bodyWidthScale

    val shoulderLeft = Offset(center.x - shoulderSpan, shoulderY)
    val shoulderRight = Offset(center.x + shoulderSpan, shoulderY)

    if (isHanging) {
        val leftHand = Offset(center.x - radius * 0.18f, center.y - (radius * 0.42f) - (if (archetype == AvatarArchetype.LONG_STRIDER) radius * 0.15f else 0f))
        val rightHand = Offset(center.x + radius * 0.18f, center.y - (radius * 0.42f) - (if (archetype == AvatarArchetype.LONG_STRIDER) radius * 0.15f else 0f))
        drawTwigArm(shoulderLeft, leftHand, color, armThick)
        drawTwigArm(shoulderRight, rightHand, color, armThick)
        drawRoundLeafHand(leftHand, -85f, color, archetype)
        drawRoundLeafHand(rightHand, 85f, color, archetype)
        return
    }

    when (state) {
        CompanionState.SPEAKING -> {
            val waveL = sin(phase * 2.5f) * armLen * 0.35f
            val waveR = cos(phase * 2.5f) * armLen * 0.35f

            val leftHand = Offset(shoulderLeft.x - armLen * 0.85f - waveL, shoulderY - waveL)
            val rightHand = Offset(shoulderRight.x + armLen * 0.85f + waveR, shoulderY - waveR)

            drawTwigArm(shoulderLeft, leftHand, color, armThick)
            drawTwigArm(shoulderRight, rightHand, color, armThick)
            drawRoundLeafHand(leftHand, -40f + sin(phase * 2.5f) * 20f, color, archetype)
            drawRoundLeafHand(rightHand, 40f - cos(phase * 2.5f) * 20f, color, archetype)
        }
        CompanionState.THINKING -> {
            val cheekHand = Offset(center.x - radius * 0.16f, center.y - radius * 0.06f)
            val restHand = Offset(shoulderRight.x + armLen * 0.6f, shoulderY + armLen * 0.5f)

            drawTwigArm(shoulderLeft, cheekHand, color, armThick)
            drawTwigArm(shoulderRight, restHand, color, armThick)
            drawRoundLeafHand(cheekHand, -75f, color, archetype)
            drawRoundLeafHand(restHand, 15f, color, archetype)
        }
        CompanionState.LISTENING -> {
            val earHand = Offset(center.x + shoulderSpan + armLen * 0.3f, center.y - radius * 0.14f)
            val restHand = Offset(shoulderLeft.x - armLen * 0.4f, shoulderY + armLen * 0.5f)

            drawTwigArm(shoulderLeft, restHand, color, armThick)
            drawTwigArm(shoulderRight, earHand, color, armThick)
            drawRoundLeafHand(restHand, -15f, color, archetype)
            drawRoundLeafHand(earHand, 60f, color, archetype)
        }
        CompanionState.COMPLETED -> {
            val leftHand = Offset(shoulderLeft.x - armLen * 0.5f, center.y - armLen * 0.9f)
            val rightHand = Offset(shoulderRight.x + armLen * 0.5f, center.y - armLen * 0.9f)

            drawTwigArm(shoulderLeft, leftHand, color, armThick)
            drawTwigArm(shoulderRight, rightHand, color, armThick)
            drawRoundLeafHand(leftHand, -115f, color, archetype)
            drawRoundLeafHand(rightHand, 115f, color, archetype)
        }
        else -> {
            // Idle / Leaning: Long Strider folds one arm to lean on canvas walls!
            val leftHand = if (archetype == AvatarArchetype.LONG_STRIDER) {
                Offset(center.x - (radius * 0.82f), shoulderY - (radius * 0.10f))
            } else {
                Offset(shoulderLeft.x - armLen * 0.5f, shoulderY + armLen * 0.4f + sin(phase) * 2.5f)
            }
            val rightHand = Offset(shoulderRight.x + armLen * 0.5f, shoulderY + armLen * 0.4f - sin(phase) * 2.5f)

            drawTwigArm(shoulderLeft, leftHand, color, armThick)
            drawTwigArm(shoulderRight, rightHand, color, armThick)
            drawRoundLeafHand(leftHand, if (archetype == AvatarArchetype.LONG_STRIDER) -80f else -20f, color, archetype)
            drawRoundLeafHand(rightHand, 20f, color, archetype)
        }
    }
}

private fun DrawScope.drawTwigArm(start: Offset, end: Offset, color: Color, thickness: Float) {
    val mid = Offset((start.x + end.x) / 2f + 3f, (start.y + end.y) / 2f + 3f)
    val path = Path().apply {
        moveTo(start.x, start.y)
        quadraticBezierTo(mid.x, mid.y, end.x, end.y)
    }
    drawPath(path, color = color.copy(alpha = 0.88f), style = Stroke(width = thickness, cap = StrokeCap.Round))
}

private fun DrawScope.drawRoundLeafHand(position: Offset, angle: Float, color: Color, archetype: AvatarArchetype) {
    rotate(angle, pivot = position) {
        val handScale = if (archetype == AvatarArchetype.GINKGO_SAGE) 1.35f else if (archetype == AvatarArchetype.CHUBBY_SPROUT) 1.25f else 1.0f
        val handW = 13f * handScale
        val handH = 17f * handScale
        val path = Path().apply {
            moveTo(position.x, position.y)
            cubicTo(
                position.x - handW * 0.5f, position.y - handH * 0.35f,
                position.x - handW * 0.4f, position.y - handH,
                position.x, position.y - handH
            )
            cubicTo(
                position.x + handW * 0.4f, position.y - handH,
                position.x + handW * 0.5f, position.y - handH * 0.35f,
                position.x, position.y
            )
            close()
        }
        drawPath(path, color = color.copy(alpha = 0.90f))
        drawPath(path, color = Color.White.copy(alpha = 0.35f), style = Stroke(width = 1.2f))
    }
}

/**
 * 4. Morphological Legs / Paws per Archetype (Chubby, Skinny, Tall, Long Limbs, Bonsai)
 */
private fun DrawScope.drawCustomPaws(
    center: Offset,
    radius: Float,
    phase: Float,
    color: Color,
    isHopping: Boolean,
    isHanging: Boolean,
    archetype: AvatarArchetype
) {
    val legLen = radius * 0.38f * archetype.legLengthScale
    val legThick = 3.6f * archetype.legThicknessScale
    val pawSpacing = radius * 0.18f * archetype.bodyWidthScale
    val hipY = center.y + (radius * 0.38f * archetype.bodyHeightScale)

    val hopL = if (isHopping) sin(phase * 2f).coerceAtLeast(0f) * 6f else 0f
    val hopR = if (isHopping) cos(phase * 2f).coerceAtLeast(0f) * 6f else 0f

    for (isLeft in listOf(true, false)) {
        val sign = if (isLeft) -1f else 1f
        val hop = if (isLeft) hopL else hopR
        val hip = Offset(center.x + (sign * pawSpacing), hipY)

        if (archetype == AvatarArchetype.LONG_STRIDER) {
            // Extra-long springy stilt legs with articulated knee joint!
            val kneeX = hip.x + (sign * 8f) + sin(phase * 1.5f + (if (isLeft) 0f else 1f)) * 4f
            val kneeY = hip.y + (legLen * 0.52f) - hop
            val footX = kneeX - (sign * 4f)
            val footY = hip.y + legLen - (hop * 1.5f)

            drawLine(color = color.copy(alpha = 0.90f), start = hip, end = Offset(kneeX, kneeY), strokeWidth = legThick, cap = StrokeCap.Round)
            drawLine(color = color.copy(alpha = 0.90f), start = Offset(kneeX, kneeY), end = Offset(footX, footY), strokeWidth = legThick, cap = StrokeCap.Round)
            drawCircle(color = color, radius = legThick * 0.9f, center = Offset(kneeX, kneeY))
            drawPaw(Offset(footX, footY), color, scale = 1.0f)
        } else if (archetype == AvatarArchetype.TALL_WILLOW) {
            val footY = hip.y + legLen - hop
            drawLine(color = color.copy(alpha = 0.90f), start = hip, end = Offset(hip.x, footY), strokeWidth = legThick * 1.2f, cap = StrokeCap.Round)
            drawPaw(Offset(hip.x, footY), color, scale = 1.15f)
        } else if (archetype == AvatarArchetype.SKINNY_TWIGLET) {
            val footY = hip.y + legLen - hop
            drawLine(color = color.copy(alpha = 0.85f), start = hip, end = Offset(hip.x + sign * 2f, footY), strokeWidth = legThick, cap = StrokeCap.Round)
            drawPaw(Offset(hip.x + sign * 2f, footY), color, scale = 0.85f)
        } else {
            val footY = hip.y + (legLen * 0.45f) - hop
            drawPaw(Offset(hip.x, footY), color, scale = if (archetype == AvatarArchetype.CHUBBY_SPROUT) 1.28f else 1.1f)
        }
    }
}

private fun DrawScope.drawPaw(pos: Offset, color: Color, scale: Float = 1.0f) {
    val pawW = 18f * scale
    val pawH = 11f * scale
    drawOval(
        color = color.copy(alpha = 0.92f),
        topLeft = Offset(pos.x - pawW * 0.5f, pos.y),
        size = Size(pawW, pawH)
    )
    drawOval(
        color = Color.White.copy(alpha = 0.30f),
        topLeft = Offset(pos.x - pawW * 0.38f, pos.y + 1f),
        size = Size(pawW * 0.76f, pawH * 0.65f),
        style = Stroke(width = 1.2f)
    )
}

/**
 * 5. Distinct Tendril Styles per Archetype
 */
private fun DrawScope.drawCustomCrownSproutAndTendrils(
    center: Offset,
    radius: Float,
    phase: Float,
    state: CompanionState,
    color: Color,
    archetype: AvatarArchetype
) {
    val topY = center.y - (radius * 0.38f * archetype.bodyHeightScale)
    val topCenter = Offset(center.x, topY)

    // Base dual sprout leaves
    val leafW = 13f
    val leafH = 19f
    val wiggle = sin(phase * 2f) * 2f

    val leftLeaf = Path().apply {
        moveTo(topCenter.x, topCenter.y)
        quadraticBezierTo(topCenter.x - leafW + wiggle, topCenter.y - leafH * 0.5f, topCenter.x - leafW * 0.8f + wiggle, topCenter.y - leafH)
        quadraticBezierTo(topCenter.x - leafW * 0.2f, topCenter.y - leafH * 0.7f, topCenter.x, topCenter.y)
    }
    val rightLeaf = Path().apply {
        moveTo(topCenter.x, topCenter.y)
        quadraticBezierTo(topCenter.x + leafW + wiggle, topCenter.y - leafH * 0.5f, topCenter.x + leafW * 0.8f + wiggle, topCenter.y - leafH)
        quadraticBezierTo(topCenter.x + leafW * 0.2f, topCenter.y - leafH * 0.7f, topCenter.x, topCenter.y)
    }
    drawPath(leftLeaf, color = color.copy(alpha = 0.95f))
    drawPath(rightLeaf, color = color.copy(alpha = 0.95f))

    // Archetype-Specific Tendril Structures
    when (archetype.tendrilStyle) {
        TendrilStyle.FIDDLEHEAD_COIL -> {
            // Chubby Sprout: Rolled spiral fern fiddleheads with warm golden tips
            for (dir in listOf(-1f, 1f)) {
                val coilPath = Path()
                val startX = topCenter.x + (dir * 6f)
                val startY = topCenter.y
                coilPath.moveTo(startX, startY)
                val cx1 = startX + (dir * 12f)
                val cy1 = startY - 14f + sin(phase * 2f) * 2f
                val cx2 = startX + (dir * 18f)
                val cy2 = startY - 26f
                val endX = startX + (dir * 12f)
                val endY = startY - 30f
                coilPath.cubicTo(cx1, cy1, cx2, cy2, endX, endY)
                coilPath.quadraticBezierTo(startX + (dir * 6f), startY - 28f, startX + (dir * 10f), startY - 24f)
                drawPath(coilPath, color = color.copy(alpha = 0.95f), style = Stroke(width = 3.2f, cap = StrokeCap.Round))
                drawCircle(color = Color(0xFFFFD54F), radius = 3.5f, center = Offset(startX + (dir * 10f), startY - 24f))
            }
        }

        TendrilStyle.SPIRAL_ANTENNAE -> {
            // Skinny Twiglet: Twin high-reaching flexible coiled vine antennas with reactive tip lights
            for (dir in listOf(-1f, 1f)) {
                val antPath = Path()
                val startX = topCenter.x + (dir * 5f)
                antPath.moveTo(startX, topCenter.y)
                val sway = sin(phase * 2.5f + dir) * 6f
                val antMidX = startX + (dir * 14f) + sway
                val antMidY = topCenter.y - (radius * 0.32f)
                val antTipX = startX + (dir * 24f) + (sway * 1.5f)
                val antTipY = topCenter.y - (radius * 0.55f)
                antPath.cubicTo(antMidX, antMidY, antMidX + dir * 6f, antTipY + 12f, antTipX, antTipY)
                drawPath(antPath, color = color.copy(alpha = 0.95f), style = Stroke(width = 2.4f, cap = StrokeCap.Round))
                drawCircle(color = Color.White, radius = 3.2f, center = Offset(antTipX, antTipY))
                drawCircle(color = color.copy(alpha = 0.60f), radius = 6f, center = Offset(antTipX, antTipY))
            }
        }

        TendrilStyle.WEEPING_WILLOW -> {
            // Tall Willow: Cascading weeping willow fronds draping down both sides like a regal cloak of green strands
            for (side in listOf(-1f, 1f)) {
                for (strand in 0..3) {
                    val strandPath = Path()
                    val startX = topCenter.x + (side * (6f + strand * 7f))
                    strandPath.moveTo(startX, topCenter.y)
                    val sway = sin(phase * 1.2f + strand) * (5f + strand * 2f)
                    val drapeX = startX + (side * (12f + strand * 6f)) + sway
                    val drapeMidY = topCenter.y + (radius * 0.25f)
                    val drapeTipY = topCenter.y + (radius * 0.55f) + (strand * 8f)
                    strandPath.cubicTo(startX + side * 10f, topCenter.y + 10f, drapeX, drapeMidY, drapeX + side * 4f, drapeTipY)
                    drawPath(strandPath, color = color.copy(alpha = 0.75f - strand * 0.12f), style = Stroke(width = 2.0f, cap = StrokeCap.Round))
                    drawCircle(color = color, radius = 2.4f, center = Offset(drapeX + side * 4f, drapeTipY))
                }
            }
        }

        TendrilStyle.STARBURST_TENDRILS -> {
            // Long Strider: 5 radiating bouncy starburst vine shoots with star leaf tips
            val shootCount = 5
            for (i in 0 until shootCount) {
                val angleDeg = -65f + (i * (130f / (shootCount - 1)))
                val angleRad = (angleDeg * PI / 180f).toFloat()
                val len = radius * 0.42f * (1f + sin(phase * 2f + i) * 0.18f)
                val curl = sin(phase * 1.8f + i) * 12f

                val start = Offset(topCenter.x + sin(angleRad) * 4f, topCenter.y)
                val tip = Offset(start.x + sin(angleRad) * len + curl, start.y - cos(angleRad) * len)

                val path = Path().apply {
                    moveTo(start.x, start.y)
                    quadraticBezierTo(start.x + sin(angleRad) * len * 0.5f + curl * 0.6f, start.y - cos(angleRad) * len * 0.5f, tip.x, tip.y)
                }
                drawPath(path, color = color.copy(alpha = 0.85f), style = Stroke(width = 2.8f, cap = StrokeCap.Round))
                drawCircle(color = Color(0xFFFFD54F), radius = 3.6f, center = tip)
            }
        }

        TendrilStyle.GINKGO_CANOPY -> {
            // Bonsai Sage: Multi-tiered Ginkgo biloba fan frond umbrella crown
            for (tier in 0..2) {
                val tierW = radius * (0.60f + tier * 0.16f)
                val tierY = topCenter.y - (10f + tier * 12f)
                val fanPath = Path().apply {
                    moveTo(topCenter.x - tierW * 0.5f, tierY + 6f)
                    cubicTo(
                        topCenter.x - tierW * 0.35f, tierY - 10f,
                        topCenter.x + tierW * 0.35f, tierY - 10f,
                        topCenter.x + tierW * 0.5f, tierY + 6f
                    )
                    close()
                }
                drawPath(fanPath, color = color.copy(alpha = 0.45f + tier * 0.20f))
                drawPath(fanPath, color = color, style = Stroke(width = 1.8f))
            }
        }
    }
}

/**
 * 6. Overhead Tree Branch
 */
private fun DrawScope.drawOverheadTreeBranch(
    center: Offset,
    radius: Float,
    phase: Float,
    color: Color
) {
    val branchY = center.y - radius * 0.44f
    val path = Path().apply {
        moveTo(center.x - radius * 0.55f, branchY + 8f)
        quadraticBezierTo(center.x, branchY - 10f + sin(phase) * 2f, center.x + radius * 0.55f, branchY + 6f)
    }

    drawPath(path, color = color.copy(alpha = 0.85f), style = Stroke(width = 4.2f, cap = StrokeCap.Round))
    drawCircle(color = color, radius = 4f, center = Offset(center.x - radius * 0.28f, branchY - 6f))
    drawCircle(color = color, radius = 5f, center = Offset(center.x + radius * 0.22f, branchY - 7f))
}

/**
 * 7. Celebration Burst / Congratulations Explosion Particles
 */
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
            0 -> Color(0xFFFFD54F) // Warm gold
            1 -> color             // Companion primary state color
            else -> Color(0xFF80CBC4) // Soft mint
        }

        drawCircle(
            color = particleColor.copy(alpha = 0.85f),
            radius = 3.5f + (i % 2) * 2f,
            center = Offset(pX, pY)
        )
    }
}
