package com.teblung.dicodingstory.data.source.local.preference

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.teblung.dicodingstory.BuildConfig.PREF_NAME_KEY
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = PREF_NAME_KEY)

class SessionUser @Inject constructor(@ApplicationContext val context: Context) {
    private val dataStore = context.datastore

    suspend fun setUserLogin(user: User) {
        dataStore.edit {
            it[NAME_KEY] = user.name
            it[TOKEN_KEY] = user.token
            it[USER_ID_KEY] = user.userId
            it[STATE_KEY] = user.isLogin
        }
    }

    suspend fun setUserLogout() {
        dataStore.edit {
            it[NAME_KEY] = ""
            it[TOKEN_KEY] = ""
            it[USER_ID_KEY] = ""
            it[STATE_KEY] = false
        }
    }

    fun getLoginData(): Flow<User> {
        return dataStore.data.map {
            User(
                it[NAME_KEY].toString(),
                it[TOKEN_KEY].toString(),
                it[USER_ID_KEY].toString(),
                it[STATE_KEY] ?: false
            )
        }
    }

    companion object {
        private val NAME_KEY = stringPreferencesKey("NAME")
        private val TOKEN_KEY = stringPreferencesKey("TOKEN")
        private val USER_ID_KEY = stringPreferencesKey("USER_ID")
        private val STATE_KEY = booleanPreferencesKey("STATE")
    }
}