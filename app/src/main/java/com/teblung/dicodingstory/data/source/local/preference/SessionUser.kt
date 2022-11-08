package com.teblung.dicodingstory.data.source.local.preference

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.teblung.dicodingstory.BuildConfig.PREF_NAME_KEY

class SessionUser(context: Context) {
    private val pref: SharedPreferences =
        context.getSharedPreferences(PREF_NAME_KEY, Context.MODE_PRIVATE)
    private val editor = pref.edit()

    fun setUserLogin(user: User) {
        editor.apply {
            putString(NAME_KEY, user.name)
            putString(TOKEN_KEY, user.token)
            putString(USER_ID_KEY, user.userId)
            putBoolean(STATE_KEY, user.isLogin)
            apply()
        }
    }

    fun setUserLogout() {
        editor.apply {
            remove(NAME_KEY)
            remove(TOKEN_KEY)
            remove(USER_ID_KEY)
            putBoolean(STATE_KEY, false)
            apply()
        }
    }

    fun getLoginData(): User {
        val userData = User(
            pref.getString(NAME_KEY, "").toString(),
            pref.getString(TOKEN_KEY, "").toString(),
            pref.getString(USER_ID_KEY, "").toString(),
            pref.getBoolean(STATE_KEY, false)
        )
        Log.d(TAG, "getLoginData: $userData")
        return userData
    }

    companion object {
        private const val NAME_KEY = "NAME"
        private const val TOKEN_KEY = "TOKEN"
        private const val USER_ID_KEY = "USER_ID"
        private const val STATE_KEY = "STATE"
        private val TAG: String = SessionUser::class.java.simpleName
    }
}