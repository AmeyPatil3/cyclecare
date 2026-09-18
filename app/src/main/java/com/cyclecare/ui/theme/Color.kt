package com.cyclecare.ui.theme

import androidx.compose.ui.graphics.Color

// ── Primary (Rose/Coral) ──────────────────────────────────────────────────────
val Primary = Color(0xFF9C3D38)
val OnPrimary = Color(0xFFFFFFFF)
val PrimaryContainer = Color(0xFFFFDAD6)
val OnPrimaryContainer = Color(0xFF410002)

// Primary Fixed (Menstrual phase arc / Log Period icon bg)
val PrimaryFixed = Color(0xFFFFDAD6)
val PrimaryFixedDim = Color(0xFFBC544F)          // Ovulation arc (heavier)
val OnPrimaryFixed = Color(0xFF410002)
val OnPrimaryFixedVariant = Color(0xFF73302D)

// ── Secondary (Lavender) ─────────────────────────────────────────────────────
val Secondary = Color(0xFF7A5C8E)
val OnSecondary = Color(0xFFFFFFFF)
val SecondaryContainer = Color(0xFFCFBFED)       // Fertile window / Luteal arc
val OnSecondaryContainer = Color(0xFF32105E)

// Secondary Fixed (base dial ring / chart grid)
val SecondaryFixed = Color(0xFFEBEDFF)           // Base dial ring
val SecondaryFixedDim = Color(0xFFDEE1F8)        // Chart grid lines
val OnSecondaryFixed = Color(0xFF1A0036)
val OnSecondaryFixedVariant = Color(0xFF543E72)

// ── Tertiary (Sage Green) ────────────────────────────────────────────────────
val Tertiary = Color(0xFF436352)                 // Positive states / check icons
val OnTertiary = Color(0xFFFFFFFF)
val TertiaryContainer = Color(0xFFC5EDD2)
val OnTertiaryContainer = Color(0xFF002112)

// Tertiary Fixed (Follicular phase arc)
val TertiaryFixed = Color(0xFFACCEBA)            // Follicular arc color
val TertiaryFixedDim = Color(0xFF91C1A4)
val OnTertiaryFixed = Color(0xFF002112)
val OnTertiaryFixedVariant = Color(0xFF2C4C3B)

// ── Error ────────────────────────────────────────────────────────────────────
val Error = Color(0xFFBA1A1A)
val OnError = Color(0xFFFFFFFF)
val ErrorContainer = Color(0xFFFFDAD6)
val OnErrorContainer = Color(0xFF410002)

// ── Surface / Background ─────────────────────────────────────────────────────
val Background = Color(0xFFFAF8F6)              // Warm off-white
val OnBackground = Color(0xFF171B2B)

val Surface = Color(0xFFFAF8F6)
val OnSurface = Color(0xFF171B2B)               // Deep charcoal text
val SurfaceVariant = Color(0xFFF3EEF3)
val OnSurfaceVariant = Color(0xFF564240)        // Secondary labels

val SurfaceContainerLowest = Color(0xFFFFFFFF)  // Card backgrounds
val SurfaceContainerLow = Color(0xFFF5F0F5)     // Metric cells
val SurfaceContainer = Color(0xFFEFEAEF)        // Inactive chips
val SurfaceContainerHigh = Color(0xFFE9E4E9)
val SurfaceContainerHighest = Color(0xFFE3DEE3)

val Outline = Color(0xFF857280)
val OutlineVariant = Color(0xFFD8C3D5)
val Scrim = Color(0xFF000000)
val InverseSurface = Color(0xFF322F31)
val InverseOnSurface = Color(0xFFFAEEF1)
val InversePrimary = Color(0xFFFFB3AC)

// ── Dark theme ───────────────────────────────────────────────────────────────
val PrimaryDark = Color(0xFFFFB3AC)
val OnPrimaryDark = Color(0xFF68100D)
val PrimaryContainerDark = Color(0xFF862722)
val OnPrimaryContainerDark = Color(0xFFFFDAD6)

val SecondaryDark = Color(0xFFD9B8F5)
val OnSecondaryDark = Color(0xFF44276A)
val SecondaryContainerDark = Color(0xFF5C3F80)
val OnSecondaryContainerDark = Color(0xFFEEDAFF)

val TertiaryDark = Color(0xFF97D4B1)
val OnTertiaryDark = Color(0xFF003828)
val TertiaryContainerDark = Color(0xFF1F5040)
val OnTertiaryContainerDark = Color(0xFFB3F0CA)

val BackgroundDark = Color(0xFF0F0D0E)
val OnBackgroundDark = Color(0xFFE9E0E4)
val SurfaceDark = Color(0xFF0F0D0E)
val OnSurfaceDark = Color(0xFFE9E0E4)
val SurfaceVariantDark = Color(0xFF4D4043)
val OnSurfaceVariantDark = Color(0xFFD0BFBB)
val SurfaceContainerLowestDark = Color(0xFF090709)
val SurfaceContainerLowDark = Color(0xFF201B1C)
val SurfaceContainerDark = Color(0xFF241F20)
val SurfaceContainerHighDark = Color(0xFF2F292A)
val SurfaceContainerHighestDark = Color(0xFF3A3435)
val OutlineDark = Color(0xFF9E8C88)
val OutlineVariantDark = Color(0xFF4D4043)

// ── Chart-specific colours ───────────────────────────────────────────────────
val ChartBarStart = Color(0xFF9C3D38)
val ChartBarEnd = Color(0xFFBC544F)
val ChartCurrentBar = Color(0xFF436352)
val ChartGridLine = Color(0xFFDEE1F8)
val ChartAvgLine = Color(0xFF9C3D38)

// ── Cycle arc strokes ────────────────────────────────────────────────────────
val ArcFollicular = Color(0xFFACCEBA)
val ArcOvulation = Color(0xFFBC544F)
val ArcLuteal = Color(0xFFCFBFED)
val ArcMenstrual = Color(0xFFFFDAD6)
val ArcBase = Color(0xFFEBEDFF)
val ArcPinOuter = Color(0xFFFFFFFF)
val ArcPinInner = Color(0xFF9C3D38)

// ── Named semantic colours used in UI components ──────────────────────────────
val SageFollicular = Color(0xFF436352)   // Sage green — follicular "Web Connected" indicator
val AmberEnergy = Color(0xFFE08C2E)      // Warm amber — cortisol / energy warning banners

// ── Phase theme colours ───────────────────────────────────────────────────────
val RoseMenstrual = Color(0xFFFFDAD6)
val CoralPink = Color(0xFFBC544F)
val LavenderLuteal = Color(0xFFCFBFED)
