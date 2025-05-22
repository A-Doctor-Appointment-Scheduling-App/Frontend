package com.example.doccur.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class TokenManager(context: Context) {
    companion object {
        private const val PREFS_NAME = "doccur_prefs"
        private const val ACCESS_TOKEN = "access_token"
        private const val REFRESH_TOKEN = "refresh_token"
        private const val USER_ROLE = "user_role"
        private const val USER_ID = "user_id"
    }

    private val prefs: SharedPreferences = 
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveTokens(accessToken: String, refreshToken: String) {
        val editor = prefs.edit()
        editor.putString(ACCESS_TOKEN, accessToken)
        editor.putString(REFRESH_TOKEN, refreshToken)
        editor.apply()
    }

    fun saveUserInfo(role: String, userId: Int) {
        val editor = prefs.edit()
        editor.putString(USER_ROLE, role)
        editor.putInt(USER_ID, userId)
        Log.d("saving user role ","saving user role:$role")
        editor.apply()
    }

    fun getAccessToken(): String? = prefs.getString(ACCESS_TOKEN, null)

    fun getRefreshToken(): String? = prefs.getString(REFRESH_TOKEN, null)

    fun getUserRole(): String? = prefs.getString(USER_ROLE, null)

    fun getUserId(): Int = prefs.getInt(USER_ID, -1)

    fun clearTokens() {
        val editor = prefs.edit()
        editor.remove(ACCESS_TOKEN)
        editor.remove(REFRESH_TOKEN)
        editor.remove(USER_ROLE)
        editor.remove(USER_ID)
        editor.apply()
    }

    fun isLoggedIn(): Boolean = !getAccessToken().isNullOrEmpty()

    fun isDoctor(): Boolean = getUserRole() == "doctor"

    fun isPatient(): Boolean = getUserRole() == "patient"
}