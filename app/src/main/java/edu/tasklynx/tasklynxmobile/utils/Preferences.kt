package edu.tasklynx.tasklynxmobile.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

class Preferences(context: Context) {
    private val PREFS_NAME = "edu.tasklynx.tasklynxmobile"
    private val EMPLOYEE_ID = "employee_id"
    private val EMPLOYEE_PASS = "employee_pass"
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

    var employeeId: String
        get() = prefs.getString(EMPLOYEE_ID, "").toString()
        set(value) = prefs.edit().putString(EMPLOYEE_ID, value).apply()

    var employeePassword: String
        get() = prefs.getString(EMPLOYEE_PASS, "").toString()
        set(value) = prefs.edit().putString(EMPLOYEE_PASS, value).apply()

    fun deletePreferences() {
        prefs.edit().apply {
            remove(EMPLOYEE_ID)
            remove(EMPLOYEE_PASS)
            apply()
        }
    }

    fun checkPreferences(): Boolean {
        return employeeId.isNotEmpty() || employeePassword.isNotEmpty()
    }
}