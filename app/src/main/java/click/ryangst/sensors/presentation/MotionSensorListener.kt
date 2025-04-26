import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import kotlin.math.abs
import kotlin.math.sqrt

class MotionSensorListener(
    private val onImmobilityDetected: () -> Unit
) : SensorEventListener {

    private var lastMovementTime: Long = System.currentTimeMillis()
    private var isImmobileNotified = false
    private var lastLoggedSecond = 0


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
                val secondsWithoutMovement = ((now - lastMovementTime) / 1000).toInt()

                if (secondsWithoutMovement > 0 && secondsWithoutMovement != lastLoggedSecond) {
                    Log.d("MovementSensor", "Parado há $secondsWithoutMovement segundos")
                    lastLoggedSecond = secondsWithoutMovement
                }

                if (secondsWithoutMovement >= 10 && !isImmobileNotified) {
                    isImmobileNotified = true
                    onImmobilityDetected()
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }
}