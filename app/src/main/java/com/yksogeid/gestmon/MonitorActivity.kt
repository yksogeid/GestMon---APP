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

class MonitorActivity : AppCompatActivity() {
    private lateinit var nombreUsuario: TextView
    private lateinit var cardVerMonitores: MaterialCardView
    private lateinit var cardHistorialMonitorias: MaterialCardView
    private lateinit var cardHistorial: MaterialCardView
    private lateinit var cardTipsEstudio: MaterialCardView
    private lateinit var cardLogo: MaterialCardView
    private lateinit var cardPQRS: MaterialCardView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monitor)

        sessionManager = SessionManager(this)

        if (!sessionManager.isLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        if (sessionManager.getRol() != "Estudiante Monitor") {
            sessionManager.clearSession()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

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
        cardLogo = findViewById(R.id.cardLogo)
        cardPQRS = findViewById(R.id.cardPQRS)

        val nombre = sessionManager.getNombre() ?: "Usuario"
        val apellido = sessionManager.getApellido() ?: ""
        val rol = sessionManager.getRol() ?: "default"
        val nombreCompleto = "$nombre $apellido"

        Log.d("MonitorActivity", "Nombre: $nombreCompleto, Rol: $rol")

        val headerView = navigationView.getHeaderView(0)
        val usuarioNombre = headerView.findViewById<TextView>(R.id.usuarioNombre)
        val rolUsuario = headerView.findViewById<TextView>(R.id.rolUsuario)

        nombreUsuario.text = nombreCompleto
        usuarioNombre?.text = nombreCompleto
        rolUsuario?.text = rol
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)
    }

    private fun setupClickListeners() {
        cardVerMonitores.setOnClickListener {
            Toast.makeText(this, "Registrar Monitoria", Toast.LENGTH_SHORT).show()
        }

        cardHistorialMonitorias.setOnClickListener {
            Toast.makeText(this, "Tu agenda", Toast.LENGTH_SHORT).show()
        }

        cardHistorial.setOnClickListener {
            Toast.makeText(this, "Historial", Toast.LENGTH_SHORT).show()
        }

        cardTipsEstudio.setOnClickListener {
            Toast.makeText(this, "Solicitudes", Toast.LENGTH_SHORT).show()
        }

        cardPQRS.setOnClickListener {
            Toast.makeText(this, "Bonificación de PQRS", Toast.LENGTH_SHORT).show()
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
                    startActivity(Intent(this@MonitorActivity, LoginActivity::class.java))
                    finish()
                    Toast.makeText(this@MonitorActivity, "Sesión cerrada exitosamente", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MonitorActivity, "Error al cerrar sesión", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                Toast.makeText(this@MonitorActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
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