package com.cyclecare.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyclecare.data.repository.CycleRepository
import com.cyclecare.domain.model.TrackingMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val currentMode: TrackingMode = TrackingMode.REGULAR,
    val isWiped: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val cycleRepository: CycleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            cycleRepository.trackingMode.collect { mode ->
                _uiState.value = _uiState.value.copy(currentMode = mode)
            }
        }
    }

    fun setTrackingMode(mode: TrackingMode) {
        cycleRepository.setTrackingMode(mode)
    }

    fun panicWipeAllData(onComplete: () -> Unit) {
        viewModelScope.launch {
            cycleRepository.panicWipeAllData()
            _uiState.value = _uiState.value.copy(isWiped = true)
            onComplete()
        }
    }
}
