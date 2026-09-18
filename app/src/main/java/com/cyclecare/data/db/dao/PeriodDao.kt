package com.cyclecare.data.db.dao

import androidx.room.*
import com.cyclecare.data.db.entity.PeriodEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

@Dao
interface PeriodDao {

    @Query("SELECT * FROM period_entries ORDER BY startDate DESC")
    fun observeAllPeriods(): Flow<List<PeriodEntry>>

    @Query("SELECT * FROM period_entries ORDER BY startDate DESC")
    suspend fun getAllPeriods(): List<PeriodEntry>

    @Query("SELECT * FROM period_entries ORDER BY startDate DESC LIMIT 1")
    suspend fun getLatestPeriod(): PeriodEntry?

    @Query("SELECT * FROM period_entries ORDER BY startDate DESC LIMIT 1")
    fun observeLatestPeriod(): Flow<PeriodEntry?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPeriod(period: PeriodEntry): Long

    @Update
    suspend fun updatePeriod(period: PeriodEntry)

    @Delete
    suspend fun deletePeriod(period: PeriodEntry)

    @Query("DELETE FROM period_entries")
    suspend fun deleteAllPeriods()
}
