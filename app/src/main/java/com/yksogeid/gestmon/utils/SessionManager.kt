package com.yksogeid.gestmon.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private var editor: SharedPreferences.Editor = prefs.edit()

    companion object {
        const val PREF_NAME = "GestMonSession"
        const val KEY_TOKEN = "token"
        const val KEY_TOKEN_TYPE = "token_type"
        const val KEY_NOMBRE = "nombre"
        const val KEY_APELLIDO = "apellido"
        const val KEY_EMAIL = "email"
        const val KEY_ROL = "rol"
        const val KEY_USERNAME = "username"
        const val KEY_USER_ID = "userId"
        const val IS_LOGIN = "isLoggedIn"
        const val KEY_CARRERA = "carrera"
    }

    fun saveAuthToken(token: String) {
        editor.putString(KEY_TOKEN, token)
        editor.apply()
    }

    fun saveUserData(
        tokenType: String,
        nombre: String,
        apellido: String,
        email: String,
        rol: String,
        username: String,
        userId: Int,
        carrera: String = "Sin carrera" // Optional parameter with default value
    ) {
        editor.putString(KEY_TOKEN_TYPE, tokenType)
        editor.putString(KEY_NOMBRE, nombre)
        editor.putString(KEY_APELLIDO, apellido)
        editor.putString(KEY_EMAIL, email)
        editor.putString(KEY_ROL, rol)
        editor.putString(KEY_USERNAME, username)
        editor.putInt(KEY_USER_ID, userId)
        editor.putString(KEY_CARRERA, carrera)
        editor.putBoolean(IS_LOGIN, true)
        editor.apply()
    }

    fun getToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }

    fun getUserId(): Int {
        return prefs.getInt(KEY_USER_ID, -1)
    }

    fun getUserName(): String? {
        return prefs.getString(KEY_USERNAME, null)
    }

    fun getNombre(): String? {
        return prefs.getString(KEY_NOMBRE, null)
    }

    fun getApellido(): String? {
        return prefs.getString(KEY_APELLIDO, null)
    }

    fun getEmail(): String? {
        return prefs.getString(KEY_EMAIL, null)
    }

    fun getRol(): String? {
        return prefs.getString(KEY_ROL, null)
    }
    
    fun getCarrera(): String? {
        return prefs.getString(KEY_CARRERA, null)
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(IS_LOGIN, false)
    }

    fun clearSession() {
        editor.clear()
        editor.apply()
    }
}