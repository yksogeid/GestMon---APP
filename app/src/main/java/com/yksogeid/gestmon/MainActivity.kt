package com.yksogeid.gestmon

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.widget.Toolbar
import com.yksogeid.gestmon.utils.SessionManager

class MainActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager
    private lateinit var nombreUsuario: TextView
    private lateinit var cardVerMonitores: CardView
    private lateinit var cardHistorialMonitorias: CardView
    private lateinit var cardHistorial: CardView
    private lateinit var cardTipsEstudio: CardView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize SessionManager
        sessionManager = SessionManager(this)

        // Check if user is logged in
        if (!sessionManager.isLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Check if user role is student
        if (sessionManager.getRol() != "Estudiante") {
            sessionManager.clearSession()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Continue with normal MainActivity flow for students
        // Inicializar vistas
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

        // Redirigir según el rol
        when (rol) {
            "Administrador" -> {
                val intent = Intent(this, AdminActivity::class.java).apply {
                    putExtra("token", sessionManager.getToken())
                    putExtra("userId", sessionManager.getUserId())
                }
                startActivity(intent)
                finish()
                return
            }
            "Estudiante Monitor" -> {
                val intent = Intent(this, MonitorActivity::class.java).apply {
                    putExtra("token", sessionManager.getToken())
                    putExtra("userId", sessionManager.getUserId())
                }
                startActivity(intent)
                finish()
                return
            }
            "Docente" -> {
                val intent = Intent(this, TeacherActivity::class.java).apply {
                    putExtra("token", sessionManager.getToken())
                    putExtra("userId", sessionManager.getUserId())
                }
                startActivity(intent)
                finish()
                return
            }
            "Estudiante" -> {
                // Stay in MainActivity as it's the default view for students
                // Continue with the normal flow
            }
            else -> {
                // Handle unknown role
                Toast.makeText(this, "Rol no reconocido", Toast.LENGTH_SHORT).show()
                sessionManager.clearSession()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                return
            }
        }

        // Obtener la vista del header del NavigationView
        val headerView = navigationView.getHeaderView(0)
        val usuarioNombre = headerView.findViewById<TextView>(R.id.usuarioNombre)
        val rolUsuario = headerView.findViewById<TextView>(R.id.rolUsuario)

        // Establecer textos
        nombreUsuario.text = nombreCompleto
        usuarioNombre?.text = nombreCompleto
        rolUsuario?.text = rol

        // Configurar Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)

        // Configurar NavigationView
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    Log.d("MainActivity", "Inicio seleccionado")
                    true
                }
                R.id.nav_profile -> {
                    Log.d("MainActivity", "Perfil seleccionado")
                    true
                }
                R.id.nav_settings -> {
                    Log.d("MainActivity", "Configuración seleccionada")
                    true
                }
                R.id.nav_logout -> {
                    sessionManager.clearSession()
                    startActivity(Intent(this, LoginActivity::class.java))
                    Toast.makeText(this, "Has cerrado sesión", Toast.LENGTH_SHORT).show()
                    finish()
                    true
                }
                else -> false
            }
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

    override fun onSupportNavigateUp(): Boolean {
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawer(navigationView)
        } else {
            drawerLayout.openDrawer(navigationView)
        }
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawer(navigationView)
        } else {
            super.onBackPressed()
        }
    }
}
