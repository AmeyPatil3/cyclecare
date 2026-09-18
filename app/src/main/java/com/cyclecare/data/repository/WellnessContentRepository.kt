package com.cyclecare.data.repository

import com.cyclecare.data.network.DynamicWellnessFact
import com.cyclecare.data.network.WellnessApiService
import com.cyclecare.domain.model.CyclePhase
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WellnessContentRepository @Inject constructor(
    private val apiService: WellnessApiService
) {
    private val cache = mutableMapOf<CyclePhase, List<DynamicWellnessFact>>()
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    suspend fun getFactsForPhase(phase: CyclePhase, forceRefresh: Boolean = false): List<DynamicWellnessFact> {
        if (!forceRefresh && cache.containsKey(phase)) {
            return cache[phase] ?: emptyList()
        }

        _isRefreshing.value = true
        val remote = apiService.fetchDynamicFactsForPhase(phase)
        cache[phase] = remote
        _isRefreshing.value = false
        return remote
    }

    suspend fun refreshAllPhases() {
        _isRefreshing.value = true
        for (phase in CyclePhase.values()) {
            cache[phase] = apiService.fetchDynamicFactsForPhase(phase)
        }
        _isRefreshing.value = false
    }
}
