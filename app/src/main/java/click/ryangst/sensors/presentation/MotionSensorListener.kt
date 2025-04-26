import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import click.ryangst.sensors.Config
import kotlin.math.abs
import kotlin.math.sqrt

class MotionSensorListener(
    private val onImmobilityDetected: () -> Unit
) : SensorEventListener {

    private var lastMovementTime: Long = System.currentTimeMillis()
    private var isImmobileNotified = false
    private var lastLoggedSecond = 0

    fun resetTimer() {
        lastMovementTime = System.currentTimeMillis()
        lastLoggedSecond = 0
        isImmobileNotified = false
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val x = it.values[0]
            val y = it.values[1]
            val z = it.values[2]

            val acceleration = sqrt((x * x + y * y + z * z).toDouble())
            val now = System.currentTimeMillis()

            val movementThreshold = 0.5 // define quanto é considerado movimento

            if (abs(acceleration - SensorManager.GRAVITY_EARTH) > movementThreshold) {
                // Movimento detectado: reseta a contagem
                Log.d("MovementSensor", "Movimentação detectada! Resetando contador.")
                Log.d("MovementSensor", "Movimento detectado - x: $x, y: $y, z: $z")

                lastMovementTime = now
                lastLoggedSecond = 0
                if (isImmobileNotified) {
                    isImmobileNotified = false
                }
            } else {
                // Calculate elapsed time in milliseconds
                val elapsedMillis = now - lastMovementTime
                val elapsedSeconds = (elapsedMillis / 1000).toInt()

                // Log every second if needed (optional)
                if (elapsedSeconds > 0 && elapsedSeconds != lastLoggedSecond) {
                    Log.d("MovementSensor", "Parado há $elapsedSeconds segundos")
                    lastLoggedSecond = elapsedSeconds
                }

                // Check if timeout reached
                if (elapsedMillis >= Config.INNACTIVE_TIMEOUT && !isImmobileNotified) {
                    Log.d("MovementSensor", "Imobilidade detectada após ${Config.INNACTIVE_TIMEOUT / 1000} segundos.")
                    isImmobileNotified = true
                    lastMovementTime = now
                    lastLoggedSecond = 0
                    onImmobilityDetected()
                }
            }
        }
    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }
}