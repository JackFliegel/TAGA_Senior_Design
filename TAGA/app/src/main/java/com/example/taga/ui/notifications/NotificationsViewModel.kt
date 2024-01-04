package com.example.taga.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

//class NotificationsViewModel : ViewModel() {
//
//    private val _text = MutableLiveData<String>().apply {
//        value = "This is notifications Fragment test"
//    }
//    val text: LiveData<String> = _text
//}

data class NotificationItem(val id: Int, val text: String, val timestamp: Long)

class NotificationsViewModel : ViewModel() {
    // LiveData to hold the list of notifications
    private val _notifications = MutableLiveData<List<NotificationItem>>()
    val notifications: LiveData<List<NotificationItem>> = _notifications

    // Initialize with an empty list
    init {
        _notifications.value = listOf()
    }

    fun addNotification(notificationText: String) {
        // Create a new NotificationItem (ID generation logic needed)
        val newNotification = NotificationItem(generateId(), notificationText, System.currentTimeMillis())

        // Add new notification to the existing list
        val updatedList = _notifications.value.orEmpty() + newNotification
        _notifications.value = updatedList
    }

    fun deleteNotification(notificationId: Int) {
        // Filter out the notification to delete
        val updatedList = _notifications.value.orEmpty().filter { it.id != notificationId }
        _notifications.value = updatedList
    }

    // Dummy function to generate unique IDs for notifications
    // ****replace this with a more robust ID generation logic
    private fun generateId(): Int {
        return _notifications.value.orEmpty().size + 1
    }
}
