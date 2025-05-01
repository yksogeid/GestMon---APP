package com.yksogeid.gestmon.services

import com.yksogeid.gestmon.models.Monitor
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.GET  // Add this import

// Datos para el login
data class LoginRequest(val username: String, val password: String)
data class LoginResponse(
    val access_token: String,
    val token_type: String,
    val user: UserResponse
)

data class UserResponse(
    val id: Int,
    val username: String,
    val persona: Persona,
    val roles: List<Role>,
    val carreras: List<Carrera>
)

data class Persona(
    val nombres: String,
    val apellidos: String,
    val email: String
) {
}

data class Role(
    val id: Int,
    val nombre: String
)
data class Carrera(
    val id: Int,
    val nombre: String
)

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

// Add this data class with the existing data classes
data class CarreraResponse(
    val idcarrera: Int,
    val nombre: String,
    val created_at: String,
    val updated_at: String
)

// Add this data class with the existing data classes
data class MateriaResponse(
    val idmateria: Int,
    val nombre: String,
    val created_at: String,
    val updated_at: String
)

interface ApiService {
    @Headers("Content-Type: application/json")
    @POST("/api/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @Headers("Content-Type: application/json")
    @POST("usuario/register")
    fun registerUser(@Body user: RegisterRequest): Call<ApiResponse>

    @POST("/api/personas")
    fun register(@Body registerRequest: HashMap<String, Any>): Call<Any>

    @POST("api/logout")
    fun logout(@Header("Authorization") token: String): Call<Any>

    // Add this endpoint to your existing ApiService interface
    @GET("api/carreras")
    fun getCarreras(): Call<List<CarreraResponse>>

    @GET("api/materias")
    fun getMaterias(): Call<List<MateriaResponse>>

    @GET("api/persona-materias")
    fun getMonitores(): Call<List<Monitor>>
}
