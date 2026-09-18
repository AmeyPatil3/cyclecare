package com.cyclecare.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.cyclecare.domain.model.CycleDialData
import com.cyclecare.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

/**
 * Faithful Compose reproduction of the Stitch home_dashboard.html SVG cycle dial.
 *
 * Draws 4 arcs (Follicular, Ovulation, Luteal, Menstrual) on a base ring,
 * plus a glowing position pin at [dialData.currentDay].
 */
@Composable
fun CycleDialVisualization(
    dialData: CycleDialData,
    modifier: Modifier = Modifier,
    size: Dp = 256.dp
) {
    // Subtle animated pulse on the pin
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
            val radius = this.size.width * 0.38f   // ~98/256 ratio from Stitch SVG
            val baseStroke = this.size.width * 0.055f   // 14px / 256
            val ovulationStroke = this.size.width * 0.07f  // 18px / 256

            // ── Base ring ────────────────────────────────────────────────
            drawArcSegment(center, radius, ArcBase, 0f, 360f, baseStroke)

            // ── Follicular arc (Days 6–12 ≈ first ~50% minus period) ───
            val follicularSweep = (dialData.follicularDays.count().toFloat() / dialData.totalDays) * 360f
            val follicularStart = (dialData.menstrualDays.count().toFloat() / dialData.totalDays) * 360f - 90f
            drawArcSegment(center, radius, ArcFollicular, follicularStart, follicularSweep, baseStroke)

            // ── Ovulation arc (Days 13–18, wider stroke) ─────────────────
            val ovulationSweep = (dialData.ovulatoryDays.count().toFloat() / dialData.totalDays) * 360f
            val ovulationStart = ((dialData.menstrualDays.count() + dialData.follicularDays.count()).toFloat() / dialData.totalDays) * 360f - 90f
            drawArcSegment(center, radius, ArcOvulation, ovulationStart, ovulationSweep, ovulationStroke)

            // ── Luteal arc ────────────────────────────────────────────────
            val lutealSweep = (dialData.lutealDays.count().toFloat() / dialData.totalDays) * 360f
            val lutealStart = ovulationStart + ovulationSweep
            drawArcSegment(center, radius, ArcLuteal, lutealStart, lutealSweep, baseStroke)

            // ── Menstrual arc ─────────────────────────────────────────────
            val menstrualSweep = (dialData.menstrualDays.count().toFloat() / dialData.totalDays) * 360f
            drawArcSegment(center, radius, ArcMenstrual, -90f, menstrualSweep, baseStroke)

            // ── Glowing position pin ──────────────────────────────────────
            val pinAngleRad = Math.toRadians((dialData.positionAngleDegrees - 90f).toDouble()).toFloat()
            val pinX = center.x + radius * cos(pinAngleRad)
            val pinY = center.y + radius * sin(pinAngleRad)
            val pinOuter = this.size.width * 0.031f * pinPulse   // outer white circle
            val pinInner = this.size.width * 0.020f               // inner colored circle

            // Outer white circle (glow shadow simulation)
            drawCircle(color = ArcPinOuter, radius = pinOuter, center = Offset(pinX, pinY))
            // Inner rose circle
            drawCircle(color = ArcPinInner, radius = pinInner, center = Offset(pinX, pinY))
        }

        // ── Center telemetry ─────────────────────────────────────────────
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
                text = "Day ${dialData.currentDay}",
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
