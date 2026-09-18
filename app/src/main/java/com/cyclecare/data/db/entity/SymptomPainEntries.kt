package com.cyclecare.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cyclecare.domain.model.PainDuration
import com.cyclecare.domain.model.PainLocation
import com.cyclecare.domain.model.Symptom
import kotlinx.datetime.LocalDate

/**
 * Symptom entry — lists selected symptoms for a given date.
 */
@Entity(tableName = "symptom_entries")
data class SymptomEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: LocalDate,
    val symptoms: List<Symptom> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Pain detail entry — severity, locations, duration.
 */
@Entity(tableName = "pain_entries")
data class PainEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: LocalDate,
    val severity: Int = 0,              // 0–10
    val locations: List<PainLocation> = emptyList(),
    val duration: PainDuration? = null,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
