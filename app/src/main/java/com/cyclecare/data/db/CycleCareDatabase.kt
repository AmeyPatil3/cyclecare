package com.cyclecare.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.cyclecare.data.db.dao.DailyLogDao
import com.cyclecare.data.db.dao.PainDao
import com.cyclecare.data.db.dao.PeriodDao
import com.cyclecare.data.db.dao.SymptomDao
import com.cyclecare.data.db.entity.DailyLog
import com.cyclecare.data.db.entity.PainEntry
import com.cyclecare.data.db.entity.PeriodEntry
import com.cyclecare.data.db.entity.SymptomEntry

@Database(
    entities = [
        PeriodEntry::class,
        DailyLog::class,
        SymptomEntry::class,
        PainEntry::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CycleCareDatabase : RoomDatabase() {
    abstract fun periodDao(): PeriodDao
    abstract fun dailyLogDao(): DailyLogDao
    abstract fun symptomDao(): SymptomDao
    abstract fun painDao(): PainDao
}
