package com.utad.practica_2_v2

import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val type = object : TypeToken<List<User>>() {}.type

class DataStoreManager(val context: Context) {

    private val gson = Gson()


    suspend fun saveData(name: String, password: String) {
        context.dataStore.edit { editor ->
            editor[USER_FIRST_NAME] = name
            editor[USER_PASSWORD] = password
        }
    }

    fun loadData(): Flow<User> {
        return context.dataStore.data.map { preferences ->
            User(
                name = preferences[USER_FIRST_NAME] ?: "",
                password = preferences[USER_PASSWORD] ?: ""
            )
        }
    }


    suspend fun saveUsersList(users: List<User>) {
        val jsonString = gson.toJson(users)
        context.dataStore.edit { editor ->
            editor[USERS_LIST_KEY] = jsonString
        }
    }

    fun loadUsersList(): Flow<List<User>> {
        return context.dataStore.data.map { preferences ->
            val jsonString = preferences[USERS_LIST_KEY] ?: "[]"
            gson.fromJson(jsonString, type)
        }
    }

    companion object {
        const val DATA_STORE_NAME = "MIS_PREFERENCIAS2"

        val USER_FIRST_NAME = stringPreferencesKey("name")
        val USER_PASSWORD = stringPreferencesKey("password")

        val USERS_LIST_KEY = stringPreferencesKey("users_list")


        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: DataStoreManager? = null

        fun getInstance(context: Context): DataStoreManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: DataStoreManager(context).also { INSTANCE = it }
            }
        }

        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = DATA_STORE_NAME)
    }
}