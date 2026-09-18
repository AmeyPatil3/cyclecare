package com.cyclecare.ui.quicklog

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
import com.cyclecare.domain.model.*
import com.cyclecare.ui.components.PrimaryButton
import com.cyclecare.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickLogScreen(
    viewModel: QuickLogViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Quick Log",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .padding(Spacing.md)
                ) {
                    PrimaryButton(
                        text = if (state.isSaving) "Saving..." else "Save Check-in",
                        icon = Icons.Filled.CheckCircle,
                        enabled = !state.isSaving,
                        onClick = {
                            viewModel.saveCheckIn(onSuccess = onNavigateBack)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
            contentPadding = PaddingValues(top = Spacing.sm, bottom = Spacing.xl)
        ) {
            // ── Prompt ──────────────────────────────────────────
            item {
                Text(
                    text = "How are you feeling today?",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Track your daily vitals, supplements, and mood to unlock personalized cycle harmony.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // ── Supplements & Pill Tracker ────────────────────────
            item {
                LogSection(
                    title = "Daily Supplements & Pills",
                    icon = Icons.Outlined.Medication
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                        SupplementType.values().toList().chunked(2).forEach { rowSupplements ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                            ) {
                                rowSupplements.forEach { supp ->
                                    val isSelected = state.selectedSupplements.contains(supp)
                                    Surface(
                                        shape = RoundedCornerShape(Radius.md),
                                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(Radius.md))
                                            .clickable { viewModel.toggleSupplement(supp) }
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (isSelected) Icons.Filled.CheckCircle else Icons.Outlined.Circle,
                                                contentDescription = null,
                                                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = supp.displayName,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                                if (rowSupplements.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }

            // ── Mood Section ─────────────────────────────────────
            item {
                LogSection(
                    title = "Daily Mood",
                    icon = Icons.Outlined.SentimentSatisfied
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MoodOption(
                            emoji = "😄",
                            label = "Great",
                            selected = state.selectedMood == MoodLevel.GREAT,
                            onClick = { viewModel.selectMood(MoodLevel.GREAT) }
                        )
                        MoodOption(
                            emoji = "🙂",
                            label = "Good",
                            selected = state.selectedMood == MoodLevel.GOOD,
                            onClick = { viewModel.selectMood(MoodLevel.GOOD) }
                        )
                        MoodOption(
                            emoji = "😐",
                            label = "Okay",
                            selected = state.selectedMood == MoodLevel.OKAY,
                            onClick = { viewModel.selectMood(MoodLevel.OKAY) }
                        )
                        MoodOption(
                            emoji = "😔",
                            label = "Low",
                            selected = state.selectedMood == MoodLevel.LOW,
                            onClick = { viewModel.selectMood(MoodLevel.LOW) }
                        )
                        MoodOption(
                            emoji = "😣",
                            label = "Very low",
                            selected = state.selectedMood == MoodLevel.VERY_LOW,
                            onClick = { viewModel.selectMood(MoodLevel.VERY_LOW) }
                        )
                    }
                }
            }

            // ── Cervical Fluid & Biological Ovulation Biomarkers ──
            item {
                LogSection(
                    title = "Cervical Fluid & Ovulation Signals",
                    icon = Icons.Outlined.WaterDrop
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                        Text(
                            text = "Natural biomarker of estrogen surge. When you log Egg-White or Watery fluid, CycleCare dynamically adapts to your ovulation date in real time.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        CervicalMucusType.values().toList().chunked(2).forEach { rowMucus ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                            ) {
                                rowMucus.forEach { mucus ->
                                    val isSelected = state.selectedCervicalMucus == mucus
                                    Surface(
                                        shape = RoundedCornerShape(Radius.md),
                                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(Radius.md))
                                            .clickable { viewModel.selectCervicalMucus(mucus) }
                                    ) {
                                        Column(modifier = Modifier.padding(8.dp)) {
                                            Text(
                                                text = mucus.displayName,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = mucus.fertilityImpact,
                                                style = MaterialTheme.typography.bodySmall,
                                                fontSize = 10.sp,
                                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                                if (rowMucus.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Optional Ovulation Strip (LH Test)",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                        ) {
                            LhTestResult.values().forEach { result ->
                                val isSelected = state.selectedLhTest == result
                                SelectableChip(
                                    label = result.displayName.split(" ").first(),
                                    selected = isSelected,
                                    onClick = { viewModel.selectLhTest(result) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            // ── PMDD & Emotional Dysphoria Screener ───────────────
            item {
                LogSection(
                    title = "PMDD / Sensory Tension (${state.pmddScore}/5)",
                    icon = Icons.Outlined.Psychology
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                        Text(
                            text = "Tracks prospective luteal emotional shifts (irritability, anxiety, sensory overload) for clinical correlation.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            (1..5).forEach { score ->
                                val isSelected = state.pmddScore == score
                                val label = when (score) {
                                    1 -> "Calm"
                                    2 -> "Mild"
                                    3 -> "Moderate"
                                    4 -> "High"
                                    else -> "Overload"
                                }
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(Radius.sm))
                                        .background(
                                            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                        )
                                        .clickable { viewModel.setPmddScore(score) }
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = score.toString(),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 10.sp,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ── Energy Level Slider ──────────────────────────────
            item {
                LogSection(
                    title = "Energy Level: ${state.energyLevel}/10",
                    icon = Icons.Outlined.Bolt
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Slider(
                            value = state.energyLevel.toFloat(),
                            onValueChange = { viewModel.setEnergy(it.toInt()) },
                            valueRange = 1f..10f,
                            steps = 8,
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary,
                                inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "1 (Exhausted)",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "5 (Balanced)",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "10 (Peak Energy)",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // ── Emergency Contraception / Anomaly Tag ─────────────
            item {
                LogSection(
                    title = "Hormonal Event Tag",
                    icon = Icons.Outlined.Shield
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                        Text(
                            text = "Taking emergency contraception or medication shifts hormonal timing. Tagging this isolates the event so your long-term prediction algorithm is not distorted.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                        ) {
                            EmergencyEvent.values().forEach { event ->
                                val isSelected = state.emergencyEvent == event
                                Surface(
                                    shape = RoundedCornerShape(Radius.full),
                                    color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(Radius.full))
                                        .clickable { viewModel.setEmergencyEvent(event) }
                                ) {
                                    Text(
                                        text = event.displayName.split(" ").take(2).joinToString(" "),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ── Pain & Discomfort ────────────────────────────────
            item {
                LogSection(
                    title = "Pain & Discomfort",
                    icon = Icons.Outlined.Healing
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                        val painLocations = listOf(
                            PainLocation.NONE,
                            PainLocation.LOWER_ABDOMEN,
                            PainLocation.LOWER_BACK,
                            PainLocation.HEADACHE,
                            PainLocation.BREASTS,
                            PainLocation.OVARY_PAIN
                        )
                        FlowChipRow(
                            items = painLocations,
                            selectedItems = state.selectedPainLocations,
                            onItemClick = { viewModel.togglePainLocation(it) },
                            itemLabel = { it.displayName }
                        )
                    }
                }
            }

            // ── Sleep Quality ────────────────────────────────────
            item {
                LogSection(
                    title = "Sleep Quality",
                    icon = Icons.Outlined.Bedtime
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                    ) {
                        SleepQuality.values().forEach { sleep ->
                            val isSelected = state.selectedSleep == sleep
                            SelectableChip(
                                label = sleep.displayName,
                                selected = isSelected,
                                onClick = { viewModel.selectSleep(sleep) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // ── Appetite & Nourishment ───────────────────────────
            item {
                LogSection(
                    title = "Appetite & Nourishment",
                    icon = Icons.Outlined.Restaurant
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                    ) {
                        AppetiteLevel.values().forEach { appetite ->
                            val isSelected = state.selectedAppetite == appetite
                            SelectableChip(
                                label = appetite.displayName,
                                selected = isSelected,
                                onClick = { viewModel.selectAppetite(appetite) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // ── Optional Notes ───────────────────────────────────
            item {
                LogSection(
                    title = "Personal Journal & Notes",
                    icon = Icons.Outlined.EditNote
                ) {
                    OutlinedTextField(
                        value = state.notes,
                        onValueChange = { viewModel.setNotes(it) },
                        placeholder = {
                            Text(
                                text = "Observations, cervical mucus changes, cravings or reflections...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        shape = RoundedCornerShape(Radius.md),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun LogSection(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(Radius.lg),
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        shadowElevation = 1.dp,
        modifier = modifier.fillMaxWidth()
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
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            content()
        }
    }
}

@Composable
fun MoodOption(
    emoji: String,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor by animateColorAsState(
        if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
    )
    val borderColor by animateColorAsState(
        if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(Radius.md))
            .border(1.dp, borderColor, RoundedCornerShape(Radius.md))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 10.dp)
    ) {
        Text(text = emoji, fontSize = 26.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun <T> FlowChipRow(
    items: List<T>,
    selectedItems: Set<T>,
    onItemClick: (T) -> Unit,
    itemLabel: (T) -> String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
    ) {
        items.take(3).forEach { item ->
            val isSelected = selectedItems.contains(item)
            SelectableChip(
                label = itemLabel(item),
                selected = isSelected,
                onClick = { onItemClick(item) },
                modifier = Modifier.weight(1f)
            )
        }
    }
    if (items.size > 3) {
        Spacer(modifier = Modifier.height(Spacing.xs))
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
        ) {
            items.drop(3).take(3).forEach { item ->
                val isSelected = selectedItems.contains(item)
                SelectableChip(
                    label = itemLabel(item),
                    selected = isSelected,
                    onClick = { onItemClick(item) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun SelectableChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(Radius.full),
        color = if (selected) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = if (selected) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null,
        modifier = modifier
            .clip(RoundedCornerShape(Radius.full))
            .clickable(onClick = onClick)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
                else MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}
