package com.cyclecare.ui.home

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cyclecare.domain.model.*
import com.cyclecare.ui.components.*
import com.cyclecare.ui.theme.*

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToQuickLog: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToInsights: () -> Unit,
    onNavigateToSymptoms: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            HomeTopBar(
                userName = state.userName,
                isStealthMode = state.isStealthMode,
                onToggleStealth = { viewModel.toggleStealthMode() }
            )
        },
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
            contentPadding = PaddingValues(top = Spacing.sm, bottom = 100.dp)
        ) {
            // ── Greeting & Phase Pill / Stealth disguise ─────────
            item {
                if (state.isStealthMode) {
                    StealthGreetingHeader()
                } else {
                    GreetingHeader(
                        userName = state.userName,
                        cycleDay = state.cycleState?.cycleDay ?: 18,
                        phase = state.cycleState?.cyclePhase ?: CyclePhase.OVULATORY,
                        isPcos = state.trackingMode == TrackingMode.PCOS_IRREGULAR,
                        totalCycleLength = state.cycleState?.totalCycleLength ?: 28
                    )
                }
            }

            // ── Signature Cycle Dial ─────────────────────────────
            item {
                state.cycleState?.let { cycle ->
                    CycleDialVisualization(
                        cycleState = cycle,
                        onDialClick = onNavigateToCalendar,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = Spacing.xs)
                    )
                }
            }

            // ── Daily Supplement / Pill Quick Check-off ──────────
            item {
                Surface(
                    shape = RoundedCornerShape(Radius.lg),
                    color = MaterialTheme.colorScheme.surfaceContainerLowest,
                    shadowElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(Spacing.sm),
                        verticalArrangement = Arrangement.spacedBy(Spacing.xs)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Medication,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Daily Regimen Quick Check",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Text(
                                text = "Tap to toggle",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        val loggedSupplements = state.todayLog?.supplementsTaken ?: emptyList()
                        val quickPills = listOf(
                            SupplementType.BIRTH_CONTROL,
                            SupplementType.INOSITOL,
                            SupplementType.MAGNESIUM,
                            SupplementType.IRON
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            quickPills.forEach { pill ->
                                val taken = loggedSupplements.contains(pill)
                                Surface(
                                    shape = RoundedCornerShape(Radius.full),
                                    color = if (taken) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    border = if (taken) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(Radius.full))
                                        .clickable { viewModel.toggleSupplementQuick(pill) }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center,
                                        modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (taken) Icons.Filled.CheckCircle else Icons.Outlined.Circle,
                                            contentDescription = null,
                                            tint = if (taken) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = pill.displayName.split(" ").first(),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = if (taken) FontWeight.Bold else FontWeight.Medium,
                                            color = if (taken) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ── Cycle Syncing Hub ────────────────────────────────
            item {
                state.cycleState?.let { cycle ->
                    CycleSyncingHub(
                        phase = cycle.cyclePhase,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // ── Quick Actions Row ────────────────────────────────
            item {
                Text(
                    text = "Quick Actions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = Spacing.xs)
                )
                Spacer(modifier = Modifier.height(Spacing.sm))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    QuickActionButton(
                        icon = Icons.Outlined.EditNote,
                        label = "Quick Log",
                        onClick = onNavigateToQuickLog,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(Spacing.xs))
                    QuickActionButton(
                        icon = Icons.Outlined.WaterDrop,
                        label = if (state.periodLoggedToday) "Period Active" else "Period Start",
                        badge = if (state.periodLoggedToday) "Day 1" else null,
                        onClick = { viewModel.logPeriodStart() },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(Spacing.xs))
                    QuickActionButton(
                        icon = Icons.Outlined.Healing,
                        label = "Symptoms",
                        onClick = onNavigateToSymptoms,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(Spacing.xs))
                    QuickActionButton(
                        icon = Icons.Outlined.Insights,
                        label = "Insights",
                        onClick = onNavigateToInsights,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // ── Today's Check-in Metrics ───────────────────────────
            item {
                Text(
                    text = "Today's Check-in",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = Spacing.sm)
                )
                Spacer(modifier = Modifier.height(Spacing.sm))
                val log = state.todayLog
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        CheckInMetricCard(
                            title = "Energy Level",
                            value = "${log?.energyLevel ?: 8}/10",
                            subtitle = "High energy peak",
                            icon = Icons.Filled.Bolt,
                            accentColor = AmberEnergy,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToQuickLog
                        )
                        CheckInMetricCard(
                            title = "Sleep Rest",
                            value = log?.sleepQuality?.displayName ?: "Restful",
                            subtitle = "7.5 hrs recorded",
                            icon = Icons.Filled.Bedtime,
                            accentColor = TealSleep,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToQuickLog
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        CheckInMetricCard(
                            title = "Daily Mood",
                            value = log?.mood?.name?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "Calm",
                            subtitle = "Balanced & grounded",
                            icon = Icons.Filled.SentimentSatisfied,
                            accentColor = CoralPink,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToQuickLog
                        )
                        CheckInMetricCard(
                            title = "Appetite",
                            value = log?.appetite?.displayName ?: "Normal",
                            subtitle = "Hydration optimal",
                            icon = Icons.Filled.Restaurant,
                            accentColor = SageFollicular,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToQuickLog
                        )
                    }
                }
            }

            // ── Recent Logged Symptoms ─────────────────────────────
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Symptoms",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    TextButton(onClick = onNavigateToSymptoms) {
                        Text(
                            text = "+ Add",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(Spacing.xs))
                if (state.recentSymptoms.isEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(Radius.md),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(Spacing.md),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CheckCircle,
                                contentDescription = null,
                                tint = SageFollicular,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(Spacing.sm))
                            Text(
                                text = "No adverse symptoms reported today. Feeling balanced!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                    ) {
                        state.recentSymptoms.forEach { symptom ->
                            Surface(
                                shape = RoundedCornerShape(Radius.full),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.padding(vertical = 2.dp)
                            ) {
                                Text(
                                    text = symptom.symptom.displayName,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeTopBar(
    userName: String,
    isStealthMode: Boolean,
    onToggleStealth: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        tonalElevation = 1.dp,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = Spacing.md, vertical = Spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isStealthMode) Icons.Filled.WaterDrop else Icons.Filled.Spa,
                            contentDescription = "Logo",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Text(
                    text = if (isStealthMode) "HydraHabit" else "CycleCare",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                // Stealth mode toggle icon
                IconButton(onClick = onToggleStealth) {
                    Icon(
                        imageVector = if (isStealthMode) Icons.Filled.VisibilityOff else Icons.Outlined.Visibility,
                        contentDescription = "Stealth Camouflage",
                        tint = if (isStealthMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(34.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = userName.take(1).uppercase(),
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GreetingHeader(
    userName: String,
    cycleDay: Int,
    phase: CyclePhase,
    isPcos: Boolean,
    totalCycleLength: Int = 28,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
            Text(
                text = "Good morning, $userName",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Surface(
                shape = RoundedCornerShape(Radius.full),
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(phase.themeColor)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    val badgeText = when {
                        cycleDay > totalCycleLength -> "Cycle Day $cycleDay • Extended Phase (Natural Body Shift)"
                        isPcos -> "Cycle Day $cycleDay • ${phase.displayName} (Adaptive PCOS Window)"
                        else -> "Cycle Day $cycleDay • ${phase.displayName} Phase"
                    }
                    Text(
                        text = badgeText,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun StealthGreetingHeader(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
        Text(
            text = "Daily Wellness & Hydration",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Surface(
            shape = RoundedCornerShape(Radius.full),
            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Daily Target: 2.5L • 80% Complete",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
