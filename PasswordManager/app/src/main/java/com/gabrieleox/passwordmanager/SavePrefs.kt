package com.gabrieleox.passwordmanager

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

private val Context.dataStore by preferencesDataStore(name = "prefs")

fun saveTheme(
    value: Boolean,
    context: Context
){
    runBlocking {
        context.dataStore.edit { prefs ->
            prefs[stringPreferencesKey("AppTheme")] = Json.encodeToString(value)
        }
    }
}

fun loadTheme(
    context: Context
): Boolean? {
    val theme = runBlocking {
        val prefs = context.dataStore.data.first()
        prefs[stringPreferencesKey("AppTheme")]
    }
    return if (theme != null){
        Json.decodeFromString<Boolean>(theme)
    }else null
}