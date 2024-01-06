package com.example.taga.ui.BLE

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.util.Log
import com.example.taga.ui.data.ConnectionState
import com.example.taga.ui.data.DataResult
import com.example.taga.ui.data.ReceiveManager
import com.example.taga.ui.util.Resource
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@OptIn(ExperimentalPermissionsApi::class)

class BLEReceiveManager @Inject constructor(
    private val bluetoothAdapter: BluetoothAdapter,
    private val context: Context
): ReceiveManager {
    private val DEVICE_NAME = "ESP32"
    private val SERVICE_UUID = "" //Can use an app to find UUID's
    private val CHARACTERISTICS_UUID = ""

    override val data: MutableSharedFlow<Resource<DataResult>> = MutableSharedFlow()

    private val bleScanner by lazy{
        bluetoothAdapter.bluetoothLeScanner
    }

    private val scanSettings = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
        .build()

    private var gatt: BluetoothGatt? = null

    private var isScanning = false

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    @SuppressLint("MissingPermission")
    private val scanCallback = object : ScanCallback(){
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            //Example had 'if' here to filter out device names
            coroutineScope.launch{
                data.emit(Resource.Loading(message = "Connecting to device..."))
            }
            if(isScanning){
                result.device.connectGatt(context, false, gattCallback)
                isScanning = false
                bleScanner.stopScan(this)
            }
        }
    }

    private var currentConnectionAttempt = 1
    private var MAX_CONNECTION_ATTEMPTS = 5

    private val gattCallback = object : BluetoothGattCallback(){
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if(status == BluetoothGatt.GATT_SUCCESS){
                if(newState == BluetoothGatt.STATE_CONNECTED){
                    coroutineScope.launch{
                        data.emit(Resource.Loading(message = "Discovering Services..."))
                    }
                    gatt.discoverServices()
                    this@BLEReceiveManager.gatt = gatt
                } else if(newState == BluetoothGatt.STATE_DISCONNECTED){
                    coroutineScope.launch {
                        data.emit(Resource.Success(data = DataResult("TODO: WHAT MESSAGE NEEDED", ConnectionState.Disconnected, alerted = null)))
                    }
                    gatt.close()
                }
            } else {
                gatt.close()
                currentConnectionAttempt+=1
                coroutineScope.launch {
                    data.emit(Resource.Loading(message="Attempting to connect $currentConnectionAttempt/$MAX_CONNECTION_ATTEMPTS"))
                }
                if(currentConnectionAttempt <= MAX_CONNECTION_ATTEMPTS){
                    startReceiving()
                } else {
                    coroutineScope.launch {
                        data.emit(Resource.Error(errorMessage = "Could not connect to BLE device"))
                    }
                }
            }
        }
        @SuppressLint("MissingPermission")
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            with(gatt){
                this?.printGattTable()
                coroutineScope.launch {
                    data.emit(Resource.Loading(message = "Adjusting MTU Space..."))
                }
                gatt.requestMtu(517) //Might have to play around with this depening on BLE device
            }
        }

        override fun onMtuChanged(gatt: BluetoothGatt, mtu: Int, status: Int) {
            val characteristic = findCharacteristic(SERVICE_UUID, CHARACTERISTICS_UUID)
            if(characteristic == null){
                coroutineScope.launch {
                    data.emit(Resource.Error(errorMessage = "Could not find UUID publisher"))
                }
                return
            }
            enableNotification(characteristic)
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            with(characteristic){
                when(uuid){
                    UUID.fromString(CHARACTERISTICS_UUID) -> {
                        // Check ESP32 BLE documentation for response format
                        val multiplicator = if(value.first().toInt()>0)-1 else 1
                        val testData = "TEST DATA"

                        coroutineScope.launch {
                            data.emit(
                                Resource.Success(data = testData)
                            )
                        }
                    } else -> Unit
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableNotification(characteristics: BluetoothGattCharacteristic){
        val cccdUuid = UUID.fromString(CCCD_DESCRIPTOR_UUID)
        val payload = when {
            characteristics.isIndicatable() -> BluetoothGattDescriptor.ENABLE_INDICATION_VALUE
            characteristics.isNotifiable() -> BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            else -> return
        }

        characteristics.getDescriptor(cccdUuid)?.let { cccdDescriptor ->
            if(gatt?.setCharacteristicNotification(characteristics, true) == false){
                Log.d("BLEReceiveManager", "set characteristics notification fail")
                return
            }
            writeDescription(cccdDescriptor, payload)
        }
    }

    @SuppressLint("MissingPermission")
    private fun writeDescription(descriptor: BluetoothGattDescriptor, payload: ByteArray){
        gatt?.let { gatt ->
            descriptor.value = payload
            val success = gatt.writeDescriptor(descriptor)
            if (!success) {
                // Handle the case where the write operation was not initiated successfully
                error("Failed to initiate descriptor write operation!")
            }
        } ?: error("Not connected to a BLE device!")
    }

    private fun findCharacteristic(serviceUUID: String, characteristicsUUID: String):BluetoothGattCharacteristic?{
        return gatt?.services?.find { service ->
            service.uuid.toString() == serviceUUID
        }?.characteristics?.find { characteristics ->
            characteristics.uuid.toString() == characteristicsUUID
        }
    }

    @SuppressLint("MissingPermission")
    override fun reconnect() {
        gatt?.connect()
    }

    @SuppressLint("MissingPermission")
    override fun disconnect() {
        gatt?.disconnect()
    }

    @SuppressLint("MissingPermission")
    override fun startReceiving() {
        coroutineScope.launch {
            data.emit(Resource.Loading(message = "Scanning for BLE devices..."))
        }
        isScanning = true
        bleScanner.startScan(null, scanSettings, scanCallback)
    }

    @SuppressLint("MissingPermission")
    override fun stopReceiving() {
        bleScanner.stopScan(scanCallback)
        val characteristic = findCharacteristic(SERVICE_UUID, CHARACTERISTICS_UUID)
        if(characteristic != null){
            disconnectCharacteristic(characteristic)
            gatt?.close()
        }
    }

    @SuppressLint("MissingPermission")
    private fun disconnectCharacteristic(characteristic: BluetoothGattCharacteristic){
        val cccdUUID = UUID.fromString(CCCD_DESCRIPTOR_UUID)
        characteristic.getDescriptor(cccdUUID)?.let { cccdDescriptor ->
            if(gatt?.setCharacteristicNotification(characteristic, false) == false){
                Log.d("BLEReceiveManager", "set characteristic notification failed")
                return
            }
            writeDescription(cccdDescriptor, BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE)
        }
    }


//    val permissionState = rememberMultiplePermissionsState(permissions = PermissionUtils.permissions)

}