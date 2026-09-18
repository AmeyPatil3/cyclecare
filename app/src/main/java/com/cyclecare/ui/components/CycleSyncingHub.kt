package com.cyclecare.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cyclecare.data.network.DynamicWellnessFact
import com.cyclecare.data.network.WellnessApiService
import com.cyclecare.domain.model.*
import com.cyclecare.ui.theme.*
import kotlinx.coroutines.launch

enum class SyncCategory(val title: String, val icon: ImageVector) {
    MOVEMENT("Workouts", Icons.Outlined.FitnessCenter),
    NUTRITION("Nourishment", Icons.Outlined.Restaurant),
    MINDSET("Mindset", Icons.Outlined.Psychology),
    RESEARCH("Live Science", Icons.Outlined.TravelExplore)
}

@Composable
fun CycleSyncingHub(
    phase: CyclePhase,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf(SyncCategory.MOVEMENT) }
    val guide = getGuideForPhase(phase)
    val coroutineScope = rememberCoroutineScope()
    val apiService = remember { WellnessApiService() }

    var liveFacts by remember(phase) { mutableStateOf<List<DynamicWellnessFact>>(emptyList()) }
    var isFetchingLive by remember { mutableStateOf(false) }

    LaunchedEffect(phase) {
        liveFacts = apiService.fetchCuratedOnlineFacts(phase)
        isFetchingLive = true
        liveFacts = apiService.fetchDynamicFactsForPhase(phase)
        isFetchingLive = false
    }

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
            // Header
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
                        imageVector = Icons.Filled.AutoAwesome,
                        contentDescription = null,
                        tint = phase.themeColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Cycle Syncing Hub",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    shape = RoundedCornerShape(Radius.full),
                    color = phase.themeColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "${phase.displayName} Phase",
                        style = MaterialTheme.typography.labelSmall,
                        color = phase.themeColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Tab bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                SyncCategory.values().forEach { category ->
                    val isSelected = selectedCategory == category
                    Surface(
                        shape = RoundedCornerShape(Radius.full),
                        color = if (isSelected) phase.themeColor else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(Radius.full))
                            .clickable { selectedCategory = category }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = category.icon,
                                contentDescription = null,
                                tint = if (isSelected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = category.title,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Dynamic Content
            AnimatedContent(
                targetState = selectedCategory,
                label = "sync_content"
            ) { category ->
                when (category) {
                    SyncCategory.MOVEMENT -> MovementGuideContent(guide.movement, phase.themeColor)
                    SyncCategory.NUTRITION -> NutritionGuideContent(guide.nutrition, phase.themeColor)
                    SyncCategory.MINDSET -> MindsetGuideContent(guide.mindset, phase.themeColor)
                    SyncCategory.RESEARCH -> LiveResearchContent(
                        facts = liveFacts,
                        isFetching = isFetchingLive,
                        accent = phase.themeColor,
                        onRefresh = {
                            coroutineScope.launch {
                                isFetchingLive = true
                                liveFacts = apiService.fetchDynamicFactsForPhase(phase)
                                isFetchingLive = false
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LiveResearchContent(
    facts: List<DynamicWellnessFact>,
    isFetching: Boolean,
    accent: Color,
    onRefresh: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Live Science & Nutrition Updates",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                    shape = RoundedCornerShape(Radius.full),
                    color = SageFollicular.copy(alpha = 0.2f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(SageFollicular)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Web Connected",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            color = SageFollicular,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            IconButton(
                onClick = onRefresh,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh from Web",
                    tint = accent,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        if (isFetching) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp),
                color = accent
            )
        }

        facts.forEach { fact ->
            Surface(
                shape = RoundedCornerShape(Radius.md),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = fact.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 13.sp
                        )
                        Surface(
                            shape = RoundedCornerShape(Radius.sm),
                            color = accent.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = fact.category,
                                style = MaterialTheme.typography.labelSmall,
                                color = accent,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = fact.content,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            tint = accent,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Source: ${fact.sourceName}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MovementGuideContent(movement: MovementGuide, accent: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = movement.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Surface(
                shape = RoundedCornerShape(Radius.full),
                color = accent.copy(alpha = 0.2f)
            ) {
                Text(
                    text = movement.intensity,
                    style = MaterialTheme.typography.labelSmall,
                    color = accent,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            movement.recommendedActivities.forEach { activity ->
                Surface(
                    shape = RoundedCornerShape(Radius.sm),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                ) {
                    Text(
                        text = activity,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        movement.cortisolWarning?.let { warning ->
            Surface(
                shape = RoundedCornerShape(Radius.md),
                color = AmberEnergy.copy(alpha = 0.15f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = AmberEnergy,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = warning,
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun NutritionGuideContent(nutrition: NutritionGuide, accent: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
        Text(
            text = nutrition.title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        // Seed cycling badge
        Surface(
            shape = RoundedCornerShape(Radius.md),
            color = accent.copy(alpha = 0.15f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Spa,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Seed Cycling Protocol",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = accent
                    )
                    Text(
                        text = nutrition.seedCycling,
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Focus foods
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            nutrition.focusFoods.forEach { food ->
                Surface(
                    shape = RoundedCornerShape(Radius.sm),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                ) {
                    Text(
                        text = food,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MindsetGuideContent(mindset: MindsetGuide, accent: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Energy Type: ${mindset.energyType}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = mindset.socialStamina,
                style = MaterialTheme.typography.labelSmall,
                color = accent,
                fontWeight = FontWeight.SemiBold
            )
        }
        Text(
            text = mindset.idealFocus,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun getGuideForPhase(phase: CyclePhase): CycleSyncingGuide {
    return when (phase) {
        CyclePhase.MENSTRUAL -> CycleSyncingGuide(
            phase = phase,
            movement = MovementGuide(
                title = "Restorative & Gentle Unwinding",
                recommendedActivities = listOf("Yin Yoga", "Walk in Nature", "Pelvic Floor Stretches"),
                intensity = "Gentle / Low Exertion",
                cortisolWarning = "Elevated cortisol impairs immune rebuilding. Avoid heavy HIIT or high-strain lifting."
            ),
            nutrition = NutritionGuide(
                title = "Warmth & Iron Replenishment",
                focusFoods = listOf("Bone Broth", "Leafy Greens", "Hibiscus Tea", "Dark Berries"),
                seedCycling = "1 tbsp Ground Flaxseed + 1 tbsp Pumpkin Seeds (supports healthy estrogen)",
                keyMicronutrients = listOf("Iron", "Zinc", "Magnesium")
            ),
            mindset = MindsetGuide(
                energyType = "Reflective & Intuitive",
                idealFocus = "Review monthly progress, establish boundary guidelines, and recharge without guilt.",
                socialStamina = "Low • Introspective"
            )
        ),
        CyclePhase.FOLLICULAR -> CycleSyncingGuide(
            phase = phase,
            movement = MovementGuide(
                title = "Cardio & Progressive Strength",
                recommendedActivities = listOf("Jogging / Running", "Pilates Sculpt", "Hypertrophy Lifting"),
                intensity = "Moderate to High",
                cortisolWarning = null
            ),
            nutrition = NutritionGuide(
                title = "Sprouted Foods & Estrogen Clearance",
                focusFoods = listOf("Fermented Foods", "Broccoli Sprouts", "Lean Protein", "Citrus"),
                seedCycling = "1 tbsp Ground Flaxseed + 1 tbsp Pumpkin Seeds",
                keyMicronutrients = listOf("B-Complex", "Glutathione", "Vitamin C")
            ),
            mindset = MindsetGuide(
                energyType = "Creative & Brainstorming",
                idealFocus = "Start ambitious new projects, learn complex skills, and architect plans for the upcoming month.",
                socialStamina = "Rising • Collaborative"
            )
        ),
        CyclePhase.OVULATORY -> CycleSyncingGuide(
            phase = phase,
            movement = MovementGuide(
                title = "Peak Power & High-Intensity",
                recommendedActivities = listOf("Sprint HIIT", "Personal Record Lifting", "Dance / Spin"),
                intensity = "Maximum Exertion",
                cortisolWarning = "Body temperature is slightly elevated. Keep electrolyte hydration plentiful."
            ),
            nutrition = NutritionGuide(
                title = "Anti-inflammatory & Antioxidants",
                focusFoods = listOf("Wild Salmon", "Avocado", "Matcha", "Colorful Bell Peppers"),
                seedCycling = "1 tbsp Sesame Seeds + 1 tbsp Sunflower Seeds (transition window)",
                keyMicronutrients = listOf("Omega-3", "Selenium", "Zinc")
            ),
            mindset = MindsetGuide(
                energyType = "Magnetic & Expressive",
                idealFocus = "High-stakes meetings, public speaking, interviews, pitching, and rich social connection.",
                socialStamina = "Peak • Outgoing"
            )
        ),
        CyclePhase.LUTEAL -> CycleSyncingGuide(
            phase = phase,
            movement = MovementGuide(
                title = "Steady Resistance & Flow",
                recommendedActivities = listOf("Moderate Strength", "Mat Pilates", "Slow Flow Yoga"),
                intensity = "Steady / Moderate",
                cortisolWarning = "Progesterone lowers stress tolerance in late luteal. Stop workouts if dizzy or exhausted."
            ),
            nutrition = NutritionGuide(
                title = "Complex Carbs & Serotonin Support",
                focusFoods = listOf("Sweet Potatoes", "Quinoa", "Dark Chocolate (85%)", "Pumpkin"),
                seedCycling = "1 tbsp Sesame Seeds + 1 tbsp Sunflower Seeds (supports progesterone synthesis)",
                keyMicronutrients = listOf("Magnesium Glycinate", "Vitamin B6", "L-Tryptophan")
            ),
            mindset = MindsetGuide(
                energyType = "Organizing & Detail-Oriented",
                idealFocus = "Edit work, organize spaces, tie up loose ends, and prepare comfortable environments.",
                socialStamina = "Moderate to Selective"
            )
        )
    }
}
