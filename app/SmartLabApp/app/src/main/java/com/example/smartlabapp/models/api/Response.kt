package com.example.smartlabapp.models.api

data class StoreSensorsResponse(
    val status: String,
    val message: String
)

data class CheckinResponse(
    val status: String,
    val message: String
)

data class SummaryResponse(
    val status: String,
    val message: String,
    val data: Summary
)

data class Summary(
    val checkin_status: Boolean,
    val total_checkins: Int,
    val total_moving: Int,
    val average_ambient_light: Float,
    val average_ambient_noise: Float,
    val devices: Array<Device>,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Summary

        if (checkin_status != other.checkin_status) return false
        if (total_checkins != other.total_checkins) return false
        if (total_moving != other.total_moving) return false
        if (average_ambient_light != other.average_ambient_light) return false
        if (average_ambient_noise != other.average_ambient_noise) return false
        if (!devices.contentEquals(other.devices)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = checkin_status.hashCode()
        result = 31 * result + total_checkins
        result = 31 * result + total_moving
        result = 31 * result + average_ambient_light.hashCode()
        result = 31 * result + average_ambient_noise.hashCode()
        result = 31 * result + devices.contentHashCode()
        return result
    }
}

data class Device(
    val device_name: String,
    val is_occupied: Boolean
)
