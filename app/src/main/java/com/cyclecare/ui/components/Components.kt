package com.cyclecare.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cyclecare.ui.theme.*

// ─────────────────────────────────────────────────────────────────────────────
//  Quick Action Button (Log Period / Symptoms / Mood / Wellness)
// ─────────────────────────────────────────────────────────────────────────────

data class QuickActionConfig(
    val label: String,
    val icon: ImageVector,
    val iconBg: Color,
    val iconTint: Color
)

/** Convenience overload — accepts params directly without constructing QuickActionConfig. */
@Composable
fun QuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badge: String? = null,
    iconBg: Color = PrimaryFixed,
    iconTint: Color = Primary
) {
    QuickActionButton(
        config = QuickActionConfig(label = label, icon = icon, iconBg = iconBg, iconTint = iconTint),
        onClick = onClick,
        modifier = modifier
    )
}

@Composable
fun QuickActionButton(
    config: QuickActionConfig,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(Radius.small))
            .background(SurfaceContainerLowest)
            .clickable(onClick = onClick)
            .padding(12.dp)
            .shadow(1.dp, RoundedCornerShape(Radius.small))
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(IconSize.quickAction)
                .clip(CircleShape)
                .background(config.iconBg)
        ) {
            Icon(
                imageVector = config.icon,
                contentDescription = config.label,
                tint = config.iconTint,
                modifier = Modifier.size(IconSize.medium)
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = config.label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Primary Button
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(Radius.full),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        ),
        modifier = modifier.height(52.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Secondary / Ghost Button
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onClick,
        shape = RoundedCornerShape(Radius.full),
        modifier = modifier.height(48.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Check-in Metric Card cell (2×2 grid on Home Dashboard)
// ─────────────────────────────────────────────────────────────────────────────

/** Overload used by HomeScreen — maps title/subtitle/accentColor/onClick to the base composable. */
@Composable
fun CheckInMetricCard(
    title: String,
    value: String,
    subtitle: String? = null,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    accentColor: Color = Secondary,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    CheckInMetricCard(
        label = title,
        value = value,
        badge = subtitle,
        badgeBg = accentColor.copy(alpha = 0.15f),
        badgeTextColor = accentColor,
        icon = icon,
        iconTint = accentColor,
        modifier = modifier
    )
}

@Composable
fun CheckInMetricCard(
    label: String,
    value: String,
    badge: String? = null,
    badgeBg: Color = PrimaryFixed,
    badgeTextColor: Color = OnPrimaryFixed,
    icon: ImageVector? = null,
    iconTint: Color = MaterialTheme.colorScheme.tertiary,
    modifier: Modifier = Modifier,
    progressFraction: Float? = null,
    progressColor: Color = Secondary
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(Radius.small))
            .background(SurfaceContainerLow)
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            icon?.let {
                Icon(it, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = value,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            badge?.let {
                Surface(
                    shape = RoundedCornerShape(Radius.full),
                    color = badgeBg,
                ) {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = badgeTextColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        }
        progressFraction?.let {
            Spacer(Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { it },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(Radius.full)),
                color = progressColor,
                trackColor = SurfaceContainer
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Insight Card (gradient bg with AI badge)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun InsightCard(
    headline: String,
    body: String,
    onViewDetails: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.medium))
            .background(
                Brush.linearGradient(
                    listOf(
                        PrimaryFixed.copy(alpha = 0.4f),
                        SurfaceContainerLowest,
                        SecondaryContainer.copy(alpha = 0.4f)
                    )
                )
            )
            .padding(Spacing.md)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            // AI badge pill
            Surface(
                shape = RoundedCornerShape(Radius.full),
                color = SurfaceContainerLowest.copy(alpha = 0.9f),
                shadowElevation = 1.dp
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text("✨", style = MaterialTheme.typography.labelSmall)
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "Cycle AI Insight",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Text(
                text = headline,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                PrimaryButton(text = "View Details", onClick = onViewDetails)
                SecondaryButton(text = "Dismiss", onClick = onDismiss)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Empty State
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun EmptyStateView(
    icon: ImageVector,
    title: String,
    body: String,
    ctaText: String,
    onCta: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.padding(Spacing.xl)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(PrimaryFixed)
        ) {
            Icon(icon, contentDescription = null, tint = Primary, modifier = Modifier.size(36.dp))
        }
        Spacer(Modifier.height(Spacing.md))
        Text(title, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface)
        Spacer(Modifier.height(Spacing.sm))
        Text(body, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(Spacing.lg))
        PrimaryButton(text = ctaText, onClick = onCta)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Phase legend pill row
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun PhaseLegendRow(modifier: Modifier = Modifier) {
    val items = listOf(
        "Follicular" to ArcFollicular,
        "Ovulatory" to ArcOvulation,
        "Luteal" to ArcLuteal,
        "Period" to ArcMenstrual
    )
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEachIndexed { i, (label, color) ->
            if (i > 0) Spacer(Modifier.width(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(color)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (label == "Ovulatory") Primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (label == "Ovulatory") FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Log Section wrapper (used by QuickLogScreen / SymptomsScreen)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun LogSection(
    title: String,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
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
                icon?.let {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            content()
        }
    }
}
