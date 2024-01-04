import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattCallback
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.getSystemService
import com.example.taga.R
import java.security.AccessController.getContext


// Inside your BluetoothGattCallback implementation
@SuppressLint("MissingPermission")
fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, context: Context) {
    gatt?.readCharacteristic(characteristic)
    val message = characteristic.getStringValue(0)
    if (isTargetMessage(message)) {
        createNotification(context)
    }
}

private fun isTargetMessage(message: String): Boolean {
    // Add your logic to check the message
    return message == "specific message"
}

private fun createNotification(context: Context) {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel("channel_id", "Channel Name", NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(channel)
    }

    val builder = NotificationCompat.Builder(context, "channel_id")
        .setContentTitle("BLE Notification")
        .setContentText("Your BLE device sent a message")
        .setSmallIcon(android.R.drawable.ic_dialog_info) // Replace with your icon

    notificationManager.notify(1, builder.build())
}
