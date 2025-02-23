package com.yksogeid.gestmon.services

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

data class LoginRequest(val usuario: String, val clave: String)
data class LoginResponse(val success: Boolean, val message: String, val user: User)
data class User(val nombre: String, val apellido: String, val email: String, val rol: String)

interface ApiService {
    @Headers("Content-Type: application/json")
    @POST("api/v1/usuario/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>
}
