package com.app.hydratracker.ui.notification

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.app.hydratracker.R
import com.app.hydratracker.data.Notifications
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotificationAdapter(private val notifications: List<Notifications>, var onclick:(position:Int)->Unit) :
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var notification: CardView = view.findViewById(R.id.notifica)
        val title: TextView = view.findViewById(R.id.title)
        val image: ImageView = view.findViewById(R.id.image)
        val description: TextView = view.findViewById(R.id.description)
        val date: TextView = view.findViewById(R.id.date)
        val notRead: ImageView = view.findViewById(R.id.not_read)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.notification, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val notification = notifications[position]
        viewHolder.title.text = notification.title
        viewHolder.image.setImageResource(notification.image)
        viewHolder.description.text = notification.description
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        viewHolder.date.text = dateFormat.format(notification.date)
        viewHolder.notRead.visibility = if(notification.readed)  View.GONE else View.VISIBLE
        viewHolder.notification.setOnClickListener {
            onclick(position)
            markAsRead(position)

        }
    }

    override fun getItemCount() = notifications.size

    fun markAsRead(position: Int) {
        if (position in notifications.indices) {
            notifications[position].readed = true
            notifyItemChanged(position)
        }
    }

}