package com.cyclecare.domain.model

import kotlinx.datetime.LocalDate

/**
 * Summary of the user's current cycle state — drives the Home Dashboard and Calendar.
 */
data class CycleState(
    val today: LocalDate,
    val cycleDay: Int,
    val cyclePhase: CyclePhase,
    val totalCycleLength: Int = 28,
    val periodLength: Int = 5,
    val nextPeriodDate: LocalDate? = null,
    val predictedWindowStart: LocalDate? = null,
    val predictedWindowEnd: LocalDate? = null,
    val fertileWindowStart: LocalDate? = null,
    val fertileWindowEnd: LocalDate? = null,
    val ovulationDate: LocalDate? = null,
    val daysUntilNextPeriod: Int = 10,
    val conceptionChance: String = "High",
    val trackingMode: TrackingMode = TrackingMode.REGULAR,
    val isPcosMode: Boolean = false
)

/**
 * Science-backed recommendations aligned with specific cycle phases.
 */
data class CycleSyncingGuide(
    val phase: CyclePhase,
    val movement: MovementGuide,
    val nutrition: NutritionGuide,
    val mindset: MindsetGuide
)

data class MovementGuide(
    val title: String,
    val recommendedActivities: List<String>,
    val intensity: String,
    val cortisolWarning: String? = null
)

data class NutritionGuide(
    val title: String,
    val focusFoods: List<String>,
    val seedCycling: String,
    val keyMicronutrients: List<String>
)

data class MindsetGuide(
    val energyType: String,
    val idealFocus: String,
    val socialStamina: String
)

/**
 * Prospective screening data comparing luteal phase emotional distress vs. follicular phase.
 */
data class PmddScreenerData(
    val averageLutealScore: Float,     // Scale 1-5
    val averageFollicularScore: Float, // Scale 1-5
    val isSymptomClusterDetected: Boolean,
    val diagnosticSummary: String
)

/**
 * Long-term metrics for the Insights Dashboard.
 */
data class CycleInsightSummary(
    val averageCycleLength: Int,
    val averagePeriodLength: Int,
    val cycleVariationDays: Int,
    val regularityScore: Int,
    val recordedCyclesCount: Int,
    val longestCycle: Int,
    val shortestCycle: Int
)

/**
 * Visual arc geometry data for CycleDialVisualization.
 */
data class DialArcData(
    val phase: CyclePhase,
    val startAngleDeg: Float,
    val sweepAngleDeg: Float,
    val strokeWidthDp: Float,
    val color: androidx.compose.ui.graphics.Color
)
