package com.dicoding.asclepius.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Prediction::class], version = 1, exportSchema = false)
abstract class PredictionDatabase: RoomDatabase() {
    abstract fun predictionDao(): PredictionDao

    companion object {
        const val DATABASE_NAME = "prediction_db"

        @Volatile
        private var instance: PredictionDatabase? = null

        fun getInstance(context: Context): PredictionDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    PredictionDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
            }
        }
    }
}