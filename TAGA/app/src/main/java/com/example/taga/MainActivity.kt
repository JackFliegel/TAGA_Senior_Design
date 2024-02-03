package com.example.taga

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.taga.databinding.ActivityMainBinding
import com.example.taga.presentation.Navigation
import com.example.taga.ui.theme.BLETutorialTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

//@AndroidEntryPoint
//class MainActivity : ComponentActivity() {
//    @Inject
//    lateinit var bluetoothAdapter: BluetoothAdapter
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        val navView: BottomNavigationView = binding.navView
//
//        val navController = findNavController(R.id.nav_host_fragment_activity_main)
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
//        val appBarConfiguration = AppBarConfiguration(setOf(
//            R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications))
//        setupActionBarWithNavController(navController, appBarConfiguration)
//        navView.setupWithNavController(navController)
//
//        super.onCreate(savedInstanceState)
//        setContent {
//            BLETutorialTheme {
//                androidx.navigation.Navigation(
//                    onBluetoothStateChanged = {
//                        showBluetoothDialog()
//                    }
//                )
//            }
//        }
//    }
//
//    override fun onStart() {
//        super.onStart()
//        showBluetoothDialog()
//    }
//
//    private var isBluetootDialogAlreadyShown = false
//    private fun showBluetoothDialog(){
//        if(!bluetoothAdapter.isEnabled){
//            if(!isBluetootDialogAlreadyShown){
//                val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
//                startBluetoothIntentForResult.launch(enableBluetoothIntent)
//                isBluetootDialogAlreadyShown = true
//            }
//        }
//    }
//
//    private val startBluetoothIntentForResult =
//        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
//            isBluetootDialogAlreadyShown = false
//            if(result.resultCode != Activity.RESULT_OK){
//                showBluetoothDialog()
//            }
//        }
//    private lateinit var binding: ActivityMainBinding
//}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var bluetoothAdapter: BluetoothAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BLETutorialTheme {
                Navigation(
                    onBluetoothStateChanged = {
                        showBluetoothDialog()
                    }
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        showBluetoothDialog()
    }

    private var isBluetootDialogAlreadyShown = false
    private fun showBluetoothDialog(){
        if(!::bluetoothAdapter.isInitialized || !bluetoothAdapter.isEnabled){
            if(!isBluetootDialogAlreadyShown){
                val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startBluetoothIntentForResult.launch(enableBluetoothIntent)
                isBluetootDialogAlreadyShown = true
            }
        }
    }

    private val startBluetoothIntentForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            isBluetootDialogAlreadyShown = false
            if(result.resultCode != Activity.RESULT_OK){
                showBluetoothDialog()
            }
        }

}