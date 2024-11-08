package com.dicoding.asclepius.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.asclepius.R
import com.dicoding.asclepius.adapter.HistoryAdapter
import com.dicoding.asclepius.data.Prediction
import com.dicoding.asclepius.data.PredictionDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HistoryActivity : AppCompatActivity(), HistoryAdapter.OnDeleteClickListener {
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var recyclerView: RecyclerView
    private var predictionList: MutableList<Prediction> = mutableListOf()

    companion object {
        const val TAG = "HistoryActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        recyclerView = findViewById(R.id.rv_history)
        historyAdapter = HistoryAdapter(predictionList)
        historyAdapter.setOnDeleteClickListener(this)
        recyclerView.adapter = historyAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        GlobalScope.launch(Dispatchers.Main) {
            getPredictionsFromDatabase()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            GlobalScope.launch(Dispatchers.Main) {
                getPredictionsFromDatabase()
            }
        }
    }

    private fun getPredictionsFromDatabase() {
        GlobalScope.launch(Dispatchers.Main) {
            val predictions = PredictionDatabase.getInstance(this@HistoryActivity).predictionDao().getAllPredictions()
            Log.d(TAG, "Predictions: ${predictions.size}")
            predictionList.clear()
            predictionList.addAll(predictions)
            historyAdapter.notifyDataSetChanged()
            showOrHideEmpty()
        }

    }

    private fun showOrHideEmpty() {
        if (predictionList.isEmpty()) {
            recyclerView.visibility = View.GONE
        } else {
            recyclerView.visibility = View.VISIBLE
        }
    }

    override fun onDeleteClick(position: Int) {
        val prediction = predictionList[position]
        if (prediction.predictionResult.isNotEmpty()) {
            GlobalScope.launch(Dispatchers.IO) {
                PredictionDatabase.getInstance(this@HistoryActivity).predictionDao().deletePrediction(prediction)
            }
            predictionList.removeAt(position)
            historyAdapter.notifyItemRemoved(position)
            showOrHideEmpty()
        }
    }
}