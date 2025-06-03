@file:kotlin.OptIn(ExperimentalPermissionsApi::class)

package com.example.smartlabapp.ui.components

import android.Manifest
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun FeatureThatRequiresCameraPermission(
    deniedContent: @Composable (PermissionStatus.Denied) -> Unit,
    grantedContent: @Composable () -> Unit
) {

    // Camera permission state
    val cameraPermissionState = rememberPermissionState(
        Manifest.permission.CAMERA
    )

    val status = cameraPermissionState.status
    AnimatedContent(targetState = status) { permissionStatus ->
        when(permissionStatus) {
            is PermissionStatus.Granted -> grantedContent()
            is PermissionStatus.Denied -> deniedContent(permissionStatus)
        }
    }
}

@Composable
fun FeatureThatRequiresMicrophonePermission(
    deniedContent: @Composable (PermissionStatus.Denied) -> Unit,
    grantedContent: @Composable () -> Unit
) {

    // Camera permission state
    val microphonePermissionState = rememberPermissionState(
        Manifest.permission.RECORD_AUDIO
    )

    val status = microphonePermissionState.status
    AnimatedContent(targetState = status) { permissionStatus ->
        when(permissionStatus) {
            is PermissionStatus.Granted -> grantedContent()
            is PermissionStatus.Denied -> deniedContent(permissionStatus)
        }
    }
}

@Composable
fun FeatureThatRequiresReadPhoneStatePermission(
    deniedContent: @Composable (PermissionStatus.Denied) -> Unit,
    grantedContent: @Composable () -> Unit
) {

    // Phone state permission state
    val microphonePermissionState = rememberPermissionState(
        Manifest.permission.READ_PHONE_STATE
    )

    val status = microphonePermissionState.status
    AnimatedContent(targetState = status) { permissionStatus ->
        when(permissionStatus) {
            is PermissionStatus.Granted -> grantedContent()
            is PermissionStatus.Denied -> deniedContent(permissionStatus)
        }
    }
}

@Composable
fun NeedPermissionScreen(
    requestPermission: () -> Unit,
    shouldShowRationale: Boolean,
) {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val textToShow = if (shouldShowRationale) {
            // If the user has denied the permission but the rationale can be shown,
            // then gently explain why the app requires this permission
            "Please grant the permission."
        } else {
            // If it's the first time the user lands on this feature, or the user
            // doesn't want to be asked again for this permission, explain that the
            // permission is required
            "Additional permission required for this feature to be available. " +
                    "Please grant the permission"
        }
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = textToShow,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = requestPermission) {
            Text("Request permission")
        }
    }
}