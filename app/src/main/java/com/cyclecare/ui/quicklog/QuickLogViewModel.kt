package com.cyclecare.ui.quicklog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyclecare.data.db.entity.DailyLog
import com.cyclecare.data.db.entity.PainEntry
import com.cyclecare.data.db.entity.SymptomEntry
import com.cyclecare.data.repository.LogRepository
import com.cyclecare.data.repository.SymptomRepository
import com.cyclecare.domain.model.*
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

data class QuickLogUiState(
    val date: LocalDate,
    val selectedMood: MoodLevel = MoodLevel.GOOD,
    val energyLevel: Int = 8,
    val selectedPainLocations: Set<PainLocation> = emptySet(),
    val painIntensity: Int = 0,
    val selectedSleep: SleepQuality = SleepQuality.RESTFUL,
    val stressLevel: Int = 3,
    val selectedAppetite: AppetiteLevel = AppetiteLevel.NORMAL,
    val selectedSymptoms: Set<Symptom> = emptySet(),
    val selectedSupplements: Set<SupplementType> = emptySet(),
    val pmddScore: Int = 1,
    val emergencyEvent: EmergencyEvent = EmergencyEvent.NONE,
    val selectedCervicalMucus: CervicalMucusType = CervicalMucusType.DRY,
    val selectedLhTest: LhTestResult = LhTestResult.NOT_TESTED,
    val notes: String = "",
    val isSaved: Boolean = false,
    val isSaving: Boolean = false
)

@HiltViewModel
class QuickLogViewModel @Inject constructor(
    private val logRepository: LogRepository,
    private val symptomRepository: SymptomRepository
) : ViewModel() {

    private val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    private val _uiState = MutableStateFlow(QuickLogUiState(date = today))
    val uiState: StateFlow<QuickLogUiState> = _uiState.asStateFlow()

    init {
        loadTodayLog()
    }

    private fun loadTodayLog() {
        viewModelScope.launch {
            val existing = logRepository.getLog(today)
            if (existing != null) {
                _uiState.value = _uiState.value.copy(
                    selectedMood = existing.mood,
                    energyLevel = existing.energyLevel,
                    selectedSleep = existing.sleepQuality,
                    stressLevel = existing.stressLevel,
                    selectedAppetite = existing.appetite,
                    selectedSupplements = existing.supplementsTaken.toSet(),
                    pmddScore = existing.pmddScore.coerceAtLeast(1),
                    emergencyEvent = existing.emergencyEvent,
                    selectedCervicalMucus = existing.cervicalMucus,
                    selectedLhTest = existing.lhTestResult,
                    notes = existing.notes
                )
            }
        }
    }

    fun selectMood(mood: MoodLevel) {
        _uiState.value = _uiState.value.copy(selectedMood = mood)
    }

    fun setEnergy(energy: Int) {
        _uiState.value = _uiState.value.copy(energyLevel = energy)
    }

    fun togglePainLocation(location: PainLocation) {
        val current = _uiState.value.selectedPainLocations.toMutableSet()
        if (location == PainLocation.NONE) {
            current.clear()
            current.add(PainLocation.NONE)
        } else {
            current.remove(PainLocation.NONE)
            if (current.contains(location)) current.remove(location) else current.add(location)
        }
        _uiState.value = _uiState.value.copy(selectedPainLocations = current)
    }

    fun selectSleep(sleep: SleepQuality) {
        _uiState.value = _uiState.value.copy(selectedSleep = sleep)
    }

    fun selectStress(stress: Int) {
        _uiState.value = _uiState.value.copy(stressLevel = stress)
    }

    fun selectAppetite(appetite: AppetiteLevel) {
        _uiState.value = _uiState.value.copy(selectedAppetite = appetite)
    }

    fun toggleSymptom(symptom: Symptom) {
        val current = _uiState.value.selectedSymptoms.toMutableSet()
        if (current.contains(symptom)) current.remove(symptom) else current.add(symptom)
        _uiState.value = _uiState.value.copy(selectedSymptoms = current)
    }

    fun toggleSupplement(supplement: SupplementType) {
        val current = _uiState.value.selectedSupplements.toMutableSet()
        if (current.contains(supplement)) current.remove(supplement) else current.add(supplement)
        _uiState.value = _uiState.value.copy(selectedSupplements = current)
    }

    fun setPmddScore(score: Int) {
        _uiState.value = _uiState.value.copy(pmddScore = score)
    }

    fun setEmergencyEvent(event: EmergencyEvent) {
        _uiState.value = _uiState.value.copy(emergencyEvent = event)
    }

    fun selectCervicalMucus(type: CervicalMucusType) {
        _uiState.value = _uiState.value.copy(selectedCervicalMucus = type)
    }

    fun selectLhTest(result: LhTestResult) {
        _uiState.value = _uiState.value.copy(selectedLhTest = result)
    }

    fun setNotes(notes: String) {
        _uiState.value = _uiState.value.copy(notes = notes)
    }

    fun saveCheckIn(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            val state = _uiState.value

            val log = DailyLog(
                date = state.date,
                mood = state.selectedMood,
                energyLevel = state.energyLevel,
                sleepQuality = state.selectedSleep,
                stressLevel = state.stressLevel,
                appetite = state.selectedAppetite,
                supplementsTaken = state.selectedSupplements.toList(),
                pmddScore = state.pmddScore,
                emergencyEvent = state.emergencyEvent,
                cervicalMucus = state.selectedCervicalMucus,
                lhTestResult = state.selectedLhTest,
                notes = state.notes
            )
            logRepository.saveLog(log)

            // Save symptoms
            for (s in state.selectedSymptoms) {
                symptomRepository.insertSymptom(
                    SymptomEntry(
                        date = state.date,
                        symptom = s,
                        severity = 2
                    )
                )
            }

            // Save pain entries
            for (p in state.selectedPainLocations) {
                if (p != PainLocation.NONE) {
                    symptomRepository.insertPain(
                        PainEntry(
                            date = state.date,
                            location = p,
                            intensity = if (state.painIntensity > 0) state.painIntensity else 3,
                            duration = PainDuration.INTERMITTENT
                        )
                    )
                }
            }

            _uiState.value = _uiState.value.copy(isSaving = false, isSaved = true)
            onSuccess()
        }
    }
}
