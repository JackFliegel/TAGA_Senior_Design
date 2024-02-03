package com.example.taga.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.taga.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import com.example.taga.R
import com.google.android.material.switchmaterial.SwitchMaterial

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()

    private lateinit var switchConnect: SwitchMaterial
    private lateinit var textViewLifecycleState: TextView
    private lateinit var textViewReadValue: TextView
    private lateinit var editTextWriteValue: EditText
    private lateinit var textViewIndicateValue: TextView
    private lateinit var textViewSubscription: TextView
    private lateinit var textViewLog: TextView
    private lateinit var scrollViewLog: ScrollView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize UI components
        switchConnect = view.findViewById(R.id.switchConnect)
        textViewLifecycleState = view.findViewById(R.id.textViewLifecycleState)
        textViewReadValue = view.findViewById(R.id.textViewReadValue)
        editTextWriteValue = view.findViewById(R.id.editTextWriteValue)
        textViewIndicateValue = view.findViewById(R.id.textViewIndicateValue)
        textViewSubscription = view.findViewById(R.id.textViewSubscription)
        textViewLog = view.findViewById(R.id.textViewLog)
        scrollViewLog = view.findViewById(R.id.scrollViewLog)

        setupObservers()

        return view
    }

    private fun setupObservers() {
        viewModel.bleData.observe(viewLifecycleOwner, Observer { data ->
            textViewReadValue.text = data
        })

        // Observe other LiveData and state as necessary
    }

    // Additional methods to handle UI updates based on ViewModel state
}