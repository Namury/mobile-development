package com.example.smartlabapp.models.api


data class StoreSensorsRequest(
    val deviceImei:   String,
    val deviceID:     String,
    val isMoving:     Boolean,
    val direction:    String,
    val ambientLight: Int,
    val ambientNoise: Int,
    val timestamp:    String,
)

data class GetSummaryRequest(
    val deviceImei:   String,
    val deviceID:     String,
    val interval:     String,
)

data class CheckinRequest(
    val deviceImei:   String,
    val deviceID:     String,
    val checkinID:     String,
)