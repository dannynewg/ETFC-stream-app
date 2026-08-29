package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        DownloadedFightEntity::class,
        PpvPassEntity::class,
        UserPredictionEntity::class,
        FavoriteFighterEntity::class,
        FighterProfileEntity::class,
        FighterStatsEntity::class,
        FightRecordHistoryEntity::class,
        WeightClassHistoryEntity::class,
        UserAccountEntity::class,
        ChatMessageEntity::class,
        ModerationLogEntity::class,
        HeroSlideEntity::class,
        PreviousVideoEntity::class,
        NewsArticleEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class EtfcDatabase : RoomDatabase() {
    abstract fun etfcDao(): EtfcDao

    companion object {
        @Volatile
        private var INSTANCE: EtfcDatabase? = null

        fun getDatabase(context: Context): EtfcDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EtfcDatabase::class.java,
                    "etfc_stream_database.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
