package com.example.taga.ui.data

import com.example.taga.ui.util.Resource
import kotlinx.coroutines.flow.MutableSharedFlow

interface ReceiveManager {

    val data: MutableSharedFlow<Resource<DataResult>>

    fun reconnect()

    fun disconnect()

    fun startReceiving()

    fun stopReceiving()
}