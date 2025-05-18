package com.example.smartlabapp

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.smartlabapp.ui.SensorViewModel
import com.example.smartlabapp.ui.components.SmartLabNavigationBar

/**
 * enum values that represent the screens in the app
 */
enum class SmartLabScreen(@StringRes val title: Int) {
    Dashboard(title = R.string.dashboard),
    Info(title = R.string.info),
    ScanBarcode(title = R.string.scan_barcode)
}

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun SmartLabApp(
    viewModel: SensorViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    // Get current back stack entry
    val backStackEntry by navController.currentBackStackEntryAsState()

    SmartLabNavigationBar()
}

