package com.example.smartlabapp.data

data class DeviceUIState(
    val deviceUUID: String = "",
    val deviceID: String = "",
)

data class SensorUIState(
    val deviceUUID: String = "",
    val deviceID: String = "",
    val isMoving: Boolean = false,
    val direction: String = "",
    val ambientLight: Int = 0,
    val ambientNoise: Int = 0,
)
