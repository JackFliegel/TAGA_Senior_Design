package com.example.taga.ui.notifications

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.taga.databinding.FragmentNotificationsBinding
import androidx.recyclerview.widget.LinearLayoutManager


class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
        ): View {
            _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
            val root: View = binding.root
            Log.d("notification tag", "Made it to notifications")

            val notificationsViewModel =
                ViewModelProvider(this).get(NotificationsViewModel::class.java)

            val addButton: Button = binding.addButton
            val notificationInput: EditText = binding.notificationInput
            val notificationsList: RecyclerView = binding.notificationsList

            // Setup RecyclerView with adapter and layout manager
            val adapter = NotificationAdapter()
            binding.notificationsList.adapter = adapter
            binding.notificationsList.layoutManager = LinearLayoutManager(context)

            // Observe LiveData from ViewModel
            notificationsViewModel.notifications.observe(viewLifecycleOwner) { notifications ->
                adapter.submitList(notifications)
            }


        addButton.setOnClickListener {
            val text = notificationInput.text.toString()
            if (text.isNotEmpty()) {
                notificationsViewModel.addNotification(text)
                notificationInput.text.clear()
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}