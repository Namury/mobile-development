package com.example.smartlabapp.ui

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.smartlabapp.R
import com.example.smartlabapp.models.api.Device
import com.example.smartlabapp.models.api.GetSummaryRequest
import com.example.smartlabapp.models.api.Summary
import com.example.smartlabapp.models.api.SummaryResponse
import com.example.smartlabapp.models.constants.DebuggingIdentifiers
import com.example.smartlabapp.tools.DataCoordinator
import com.example.smartlabapp.tools.DataCoordinator.Companion.identifier
import com.example.smartlabapp.tools.getSummaryAPI
import com.example.smartlabapp.ui.theme.SmartLabAppTheme
import android.Manifest
import android.content.Context
import android.content.Context.TELEPHONY_SERVICE
import android.content.pm.PackageManager
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartlabapp.ui.DeviceViewModel

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("MutableCollectionMutableState", "HardwareIds")
@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    viewModel: DeviceViewModel
) {
    val resources = LocalContext.current.resources
    var checkinStatus by remember {mutableStateOf("")}
    var totalCheckin by remember {mutableStateOf("")}
    var totalMoving by remember {mutableStateOf("")}
    var averageAmbientLight by remember {mutableStateOf("")}
    var averageAmbientNoise by remember {mutableStateOf("")}
    var devices by remember { mutableStateOf(ArrayList<Device>()) }

    val uiState by viewModel.uiState.collectAsState()

    DataCoordinator.shared.getSummaryAPI(
        onSuccess = {responseApi ->
            checkinStatus = responseApi.data.checkin_status.toString()
            totalCheckin = responseApi.data.total_checkins.toString()
            totalMoving = responseApi.data.total_moving.toString()
            averageAmbientLight = responseApi.data.average_ambient_light.toString()
            averageAmbientNoise = responseApi.data.average_ambient_noise.toString()
            responseApi.data.devices.forEach { device ->
                devices.add(device)
            }
                    },
        onError = {},
        request = GetSummaryRequest(
            deviceImei = uiState.deviceUUID,
            deviceID = uiState.deviceID,
            interval = "30"
        )
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
        ) {
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_medium)))
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_medium)))
            Text(
                text = stringResource(R.string.dashboard),
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_small)))

            Text(
                text = "Checkin Status : $checkinStatus",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "Total Checkin : $totalCheckin",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "Total Moving : $totalMoving",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "Average Ambient Light: $averageAmbientLight",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "Average Ambient Noise: $averageAmbientNoise",
                style = MaterialTheme.typography.headlineSmall
            )
            devices.forEach { device ->
                Text(
                    text = "Device Name: ${device.device_name}",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "Is Occupied: ${device.is_occupied}",
                    style = MaterialTheme.typography.headlineSmall
                )
            }

        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun DashboardScreenPreview() {
    SmartLabAppTheme {
        DashboardScreen(
            modifier = Modifier.fillMaxHeight(),
            viewModel = viewModel()
        )
    }
}
