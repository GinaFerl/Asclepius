package com.dicoding.asclepius.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.Prediction

class HistoryAdapter(private val predictions: List<Prediction>): RecyclerView.Adapter<HistoryAdapter.ViewModel>() {
    private var onDeleteClickListener: OnDeleteClickListener? = null

    interface OnDeleteClickListener {
        fun onDeleteClick(position: Int)
    }

    fun setOnDeleteClickListener(listener: OnDeleteClickListener) {
        onDeleteClickListener = listener
    }

    inner class ViewModel(itemView: View): RecyclerView.ViewHolder(itemView) {
        val imageView = itemView.findViewById<ImageView>(R.id.result_image)
        val textView = itemView.findViewById<TextView>(R.id.result_label)
        val deleteButton = itemView.findViewById<Button>(R.id.btn_delete)

        fun bind(prediction: Prediction) {
            Glide.with(itemView)
                .load(prediction.imagePath)
                .into(imageView)

            textView.text = prediction.predictionResult
            deleteButton.setOnClickListener {
                onDeleteClickListener?.onDeleteClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewModel {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
        return ViewModel(view)
    }

    override fun getItemCount() = predictions.size

    override fun onBindViewHolder(holder: ViewModel, position: Int) {
        val currentPrediction = predictions[position]
        if (currentPrediction.predictionResult.isNotEmpty()) {
            holder.bind(currentPrediction)
            holder.itemView.visibility = View.VISIBLE
            holder.itemView.layoutParams = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        } else {
            holder.itemView.visibility = View.GONE
            holder.itemView.layoutParams = RecyclerView.LayoutParams(0,0)
        }
    }
}