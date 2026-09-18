package com.cyclecare.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cyclecare.domain.model.*
import kotlinx.datetime.LocalDate

/**
 * Daily check-in log for a user on a given date.
 */
@Entity(tableName = "daily_logs")
data class DailyLog(
    @PrimaryKey
    val date: LocalDate,
    val mood: MoodLevel = MoodLevel.GOOD,
    val energyLevel: Int = 5,
    val sleepQuality: SleepQuality = SleepQuality.RESTFUL,
    val stressLevel: Int = 3,
    val appetite: AppetiteLevel = AppetiteLevel.NORMAL,
    val supplementsTaken: List<SupplementType> = emptyList(),
    val pmddScore: Int = 0,
    val emergencyEvent: EmergencyEvent = EmergencyEvent.NONE,
    val basalBodyTemp: Float? = null,
    val cervicalMucus: CervicalMucusType = CervicalMucusType.DRY,
    val lhTestResult: LhTestResult = LhTestResult.NOT_TESTED,
    val notes: String = ""
)
