package com.example.appnebula.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

// El nombre "session" para el DataStore está bien.
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class SessionManager(context: Context) {

    // Cambiado para ser privado y consistente
    private val dataStore = context.dataStore

    companion object {
        val USER_ID_KEY = stringPreferencesKey("user_id")
        val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        val AUTH_TOKEN_KEY = stringPreferencesKey("auth_token")
    }

    suspend fun saveSession(userId: String, userEmail: String, token: String) {
        dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId
            preferences[USER_EMAIL_KEY] = userEmail
            preferences[AUTH_TOKEN_KEY] = token
        }
    }

    suspend fun clearSession() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    // --- FUNCIÓN AÑADIDA ---
    /**
     * Obtiene el token de autenticación de forma síncrona para ser usado en
     * contextos que no son coroutines, como un Interceptor de OkHttp.
     * Utiliza runBlocking para esperar a que el Flow de DataStore emita el primer valor.
     */
    fun getToken(): String? {
        // runBlocking ejecuta una coroutine y bloquea el hilo actual hasta que finalice.
        return runBlocking {
            // .data es el Flow<Preferences>
            // .map extrae el valor del token (puede ser null si no existe)
            // .first() obtiene el primer valor emitido por el Flow y cancela la recolección.
            dataStore.data.map { preferences ->
                preferences[AUTH_TOKEN_KEY]
            }.first()
        }
    }
    // --- FIN DE LA FUNCIÓN AÑADIDA ---

    // Tus Flows existentes para la observación reactiva en la UI. Están perfectos.
    val userIdFlow: Flow<String?> = dataStore.data.map { preferences ->
        preferences[USER_ID_KEY]
    }

    val userEmailFlow: Flow<String?> = dataStore.data.map { preferences ->
        preferences[USER_EMAIL_KEY]
    }

    val authTokenFlow: Flow<String?> = dataStore.data.map { preferences ->
        preferences[AUTH_TOKEN_KEY]
    }
}
