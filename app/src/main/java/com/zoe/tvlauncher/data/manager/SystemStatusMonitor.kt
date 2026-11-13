package com.zoe.tvlauncher.data.manager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.BatteryManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class SystemStatus(
    val timeFormatted: String = "",
    val timePeriod: String = "", // AM/PM when in 12h mode
    val dateFormatted: String = "",
    val dayOfWeek: String = "",
    val isNetworkConnected: Boolean = true,
    val networkType: NetworkType = NetworkType.WIFI,
    val wifiSsid: String? = null,
    val wifiSignalLevel: Int = 3, // 0-4
    val hasBattery: Boolean = false,
    val batteryPercent: Int = 100,
    val isCharging: Boolean = false
)

enum class NetworkType {
    NONE,
    WIFI,
    ETHERNET,
    CELLULAR
}

class SystemStatusMonitor(
    private val context: Context,
    private val scope: CoroutineScope
) {
    private val _status = MutableStateFlow(SystemStatus())
    val status: StateFlow<SystemStatus> = _status.asStateFlow()

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
    private val wifiManager =
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager

    private var is24HourFormat = false

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let { updateBattery(it) }
        }
    }

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            updateNetworkStatus()
        }

        override fun onLost(network: Network) {
            updateNetworkStatus()
        }

        override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
            updateNetworkStatus()
        }
    }

    fun start(is24Hour: Boolean) {
        this.is24HourFormat = is24Hour
        startTimeTicker()
        registerNetworkCallback()
        registerBatteryReceiver()
        updateNetworkStatus()
    }

    fun setTimeFormat(is24Hour: Boolean) {
        this.is24HourFormat = is24Hour
        updateDateTime()
    }

    private fun startTimeTicker() {
        scope.launch(Dispatchers.Default) {
            while (isActive) {
                updateDateTime()
                delay(1000)
            }
        }
    }

    private fun updateDateTime() {
        val now = Date()
        val timePattern = if (is24HourFormat) "HH:mm" else "h:mm"
        val timeStr = SimpleDateFormat(timePattern, Locale.getDefault()).format(now)
        val periodStr = if (!is24HourFormat) {
            SimpleDateFormat("a", Locale.getDefault()).format(now).uppercase()
        } else ""

        val dateStr = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(now)
        val dayStr = SimpleDateFormat("EEEE", Locale.getDefault()).format(now)

        _status.value = _status.value.copy(
            timeFormatted = timeStr,
            timePeriod = periodStr,
            dateFormatted = dateStr,
            dayOfWeek = dayStr
        )
    }

    private fun registerNetworkCallback() {
        try {
            val request = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()
            connectivityManager?.registerNetworkCallback(request, networkCallback)
        } catch (e: Exception) {
            // Fallback for restricted permissions
            updateNetworkStatus()
        }
    }

    private fun updateNetworkStatus() {
        val cm = connectivityManager ?: return
        val activeNetwork = cm.activeNetwork
        val caps = cm.getNetworkCapabilities(activeNetwork)

        val isConnected = caps != null && caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        var netType = NetworkType.NONE
        var ssid: String? = null
        var signalLevel = 3

        if (caps != null) {
            when {
                caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                    netType = NetworkType.ETHERNET
                }
                caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    netType = NetworkType.WIFI
                    try {
                        val info: WifiInfo? = wifiManager?.connectionInfo
                        if (info != null) {
                            val rawSsid = info.ssid
                            if (!rawSsid.isNullOrBlank() && rawSsid != "<unknown ssid>") {
                                ssid = rawSsid.removeSurrounding("\"")
                            }
                            signalLevel = WifiManager.calculateSignalLevel(info.rssi, 5)
                        }
                    } catch (_: Exception) {}
                }
                caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    netType = NetworkType.CELLULAR
                }
            }
        }

        _status.value = _status.value.copy(
            isNetworkConnected = isConnected,
            networkType = netType,
            wifiSsid = ssid,
            wifiSignalLevel = signalLevel
        )
    }

    private fun registerBatteryReceiver() {
        try {
            val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            val batteryStatus = context.registerReceiver(batteryReceiver, filter)
            batteryStatus?.let { updateBattery(it) }
        } catch (_: Exception) {}
    }

    private fun updateBattery(intent: Intent) {
        val present = intent.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false)
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)

        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL

        val batteryPct = if (level >= 0 && scale > 0) {
            (level * 100 / scale.toFloat()).toInt()
        } else {
            100
        }

        _status.value = _status.value.copy(
            hasBattery = present,
            batteryPercent = batteryPct,
            isCharging = isCharging
        )
    }

    fun stop() {
        try {
            connectivityManager?.unregisterNetworkCallback(networkCallback)
        } catch (_: Exception) {}
        try {
            context.unregisterReceiver(batteryReceiver)
        } catch (_: Exception) {}
    }
}
