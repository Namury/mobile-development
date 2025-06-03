package com.example.smartlabapp.data

import android.Manifest
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartlabapp.models.api.CheckinRequest
import com.example.smartlabapp.models.api.StoreSensorsRequest
import com.example.smartlabapp.tools.DataCoordinator
import com.example.smartlabapp.tools.checkinAPI
import com.example.smartlabapp.tools.storeSensorsAPI
import com.example.smartlabapp.ui.DeviceViewModel
import com.example.smartlabapp.ui.SensorViewModel
import com.example.smartlabapp.ui.theme.SmartLabAppTheme
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.log10
import kotlin.math.sqrt


@RequiresPermission(Manifest.permission.RECORD_AUDIO)
@Composable
fun SensorReading(
    sensorManager: SensorManager,
    deviceViewModel: DeviceViewModel,
    sensorViewModel: SensorViewModel,
    ){
    GetMovementAndRotation(sensorManager, sensorViewModel)
    GetAmbientLight(sensorManager, sensorViewModel)
    GetAmbientNoise(sensorViewModel)

    var storeSensorsStatus by remember {mutableStateOf(false)}
    val ctx = LocalContext.current
    val deviceUiState by deviceViewModel.uiState.collectAsState()
    val sensorUiState by sensorViewModel.uiState.collectAsState()

    if(!storeSensorsStatus) {
        DataCoordinator.shared.storeSensorsAPI(
            onSuccess = {
                Toast.makeText(ctx, "Success Sync Sensor Data", Toast.LENGTH_SHORT).show()
                storeSensorsStatus = true
            },
            onError = {Toast.makeText(ctx, "Error Sync Sensor Data", Toast.LENGTH_SHORT).show()},
            request = StoreSensorsRequest(
                deviceImei = deviceUiState.deviceUUID,
                deviceID = deviceUiState.deviceID,
                isMoving = sensorUiState.isMoving,
                direction = sensorUiState.direction,
                ambientLight = sensorUiState.ambientLight,
                ambientNoise = sensorUiState.ambientNoise,
                timestamp = ""
            )
        )
        storeSensorsStatus=false
    }
}

// Accelerometer
@Composable
fun GetMovementAndRotation(sensorManager: SensorManager, sensorViewModel: SensorViewModel) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()

    val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    val magnetometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    var accelerometerStatus by remember {mutableStateOf("")}
    var magnetometerStatus by remember {mutableStateOf("")}
    var rotationDegree by remember { mutableIntStateOf(0) }
    var accelDelta by remember { mutableFloatStateOf(0f) }
    var accelCurrent = SensorManager.GRAVITY_EARTH
    var accelLast = SensorManager.GRAVITY_EARTH
    val accelerometerReading = FloatArray(3)
    val magnetometerReading = FloatArray(3)

    var rotationMatrix = FloatArray(9)
    val orientationAngles = FloatArray(3)

    val accelerometerListener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

        }

        override fun onSensorChanged(event: SensorEvent?) {
            if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER){
                System.arraycopy(
                    event.values,
                    0,
                    accelerometerReading,
                    0,
                    accelerometerReading.size
                )
                // Shake detection
                val x = accelerometerReading[0]
                val y = accelerometerReading[1]
                val z = accelerometerReading[2]

                accelLast = accelCurrent
                accelCurrent = sqrt(x * x + y * y + z * z)
                accelDelta = accelCurrent - accelLast

                updateMovementAndRotationStatus()
            }
            if (event?.sensor?.type == Sensor.TYPE_MAGNETIC_FIELD){
                System.arraycopy(
                    event.values,
                    0,
                    magnetometerReading,
                    0,
                    magnetometerReading.size
                )

                updateMovementAndRotationStatus()
            }
        }

        private fun updateMovementAndRotationStatus() {
            SensorManager.getRotationMatrix(
                rotationMatrix,
                null,
                accelerometerReading,
                magnetometerReading
            )

            SensorManager.getOrientation(rotationMatrix, orientationAngles)

            rotationDegree = (orientationAngles[0] * 180 / PI).toRotationInDegrees()

            accelerometerStatus = if (accelDelta > 0.5) {
                "Moving"
            } else {
                "Stationary"
            }

            magnetometerStatus = if (rotationDegree < 22 || rotationDegree >= 337){
                "North"
            } else if (rotationDegree <67){
                "North East"
            } else if (rotationDegree <112){
                "East"
            } else if (rotationDegree <157 ){
                "South East"
            } else if (rotationDegree <202 ){
                "South"
            } else if (rotationDegree <247 ){
                "South West"
            } else if (rotationDegree <292 ){
                "West"
            } else{
                "North West"
            }

            sensorViewModel.setDirectionState(
                isMoving = accelDelta > 0.5,
                direction = magnetometerStatus
            )
        }
    }

    when (lifecycleState) {
        Lifecycle.State.RESUMED -> {
        }
        Lifecycle.State.DESTROYED -> {
            sensorManager.unregisterListener(accelerometerListener, accelerometer)
            sensorManager.unregisterListener(accelerometerListener, magnetometer)
        }
        Lifecycle.State.INITIALIZED -> {
        }
        Lifecycle.State.CREATED -> {
        }
        Lifecycle.State.STARTED -> {
            sensorManager.registerListener(accelerometerListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
            sensorManager.registerListener(accelerometerListener, magnetometer, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    Column {
        Text(
            text = "Movement Status"
        )
        Text(
            text = accelerometerStatus
        )
    }

    Column {
        Text(
            text = "Rotation Status"
        )
        Text(
            text = magnetometerStatus
        )
    }

    Column {
        Text(
            text = "Rotation Degree"
        )
        Text(
            text = rotationDegree.toString()
        )
    }
}

private fun Double.toRotationInDegrees(): Int {
    return (this.toInt() + 360) % 360
}

// Light Sensor
@Composable
fun GetAmbientLight(sensorManager: SensorManager, sensorViewModel: SensorViewModel){
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()

    var lightsensorStatus by remember {mutableFloatStateOf(0f)}
    val lightsensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
    val lightsensorListener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        }

        override fun onSensorChanged(event: SensorEvent?) {
            lightsensorStatus = event?.values!![0]
            sensorViewModel.setAmbientLightState(
                ambientLight = lightsensorStatus.toInt()
            )
        }
    }

    var isRegistered by remember {mutableStateOf(false)}
    when (lifecycleState) {
        Lifecycle.State.RESUMED -> {
            if(!isRegistered){
                sensorManager.registerListener(lightsensorListener, lightsensor, SensorManager.SENSOR_DELAY_NORMAL)
                isRegistered = true
            }
        }
        Lifecycle.State.DESTROYED -> {
            isRegistered = false
            sensorManager.unregisterListener(lightsensorListener, lightsensor)
        }
        Lifecycle.State.INITIALIZED -> {}
        Lifecycle.State.CREATED -> {}
        Lifecycle.State.STARTED -> {}
    }
    Column {
        Text(
            text = "Ambient Light"
        )
        Text(
            text = "%.2f".format(lightsensorStatus) + " lx"
        )
    }
}

// Microphone
@Composable
@RequiresPermission(Manifest.permission.RECORD_AUDIO)
fun GetAmbientNoise(sensorViewModel: SensorViewModel){
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()

    val AUDIO_SOURCE = MediaRecorder.AudioSource.MIC
    val SAMPLE_RATE = 44100   // using 44100 Hz since it should be supported on all devices
    val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
    val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    val BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)

    val audioRecord = AudioRecord(AUDIO_SOURCE, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, BUFFER_SIZE)
    val audioBuffer = ShortArray(BUFFER_SIZE)

    var isRecording = false
    when (lifecycleState) {
        Lifecycle.State.RESUMED -> {
            audioRecord.startRecording()
            isRecording = true
        }
        Lifecycle.State.DESTROYED -> {
            audioRecord.stop()
            isRecording = false
        }
        Lifecycle.State.INITIALIZED -> {}
        Lifecycle.State.CREATED -> {}
        Lifecycle.State.STARTED -> {}
    }

    audioRecord.read(audioBuffer, 0, BUFFER_SIZE)
    var sum = 0.0
    for (s in audioBuffer) {
        sum += s * s
    }
    val amplitude = sum / audioBuffer.size
    val decibel = 20 * log10(amplitude)

    sensorViewModel.setAmbientNoiseState(
        ambientNoise = decibel.toInt()
    )

    Column {
        Text(
            text = "Ambient Noise"
        )
        Text(
            text = "%.2f".format(decibel) + " db"
        )
    }
}


@Preview
@Composable
@RequiresPermission(Manifest.permission.RECORD_AUDIO)
fun SensorReadingPreview() {
    val ctx = LocalContext.current
    var sensorManager: SensorManager =
        ctx.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    SmartLabAppTheme {
        SensorReading(sensorManager, viewModel(), viewModel())
    }
}



