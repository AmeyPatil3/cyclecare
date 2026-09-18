package com.cyclecare.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyclecare.data.db.entity.DailyLog
import com.cyclecare.data.db.entity.PeriodEntry
import com.cyclecare.data.db.entity.SymptomEntry
import com.cyclecare.data.repository.CycleRepository
import com.cyclecare.data.repository.LogRepository
import com.cyclecare.data.repository.SymptomRepository
import com.cyclecare.domain.model.CyclePhase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import javax.inject.Inject

data class CalendarDayModel(
    val date: LocalDate,
    val dayNumber: Int,
    val isCurrentMonth: Boolean,
    val isToday: Boolean,
    val isSelected: Boolean,
    val phase: CyclePhase?,
    val isPeriodDay: Boolean,
    val isOvulationDay: Boolean,
    val hasLog: Boolean
)

data class CalendarUiState(
    val currentMonth: LocalDate,
    val selectedDate: LocalDate,
    val days: List<CalendarDayModel> = emptyList(),
    val selectedPhase: CyclePhase = CyclePhase.OVULATORY,
    val selectedLog: DailyLog? = null,
    val selectedSymptoms: List<SymptomEntry> = emptyList(),
    val periodEntries: List<PeriodEntry> = emptyList()
)

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val cycleRepository: CycleRepository,
    private val logRepository: LogRepository,
    private val symptomRepository: SymptomRepository
) : ViewModel() {

    private val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    private val _uiState = MutableStateFlow(
        CalendarUiState(
            currentMonth = LocalDate(today.year, today.monthNumber, 1),
            selectedDate = today
        )
    )
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    init {
        loadMonthData()
    }

    fun previousMonth() {
        val current = _uiState.value.currentMonth
        val prev = if (current.monthNumber == 1) {
            LocalDate(current.year - 1, 12, 1)
        } else {
            LocalDate(current.year, current.monthNumber - 1, 1)
        }
        _uiState.value = _uiState.value.copy(currentMonth = prev)
        loadMonthData()
    }

    fun nextMonth() {
        val current = _uiState.value.currentMonth
        val next = if (current.monthNumber == 12) {
            LocalDate(current.year + 1, 1, 1)
        } else {
            LocalDate(current.year, current.monthNumber + 1, 1)
        }
        _uiState.value = _uiState.value.copy(currentMonth = next)
        loadMonthData()
    }

    fun jumpToToday() {
        val firstOfMonth = LocalDate(today.year, today.monthNumber, 1)
        _uiState.value = _uiState.value.copy(
            currentMonth = firstOfMonth,
            selectedDate = today
        )
        loadMonthData()
    }

    fun selectDate(date: LocalDate) {
        _uiState.value = _uiState.value.copy(selectedDate = date)
        loadDayDetail(date)
        generateGrid()
    }

    private fun loadMonthData() {
        viewModelScope.launch {
            loadDayDetail(_uiState.value.selectedDate)
            generateGrid()
        }
    }

    private fun loadDayDetail(date: LocalDate) {
        viewModelScope.launch {
            val log = logRepository.getLog(date)
            val symptoms = symptomRepository.getSymptomsForDate(date)
            val phase = determinePhaseForDate(date)
            _uiState.value = _uiState.value.copy(
                selectedPhase = phase,
                selectedLog = log,
                selectedSymptoms = symptoms
            )
        }
    }

    private fun determinePhaseForDate(date: LocalDate): CyclePhase {
        // Based on a standard 28-day reference cycle for calendar projection
        val dayOfCycle = ((date.dayOfMonth - 1) % 28) + 1
        return when (dayOfCycle) {
            in 1..5 -> CyclePhase.MENSTRUAL
            in 6..13 -> CyclePhase.FOLLICULAR
            in 14..18 -> CyclePhase.OVULATORY
            else -> CyclePhase.LUTEAL
        }
    }

    private fun generateGrid() {
        val month = _uiState.value.currentMonth
        val selected = _uiState.value.selectedDate
        val daysInMonth = daysInMonth(month.year, month.monthNumber)

        // Day of week of the 1st (0 = Sunday, 1 = Monday, etc.)
        val firstDayOfWeek = month.dayOfWeek.ordinal // Monday is 0, Sunday is 6
        // Let's adjust so Sunday is 0
        val sundayFirstOffset = (firstDayOfWeek + 1) % 7

        val list = mutableListOf<CalendarDayModel>()

        // Previous month padding days
        for (i in 0 until sundayFirstOffset) {
            val prevDay = 30 - sundayFirstOffset + 1 + i
            list.add(
                CalendarDayModel(
                    date = LocalDate(month.year, if (month.monthNumber == 1) 12 else month.monthNumber - 1, prevDay.coerceIn(1, 31)),
                    dayNumber = prevDay,
                    isCurrentMonth = false,
                    isToday = false,
                    isSelected = false,
                    phase = null,
                    isPeriodDay = false,
                    isOvulationDay = false,
                    hasLog = false
                )
            )
        }

        // Current month days
        for (day in 1..daysInMonth) {
            val date = LocalDate(month.year, month.monthNumber, day)
            val phase = determinePhaseForDate(date)
            list.add(
                CalendarDayModel(
                    date = date,
                    dayNumber = day,
                    isCurrentMonth = true,
                    isToday = date == today,
                    isSelected = date == selected,
                    phase = phase,
                    isPeriodDay = phase == CyclePhase.MENSTRUAL,
                    isOvulationDay = phase == CyclePhase.OVULATORY && day % 28 == 14,
                    hasLog = date <= today
                )
            )
        }

        // Next month padding to round up to complete rows
        val remaining = (7 - (list.size % 7)) % 7
        for (i in 1..remaining) {
            list.add(
                CalendarDayModel(
                    date = LocalDate(month.year, if (month.monthNumber == 12) 1 else month.monthNumber + 1, i),
                    dayNumber = i,
                    isCurrentMonth = false,
                    isToday = false,
                    isSelected = false,
                    phase = null,
                    isPeriodDay = false,
                    isOvulationDay = false,
                    hasLog = false
                )
            )
        }

        _uiState.value = _uiState.value.copy(days = list)
    }

    private fun daysInMonth(year: Int, month: Int): Int {
        return when (month) {
            2 -> if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28
            4, 6, 9, 11 -> 30
            else -> 31
        }
    }
}
