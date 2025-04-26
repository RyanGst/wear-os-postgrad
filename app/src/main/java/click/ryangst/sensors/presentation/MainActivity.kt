import android.Manifest
import android.app.AlertDialog
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.os.VibratorManager
import android.transition.TransitionManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.wear.tooling.preview.devices.WearDevices
import click.ryangst.sensors.R
import click.ryangst.sensors.presentation.Config
import click.ryangst.sensors.presentation.theme.SensorsTheme

class MainActivity : ComponentActivity() {

    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private lateinit var motionSensorListener: MotionSensorListener
    private var vibrator: Vibrator? = null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    android.Manifest.permission.POST_NOTIFICATIONS,
                    android.Manifest.permission.VIBRATE
                ), 1
            )
            return
        }

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!!

        motionSensorListener = MotionSensorListener {
            sendImmobilityNotification()
        }

        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        sensorManager.registerListener(
            motionSensorListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL
        )

    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(motionSensorListener)
    }

    private fun sendImmobilityNotification() {
        TransitionManager.beginDelayedTransition(findViewById(android.R.id.content))
        runOnUiThread {
            AlertDialog.Builder(this).setTitle(getString(R.string.timeout_title))
                .setMessage("Você está parado há ${Config.INNACTIVE_TIMEOUT / 1000} segundos.")
                .setPositiveButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                }.show()
        }
    }

}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    RestingStageScreen(222)
}