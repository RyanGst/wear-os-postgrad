package click.ryangst.sensors.presentation.test

import click.ryangst.sensors.presentation.HeartRateViewModel
import org.junit.Assert.*
import org.junit.Test

class HeartRateViewModelTest {

    @Test
    fun updateHeartRate() {
        val viewModel = HeartRateViewModel()

        viewModel.updateHeartRate(80f)
        assertEquals(80, viewModel.heartRate.value)

        Thread.sleep(3000)
        viewModel.updateHeartRate(85f)
        assertEquals(80, viewModel.heartRate.value)

    }
}