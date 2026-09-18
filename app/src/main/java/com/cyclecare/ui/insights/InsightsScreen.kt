package com.cyclecare.ui.insights

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cyclecare.ui.components.PrimaryButton
import com.cyclecare.ui.theme.*

@Composable
fun InsightsScreen(
    viewModel: InsightsViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    var showExportDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            InsightsTopBar()
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
            // ── Overview Stat Cards Row ──────────────────────────
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    InsightStatCard(
                        title = "Regularity",
                        value = "${state.summary?.regularityScore ?: 94}%",
                        subtitle = "+2% vs avg",
                        positive = true,
                        modifier = Modifier.weight(1f)
                    )
                    InsightStatCard(
                        title = "Avg Cycle",
                        value = "${state.summary?.averageCycleLength ?: 28}d",
                        subtitle = "±1.2 days",
                        positive = true,
                        modifier = Modifier.weight(1f)
                    )
                    InsightStatCard(
                        title = "Avg Period",
                        value = "${state.summary?.averagePeriodLength ?: 5}d",
                        subtitle = "Optimal",
                        positive = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // ── PMDD & Luteal Correlation Card ───────────────────
            item {
                Surface(
                    shape = RoundedCornerShape(Radius.lg),
                    color = MaterialTheme.colorScheme.surfaceContainerLowest,
                    shadowElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(Spacing.md),
                        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Psychology,
                                    contentDescription = null,
                                    tint = LavenderLuteal,
                                    modifier = Modifier.size(22.dp)
                                )
                                Text(
                                    text = "PMDD Prospective Screener",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(Radius.full),
                                color = LavenderLuteal.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "DRSP Criteria",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = LavenderLuteal,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        val pmdd = state.pmddData
                        val lutealScore = pmdd?.averageLutealScore ?: 3.8f
                        val follicularScore = pmdd?.averageFollicularScore ?: 1.2f

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(Radius.md),
                                color = LavenderLuteal.copy(alpha = 0.12f),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "Late Luteal Distress",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "${String.format("%.1f", lutealScore)} / 5",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = LavenderLuteal
                                    )
                                    Text(
                                        text = "Days 21–28",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(Radius.md),
                                color = SageFollicular.copy(alpha = 0.12f),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "Follicular Baseline",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "${String.format("%.1f", follicularScore)} / 5",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = SageFollicular
                                    )
                                    Text(
                                        text = "Days 6–13",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Text(
                            text = pmdd?.diagnosticSummary ?: "Consistent prospective luteal symptom elevation observed across cycles.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // ── Cycle Length Trend Bar Chart ─────────────────────
            item {
                Surface(
                    shape = RoundedCornerShape(Radius.lg),
                    color = MaterialTheme.colorScheme.surfaceContainerLowest,
                    shadowElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(Spacing.md),
                        verticalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Cycle Length Trend",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Surface(
                                shape = RoundedCornerShape(Radius.full),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = "Last 6 Cycles",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .padding(top = Spacing.sm),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            val maxDays = 35f
                            state.cycleHistory.forEach { bar ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Bottom,
                                    modifier = Modifier.fillMaxHeight()
                                ) {
                                    Text(
                                        text = "${bar.lengthDays}d",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (bar.isCurrent) FontWeight.Bold else FontWeight.Normal,
                                        color = if (bar.isCurrent) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .width(28.dp)
                                            .fillMaxHeight((bar.lengthDays / maxDays).coerceIn(0.2f, 1f))
                                            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                            .background(
                                                if (bar.isCurrent) MaterialTheme.colorScheme.primary
                                                else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                                            )
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = bar.monthLabel,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (bar.isCurrent) FontWeight.Bold else FontWeight.Normal,
                                        color = if (bar.isCurrent) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ── Phase Distribution Section ───────────────────────
            item {
                Surface(
                    shape = RoundedCornerShape(Radius.lg),
                    color = MaterialTheme.colorScheme.surfaceContainerLowest,
                    shadowElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(Spacing.md),
                        verticalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        Text(
                            text = "Phase Distribution & Patterns",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                        ) {
                            InsightsTab.values().forEach { tab ->
                                val isSelected = state.selectedTab == tab
                                Surface(
                                    shape = RoundedCornerShape(Radius.full),
                                    color = if (isSelected) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(Radius.full))
                                        .clickable { viewModel.selectTab(tab) }
                                ) {
                                    Text(
                                        text = tab.name.lowercase().replaceFirstChar { it.uppercase() },
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                                        else MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }
                            }
                        }

                        when (state.selectedTab) {
                            InsightsTab.ENERGY -> {
                                DistributionProgressRow("Ovulatory Phase (Peak)", 0.85f, CoralPink)
                                DistributionProgressRow("Follicular Phase (Rising)", 0.72f, SageFollicular)
                                DistributionProgressRow("Luteal Phase (Steady)", 0.55f, LavenderLuteal)
                                DistributionProgressRow("Menstrual Phase (Resting)", 0.38f, RoseMenstrual)
                            }
                            InsightsTab.MOOD -> {
                                DistributionProgressRow("Calm / Centered", 0.65f, SageFollicular)
                                DistributionProgressRow("Social / Outgoing", 0.80f, CoralPink)
                                DistributionProgressRow("Reflective / Introspective", 0.50f, LavenderLuteal)
                                DistributionProgressRow("Sensitive / Vulnerable", 0.30f, RoseMenstrual)
                            }
                            InsightsTab.SYMPTOMS -> {
                                DistributionProgressRow("Cramps (Days 1–2)", 0.40f, RoseMenstrual)
                                DistributionProgressRow("Breast Tenderness (Days 24–27)", 0.35f, LavenderLuteal)
                                DistributionProgressRow("Headache (Mild)", 0.15f, CoralPink)
                                DistributionProgressRow("Bloating", 0.25f, SageFollicular)
                            }
                            InsightsTab.SLEEP -> {
                                DistributionProgressRow("Deep Sleep (Avg 7.8 hrs)", 0.82f, TealSleep)
                                DistributionProgressRow("Restful Nights", 0.75f, SageFollicular)
                                DistributionProgressRow("Interrupted Nights", 0.20f, RoseMenstrual)
                                DistributionProgressRow("REM Proportion", 0.68f, LavenderLuteal)
                            }
                        }
                    }
                }
            }

            // ── Clinical Doctor Report Card ──────────────────────
            item {
                Surface(
                    shape = RoundedCornerShape(Radius.lg),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(Spacing.md),
                        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Description,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Clinical Cycle & PMDD Summary",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "Generates a clinical DRSP chart and cycle irregularity report for your OB-GYN or endocrinologist visit. 100% on-device encryption.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        PrimaryButton(
                            text = if (state.isExporting) "Generating PDF Report..." else "Prepare Doctor Report (DRSP)",
                            icon = Icons.Filled.Download,
                            enabled = !state.isExporting,
                            onClick = {
                                viewModel.exportDoctorReport {
                                    showExportDialog = true
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }

    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = { Text(text = "Clinical Report Generated", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = "Your prospective PMDD and cycle irregularity report has been compiled according to DSM-5 criteria. All data stays strictly on your phone.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(onClick = { showExportDialog = false }) {
                    Text("Done")
                }
            }
        )
    }
}

@Composable
fun InsightsTopBar(modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = Spacing.md, vertical = Spacing.sm),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Your Insights",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Notifications",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun InsightStatCard(
    title: String,
    value: String,
    subtitle: String,
    positive: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(Radius.lg),
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        shadowElevation = 1.dp,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(Spacing.sm),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Surface(
                shape = RoundedCornerShape(Radius.full),
                color = if (positive) SageFollicular.copy(alpha = 0.2f) else RoseMenstrual.copy(alpha = 0.2f)
            ) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    color = if (positive) MaterialTheme.colorScheme.primary else RoseMenstrual,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
fun DistributionProgressRow(
    label: String,
    fraction: Float,
    barColor: Color,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${(fraction * 100).toInt()}%",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        LinearProgressIndicator(
            progress = { fraction },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = barColor,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}
