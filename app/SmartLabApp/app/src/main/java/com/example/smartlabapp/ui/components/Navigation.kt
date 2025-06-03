package com.example.smartlabapp.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.smartlabapp.ui.DashboardScreen
import com.example.smartlabapp.ui.DeviceViewModel
import com.example.smartlabapp.ui.InfoScreen
import com.example.smartlabapp.ui.ScanBarcodeScreen
import com.example.smartlabapp.ui.SensorViewModel

enum class Destination(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val contentDescription: String
) {
    DASHBOARD("dashboard", "Dashboard", Icons.Filled.Home, "Dashboard"),
    SCANBARCODE("scan", "Scan", Icons.Default.AddCircle, "Scan"),
    INFO("info", "Info", Icons.Default.Info, "Info")
}


@Preview
@Composable
fun SmartLabNavigationBar(
    modifier: Modifier = Modifier,
    deviceViewModel: DeviceViewModel = viewModel(),
    sensorViewModel: SensorViewModel = viewModel()
) {
    val navController = rememberNavController()
    val startDestination = Destination.DASHBOARD
    var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                Destination.entries.forEachIndexed { index, destination ->
                    NavigationBarItem(
                        selected = selectedDestination == index,
                        onClick = {
                            navController.navigate(route = destination.route)
                            selectedDestination = index
                        },
                        icon = {
                            Icon(
                                destination.icon,
                                contentDescription = destination.contentDescription
                            )
                        },
                        label = { Text(destination.label) }
                    )
                }
            }
        }
    ) { contentPadding ->
        AppNavHost(navController, startDestination, modifier = Modifier.padding(contentPadding), deviceViewModel, sensorViewModel)
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: Destination,
    modifier: Modifier = Modifier,
    deviceViewModel: DeviceViewModel,
    sensorViewModel: SensorViewModel,
) {
    NavHost(
        navController,
        startDestination = startDestination.route
    ) {
        Destination.entries.forEach { destination ->
            composable(destination.route) {
                when (destination) {
                    Destination.DASHBOARD -> DashboardScreen(modifier, deviceViewModel)
                    Destination.SCANBARCODE -> ScanBarcodeScreen(modifier, deviceViewModel)
                    Destination.INFO -> InfoScreen(modifier, deviceViewModel, sensorViewModel)
                }
            }
        }
    }
}
