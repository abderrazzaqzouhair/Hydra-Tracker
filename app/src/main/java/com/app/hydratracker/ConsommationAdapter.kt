package com.app.hydratracker

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.app.hydratracker.data.DailyTarget


class ConsommationAdapter(private val consommations: List<DailyTarget>) :
    RecyclerView.Adapter<ConsommationAdapter.ConsommationViewHolder>() {

    class ConsommationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var consomationCard:CardView = view.findViewById(R.id.consomationCard)
        val descriptionTextView: TextView = view.findViewById(R.id.textDescription)
        val dateTextView: TextView = view.findViewById(R.id.textDate)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsommationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_consommation, parent, false)
        return ConsommationViewHolder(view)
    }
    override fun onBindViewHolder(holder: ConsommationViewHolder, position: Int) {
        val consommation = consommations[position]
        holder.descriptionTextView.text = consommation.targetAmount.toString()
        holder.dateTextView.text = consommation.time
        if (consommation.isdrinked){
            val customGreen = Color.parseColor("#4CAF50")
            holder.consomationCard.backgroundTintList = ColorStateList.valueOf(customGreen)
        }
    }

    override fun getItemCount(): Int = consommations.size
}
