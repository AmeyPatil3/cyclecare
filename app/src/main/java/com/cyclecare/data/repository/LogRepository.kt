package com.cyclecare.data.repository

import com.cyclecare.data.db.dao.DailyLogDao
import com.cyclecare.data.db.dao.PainDao
import com.cyclecare.data.db.dao.SymptomDao
import com.cyclecare.data.db.entity.DailyLog
import com.cyclecare.data.db.entity.PainEntry
import com.cyclecare.data.db.entity.SymptomEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LogRepository @Inject constructor(
    private val dailyLogDao: DailyLogDao
) {
    fun observeLog(date: LocalDate): Flow<DailyLog?> =
        dailyLogDao.observeLogForDate(date)

    suspend fun getLog(date: LocalDate): DailyLog? =
        dailyLogDao.getLogForDate(date)

    suspend fun saveLog(log: DailyLog) =
        dailyLogDao.insertOrUpdateLog(log)

    suspend fun getAllLogs(): List<DailyLog> =
        dailyLogDao.getAllLogs()

    suspend fun getLogsBetween(start: LocalDate, end: LocalDate): List<DailyLog> =
        dailyLogDao.getLogsBetween(start, end)

    suspend fun deleteAllLogs() =
        dailyLogDao.deleteAllLogs()
}

@Singleton
class SymptomRepository @Inject constructor(
    private val symptomDao: SymptomDao,
    private val painDao: PainDao
) {
    fun observeSymptoms(date: LocalDate): Flow<List<SymptomEntry>> =
        symptomDao.observeSymptomsForDate(date)

    suspend fun getSymptomsForDate(date: LocalDate): List<SymptomEntry> =
        symptomDao.getSymptomsForDate(date)

    suspend fun insertSymptom(entry: SymptomEntry): Long =
        symptomDao.insertSymptom(entry)

    suspend fun getPainForDate(date: LocalDate): List<PainEntry> =
        painDao.getPainForDate(date)

    suspend fun insertPain(entry: PainEntry): Long =
        painDao.insertPain(entry)

    suspend fun deleteAllSymptomsAndPain() {
        symptomDao.deleteAllSymptoms()
        painDao.deleteAllPain()
    }
}
