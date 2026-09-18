package com.cyclecare.di

import android.content.Context
import androidx.room.Room
import com.cyclecare.data.db.CycleCareDatabase
import com.cyclecare.data.db.dao.DailyLogDao
import com.cyclecare.data.db.dao.PeriodDao
import com.cyclecare.data.db.dao.SymptomDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): CycleCareDatabase =
        Room.databaseBuilder(
            context,
            CycleCareDatabase::class.java,
            "cyclecare.db"
        )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun providePeriodDao(db: CycleCareDatabase): PeriodDao = db.periodDao()

    @Provides
    fun provideDailyLogDao(db: CycleCareDatabase): DailyLogDao = db.dailyLogDao()

    @Provides
    fun provideSymptomDao(db: CycleCareDatabase): SymptomDao = db.symptomDao()

    @Provides
    fun providePainDao(db: CycleCareDatabase): com.cyclecare.data.db.dao.PainDao = db.painDao()
}
