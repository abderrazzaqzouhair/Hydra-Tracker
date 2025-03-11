package com.app.hydratracker.ui.advice


import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.hydratracker.R
import com.app.hydratracker.data.AdviceItem

class AdviceAdapter(private val cardList: List<AdviceItem>) :
    RecyclerView.Adapter<AdviceAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardImage: ImageView = view.findViewById(R.id.cardImage)
        val cardTitle: TextView = view.findViewById(R.id.cardTitle)
        val cardDescription: TextView = view.findViewById(R.id.cardDescription)
        val readMoreButton: Button = view.findViewById(R.id.readMoreButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.advice_card_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cardItem = cardList[position]
        holder.cardImage.setImageResource(cardItem.imageResource)
        holder.cardTitle.text = cardItem.title
        holder.cardDescription.text = cardItem.description

        holder.readMoreButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.mayoclinichealthsystem.org/hometown-health/speaking-of-health/tips-for-drinking-more-water"))
            it.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = cardList.size
}
