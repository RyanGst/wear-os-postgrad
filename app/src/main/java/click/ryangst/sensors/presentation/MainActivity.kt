import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat

class MainActivity : ComponentActivity() {

    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private lateinit var motionSensorListener: MotionSensorListener

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                1
            )
            return
        }

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!!

        motionSensorListener = MotionSensorListener {
            sendImmobilityNotification()
        }

        sensorManager.registerListener(
            motionSensorListener,
            accelerometer,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(motionSensorListener)
    }

    private fun sendImmobilityNotification() {
        // dispara uma notificação
//        val builder = NotificationCompat.Builder(this, "motion_channel")
//            .setSmallIcon(R.drawable.ic_launcher_foreground)
//            .setContentTitle("Sem movimento detectado")
//            .setContentText("Você está parado há mais de 10 segundos.")
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//
//        val notificationManager = NotificationManagerCompat.from(this)
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.POST_NOTIFICATIONS
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            Log.d("WARN", "Não permitido notificar")
//            return
//        }
//        notificationManager.notify(1001, builder.build())
        runOnUiThread {
            AlertDialog.Builder(this)
                .setTitle("Imobilidade Detectada")
                .setMessage("Você está parado há 10 segundos.")
                .setPositiveButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }
}