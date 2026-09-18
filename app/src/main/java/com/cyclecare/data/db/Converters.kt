package com.cyclecare.data.db

import androidx.room.TypeConverter
import com.cyclecare.domain.model.*
import kotlinx.datetime.LocalDate

class Converters {

    @TypeConverter
    fun fromLocalDate(value: LocalDate?): String? = value?.toString()

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? = value?.let { LocalDate.parse(it) }

    @TypeConverter
    fun fromFlowIntensity(value: FlowIntensity?): String? = value?.name

    @TypeConverter
    fun toFlowIntensity(value: String?): FlowIntensity? =
        value?.let { enumValueOf<FlowIntensity>(it) }

    @TypeConverter
    fun fromMoodLevel(value: MoodLevel?): String? = value?.name

    @TypeConverter
    fun toMoodLevel(value: String?): MoodLevel? =
        value?.let { enumValueOf<MoodLevel>(it) }

    @TypeConverter
    fun fromPainLocation(value: PainLocation?): String? = value?.name

    @TypeConverter
    fun toPainLocation(value: String?): PainLocation? =
        value?.let { enumValueOf<PainLocation>(it) }

    @TypeConverter
    fun fromPainDuration(value: PainDuration?): String? = value?.name

    @TypeConverter
    fun toPainDuration(value: String?): PainDuration? =
        value?.let { enumValueOf<PainDuration>(it) }

    @TypeConverter
    fun fromSleepQuality(value: SleepQuality?): String? = value?.name

    @TypeConverter
    fun toSleepQuality(value: String?): SleepQuality? =
        value?.let { enumValueOf<SleepQuality>(it) }

    @TypeConverter
    fun fromAppetiteLevel(value: AppetiteLevel?): String? = value?.name

    @TypeConverter
    fun toAppetiteLevel(value: String?): AppetiteLevel? =
        value?.let { enumValueOf<AppetiteLevel>(it) }

    @TypeConverter
    fun fromSymptom(value: Symptom?): String? = value?.name

    @TypeConverter
    fun toSymptom(value: String?): Symptom? =
        value?.let { enumValueOf<Symptom>(it) }

    // List<Symptom> — used by SymptomEntry.symptoms
    @TypeConverter
    fun fromSymptomList(value: List<Symptom>?): String? =
        value?.joinToString(",") { it.name }

    @TypeConverter
    fun toSymptomList(value: String?): List<Symptom> {
        if (value.isNullOrBlank()) return emptyList()
        return value.split(",").filter { it.isNotBlank() }.mapNotNull {
            try { enumValueOf<Symptom>(it) } catch (e: Exception) { null }
        }
    }

    // List<PainLocation> — used by PainEntry.locations
    @TypeConverter
    fun fromPainLocationList(value: List<PainLocation>?): String? =
        value?.joinToString(",") { it.name }

    @TypeConverter
    fun toPainLocationList(value: String?): List<PainLocation> {
        if (value.isNullOrBlank()) return emptyList()
        return value.split(",").filter { it.isNotBlank() }.mapNotNull {
            try { enumValueOf<PainLocation>(it) } catch (e: Exception) { null }
        }
    }

    @TypeConverter
    fun fromEmergencyEvent(value: EmergencyEvent?): String? = value?.name

    @TypeConverter
    fun toEmergencyEvent(value: String?): EmergencyEvent? =
        value?.let { enumValueOf<EmergencyEvent>(it) }

    @TypeConverter
    fun fromCervicalMucus(value: CervicalMucusType?): String? = value?.name

    @TypeConverter
    fun toCervicalMucus(value: String?): CervicalMucusType? =
        value?.let { enumValueOf<CervicalMucusType>(it) }

    @TypeConverter
    fun fromLhTestResult(value: LhTestResult?): String? = value?.name

    @TypeConverter
    fun toLhTestResult(value: String?): LhTestResult? =
        value?.let { enumValueOf<LhTestResult>(it) }

    @TypeConverter
    fun fromSupplementList(value: List<SupplementType>?): String? =
        value?.joinToString(",") { it.name }

    @TypeConverter
    fun toSupplementList(value: String?): List<SupplementType>? {
        if (value.isNullOrBlank()) return emptyList()
        return value.split(",")
            .filter { it.isNotBlank() }
            .mapNotNull {
                try {
                    enumValueOf<SupplementType>(it)
                } catch (e: Exception) {
                    null
                }
            }
    }
}
