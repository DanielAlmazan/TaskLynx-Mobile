package edu.tasklynx.tasklynxmobile.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.TRANSPORT_CELLULAR
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import androidx.core.content.getSystemService

val EMPLOYEE_ID_TAG = "employee_id"
val EMPLOYEE_PASS_TAG = "employee_pass"
val TASK_FINISHED = "task_finished"

fun checkConnection(context: Context): Boolean {
    // Check if there is a network connection
    val cm = context.getSystemService<ConnectivityManager>()
    // Get the network info
    val networkInfo = cm!!.activeNetwork

    if (networkInfo != null) {
        // If there is a network connection check if it is connected
        val activeNetwork = cm.getNetworkCapabilities(networkInfo)

        if (activeNetwork != null) {
            // If the network is connected
            return when {
                activeNetwork.hasTransport(TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(TRANSPORT_CELLULAR) -> true
                else -> false
            }
        }
        return true
    } else {
        return false
    }
}

