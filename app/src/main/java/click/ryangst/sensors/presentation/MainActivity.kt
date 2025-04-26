/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package click.ryangst.sensors.presentation

import HeartRateScreen
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.wear.tooling.preview.devices.WearDevices
import click.ryangst.sensors.presentation.theme.SensorsTheme

class MainActivity : ComponentActivity() {

    private lateinit var sensorManager: SensorManager;

    private var heartRateSensor: Sensor? = null;

    private var sensorListener: SensorEventListener? = null

    private val heartRateViewModel = HeartRateViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.BODY_SENSORS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.BODY_SENSORS), 1);
        }

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)



        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {

            val heartRateState by remember { heartRateViewModel.heartRate }

            LaunchedEffect(Unit) {

                val listener = object : SensorEventListener {
                    override fun onSensorChanged(event: android.hardware.SensorEvent) {
                        event.values.firstOrNull()?.let { heartRateViewModel.updateHeartRate(it)  }
                    }

                    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
                }


                sensorListener = listener

                heartRateSensor?.let {
                    sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_NORMAL)
                }
            }

            DisposableEffect(Unit) {
                onDispose {
                    sensorListener?.let {
                        sensorManager.unregisterListener(it)
                    }
                }
            }

            SensorsTheme {
                HeartRateScreen(heartRateViewModel.heartRate.value)
            }

        }
    }
}


@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    HeartRateScreen(222)
}