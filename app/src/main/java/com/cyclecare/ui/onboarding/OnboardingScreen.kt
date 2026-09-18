package com.cyclecare.ui.onboarding

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyclecare.data.db.entity.PeriodEntry
import com.cyclecare.data.repository.CycleRepository
import com.cyclecare.domain.model.FlowIntensity
import com.cyclecare.ui.components.PrimaryButton
import com.cyclecare.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.todayIn
import javax.inject.Inject

enum class OnboardingGoal(val title: String, val description: String, val icon: ImageVector) {
    CYCLE_WELLNESS("Track Cycle & Wellness", "Understand energy shifts, moods, and hormonal harmony.", Icons.Outlined.Spa),
    TRYING_TO_CONCEIVE("Conception & Fertility", "Pinpoint peak fertility windows and ovulation markers.", Icons.Outlined.Favorite),
    MANAGE_SYMPTOMS("Monitor Symptoms & PCOS", "Detect triggers, cycle irregularities, and pain patterns.", Icons.Outlined.Healing)
}

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val cycleRepository: CycleRepository
) : ViewModel() {

    fun completeOnboarding(
        cycleLength: Int,
        periodLength: Int,
        daysAgoPeriodStarted: Int,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            val startDate = today.minus(daysAgoPeriodStarted, DateTimeUnit.DAY)
            cycleRepository.insertPeriod(
                PeriodEntry(
                    startDate = startDate,
                    flowIntensity = FlowIntensity.MEDIUM
                )
            )
            onComplete()
        }
    }
}

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentStep by remember { mutableStateOf(0) }
    var selectedGoal by remember { mutableStateOf(OnboardingGoal.CYCLE_WELLNESS) }
    var cycleLength by remember { mutableStateOf(28) }
    var periodLength by remember { mutableStateOf(5) }
    var daysAgoStarted by remember { mutableStateOf(18) } // Matches Cycle Day 18

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(Spacing.lg),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Progress indicators
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Spacing.md),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                (0..3).forEach { step ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                if (step <= currentStep) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                    )
                }
            }

            // Step Content
            AnimatedContent(
                targetState = currentStep,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = Spacing.lg),
                label = "onboarding_step"
            ) { step ->
                when (step) {
                    0 -> WelcomeStep()
                    1 -> GoalSelectionStep(
                        selectedGoal = selectedGoal,
                        onSelectGoal = { selectedGoal = it }
                    )
                    2 -> CycleSetupStep(
                        cycleLength = cycleLength,
                        onCycleLengthChange = { cycleLength = it },
                        periodLength = periodLength,
                        onPeriodLengthChange = { periodLength = it },
                        daysAgoStarted = daysAgoStarted,
                        onDaysAgoChange = { daysAgoStarted = it }
                    )
                    3 -> PrivacyStep()
                }
            }

            // Navigation Button
            PrimaryButton(
                text = if (currentStep == 3) "Enter CycleCare" else "Continue",
                icon = if (currentStep == 3) Icons.Filled.CheckCircle else Icons.Filled.ArrowForward,
                onClick = {
                    if (currentStep < 3) {
                        currentStep++
                    } else {
                        viewModel.completeOnboarding(
                            cycleLength = cycleLength,
                            periodLength = periodLength,
                            daysAgoPeriodStarted = daysAgoStarted,
                            onComplete = onFinish
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun WelcomeStep() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(96.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Filled.Spa,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(54.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(Spacing.lg))
        Text(
            text = "Welcome to CycleCare",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(Spacing.sm))
        Text(
            text = "A scientifically rooted, deeply private cycle companion designed to harmonize your energy, productivity, and physical wellness.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = Spacing.md)
        )
    }
}

@Composable
fun GoalSelectionStep(
    selectedGoal: OnboardingGoal,
    onSelectGoal: (OnboardingGoal) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "What is your main focus?",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "We will adapt your home dashboard, insights, and reminders accordingly.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, bottom = Spacing.lg)
        )

        OnboardingGoal.values().forEach { goal ->
            val isSelected = selectedGoal == goal
            Surface(
                shape = RoundedCornerShape(Radius.lg),
                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                else MaterialTheme.colorScheme.surfaceContainerLowest,
                border = androidx.compose.foundation.BorderStroke(
                    if (isSelected) 2.dp else 1.dp,
                    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable { onSelectGoal(goal) }
            ) {
                Row(
                    modifier = Modifier.padding(Spacing.md),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    Icon(
                        imageVector = goal.icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Column {
                        Text(
                            text = goal.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = goal.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CycleSetupStep(
    cycleLength: Int,
    onCycleLengthChange: (Int) -> Unit,
    periodLength: Int,
    onPeriodLengthChange: (Int) -> Unit,
    daysAgoStarted: Int,
    onDaysAgoChange: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Your Cycle Baseline",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "CycleCare learns and recalibrates automatically with every log.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, bottom = Spacing.lg)
        )

        Text(
            text = "Typical Cycle Duration: $cycleLength days",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Slider(
            value = cycleLength.toFloat(),
            onValueChange = { onCycleLengthChange(it.toInt()) },
            valueRange = 21f..38f,
            steps = 16
        )

        Spacer(modifier = Modifier.height(Spacing.md))

        Text(
            text = "Typical Period Duration: $periodLength days",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Slider(
            value = periodLength.toFloat(),
            onValueChange = { onPeriodLengthChange(it.toInt()) },
            valueRange = 3f..9f,
            steps = 5
        )

        Spacer(modifier = Modifier.height(Spacing.md))

        Text(
            text = "Last Period Started: $daysAgoStarted days ago",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Slider(
            value = daysAgoStarted.toFloat(),
            onValueChange = { onDaysAgoChange(it.toInt()) },
            valueRange = 1f..30f,
            steps = 28
        )
    }
}

@Composable
fun PrivacyStep() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = CircleShape,
            color = SageFollicular.copy(alpha = 0.2f),
            modifier = Modifier.size(80.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = null,
                    tint = SageFollicular,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(Spacing.lg))
        Text(
            text = "Your Privacy is Absolute",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(Spacing.sm))
        Text(
            text = "All personal symptoms, period dates, notes, and metrics remain strictly encrypted on your device. We do not sell or upload your intimate biological logs to third-party ad networks.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = Spacing.md)
        )
    }
}
