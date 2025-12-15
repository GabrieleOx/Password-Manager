package com.gabrieleox.passwordmanager

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import javax.crypto.Cipher

private val Context.dataStore by preferencesDataStore(name = "data")
private val NAME_LIST = stringPreferencesKey("com.gabrieleox.passwordmanager.name_list")

fun savePassword(
    newPasswordName: String,
    newPass: ByteArray,
    cipher: Cipher,
    context: Context
) {
    val crypted = encrypt(cipher, newPass)
    runBlocking {
        context.dataStore.edit { data ->
            data[stringPreferencesKey(newPasswordName)] = Json.encodeToString(crypted)
        }
    }
}

fun seePassword(
    alias: String,
    context: Context
): Pair<ByteArray, ByteArray>? {
    val crypted = runBlocking {
        val data = context.dataStore.data.first()
        data[stringPreferencesKey(alias)]
    }
    return if (crypted != null){
        Json.decodeFromString<Pair<ByteArray, ByteArray>>(crypted)
    }else null
}

fun saveNames(
    names: MutableMap<String, MutableList<String>>,
    context: Context
) {
    runBlocking {
        context.dataStore.edit { data ->
            data[NAME_LIST] = Json.encodeToString(names)
        }
    }
}

fun loadNames(
    context: Context
): MutableMap<String, MutableList<String>> {
    val names = runBlocking {
        val data = context.dataStore.data.first()
        data[NAME_LIST]
    }
    return if (names != null){
        Json.decodeFromString<MutableMap<String, MutableList<String>>>(names)
    }else mutableMapOf("Home" to mutableListOf())
}