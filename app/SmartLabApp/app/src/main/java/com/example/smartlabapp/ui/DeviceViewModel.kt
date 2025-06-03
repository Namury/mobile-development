package com.example.smartlabapp.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class DeviceViewModel : ViewModel() {
    private val _uiState =
        MutableStateFlow(com.example.smartlabapp.data.DeviceUIState(deviceID = "", deviceUUID = ""))
    val uiState: StateFlow<com.example.smartlabapp.data.DeviceUIState> = _uiState.asStateFlow()

    fun setDeviceId(deviceUUID:String, deviceID: String) {
        _uiState.update { currentState ->
            currentState.copy(
                deviceUUID = deviceUUID,
                deviceID = deviceID
            )
        }
    }
}