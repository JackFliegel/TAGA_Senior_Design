package com.example.taga.ui.notifications
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.taga.databinding.NotificationItemBinding
import java.text.SimpleDateFormat
import java.util.*

class NotificationAdapter : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    var notifications = listOf<NotificationItem>()

    class NotificationViewHolder(private val binding: NotificationItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(notification: NotificationItem) {
            binding.notificationText.text = notification.text
            binding.notificationTimestamp.text = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date(notification.timestamp))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = NotificationItemBinding.inflate(inflater, parent, false)
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notifications[position]
        holder.bind(notification)
    }

    override fun getItemCount() = notifications.size

    fun submitList(newList: List<NotificationItem>) {
        notifications = newList
        notifyDataSetChanged()
    }
}
