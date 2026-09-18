package com.cyclecare.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cyclecare.domain.model.FlowIntensity
import kotlinx.datetime.LocalDate

/**
 * A logged period entry — start date, optional end date, and flow.
 */
@Entity(tableName = "period_entries")
data class PeriodEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val startDate: LocalDate,
    val endDate: LocalDate? = null,
    val flow: FlowIntensity = FlowIntensity.MEDIUM,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
