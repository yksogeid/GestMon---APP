package com.yksogeid.gestmon

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.card.MaterialCardView
import com.google.android.material.navigation.NavigationView
import com.yksogeid.gestmon.services.RetrofitClient
import com.yksogeid.gestmon.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminActivity : AppCompatActivity() {
    private lateinit var nombreUsuario: TextView
    private lateinit var cardVerMonitores: MaterialCardView
    private lateinit var cardHistorialMonitorias: MaterialCardView
    private lateinit var cardHistorial: MaterialCardView
    private lateinit var cardTipsEstudio: MaterialCardView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        // Initialize SessionManager
        sessionManager = SessionManager(this)

        // Check if user is logged in
        if (!sessionManager.isLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Check if user role is administrator
        if (sessionManager.getRol() != "Administrador") {
            sessionManager.clearSession()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Initialize views
        initializeViews()
        setupToolbar()
        setupNavigationDrawer()
        setupClickListeners()
    }

    private fun initializeViews() {
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
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
        // Verificar datos en Logcat
        Log.d("MainActivity", "Nombre: $nombreCompleto, Rol: $rol")
// Obtener la vista del header del NavigationView
        val headerView = navigationView.getHeaderView(0)
        val usuarioNombre = headerView.findViewById<TextView>(R.id.usuarioNombre)
        val rolUsuario = headerView.findViewById<TextView>(R.id.rolUsuario)
        val carrera = headerView.findViewById<TextView>(R.id.carrera)

        // Get career from SessionManager
        val carreraNombre = sessionManager.getCarrera() ?: "Sin carrera"

        // Establecer textos
        nombreUsuario.text = nombreCompleto
        usuarioNombre?.text = nombreCompleto
        rolUsuario?.text = rol
        carrera?.text = carreraNombre
        nombreUsuario.text = "$nombre $apellido"
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)
    }

    private fun setupClickListeners() {
        cardVerMonitores.setOnClickListener {
            // Handle Reports section
            Toast.makeText(this, "Reportes", Toast.LENGTH_SHORT).show()
        }

        cardHistorialMonitorias.setOnClickListener {
            // Handle Role Assignment section
            Toast.makeText(this, "Asignar Rol", Toast.LENGTH_SHORT).show()
        }

        cardHistorial.setOnClickListener {
            // Handle Communication section
            Toast.makeText(this, "Comunicar", Toast.LENGTH_SHORT).show()
        }

        cardTipsEstudio.setOnClickListener {
            // Handle PQRS section
            Toast.makeText(this, "PQRS", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupNavigationDrawer() {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_profile -> {
                    // Handle profile
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_logout -> {
                    handleLogout()
                    true
                }
                else -> false
            }
        }
    }

    private fun handleLogout() {
        val token = sessionManager.getToken()
        val bearerToken = "Bearer $token"

        RetrofitClient.apiService.logout(bearerToken).enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                if (response.isSuccessful) {
                    sessionManager.clearSession()
                    startActivity(Intent(this@AdminActivity, LoginActivity::class.java))
                    finish()
                    Toast.makeText(this@AdminActivity, "Sesión cerrada exitosamente", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@AdminActivity, "Error al cerrar sesión", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                Toast.makeText(this@AdminActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}