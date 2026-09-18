package com.cyclecare.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cyclecare.domain.model.CycleState
import com.cyclecare.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

/**
 * Draws a 4-phase cycle dial based on live [CycleState].
 *
 * Arc proportions are derived from the cycle's totalCycleLength so the
 * visualization adapts to irregular / PCOS cycles automatically.
 */
@Composable
fun CycleDialVisualization(
    cycleState: CycleState,
    modifier: Modifier = Modifier,
    size: Dp = 256.dp,
    onDialClick: (() -> Unit)? = null
) {
    val totalDays = cycleState.totalCycleLength.coerceAtLeast(21).toFloat()
    val menstrualDays = cycleState.periodLength.coerceIn(2, 7).toFloat()
    val ovulatoryDays = 5f   // typically Days 13-17
    val lutealDays = 14f     // fixed luteal phase
    val follicularDays = (totalDays - menstrualDays - ovulatoryDays - lutealDays).coerceAtLeast(3f)

    val currentDay = cycleState.cycleDay.coerceIn(1, totalDays.toInt())
    val positionAngleDegrees = ((currentDay - 1) / totalDays) * 360f

    // Subtle animated pulse on the position pin
    val infiniteTransition = rememberInfiniteTransition(label = "pin_pulse")
    val pinPulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pin_scale"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val radius = this.size.width * 0.38f
            val baseStroke = this.size.width * 0.055f
            val ovulationStroke = this.size.width * 0.07f

            // Base ring
            drawArcSegment(center, radius, ArcBase, 0f, 360f, baseStroke)

            // Menstrual arc (starts at top, -90°)
            val menstrualSweep = (menstrualDays / totalDays) * 360f
            drawArcSegment(center, radius, ArcMenstrual, -90f, menstrualSweep, baseStroke)

            // Follicular arc
            val follicularStart = -90f + menstrualSweep
            val follicularSweep = (follicularDays / totalDays) * 360f
            drawArcSegment(center, radius, ArcFollicular, follicularStart, follicularSweep, baseStroke)

            // Ovulatory arc (wider stroke)
            val ovulationStart = follicularStart + follicularSweep
            val ovulationSweep = (ovulatoryDays / totalDays) * 360f
            drawArcSegment(center, radius, ArcOvulation, ovulationStart, ovulationSweep, ovulationStroke)

            // Luteal arc
            val lutealStart = ovulationStart + ovulationSweep
            val lutealSweep = (lutealDays / totalDays) * 360f
            drawArcSegment(center, radius, ArcLuteal, lutealStart, lutealSweep, baseStroke)

            // Glowing position pin
            val pinAngleRad = Math.toRadians((positionAngleDegrees - 90.0)).toFloat()
            val pinX = center.x + radius * cos(pinAngleRad)
            val pinY = center.y + radius * sin(pinAngleRad)
            val pinOuter = this.size.width * 0.031f * pinPulse
            val pinInner = this.size.width * 0.020f

            drawCircle(color = ArcPinOuter, radius = pinOuter, center = Offset(pinX, pinY))
            drawCircle(color = ArcPinInner, radius = pinInner, center = Offset(pinX, pinY))
        }

        // Center telemetry
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            Text(
                text = "CURRENT CYCLE",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.5.sp
            )
            Text(
                text = "Day $currentDay",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 40.sp
            )
        }
    }
}

private fun DrawScope.drawArcSegment(
    center: Offset,
    radius: Float,
    color: Color,
    startAngle: Float,
    sweepAngle: Float,
    strokeWidth: Float
) {
    val topLeft = Offset(center.x - radius, center.y - radius)
    drawArc(
        color = color,
        startAngle = startAngle,
        sweepAngle = sweepAngle,
        useCenter = false,
        topLeft = topLeft,
        size = Size(radius * 2, radius * 2),
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
    )
}
