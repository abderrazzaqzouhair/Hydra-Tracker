package com.app.hydratracker.ui.goals

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.hydratracker.R
import com.app.hydratracker.data.WaterIntake
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.cardview.widget.CardView


class WaterIntakeAdapter(private val waterIntakeList: List<WaterIntake>) :
    RecyclerView.Adapter<WaterIntakeAdapter.WaterIntakeViewHolder>() {

    class WaterIntakeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.cardView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val amountTextView: TextView = itemView.findViewById(R.id.amountTextView)
        val targetTextView: TextView = itemView.findViewById(R.id.targetTextView)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WaterIntakeViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.target_layout, parent, false)
        return WaterIntakeViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: WaterIntakeViewHolder, position: Int) {
        val currentItem = waterIntakeList[position]

        holder.dateTextView.text = "Date: ${currentItem.date}"

        holder.amountTextView.text = "Amount: ${currentItem.amount} ml"

        holder.targetTextView.text = "Target: ${currentItem.target} ml"

        val progress = (currentItem.amount.toFloat() / currentItem.target.toFloat()) * 100
        holder.progressBar.progress = progress.toInt()
        if (progress >= 70){
            holder.cardView.setCardBackgroundColor(holder.itemView.context.getColor(R.color.progress_100))
        }else{
            holder.cardView.setCardBackgroundColor(holder.itemView.context.getColor(R.color.inputHintColor))

        }
    }
    override fun getItemCount(): Int {
        return waterIntakeList.size
    }
}