package com.example.smartlabapp

import android.content.Context
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.smartlabapp.tools.DataCoordinator
import com.example.smartlabapp.ui.DeviceViewModel
import com.example.smartlabapp.ui.SensorViewModel
import com.example.smartlabapp.ui.components.SmartLabNavigationBar


@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun SmartLabApp(
    deviceViewModel: DeviceViewModel = viewModel(),
    sensorViewModel: SensorViewModel = viewModel(),

    navController: NavHostController = rememberNavController()
) {
    setupCoordinators(LocalContext.current)
    SmartLabNavigationBar(Modifier, deviceViewModel, sensorViewModel)
}

fun setupCoordinators(baseContext: Context) {
    DataCoordinator.shared.initialize(
        context = baseContext,
        onLoad = {
            // Do Something
        },
    )
}

