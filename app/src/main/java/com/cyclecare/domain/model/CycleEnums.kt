package com.cyclecare.domain.model

import androidx.compose.ui.graphics.Color
import com.cyclecare.ui.theme.*

/**
 * Represents the four phases of the menstrual cycle.
 */
enum class CyclePhase(
    val displayName: String,
    val description: String,
    val approximateDayRange: String
) {
    MENSTRUAL(
        displayName = "Menstrual",
        description = "Uterine lining sheds; progesterone and estrogen are at baseline. Prioritize warm nourishment and restorative rest.",
        approximateDayRange = "Days 1–5"
    ),
    FOLLICULAR(
        displayName = "Follicular",
        description = "Estrogen and FSH rise as follicles mature. Neuroplasticity and physical energy rebound rapidly.",
        approximateDayRange = "Days 6–13"
    ),
    OVULATORY(
        displayName = "Ovulatory",
        description = "Estrogen peaks and triggers LH surge to release an egg. Maximum communicative vitality and fertility.",
        approximateDayRange = "Days 14–18"
    ),
    LUTEAL(
        displayName = "Luteal",
        description = "Progesterone dominates to sustain the corpus luteum. Metabolic demand increases; prioritize steady strength and calm.",
        approximateDayRange = "Days 19–28"
    );

    val themeColor: Color
        get() = when (this) {
            MENSTRUAL -> RoseMenstrual
            FOLLICULAR -> SageFollicular
            OVULATORY -> CoralPink
            LUTEAL -> LavenderLuteal
        }
}

/**
 * Tracking mode for personalized experience and adapted algorithms.
 */
enum class TrackingMode(val displayName: String, val subtitle: String) {
    REGULAR("Standard Cycle", "Predicts based on historical 24–35 day averages."),
    PCOS_IRREGULAR("PCOS / Irregular Mode", "Uses adaptive prediction ranges instead of rigid dates; filters hormonal outliers."),
    PMDD_FOCUSED("PMDD Mood Screener", "Specialized prospective tracking for luteal dysphoria and sensory distress."),
    PERIMENOPAUSE("Perimenopause Support", "Monitors cycle frequency shifts, hot flashes, and hormonal transitions.")
}

/**
 * Cervical fluid biomarker — the most accurate natural indicator of estrogen surge and fertile window.
 */
enum class CervicalMucusType(val displayName: String, val fertilityImpact: String) {
    DRY("Dry / None", "Low Fertility"),
    STICKY("Sticky / Tacky", "Low Fertility"),
    CREAMY("Creamy / Lotion-like", "Rising Fertility"),
    WATERY("Watery / Slippery", "High Fertility"),
    EGG_WHITE("Egg-White / Stretchy", "Peak Fertility (Ovulation Imminent)")
}

/**
 * Luteinizing Hormone (LH) strip biomarker.
 */
enum class LhTestResult(val displayName: String) {
    NOT_TESTED("Not Tested"),
    NEGATIVE("Negative / Baseline"),
    HIGH("High LH"),
    PEAK_SURGE("Peak LH Surge (Ovulation in 12–36h)")
}

/**
 * Daily supplements & medications commonly used in cycle and hormonal health.
 */
enum class SupplementType(val displayName: String, val category: String) {
    BIRTH_CONTROL("Contraceptive Pill", "Hormonal"),
    INOSITOL("Myo-Inositol", "PCOS / Insulin"),
    MAGNESIUM("Magnesium Glycinate", "Sleep / Cramps"),
    IRON("Iron / Ferritin", "Menstrual"),
    VITAMIN_D("Vitamin D3", "Endocrine"),
    OMEGA_3("Omega-3 EPA/DHA", "Anti-inflammatory"),
    FOLIC_ACID("Folic Acid", "Fertility / Prenatal")
}

/**
 * Events that temporarily affect hormonal timing without representing long-term cycle changes.
 */
enum class EmergencyEvent(val displayName: String, val affectsCycleLength: Boolean) {
    NONE("None", false),
    EMERGENCY_CONTRACEPTION("Emergency Contraception (Plan B / Ella)", true),
    MEDICATION_CHANGE("Medication Adjustment", true),
    ACUTE_ILLNESS_FEVER("High Fever / Acute Stress", true)
}

enum class FlowIntensity(val displayName: String) {
    SPOTTING("Spotting"),
    LIGHT("Light"),
    MEDIUM("Medium"),
    HEAVY("Heavy")
}

enum class MoodLevel(val displayName: String, val emoji: String) {
    GREAT("Great", "😄"),
    GOOD("Good", "🙂"),
    OKAY("Okay", "😐"),
    LOW("Low", "😔"),
    VERY_LOW("Very Low", "😣")
}

enum class PainLocation(val displayName: String) {
    NONE("None"),
    LOWER_ABDOMEN("Lower Abdomen"),
    LOWER_BACK("Lower Back"),
    HEADACHE("Headache"),
    BREASTS("Breasts"),
    JOINTS("Joints / Muscles"),
    OVARY_PAIN("Mittelschmerz (Ovulation Pain)")
}

enum class PainDuration(val displayName: String) {
    MOMENTARY("Momentary"),
    INTERMITTENT("Intermittent"),
    CONSTANT("Constant")
}

enum class SleepQuality(val displayName: String) {
    RESTFUL("Restful"),
    INTERRUPTED("Interrupted"),
    DEEP("Deep"),
    INSOMNIA("Insomnia")
}

enum class AppetiteLevel(val displayName: String) {
    LOW("Low"),
    NORMAL("Normal"),
    HIGH("High / Cravings")
}

enum class Symptom(val displayName: String, val category: String) {
    // Physical
    CRAMPS("Pelvic Cramps", "Physical"),
    HEADACHE("Headache / Migraine", "Physical"),
    BLOATING("Bloating", "Physical"),
    BREAST_TENDERNESS("Breast Tenderness", "Physical"),
    ACNE("Acne Flare-up", "Physical"),
    FATIGUE("Fatigue / Low Stamina", "Physical"),
    HOT_FLASH("Hot Flash / Night Sweats", "Physical"),

    // Emotional / Cognitive
    ANXIETY("Anxiety / Worry", "Emotional"),
    IRRITABILITY("Irritability / Anger", "Emotional"),
    MOOD_SWINGS("Mood Swings", "Emotional"),
    BRAIN_FOG("Brain Fog", "Cognitive"),
    SENSORY_OVERWHELM("Sensory Overload", "Emotional"),
    INSOMNIA("Sleep Disruption", "Emotional"),

    // Digestion
    CRAVINGS("Sugar / Salt Cravings", "Digestion"),
    NAUSEA("Nausea", "Digestion"),
    DIARRHEA("Diarrhea", "Digestion"),
    CONSTIPATION("Constipation", "Digestion")
}
