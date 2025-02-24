package com.yksogeid.gestmon.services

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

// Datos para el login
data class LoginRequest(val usuario: String, val clave: String)
data class LoginResponse(val success: Boolean, val message: String, val user: User)

// Datos del usuario
data class User(val nombre: String, val apellido: String, val email: String, val rol: String)

// Datos para el registro
data class RegisterRequest(
    val nombre: String,
    val apellido: String,
    val email: String,
    val usuario: String,
    val clave: String,
    val rol: String
)

data class ApiResponse(
    val success: Boolean,
    val message: String
)

interface ApiService {
    @Headers("Content-Type: application/json")
    @POST("usuario/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @Headers("Content-Type: application/json")
    @POST("usuario/register")
    fun registerUser(@Body user: RegisterRequest): Call<ApiResponse>
}
