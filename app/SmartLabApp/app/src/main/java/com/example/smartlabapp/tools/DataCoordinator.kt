package com.example.smartlabapp.tools

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.datastore.preferences.preferencesDataStore
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.VolleyLog
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.Volley
import com.example.smartlabapp.models.api.CheckinRequest
import com.example.smartlabapp.tools.DataCoordinator.Companion.identifier
import com.example.smartlabapp.models.api.SampleError
import com.example.smartlabapp.models.api.SummaryResponse
import com.example.smartlabapp.models.api.CheckinResponse
import com.example.smartlabapp.models.api.GetSummaryRequest
import com.example.smartlabapp.models.api.StoreSensorsRequest
import com.example.smartlabapp.models.api.StoreSensorsResponse
import com.example.smartlabapp.models.constants.DebuggingIdentifiers
import com.example.smartlabapp.utils.GsonRequest
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset

class DataCoordinator {
    companion object {
        val shared = DataCoordinator()
        const val identifier = "[DataCoordinator]"
    }

    // MARK: Variables
    var context: Context? = null

    // API
    var apiRequestQueue: RequestQueue? = null

    fun initialize(context: Context, onLoad: () -> Unit) {
        Log.i(
            identifier,
            "${DebuggingIdentifiers.actionOrEventInProgress} initialize  ${DebuggingIdentifiers.actionOrEventInProgress}.",
        )
        // Set Context
        this.context = context
        this.apiRequestQueue = Volley.newRequestQueue(context)
    }
}

fun DataCoordinator.storeSensorsAPI(
    request: StoreSensorsRequest,
    onSuccess: () -> Unit,
    onError: () -> Unit,
) {
    val apiRequestQueue = this.apiRequestQueue ?: return
    // / Create the payload
    val payload = JSONObject()
    payload.put("device_imei", request.deviceImei)
    payload.put("device_id", request.deviceID)
    payload.put("is_moving", request.isMoving)
    payload.put("direction", request.direction)
    payload.put("ambient_light", request.ambientLight)
    payload.put("ambient_noise", request.ambientNoise)
    payload.put("timestamp", request.timestamp)

    // / Create the Headers
    var headers: MutableMap<String, String> = HashMap<String, String>()
    headers.put("Content-Type", "text/plain")
    // Create the Request
    val request = GsonRequest(
        url = "https://smartlab.namury.dev/store-sensors",
        clazz = StoreSensorsResponse::class.java,
        method = Request.Method.POST,
        headers = headers,
        jsonPayload = payload,
        listener = {
            Log.i(
                identifier,
                "${DebuggingIdentifiers.actionOrEventSucceded} request : $it.",
            )
            onSuccess()
        },
        errorListener = {
            val response = it.networkResponse
            try {
                val errorJson = String(
                    response.data,
                    Charset.forName(HttpHeaderParser.parseCharset(response.headers)),
                )
                val errorObj = Gson().fromJson(errorJson, SampleError::class.java)
                Log.i(
                    identifier,
                    "${DebuggingIdentifiers.actionOrEventFailed} request : ${errorObj.error}",
                )
                onError()
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            } catch (e: JsonSyntaxException) {
                e.printStackTrace()
            }
        },
    )

    // Make the request
    apiRequestQueue.add(request)
}

fun DataCoordinator.checkinAPI(
    request: CheckinRequest,
    onSuccess: (message:String) -> Unit,
    onError: () -> Unit,
) {
    VolleyLog.DEBUG = true;
    val apiRequestQueue = this.apiRequestQueue ?: return
    // / Create the payload
    val payload = JSONObject()
    payload.put("device_imei", request.deviceImei)
    payload.put("device_id", request.deviceID)
    payload.put("checkin_id", request.checkinID)

    // / Create the Headers
    var headers: MutableMap<String, String> = HashMap<String, String>()
    headers.put("Content-Type", "text/plain")
    // Create the Request
    val request = GsonRequest(
        url = "https://smartlab.namury.dev/checkin",
        clazz = CheckinResponse::class.java,
        method = Request.Method.POST,
        headers = headers,
        jsonPayload = payload,
        listener = {
            Log.i(
                identifier,
                "${DebuggingIdentifiers.actionOrEventSucceded} request : $it.",
            )
            onSuccess(it.message)
        },
        errorListener = {
            val response = it.networkResponse
            try {
                val errorJson = String(
                    response.data,
                    Charset.forName(HttpHeaderParser.parseCharset(response.headers)),
                )
                val errorObj = Gson().fromJson(errorJson, SampleError::class.java)
                Log.i(
                    identifier,
                    "${DebuggingIdentifiers.actionOrEventFailed} request : ${errorObj.error}",
                )
                onError()
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            } catch (e: JsonSyntaxException) {
                e.printStackTrace()
            }
        },
    )

    // Make the request
    apiRequestQueue.add(request)
}

fun DataCoordinator.getSummaryAPI(
    request: GetSummaryRequest,
    onSuccess: (SummaryResponse) -> Unit,
    onError: () -> Unit,
) {
    val apiRequestQueue = this.apiRequestQueue ?: return
    // / Create the payload
    val payload = JSONObject()
    payload.put("device_imei", request.deviceImei)
    payload.put("device_id", request.deviceID)
    payload.put("interval", request.interval)

    // / Create the Headers
    var headers: MutableMap<String, String> = HashMap<String, String>()
    headers.put("A-sample-key", "a-sample-key")
    // Create the Request
    val request = GsonRequest(
        url = "https://smartlab.namury.dev/summary?interval=${request.interval}&device_imei=${request.deviceImei}&device_id=${request.deviceID}",
        clazz = SummaryResponse::class.java,
        method = Request.Method.GET,
        headers = headers,
        jsonPayload = payload,
        listener = {
            Log.i(
                identifier,
                "${DebuggingIdentifiers.actionOrEventSucceded} request : $it.",
            )
            onSuccess(it)
        },
        errorListener = {
            val response = it.networkResponse
            try {
//                val errorJson = String(
//                    response.data,
//                    Charset.forName(HttpHeaderParser.parseCharset(response.headers)),
//                )
//                val errorObj = Gson().fromJson(errorJson, SampleError::class.java)
                Log.i(
                    identifier,
                    "${DebuggingIdentifiers.actionOrEventFailed} error Get Summary",
                )
                onError()
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            } catch (e: JsonSyntaxException) {
                e.printStackTrace()
            }
        },
    )

    // Make the request
    apiRequestQueue.add(request)
}


