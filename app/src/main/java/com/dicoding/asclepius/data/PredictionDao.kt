package com.dicoding.asclepius.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PredictionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrediction(prediction: Prediction)

    @Query("SELECT * FROM predictions")
    suspend fun getAllPredictions(): List<Prediction>

    @Delete
    suspend fun deletePrediction(prediction: Prediction)
}