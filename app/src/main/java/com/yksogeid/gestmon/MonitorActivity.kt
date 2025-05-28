package com.yksogeid.gestmon

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.widget.Toolbar
import com.yksogeid.gestmon.services.RetrofitClient
import com.yksogeid.gestmon.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MonitorActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager
    private lateinit var nombreUsuario: TextView
    private lateinit var cardVerMonitores: CardView
    private lateinit var cardHistorialMonitorias: CardView
    private lateinit var cardHistorial: CardView
    private lateinit var cardTipsEstudio: CardView
    // Replace NavigationView references with direct view access
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monitor)

        // Initialize SessionManager
        sessionManager = SessionManager(this)

        // Check if user is logged in
        if (!sessionManager.isLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Check if user role is student
        if (sessionManager.getRol() != "Estudiante Monitor") {
            sessionManager.clearSession()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Continue with normal MonitorActivity flow for students
        // Inicializar vistas
        // Update view initialization
        drawerLayout = findViewById(R.id.drawerLayout)
        // Remove this line: navigationView = findViewById(R.id.navigationView)
        toolbar = findViewById(R.id.toolbar)
        nombreUsuario = findViewById(R.id.tvNombreUsuario)
        cardVerMonitores = findViewById(R.id.cardVerMonitores)
        cardHistorialMonitorias = findViewById(R.id.cardHistorialMonitorias)
        cardHistorial = findViewById(R.id.cardHistorial)
        cardTipsEstudio = findViewById(R.id.cardTipsEstudio)

        // Get user data from SessionManager
        val nombre = sessionManager.getNombre() ?: "Usuario"
        val apellido = sessionManager.getApellido() ?: ""
        val rol = sessionManager.getRol() ?: "default"
        val nombreCompleto = "$nombre $apellido"
        val carreraNombre = sessionManager.getCarrera() ?: "Sin carrera"


        // Verificar datos en Logcat
        Log.d("MonitorActivity", "Nombre: $nombreCompleto, Rol: $rol, Carrera: $carreraNombre")


  

        // Obtener la vista del header del NavigationView
        val headerView = findViewById<View>(R.id.nav_header)
        val usuarioNombre = headerView.findViewById<TextView>(R.id.usuarioNombre)
        val rolUsuario = headerView.findViewById<TextView>(R.id.rolUsuario)
        val carrera = headerView.findViewById<TextView>(R.id.carrera)


        // Establecer textos
        nombreUsuario.text = nombreCompleto
        usuarioNombre?.text = nombreCompleto
        rolUsuario?.text = rol
        carrera?.text = carreraNombre



        // Configurar Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)

        // Replace NavigationView code with button click listeners
        headerView.findViewById<View>(R.id.nav_settings).setOnClickListener {
            Log.d("MonitorActivity", "Configuración seleccionada")
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        headerView.findViewById<View>(R.id.nav_update).setOnClickListener {
            Log.d("MonitorActivity", "Actualizar Datos seleccionado")
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        headerView.findViewById<View>(R.id.nav_notifications).setOnClickListener {
            Log.d("MonitorActivity", "Notificaciones seleccionadas")
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        headerView.findViewById<View>(R.id.nav_logout).setOnClickListener {
            val token = sessionManager.getToken()
            val bearerToken = "Bearer $token"

            // Log the API request
            Log.d("LogoutAPI", "Calling logout endpoint with token: $bearerToken")

            RetrofitClient.apiService.logout(bearerToken).enqueue(object : Callback<Any> {
                override fun onResponse(call: Call<Any>, response: Response<Any>) {
                    // Log the API response
                    Log.d("LogoutAPI", "Response Code: ${response.code()}")
                    Log.d("LogoutAPI", "Response Body: ${response.body()}")
                    Log.d("LogoutAPI", "Request URL: ${call.request().url()}")

                    if (response.isSuccessful) {
                        val message = "Sesión cerrada exitosamente"
                        Log.d("LogoutAPI", "Success: $message")
                        sessionManager.clearSession()
                        startActivity(Intent(this@MonitorActivity, LoginActivity::class.java))
                        Toast.makeText(this@MonitorActivity, message, Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("LogoutAPI", "Error Response: $errorBody")
                        Toast.makeText(
                            this@MonitorActivity,
                            "Error al cerrar sesión. Intente nuevamente.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<Any>, t: Throwable) {
                    Log.e("LogoutAPI", "Network Error: ${t.message}")
                    Log.e("LogoutAPI", "Failed Request: ${call.request().url()}")
                    Toast.makeText(
                        this@MonitorActivity,
                        "Error de conexión al cerrar sesión",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }

        // Aplicar animaciones
        aplicarAnimaciones()
    }

    private fun aplicarAnimaciones() {
        val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        nombreUsuario.startAnimation(fadeInAnimation)

        val slideUpAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        cardVerMonitores.startAnimation(slideUpAnimation)
        cardHistorialMonitorias.startAnimation(slideUpAnimation)
        cardHistorial.startAnimation(slideUpAnimation)
        cardTipsEstudio.startAnimation(slideUpAnimation)
    }

    // Update these methods to work without NavigationView
    override fun onSupportNavigateUp(): Boolean {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
