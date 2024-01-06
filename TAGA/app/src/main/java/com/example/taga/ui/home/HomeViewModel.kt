package com.example.taga.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taga.ui.BLE.BLEReceiveManager
import com.example.taga.ui.data.ConnectionState
import com.example.taga.ui.data.DataResult
import com.example.taga.ui.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val bleReceiveManager: BLEReceiveManager
) : ViewModel() {
    var initializingMessage by mutableStateOf<String?>(null )
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set
    var alerted by mutableStateOf<String?>(null)
        private set
    var connectionState by mutableStateOf<ConnectionState?>(null)
        private set

    private fun subscribeToChanges(){
        viewModelScope.launch {
            bleReceiveManager.data.collect{ result ->
                when(result){
                    is Resource.Success<*> -> {
//                        connectionState = result.data.connectionState
//                        alerted = result.data.alerted
                        (result.data as? DataResult)?.let { dataResult ->
                            connectionState = dataResult.connectionState
                            alerted = dataResult.alerted
                        }
                    }
                    is Resource.Loading<*> -> {
                        initializingMessage = result.message
                        connectionState = ConnectionState.CurrentlyInitializing
                    }
                    is Resource.Error -> {
                        errorMessage = result.errorMessage
                        connectionState= ConnectionState.Uninitialized
                    }
                }
            }
        }
    }

    fun disconnect(){
        bleReceiveManager.disconnect()
    }

    fun reconnect() {
        bleReceiveManager.reconnect()
    }

    fun initliazeConnection(){
        errorMessage = "ERROR"
        subscribeToChanges()
        bleReceiveManager.startReceiving()

    }

    override fun onCleared() {
        super.onCleared()
        bleReceiveManager.stopReceiving() //Might have to change if clearing makes BLE disconnect (original was closeConnection)
    }



    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text
}