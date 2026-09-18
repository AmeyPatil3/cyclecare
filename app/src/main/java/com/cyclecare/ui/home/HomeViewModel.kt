package com.cyclecare.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyclecare.data.db.entity.DailyLog
import com.cyclecare.data.db.entity.PeriodEntry
import com.cyclecare.data.db.entity.SymptomEntry
import com.cyclecare.data.repository.CycleRepository
import com.cyclecare.data.repository.LogRepository
import com.cyclecare.data.repository.SymptomRepository
import com.cyclecare.domain.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import javax.inject.Inject

data class HomeUiState(
    val cycleState: CycleState? = null,
    val todayLog: DailyLog? = null,
    val recentSymptoms: List<SymptomEntry> = emptyList(),
    val userName: String = "Maya",
    val isLoading: Boolean = false,
    val periodLoggedToday: Boolean = false,
    val isStealthMode: Boolean = false,
    val trackingMode: TrackingMode = TrackingMode.REGULAR
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val cycleRepository: CycleRepository,
    private val logRepository: LogRepository,
    private val symptomRepository: SymptomRepository
) : ViewModel() {

    private val _isStealthMode = MutableStateFlow(false)
    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())

            combine(
                cycleRepository.observeCycleState(),
                logRepository.observeLog(today),
                symptomRepository.observeSymptoms(today),
                cycleRepository.trackingMode,
                _isStealthMode
            ) { cycleState, todayLog, symptoms, trackingMode, stealth ->
                HomeUiState(
                    cycleState = cycleState,
                    todayLog = todayLog,
                    recentSymptoms = symptoms,
                    userName = "Maya",
                    isLoading = false,
                    periodLoggedToday = cycleState.cyclePhase == CyclePhase.MENSTRUAL,
                    isStealthMode = stealth,
                    trackingMode = trackingMode
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun toggleStealthMode() {
        _isStealthMode.value = !_isStealthMode.value
    }

    fun toggleSupplementQuick(supplement: SupplementType) {
        viewModelScope.launch {
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            val existing = logRepository.getLog(today)
            val currentSupplements = existing?.supplementsTaken?.toMutableList() ?: mutableListOf()
            if (currentSupplements.contains(supplement)) {
                currentSupplements.remove(supplement)
            } else {
                currentSupplements.add(supplement)
            }
            if (existing != null) {
                logRepository.saveLog(existing.copy(supplementsTaken = currentSupplements))
            } else {
                logRepository.saveLog(DailyLog(date = today, supplementsTaken = currentSupplements))
            }
        }
    }

    fun logPeriodStart() {
        viewModelScope.launch {
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            cycleRepository.insertPeriod(
                PeriodEntry(
                    startDate = today,
                    flowIntensity = FlowIntensity.MEDIUM
                )
            )
        }
    }
}
