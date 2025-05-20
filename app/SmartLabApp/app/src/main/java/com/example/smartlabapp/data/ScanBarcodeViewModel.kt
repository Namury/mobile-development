package com.example.smartlabapp.data

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.barcode.common.Barcode
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

sealed interface BarScanState {
    data object Ideal : BarScanState
    data class ScanSuccess(val barStateModel: BarModel) : BarScanState
    data class Error(val error: String) : BarScanState
    data object Loading : BarScanState
}

@Serializable
data class BarModel(
    val invoiceNumber: String,
    val client: Client,
    val purchase: List<PurchaseItem>,
    val totalAmount: Double
)

@Serializable
data class Client(
    val name: String,
    val email: String,
    val address: String
)

@Serializable
data class PurchaseItem(
    val item: String,
    val quantity: Int,
    val price: Double
)

class ScanBarcodeViewModel: ViewModel() {
    lateinit var barScanState:BarScanState
    fun onBarCodeDetected(barcodes: List<Barcode>) {
        viewModelScope.launch {
            if (barcodes.isEmpty()) {
                barScanState = BarScanState.Error("No barcode detected")
                return@launch
            }

            barScanState = BarScanState.Loading

            barcodes.forEach { barcode ->
                barcode.rawValue?.let { barcodeValue ->
                    try {
                        val barModel: BarModel = Json.decodeFromString(barcodeValue)
                        barScanState = BarScanState.ScanSuccess(barStateModel = barModel)
                    } catch (e: Exception) {
                        Log.i("onBarCodeDetected", "onBarCodeDetected: $e", )
                        barScanState = BarScanState.Error("Invalid JSON format in barcode")
                    }
                    return@launch
                }
            }
            barScanState = BarScanState.Error("No valid barcode value")
        }
    }

    fun resetState() {
        barScanState = BarScanState.Ideal
    }
}

