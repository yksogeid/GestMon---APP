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
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment

class AdminActivity : AppCompatActivity() {
    private lateinit var nombreUsuario: TextView
    private lateinit var cardContainer: View
    private lateinit var cardVerMonitores: CardView
    private lateinit var cardHistorialMonitorias: CardView
    private lateinit var cardAgendarMonitoria: CardView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        // Inicializar vistas
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        toolbar = findViewById(R.id.toolbar)
        cardContainer = findViewById(R.id.cardContainer)
        cardVerMonitores = findViewById(R.id.cardVerMonitores)
        cardHistorialMonitorias = findViewById(R.id.cardHistorialMonitorias)
        cardAgendarMonitoria = findViewById(R.id.cardAgendarMonitoria)

        // Obtener datos del intent
        val nombre = intent.getStringExtra("nombre") ?: "Usuario"
        val apellido = intent.getStringExtra("apellido") ?: ""
        val rol = intent.getStringExtra("rol") ?: "Administrador"
        val nombreCompleto = "$nombre $apellido"

        Log.d("AdminActivity", "Nombre: $nombreCompleto, Rol: $rol")

        // Configurar datos en el header del NavigationView
        val headerView = navigationView.getHeaderView(0)
        val usuarioNombre = headerView.findViewById<TextView>(R.id.usuarioNombre)
        val rolUsuario = headerView.findViewById<TextView>(R.id.rolUsuario)
        usuarioNombre?.text = nombreCompleto
        rolUsuario?.text = rol

        // Configurar mensaje de bienvenida
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
                    val intent = Intent(this, AdminActivity::class.java).apply {
                        putExtra("nombre", nombre)
                        putExtra("apellido", apellido)
                        putExtra("rol", rol)
                    }
                    startActivity(intent)
                    finish()
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

        // Configurar click en tarjetas para mostrar fragmentos
        cardVerMonitores.setOnClickListener {
            mostrarFragmento(VerMonitoresFragment())
        }

      //  cardHistorialMonitorias.setOnClickListener {
        //    mostrarFragmento(HistorialMonitoriasFragment())
        //}

        //cardAgendarMonitoria.setOnClickListener {
          //  mostrarFragmento(AgendarMonitoriaFragment())
        //+2}

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

    private fun mostrarFragmento(fragment: Fragment) {
        cardContainer.visibility = View.GONE // Ocultar las tarjetas
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
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
            if (supportFragmentManager.backStackEntryCount > 0) {
                supportFragmentManager.popBackStack()
                cardContainer.visibility = View.VISIBLE // Mostrar de nuevo las tarjetas
            } else {
                super.onBackPressed()
            }
        }
    }
}