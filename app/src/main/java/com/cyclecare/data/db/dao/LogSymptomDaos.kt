package com.cyclecare.data.db.dao

import androidx.room.*
import com.cyclecare.data.db.entity.DailyLog
import com.cyclecare.data.db.entity.PainEntry
import com.cyclecare.data.db.entity.SymptomEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

@Dao
interface DailyLogDao {

    @Query("SELECT * FROM daily_logs WHERE date = :date")
    suspend fun getLogForDate(date: LocalDate): DailyLog?

    @Query("SELECT * FROM daily_logs WHERE date = :date")
    fun observeLogForDate(date: LocalDate): Flow<DailyLog?>

    @Query("SELECT * FROM daily_logs WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    suspend fun getLogsBetween(startDate: LocalDate, endDate: LocalDate): List<DailyLog>

    @Query("SELECT * FROM daily_logs ORDER BY date DESC")
    suspend fun getAllLogs(): List<DailyLog>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateLog(log: DailyLog)

    @Delete
    suspend fun deleteLog(log: DailyLog)

    @Query("DELETE FROM daily_logs")
    suspend fun deleteAllLogs()
}

@Dao
interface SymptomDao {

    @Query("SELECT * FROM symptom_entries WHERE date = :date")
    suspend fun getSymptomsForDate(date: LocalDate): List<SymptomEntry>

    @Query("SELECT * FROM symptom_entries WHERE date = :date")
    fun observeSymptomsForDate(date: LocalDate): Flow<List<SymptomEntry>>

    @Query("SELECT * FROM symptom_entries WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getSymptomsBetween(startDate: LocalDate, endDate: LocalDate): List<SymptomEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSymptom(symptom: SymptomEntry): Long

    @Delete
    suspend fun deleteSymptom(symptom: SymptomEntry)

    @Query("DELETE FROM symptom_entries")
    suspend fun deleteAllSymptoms()
}

@Dao
interface PainDao {

    @Query("SELECT * FROM pain_entries WHERE date = :date")
    suspend fun getPainForDate(date: LocalDate): List<PainEntry>

    @Query("SELECT * FROM pain_entries WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getPainBetween(startDate: LocalDate, endDate: LocalDate): List<PainEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPain(pain: PainEntry): Long

    @Delete
    suspend fun deletePain(pain: PainEntry)

    @Query("DELETE FROM pain_entries")
    suspend fun deleteAllPain()
}
