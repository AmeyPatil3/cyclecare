package com.cyclecare.ui.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyclecare.data.repository.CycleRepository
import com.cyclecare.domain.model.CycleInsightSummary
import com.cyclecare.domain.model.PmddScreenerData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class InsightsTab {
    SYMPTOMS, MOOD, ENERGY, SLEEP
}

data class CycleHistoryBar(
    val monthLabel: String,
    val lengthDays: Int,
    val isCurrent: Boolean = false
)

data class InsightsUiState(
    val summary: CycleInsightSummary? = null,
    val pmddData: PmddScreenerData? = null,
    val cycleHistory: List<CycleHistoryBar> = listOf(
        CycleHistoryBar("May", 28),
        CycleHistoryBar("Jun", 29),
        CycleHistoryBar("Jul", 27),
        CycleHistoryBar("Aug", 28),
        CycleHistoryBar("Sep", 28),
        CycleHistoryBar("Oct", 29, isCurrent = true)
    ),
    val selectedTab: InsightsTab = InsightsTab.ENERGY,
    val isExporting: Boolean = false,
    val exportSuccess: Boolean = false
)

@HiltViewModel
class InsightsViewModel @Inject constructor(
    private val cycleRepository: CycleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(InsightsUiState())
    val uiState: StateFlow<InsightsUiState> = _uiState.asStateFlow()

    init {
        loadInsights()
    }

    private fun loadInsights() {
        viewModelScope.launch {
            val summary = cycleRepository.computeInsightSummary()
            val pmdd = cycleRepository.computePmddScreener()
            _uiState.value = _uiState.value.copy(
                summary = summary,
                pmddData = pmdd
            )
        }
    }

    fun selectTab(tab: InsightsTab) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
    }

    fun exportDoctorReport(onComplete: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isExporting = true)
            kotlinx.coroutines.delay(1000)
            _uiState.value = _uiState.value.copy(isExporting = false, exportSuccess = true)
            onComplete()
        }
    }
}
