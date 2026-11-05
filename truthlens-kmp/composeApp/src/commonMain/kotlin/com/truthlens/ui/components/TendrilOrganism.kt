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

/**
 * TendrilOrganism — Living Botanical Sprout Child Companion
 *
 * Designed to be lovable, warm, and playful:
 * - Leaf-mask face with expressive bioluminescent eyes that blink and smile
 * - Cute chubby toddler-sprout body with little leaf-mitten hands that explain & gesture
 * - Plump rootlet feet that do joyful hops during teaching and conversations
 * - Sprouting overhead branch where the companion can hang playfully
 * - Celebration explosion of floral sparkles, petals, and golden spores on accomplishments
 * - Bolder silhouette with the signature soothing bioluminescent aura
 */
@Composable
fun TendrilOrganism(
    state: CompanionState,
    modifier: Modifier = Modifier,
    size: Dp = 140.dp
) {
    var isInteractiveTapped by remember { mutableStateOf(false) }
    var tapCounter by remember { mutableStateOf(0) }

    val infiniteTransition = rememberInfiniteTransition()

    // Phase angle for continuous organic gestures and breathing
    val animPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (state) {
                    CompanionState.SPEAKING -> 1800
                    CompanionState.THINKING -> 2400
                    CompanionState.LISTENING -> 2200
                    CompanionState.COMPLETED -> 1400
                    else -> 3200
                },
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        )
    )

    // Gentle breathing pulse
    val breathScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Natural eye blink timer (blinks every ~3.5 seconds)
    val blinkProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 3600
                0.0f at 0
                0.0f at 3300
                1.0f at 3450 // Closed
                0.0f at 3600 // Open
            },
            repeatMode = RepeatMode.Restart
        )
    )

    // Hopping bounce (active during speaking, completed celebration, or on tap)
    val isHopping = state == CompanionState.COMPLETED ||
            state == CompanionState.SPEAKING ||
            isInteractiveTapped

    val hopOffset = if (isHopping) {
        abs(sin(animPhase * 2f)) * 12f
    } else {
        sin(animPhase) * 2.5f
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
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val center = Offset(this.size.width / 2f, (this.size.height / 2f) - hopOffset)
            val baseRadius = this.size.minDimension / 2f

            // 1. Ambient Bioluminescent Glow Aura
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        auraColor.copy(alpha = 0.45f),
                        auraColor.copy(alpha = 0.12f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = baseRadius * breathScale * 1.05f
                ),
                radius = baseRadius * breathScale * 1.05f,
                center = center
            )

            // 2. Celebration Sparkle Burst / Congratulations Particles
            if (state == CompanionState.COMPLETED || (tapCounter % 3 == 0 && tapCounter > 0)) {
                drawCelebrationBurst(
                    center = center,
                    radius = baseRadius * 0.9f,
                    phase = animPhase,
                    color = primaryColor
                )
            }

            // 3. Overhead Tree Branch (Sprouts and lets the companion hang when completed or tapped)
            val showBranch = state == CompanionState.COMPLETED || tapCounter % 2 == 1
            if (showBranch) {
                drawOverheadTreeBranch(
                    center = center,
                    radius = baseRadius,
                    phase = animPhase,
                    color = primaryColor
                )
            }

            // 4. Little Plump Rootlet Legs & Feet
            drawChildLegs(
                center = center,
                radius = baseRadius,
                phase = animPhase,
                color = primaryColor,
                isHopping = isHopping,
                isHanging = showBranch
            )

            // 5. Chubby Sprout Torso
            drawChildTorso(
                center = center,
                radius = baseRadius,
                color = primaryColor,
                auraColor = auraColor,
                breathScale = breathScale
            )

            // 6. Expressive Vine Arms & Leaf-Mitten Hands
            drawChildArms(
                center = center,
                radius = baseRadius,
                phase = animPhase,
                state = state,
                color = primaryColor,
                isHanging = showBranch
            )

            // 7. Sweet Heart-Leaf Mask Face & Expressive Glowing Eyes
            drawLeafFace(
                center = center,
                radius = baseRadius,
                phase = animPhase,
                state = state,
                color = primaryColor,
                auraColor = auraColor,
                blinkProgress = blinkProgress
            )

            // 8. Crown Seedling Sprout & Tender Thought Tendrils
            drawCrownAndThoughtTendrils(
                center = center,
                radius = baseRadius,
                phase = animPhase,
                state = state,
                color = primaryColor
            )
        }
    }
}

/**
 * 1. Heart-shaped seedling leaf face with expressive glowing eyes and gentle smile
 */
private fun DrawScope.drawLeafFace(
    center: Offset,
    radius: Float,
    phase: Float,
    state: CompanionState,
    color: Color,
    auraColor: Color,
    blinkProgress: Float
) {
    val faceCenter = Offset(center.x, center.y - radius * 0.18f)
    val faceW = radius * 0.62f
    val faceH = radius * 0.58f

    // Head tilt during thinking or listening
    val headTilt = when (state) {
        CompanionState.THINKING -> sin(phase) * 6f
        CompanionState.LISTENING -> 8f
        else -> sin(phase * 0.5f) * 2.5f
    }

    rotate(headTilt, pivot = faceCenter) {
        // Leaf face contour (curved heart/spade leaf)
        val leafFacePath = Path().apply {
            moveTo(faceCenter.x, faceCenter.y - faceH * 0.52f)
            // Left heart leaf lobe
            cubicTo(
                faceCenter.x - faceW * 0.55f, faceCenter.y - faceH * 0.65f,
                faceCenter.x - faceW * 0.65f, faceCenter.y + faceH * 0.15f,
                faceCenter.x, faceCenter.y + faceH * 0.52f
            )
            // Right heart leaf lobe
            cubicTo(
                faceCenter.x + faceW * 0.65f, faceCenter.y + faceH * 0.15f,
                faceCenter.x + faceW * 0.55f, faceCenter.y - faceH * 0.65f,
                faceCenter.x, faceCenter.y - faceH * 0.52f
            )
            close()
        }

        // Face fill with soft radial organic gradient
        drawPath(
            path = leafFacePath,
            brush = Brush.radialGradient(
                colors = listOf(
                    color.copy(alpha = 0.28f),
                    color.copy(alpha = 0.16f),
                    auraColor.copy(alpha = 0.08f)
                ),
                center = faceCenter,
                radius = faceW * 0.65f
            )
        )

        // Soft outer leaf mask border
        drawPath(
            path = leafFacePath,
            color = color.copy(alpha = 0.85f),
            style = Stroke(width = 3f, cap = StrokeCap.Round)
        )

        // Subtle organic leaf vein running down the forehead
        drawLine(
            color = color.copy(alpha = 0.35f),
            start = Offset(faceCenter.x, faceCenter.y - faceH * 0.45f),
            end = Offset(faceCenter.x, faceCenter.y - faceH * 0.05f),
            strokeWidth = 1.5f,
            cap = StrokeCap.Round
        )

        // --- EXPRESSIVE EYES ---
        val eyeSpacing = faceW * 0.24f
        val eyeY = faceCenter.y - faceH * 0.04f
        val eyeRadius = faceW * 0.085f

        val isHappySmileEyes = state == CompanionState.COMPLETED

        if (isHappySmileEyes) {
            // Happy crescent eyes: (^ ^)
            val leftEyeArc = Path().apply {
                moveTo(faceCenter.x - eyeSpacing - eyeRadius, eyeY)
                quadraticBezierTo(faceCenter.x - eyeSpacing, eyeY - eyeRadius * 1.1f, faceCenter.x - eyeSpacing + eyeRadius, eyeY)
            }
            val rightEyeArc = Path().apply {
                moveTo(faceCenter.x + eyeSpacing - eyeRadius, eyeY)
                quadraticBezierTo(faceCenter.x + eyeSpacing, eyeY - eyeRadius * 1.1f, faceCenter.x + eyeSpacing + eyeRadius, eyeY)
            }
            drawPath(leftEyeArc, color = color, style = Stroke(width = 3.5f, cap = StrokeCap.Round))
            drawPath(rightEyeArc, color = color, style = Stroke(width = 3.5f, cap = StrokeCap.Round))
        } else {
            // Gentle glowing anime eyes with natural blink
            val openHeight = (1f - blinkProgress).coerceAtLeast(0.1f)

            // Left Eye
            drawOval(
                color = color,
                topLeft = Offset(faceCenter.x - eyeSpacing - eyeRadius, eyeY - (eyeRadius * openHeight)),
                size = androidx.compose.ui.geometry.Size(eyeRadius * 2f, eyeRadius * 2f * openHeight)
            )
            // Left Eye Glimmer / Pupil sparkle
            if (openHeight > 0.4f) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.9f),
                    radius = eyeRadius * 0.35f,
                    center = Offset(faceCenter.x - eyeSpacing - eyeRadius * 0.25f, eyeY - eyeRadius * 0.25f)
                )
            }

            // Right Eye
            drawOval(
                color = color,
                topLeft = Offset(faceCenter.x + eyeSpacing - eyeRadius, eyeY - (eyeRadius * openHeight)),
                size = androidx.compose.ui.geometry.Size(eyeRadius * 2f, eyeRadius * 2f * openHeight)
            )
            // Right Eye Glimmer
            if (openHeight > 0.4f) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.9f),
                    radius = eyeRadius * 0.35f,
                    center = Offset(faceCenter.x + eyeSpacing - eyeRadius * 0.25f, eyeY - eyeRadius * 0.25f)
                )
            }
        }

        // --- CUTE MOUTH ---
        val mouthY = faceCenter.y + faceH * 0.22f
        val mouthW = faceW * 0.12f

        if (state == CompanionState.SPEAKING) {
            // Open, happy talking mouth that animates with speech rhythm
            val mouthOpen = (abs(sin(phase * 4f)) * 4f) + 2f
            val mouthPath = Path().apply {
                moveTo(faceCenter.x - mouthW, mouthY)
                quadraticBezierTo(faceCenter.x, mouthY + mouthOpen + 3f, faceCenter.x + mouthW, mouthY)
                quadraticBezierTo(faceCenter.x, mouthY + mouthOpen * 0.3f, faceCenter.x - mouthW, mouthY)
            }
            drawPath(mouthPath, color = color.copy(alpha = 0.85f))
        } else {
            // Sweet gentle smile
            val mouthPath = Path().apply {
                moveTo(faceCenter.x - mouthW, mouthY)
                quadraticBezierTo(faceCenter.x, mouthY + 3.5f, faceCenter.x + mouthW, mouthY)
            }
            drawPath(mouthPath, color = color.copy(alpha = 0.85f), style = Stroke(width = 2.2f, cap = StrokeCap.Round))
        }

        // Soft pastel blush on cheeks
        drawCircle(
            color = Color(0xFFFF8A80).copy(alpha = 0.22f),
            radius = faceW * 0.08f,
            center = Offset(faceCenter.x - eyeSpacing - faceW * 0.08f, eyeY + faceH * 0.15f)
        )
        drawCircle(
            color = Color(0xFFFF8A80).copy(alpha = 0.22f),
            radius = faceW * 0.08f,
            center = Offset(faceCenter.x + eyeSpacing + faceW * 0.08f, eyeY + faceH * 0.15f)
        )
    }
}

/**
 * 2. Plump toddler sprout torso
 */
private fun DrawScope.drawChildTorso(
    center: Offset,
    radius: Float,
    color: Color,
    auraColor: Color,
    breathScale: Float
) {
    val bodyCenter = Offset(center.x, center.y + radius * 0.22f)
    val bodyW = radius * 0.38f * breathScale
    val bodyH = radius * 0.32f * breathScale

    // Pear-shaped soft seedling tummy
    val bodyPath = Path().apply {
        moveTo(bodyCenter.x, bodyCenter.y - bodyH * 0.5f)
        cubicTo(
            bodyCenter.x - bodyW * 0.6f, bodyCenter.y - bodyH * 0.2f,
            bodyCenter.x - bodyW * 0.55f, bodyCenter.y + bodyH * 0.5f,
            bodyCenter.x, bodyCenter.y + bodyH * 0.5f
        )
        cubicTo(
            bodyCenter.x + bodyW * 0.55f, bodyCenter.y + bodyH * 0.5f,
            bodyCenter.x + bodyW * 0.6f, bodyCenter.y - bodyH * 0.2f,
            bodyCenter.x, bodyCenter.y - bodyH * 0.5f
        )
        close()
    }

    drawPath(
        path = bodyPath,
        brush = Brush.radialGradient(
            colors = listOf(
                color.copy(alpha = 0.3f),
                auraColor.copy(alpha = 0.14f)
            ),
            center = bodyCenter,
            radius = bodyW
        )
    )
    drawPath(
        path = bodyPath,
        color = color.copy(alpha = 0.75f),
        style = Stroke(width = 2.8f, cap = StrokeCap.Round)
    )
}

/**
 * 3. Expressive vine arms & leaf-mitten hands that gesture, fold, or reach
 */
private fun DrawScope.drawChildArms(
    center: Offset,
    radius: Float,
    phase: Float,
    state: CompanionState,
    color: Color,
    isHanging: Boolean
) {
    val shoulderY = center.y + radius * 0.08f
    val shoulderLeft = Offset(center.x - radius * 0.16f, shoulderY)
    val shoulderRight = Offset(center.x + radius * 0.16f, shoulderY)

    val armLength = radius * 0.28f

    if (isHanging) {
        // Both hands reaching up grasping the overhead branch
        val leftHand = Offset(shoulderLeft.x - radius * 0.05f, center.y - radius * 0.42f)
        val rightHand = Offset(shoulderRight.x + radius * 0.05f, center.y - radius * 0.42f)

        drawVineArm(shoulderLeft, leftHand, color)
        drawVineArm(shoulderRight, rightHand, color)
        drawLeafMitten(leftHand, angle = -80f, color = color)
        drawLeafMitten(rightHand, angle = 80f, color = color)
        return
    }

    when (state) {
        CompanionState.SPEAKING -> {
            // Actively explaining with hands: alternating gesturing left and right
            val leftGesture = sin(phase * 2f) * radius * 0.12f
            val rightGesture = cos(phase * 2f) * radius * 0.12f

            val leftHand = Offset(shoulderLeft.x - armLength - leftGesture, shoulderY + radius * 0.05f - leftGesture)
            val rightHand = Offset(shoulderRight.x + armLength + rightGesture, shoulderY + radius * 0.05f - rightGesture)

            drawVineArm(shoulderLeft, leftHand, color)
            drawVineArm(shoulderRight, rightHand, color)
            drawLeafMitten(leftHand, angle = -35f + sin(phase * 2f) * 20f, color = color)
            drawLeafMitten(rightHand, angle = 35f - cos(phase * 2f) * 20f, color = color)
        }

        CompanionState.THINKING -> {
            // One hand touching chin in curious wonder, other arm folded
            val chinHand = Offset(center.x - radius * 0.04f, center.y - radius * 0.05f)
            val foldedHand = Offset(shoulderRight.x + radius * 0.12f, shoulderY + radius * 0.12f)

            drawVineArm(shoulderLeft, chinHand, color)
            drawVineArm(shoulderRight, foldedHand, color)
            drawLeafMitten(chinHand, angle = -70f, color = color)
            drawLeafMitten(foldedHand, angle = 20f, color = color)
        }

        CompanionState.LISTENING -> {
            // Cupping ear with one hand, other hand resting
            val earHand = Offset(center.x + radius * 0.32f, center.y - radius * 0.18f)
            val restHand = Offset(shoulderLeft.x - radius * 0.12f, shoulderY + radius * 0.14f)

            drawVineArm(shoulderLeft, restHand, color)
            drawVineArm(shoulderRight, earHand, color)
            drawLeafMitten(restHand, angle = -15f, color = color)
            drawLeafMitten(earHand, angle = 60f, color = color)
        }

        CompanionState.COMPLETED -> {
            // Hands raised high in joyful celebration!
            val leftHand = Offset(shoulderLeft.x - radius * 0.22f, center.y - radius * 0.22f)
            val rightHand = Offset(shoulderRight.x + radius * 0.22f, center.y - radius * 0.22f)

            drawVineArm(shoulderLeft, leftHand, color)
            drawVineArm(shoulderRight, rightHand, color)
            drawLeafMitten(leftHand, angle = -120f, color = color)
            drawLeafMitten(rightHand, angle = 120f, color = color)
        }

        else -> {
            // Idle / Leaning: One arm folded, resting comfortably
            val leftHand = Offset(shoulderLeft.x - radius * 0.14f, shoulderY + radius * 0.14f + sin(phase) * 3f)
            val rightHand = Offset(shoulderRight.x + radius * 0.14f, shoulderY + radius * 0.14f - sin(phase) * 3f)

            drawVineArm(shoulderLeft, leftHand, color)
            drawVineArm(shoulderRight, rightHand, color)
            drawLeafMitten(leftHand, angle = -20f, color = color)
            drawLeafMitten(rightHand, angle = 20f, color = color)
        }
    }
}

private fun DrawScope.drawVineArm(start: Offset, end: Offset, color: Color) {
    val mid = Offset((start.x + end.x) / 2f + 4f, (start.y + end.y) / 2f + 4f)
    val armPath = Path().apply {
        moveTo(start.x, start.y)
        quadraticBezierTo(mid.x, mid.y, end.x, end.y)
    }
    drawPath(armPath, color = color.copy(alpha = 0.85f), style = Stroke(width = 3.2f, cap = StrokeCap.Round))
}

private fun DrawScope.drawLeafMitten(position: Offset, angle: Float, color: Color) {
    rotate(angle, pivot = position) {
        val mittenW = 14f
        val mittenH = 20f
        val mittenPath = Path().apply {
            moveTo(position.x, position.y)
            cubicTo(
                position.x - mittenW * 0.5f, position.y - mittenH * 0.4f,
                position.x - mittenW * 0.4f, position.y - mittenH,
                position.x, position.y - mittenH
            )
            cubicTo(
                position.x + mittenW * 0.4f, position.y - mittenH,
                position.x + mittenW * 0.5f, position.y - mittenH * 0.4f,
                position.x, position.y
            )
            close()
        }
        drawPath(mittenPath, color = color.copy(alpha = 0.85f))
        drawPath(mittenPath, color = Color.White.copy(alpha = 0.35f), style = Stroke(width = 1f))
    }
}

/**
 * 4. Chubby rootlet legs & feet that do little bouncy hops
 */
private fun DrawScope.drawChildLegs(
    center: Offset,
    radius: Float,
    phase: Float,
    color: Color,
    isHopping: Boolean,
    isHanging: Boolean
) {
    val hipY = center.y + radius * 0.36f
    val legSpacing = radius * 0.12f

    if (isHanging) {
        // Legs dangling and kicking joyfully in the air
        val leftKick = sin(phase * 3f) * 6f
        val rightKick = cos(phase * 3f) * 6f

        val leftFoot = Offset(center.x - legSpacing, hipY + radius * 0.14f + leftKick)
        val rightFoot = Offset(center.x + legSpacing, hipY + radius * 0.14f + rightKick)

        drawRootletLeg(Offset(center.x - legSpacing, hipY), leftFoot, color)
        drawRootletLeg(Offset(center.x + legSpacing, hipY), rightFoot, color)
        return
    }

    val hopLiftLeft = if (isHopping) sin(phase * 2f).coerceAtLeast(0f) * 4f else 0f
    val hopLiftRight = if (isHopping) cos(phase * 2f).coerceAtLeast(0f) * 4f else 0f

    val leftFoot = Offset(center.x - legSpacing, hipY + radius * 0.12f - hopLiftLeft)
    val rightFoot = Offset(center.x + legSpacing, hipY + radius * 0.12f - hopLiftRight)

    drawRootletLeg(Offset(center.x - legSpacing, hipY), leftFoot, color)
    drawRootletLeg(Offset(center.x + legSpacing, hipY), rightFoot, color)
}

private fun DrawScope.drawRootletLeg(hip: Offset, foot: Offset, color: Color) {
    // Soft curved rootlet leg
    val legPath = Path().apply {
        moveTo(hip.x, hip.y)
        quadraticBezierTo(hip.x + 2f, (hip.y + foot.y) / 2f, foot.x, foot.y)
    }
    drawPath(legPath, color = color.copy(alpha = 0.85f), style = Stroke(width = 4f, cap = StrokeCap.Round))

    // Cute rounded foot pad
    drawOval(
        color = color,
        topLeft = Offset(foot.x - 7f, foot.y - 2f),
        size = androidx.compose.ui.geometry.Size(14f, 8f)
    )
}

/**
 * 5. Crown seedling sprout on head & dynamic flowing thought-tendrils
 */
private fun DrawScope.drawCrownAndThoughtTendrils(
    center: Offset,
    radius: Float,
    phase: Float,
    state: CompanionState,
    color: Color
) {
    val crownTop = Offset(center.x, center.y - radius * 0.48f)

    // Crown bud / sprout leaves
    val crownW = 12f
    val crownH = 18f
    val leftSprout = Path().apply {
        moveTo(crownTop.x, crownTop.y)
        quadraticBezierTo(crownTop.x - crownW, crownTop.y - crownH * 0.6f, crownTop.x - crownW * 0.8f, crownTop.y - crownH)
        quadraticBezierTo(crownTop.x - crownW * 0.2f, crownTop.y - crownH * 0.8f, crownTop.x, crownTop.y)
    }
    val rightSprout = Path().apply {
        moveTo(crownTop.x, crownTop.y)
        quadraticBezierTo(crownTop.x + crownW, crownTop.y - crownH * 0.6f, crownTop.x + crownW * 0.8f, crownTop.y - crownH)
        quadraticBezierTo(crownTop.x + crownW * 0.2f, crownTop.y - crownH * 0.8f, crownTop.x, crownTop.y)
    }
    drawPath(leftSprout, color = color.copy(alpha = 0.9f))
    drawPath(rightSprout, color = color.copy(alpha = 0.9f))

    // Flowing thought tendrils when active (Speaking, Thinking, Listening)
    val isActive = state == CompanionState.THINKING ||
            state == CompanionState.SPEAKING ||
            state == CompanionState.LISTENING

    if (isActive) {
        val tendrilCount = if (state == CompanionState.SPEAKING) 4 else 3
        for (i in 0 until tendrilCount) {
            val angleDeg = -50f + (i * (100f / (tendrilCount - 1)))
            val angleRad = (angleDeg * PI / 180f).toFloat()

            val length = radius * 0.38f * (1f + sin(phase + i) * 0.15f)
            val curl = sin(phase * 1.5f + i) * 16f

            val start = Offset(crownTop.x + sin(angleRad) * 4f, crownTop.y)
            val mid = Offset(
                start.x + sin(angleRad) * length * 0.5f + curl,
                start.y - cos(angleRad) * length * 0.5f
            )
            val tip = Offset(
                start.x + sin(angleRad) * length + curl * 1.5f,
                start.y - cos(angleRad) * length
            )

            val tendrilPath = Path().apply {
                moveTo(start.x, start.y)
                quadraticBezierTo(mid.x, mid.y, tip.x, tip.y)
            }

            drawPath(
                path = tendrilPath,
                color = color.copy(alpha = 0.6f - (i * 0.08f)),
                style = Stroke(width = 2.2f, cap = StrokeCap.Round)
            )

            // Bioluminescent tip glow
            drawCircle(
                color = Color.White.copy(alpha = 0.85f),
                radius = 2.5f,
                center = tip
            )
        }
    }
}

/**
 * 6. Overhead tree branch where companion can grab on and hang playfully
 */
private fun DrawScope.drawOverheadTreeBranch(
    center: Offset,
    radius: Float,
    phase: Float,
    color: Color
) {
    val branchY = center.y - radius * 0.44f
    val branchPath = Path().apply {
        moveTo(center.x - radius * 0.55f, branchY + 8f)
        quadraticBezierTo(center.x, branchY - 10f + sin(phase) * 2f, center.x + radius * 0.55f, branchY + 6f)
    }

    drawPath(
        path = branchPath,
        color = color.copy(alpha = 0.85f),
        style = Stroke(width = 4.5f, cap = StrokeCap.Round)
    )

    // Little sprout leaves along the branch
    drawCircle(color = color, radius = 4f, center = Offset(center.x - radius * 0.3f, branchY - 6f))
    drawCircle(color = color, radius = 5f, center = Offset(center.x + radius * 0.25f, branchY - 7f))
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

        // Star / Petal sparkle
        drawCircle(
            color = particleColor.copy(alpha = 0.85f),
            radius = 3.5f + (i % 2) * 2f,
            center = Offset(pX, pY)
        )
    }
}
