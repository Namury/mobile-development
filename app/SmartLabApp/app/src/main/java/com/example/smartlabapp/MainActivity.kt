package com.example.smartlabapp

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.SensorManager
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.platform.LocalContext
import com.example.smartlabapp.ui.theme.SmartLabAppTheme
import java.util.UUID
import androidx.core.content.edit
import com.example.smartlabapp.ui.DeviceViewModel

class MainActivity : ComponentActivity() {
    @SuppressLint("ViewModelConstructorInComposable", "HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val sharedPreferences = getSharedPreferences("deviceUUID", MODE_PRIVATE)
        var savedUUID = sharedPreferences.getString("device_uuid", "")
        if(savedUUID == ""){
            sharedPreferences.edit() {
                putString("device_uuid", UUID.randomUUID().toString())
                apply()
            }
        }

        setContent {
            val sharedPreferences = getSharedPreferences("deviceUUID", MODE_PRIVATE)
            var deviceUUID = sharedPreferences.getString("device_uuid", "")
            val deviceID = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

            val deviceViewModel = DeviceViewModel()
            deviceViewModel.setDeviceId(deviceUUID.toString(), deviceID)
            SmartLabAppTheme {
                SmartLabApp(deviceViewModel)
            }
        }
    }

}