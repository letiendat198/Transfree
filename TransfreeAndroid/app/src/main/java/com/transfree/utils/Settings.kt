package com.transfree.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class Settings(private val context: Context) {
    suspend fun <T> readKey(key: Preferences.Key<T>): T? {
        val contentFlow: Flow<T?> = context.dataStore.data.map { preferences ->
            preferences[key]
        }
        return contentFlow.firstOrNull()
    }

    fun <T> blockingReadKey(key: Preferences.Key<T>): T?{
        var value: T?
        runBlocking {
            value = readKey(key)
        }
        return value
    }

    suspend fun <T> writeKey(key: Preferences.Key<T>, value: T){
        context.dataStore.edit { settings ->
            settings[key] = value
        }
    }

    suspend fun <T> exists(key: Preferences.Key<T>): Boolean?{
        val isExist: Flow<Boolean> = context.dataStore.data.map {preferences ->
            preferences.contains(key)
        }
        return isExist.firstOrNull()
    }
}