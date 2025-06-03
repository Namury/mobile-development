package com.example.smartlabapp.ui

import androidx.lifecycle.ViewModel
import com.example.smartlabapp.data.DeviceUIState
import com.example.smartlabapp.data.SensorUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class SensorViewModel : ViewModel() {
    private val _uiState =
        MutableStateFlow(SensorUIState(
            deviceID = "",
            deviceUUID = "",
            isMoving = false,
            direction = "",
            ambientLight = 0,
            ambientNoise = 0,
        ))
    val uiState: StateFlow<SensorUIState> = _uiState.asStateFlow()

    fun setSensorsState(
        deviceUUID:String,
        deviceID: String,
        isMoving: Boolean,
        direction: String,
        ambientLight: Int,
        ambientNoise: Int,
    ) {
        _uiState.update { currentState ->
            currentState.copy(
                deviceUUID = deviceUUID,
                deviceID = deviceID,
                isMoving = isMoving,
                direction = direction,
                ambientLight = ambientLight,
                ambientNoise = ambientNoise,
            )
        }
    }

    fun setDirectionState(
        isMoving: Boolean,
        direction: String,
    ) {
        _uiState.update { currentState ->
            currentState.copy(
                isMoving = isMoving,
                direction = direction,
            )
        }
    }

    fun setAmbientLightState(
        ambientLight: Int,
    ) {
        _uiState.update { currentState ->
            currentState.copy(
                ambientLight = ambientLight,
            )
        }
    }

    fun setAmbientNoiseState(
        ambientNoise: Int,
    ) {
        _uiState.update { currentState ->
            currentState.copy(
                ambientNoise = ambientNoise,
            )
        }
    }
}