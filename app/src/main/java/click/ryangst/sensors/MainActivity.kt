package click.ryangst.sensors

import MotionSensorListener
import RestingStageScreen

import android.app.AlertDialog
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.transition.TransitionManager
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import click.ryangst.sensors.presentation.StretchingScreen

class MainActivity : ComponentActivity() {

    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private lateinit var motionSensorListener: MotionSensorListener
    private var vibrator: Vibrator? = null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            var currentScreen by remember { mutableStateOf("resting") }

            val onStartStretching = {
                currentScreen = "stretching"
            }

            sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!!

            motionSensorListener = MotionSensorListener {
                sendImmobilityNotification(onStartStretching)
            }

            vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager =
                    getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }

            sensorManager.registerListener(
                motionSensorListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL
            )

            MaterialTheme {
                Log.d("WARN", "TELA ATUALLLL");
                Log.d("WARN", currentScreen);
                when (currentScreen) {
                    "resting" -> RestingStageScreen()
                    "stretching" -> StretchingScreen()
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(motionSensorListener)
    }

    private fun sendImmobilityNotification(onStartStretching: () -> Unit) {
        TransitionManager.beginDelayedTransition(findViewById(android.R.id.content))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Para Android 8.0+ (API 26+)
            vibrator?.vibrate(
                VibrationEffect.createOneShot(
                    500, // duração em milissegundos
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } else {
            //Android < 30
            vibrator?.vibrate(500)
        }


        vibrator
        runOnUiThread {
            AlertDialog.Builder(this).setTitle(getString(R.string.timeout_title))
                .setMessage("Você está parado há ${Config.INNACTIVE_TIMEOUT / 1000} segundos.")
                .setPositiveButton("Iniciar Alongamento") { dialog, _ ->
                    dialog.dismiss()
                    onStartStretching()
                }
                .setNegativeButton("Pular") { dialog, _ ->
                    dialog.dismiss()
                }.show()
        }
    }
}