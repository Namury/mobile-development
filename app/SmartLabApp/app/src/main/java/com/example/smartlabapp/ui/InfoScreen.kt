package com.example.smartlabapp.ui

import android.Manifest
import android.content.Context
import android.hardware.SensorManager
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.smartlabapp.R
import com.example.smartlabapp.data.SensorReading
import com.example.smartlabapp.ui.components.FeatureThatRequiresMicrophonePermission
import com.example.smartlabapp.ui.components.NeedPermissionScreen
import com.example.smartlabapp.ui.theme.SmartLabAppTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState


@OptIn(ExperimentalPermissionsApi::class)
@Composable
@RequiresPermission(Manifest.permission.RECORD_AUDIO)
fun InfoScreen(
    modifier: Modifier = Modifier
) {
    val ctx = LocalContext.current
    var sensorManager: SensorManager = ctx.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    val microphonePermissionState = rememberPermissionState(Manifest.permission.RECORD_AUDIO)
    FeatureThatRequiresMicrophonePermission(
        deniedContent = { status ->
            NeedPermissionScreen(
                requestPermission = microphonePermissionState::launchPermissionRequest,
                shouldShowRationale = status.shouldShowRationale
            )
        },
        grantedContent = {
            Column(
                modifier = modifier,
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
                        text = stringResource(R.string.info),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_small)))
                }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(
                        dimensionResource(id = R.dimen.padding_medium)
                    )
                ) {
                    SensorReading(sensorManager)
                }
            }
        }
    )

}

@Preview
@Composable
@RequiresPermission(Manifest.permission.RECORD_AUDIO)
fun InfoScreenPreview() {
    SmartLabAppTheme {
        InfoScreen(
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensionResource(R.dimen.padding_medium))
        )
    }
}
