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

class AdminActivity : AppCompatActivity() {
    private lateinit var nombreUsuario: TextView
    private lateinit var cardVerMonitores: CardView
    private lateinit var cardHistorialMonitorias: CardView
    private lateinit var cardAgendarMonitoria: CardView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin) // ✅ Asegurar que cargamos el layout correcto

        // Inicializar vistas
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        toolbar = findViewById(R.id.toolbar)
        cardVerMonitores = findViewById(R.id.cardVerMonitores)
        cardHistorialMonitorias = findViewById(R.id.cardHistorialMonitorias)
        cardAgendarMonitoria = findViewById(R.id.cardAgendarMonitoria)

        // Obtener datos del intent
        val nombre = intent.getStringExtra("nombre") ?: "Usuario"
        val apellido = intent.getStringExtra("apellido") ?: ""
        val rol = intent.getStringExtra("rol") ?: "Administrador"
        val nombreCompleto = "$nombre $apellido"

        // ✅ Verificar en Logcat si los datos se están recibiendo
        Log.d("AdminActivity", "Nombre: $nombreCompleto, Rol: $rol")

        // Obtener la vista del header del NavigationView
        val headerView = navigationView.getHeaderView(0)
        val usuarioNombre = headerView.findViewById<TextView>(R.id.usuarioNombre)
        val rolUsuario = headerView.findViewById<TextView>(R.id.rolUsuario)

        // Asegurar que `usuarioNombre` y `rolUsuario` existen antes de modificarlos
        usuarioNombre?.text = nombreCompleto
        rolUsuario?.text = rol

        // También actualizar `nombreUsuario` si está en la actividad
        nombreUsuario = findViewById(R.id.tvNombreUsuario)
        nombreUsuario.text = "Bienvenido, $nombreCompleto"

        // Configurar Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)

        // Configurar NavigationView
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    Log.d("AdminActivity", "Inicio seleccionado")
                    true
                }
                R.id.nav_profile -> {
                    Log.d("AdminActivity", "Perfil seleccionado")
                    true
                }
                R.id.nav_settings -> {
                    Log.d("AdminActivity", "Configuración seleccionada")
                    true
                }
                R.id.nav_logout -> {
                    Log.d("AdminActivity", "Cerrar sesión seleccionado")
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
        cardAgendarMonitoria.startAnimation(slideUpAnimation)

        val scaleUpAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_up)
        cardAgendarMonitoria.startAnimation(scaleUpAnimation)
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
