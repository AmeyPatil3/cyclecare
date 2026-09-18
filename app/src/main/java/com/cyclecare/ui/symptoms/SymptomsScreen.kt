package com.cyclecare.ui.symptoms

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyclecare.data.db.entity.SymptomEntry
import com.cyclecare.data.repository.SymptomRepository
import com.cyclecare.domain.model.Symptom
import com.cyclecare.ui.components.PrimaryButton
import com.cyclecare.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import javax.inject.Inject

data class SymptomsUiState(
    val selectedSymptoms: Map<Symptom, Int> = emptyMap(), // Symptom -> Severity (1-3)
    val isSaving: Boolean = false,
    val isSaved: Boolean = false
)

@HiltViewModel
class SymptomsViewModel @Inject constructor(
    private val symptomRepository: SymptomRepository
) : ViewModel() {

    private val today: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
    private val _uiState = MutableStateFlow(SymptomsUiState())
    val uiState: StateFlow<SymptomsUiState> = _uiState.asStateFlow()

    init {
        loadSymptoms()
    }

    private fun loadSymptoms() {
        viewModelScope.launch {
            val symptoms = symptomRepository.getSymptomsForDate(today)
            val map = symptoms.associate { it.symptom to it.severity }
            _uiState.value = _uiState.value.copy(selectedSymptoms = map)
        }
    }

    fun toggleSymptom(symptom: Symptom) {
        val current = _uiState.value.selectedSymptoms.toMutableMap()
        if (current.containsKey(symptom)) {
            current.remove(symptom)
        } else {
            current[symptom] = 1 // Default Mild
        }
        _uiState.value = _uiState.value.copy(selectedSymptoms = current)
    }

    fun setSeverity(symptom: Symptom, severity: Int) {
        val current = _uiState.value.selectedSymptoms.toMutableMap()
        current[symptom] = severity
        _uiState.value = _uiState.value.copy(selectedSymptoms = current)
    }

    fun saveSymptoms(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            for ((symptom, severity) in _uiState.value.selectedSymptoms) {
                symptomRepository.insertSymptom(
                    SymptomEntry(
                        date = today,
                        symptom = symptom,
                        severity = severity
                    )
                )
            }
            _uiState.value = _uiState.value.copy(isSaving = false, isSaved = true)
            onSuccess()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SymptomsScreen(
    viewModel: SymptomsViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Track Symptoms",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
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
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .padding(Spacing.md)
                ) {
                    PrimaryButton(
                        text = if (state.isSaving) "Saving..." else "Save Symptoms",
                        icon = Icons.Filled.CheckCircle,
                        enabled = !state.isSaving,
                        onClick = { viewModel.saveSymptoms(onSuccess = onNavigateBack) },
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
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
            contentPadding = PaddingValues(top = Spacing.sm, bottom = Spacing.xl)
        ) {
            item {
                Text(
                    text = "Select any symptoms experienced today and rate their intensity.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Group symptoms
            val physical = listOf(
                Symptom.CRAMPS,
                Symptom.HEADACHE,
                Symptom.BLOATING,
                Symptom.BREAST_TENDERNESS,
                Symptom.ACNE,
                Symptom.FATIGUE
            )
            val emotional = listOf(
                Symptom.ANXIETY,
                Symptom.IRRITABILITY,
                Symptom.MOOD_SWINGS,
                Symptom.INSOMNIA
            )
            val digestion = listOf(
                Symptom.CRAVINGS,
                Symptom.NAUSEA,
                Symptom.DIARRHEA,
                Symptom.CONSTIPATION
            )

            item {
                SymptomCategoryCard(
                    categoryName = "Physical Symptoms",
                    symptoms = physical,
                    selectedSymptoms = state.selectedSymptoms,
                    onToggle = { viewModel.toggleSymptom(it) },
                    onSeverityChange = { sym, sev -> viewModel.setSeverity(sym, sev) }
                )
            }

            item {
                SymptomCategoryCard(
                    categoryName = "Emotional & Mental",
                    symptoms = emotional,
                    selectedSymptoms = state.selectedSymptoms,
                    onToggle = { viewModel.toggleSymptom(it) },
                    onSeverityChange = { sym, sev -> viewModel.setSeverity(sym, sev) }
                )
            }

            item {
                SymptomCategoryCard(
                    categoryName = "Digestion & Body",
                    symptoms = digestion,
                    selectedSymptoms = state.selectedSymptoms,
                    onToggle = { viewModel.toggleSymptom(it) },
                    onSeverityChange = { sym, sev -> viewModel.setSeverity(sym, sev) }
                )
            }
        }
    }
}

@Composable
fun SymptomCategoryCard(
    categoryName: String,
    symptoms: List<Symptom>,
    selectedSymptoms: Map<Symptom, Int>,
    onToggle: (Symptom) -> Unit,
    onSeverityChange: (Symptom, Int) -> Unit,
    modifier: Modifier = Modifier
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
            Text(
                text = categoryName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            symptoms.forEach { symptom ->
                val isSelected = selectedSymptoms.containsKey(symptom)
                val severity = selectedSymptoms[symptom] ?: 1

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(Radius.md))
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                            else Color.Transparent
                        )
                        .clickable { onToggle(symptom) }
                        .padding(horizontal = Spacing.sm, vertical = Spacing.xs)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = symptom.displayName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = { onToggle(symptom) }
                        )
                    }

                    if (isSelected) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp, bottom = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                        ) {
                            listOf(1 to "Mild", 2 to "Moderate", 3 to "Severe").forEach { (level, text) ->
                                val active = severity == level
                                Surface(
                                    shape = RoundedCornerShape(Radius.full),
                                    color = if (active) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(Radius.full))
                                        .clickable { onSeverityChange(symptom, level) }
                                ) {
                                    Text(
                                        text = text,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
                                        color = if (active) MaterialTheme.colorScheme.onPrimary
                                        else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
