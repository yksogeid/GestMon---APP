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
import androidx.fragment.app.Fragment
import com.yksogeid.gestmon.fragments.*
import com.yksogeid.gestmon.services.RetrofitClient
import com.yksogeid.gestmon.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EstudianteActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager
    private lateinit var nombreUsuario: TextView
    private lateinit var cardVerMonitores: CardView
    private lateinit var cardHistorialMonitorias: CardView
    private lateinit var cardHistorial: CardView
    private lateinit var cardTipsEstudio: CardView
    private lateinit var cardPQRS: CardView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var gridLayout: View
    private lateinit var fragmentContainer: View

    // Current active fragment
    private var activeFragment: Fragment? = null
    private var currentFragmentTag: String? = null

    // Fragment tags
    private val TAG_MONITORES = "monitores_fragment"
    private val TAG_HISTORIAL_MONITORIAS = "historial_monitorias_fragment"
    private val TAG_HISTORIAL = "historial_fragment"
    private val TAG_TIPS = "tips_fragment"
    private val TAG_PQRS = "pqrs_fragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estudiante)

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
        toolbar = findViewById(R.id.toolbar)
        nombreUsuario = findViewById(R.id.tvNombreUsuario)
        cardVerMonitores = findViewById(R.id.cardVerMonitores)
        cardHistorialMonitorias = findViewById(R.id.cardHistorialMonitorias)
        cardHistorial = findViewById(R.id.cardHistorial)
        cardTipsEstudio = findViewById(R.id.cardTipsEstudio)
        cardPQRS = findViewById(R.id.cardPQRS)
        gridLayout = findViewById(R.id.gridLayout)
        fragmentContainer = findViewById(R.id.fragmentContainer)

        // Get user data from SessionManager
        val nombre = sessionManager.getNombre() ?: "Usuario"
        val apellido = sessionManager.getApellido() ?: ""
        val rol = sessionManager.getRol() ?: "default"
        val nombreCompleto = "$nombre $apellido"
        val carreraNombre = sessionManager.getCarrera() ?: "Sin carrera"

        // Verificar datos en Logcat
        Log.d("MainActivity", "Nombre: $nombreCompleto, Rol: $rol, Carrera: $carreraNombre")

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
            Log.d("MainActivity", "Configuración seleccionada")
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        headerView.findViewById<View>(R.id.nav_update).setOnClickListener {
            Log.d("MainActivity", "Actualizar Datos seleccionado")
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        headerView.findViewById<View>(R.id.nav_notifications).setOnClickListener {
            Log.d("MainActivity", "Notificaciones seleccionadas")
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
                        startActivity(Intent(this@EstudianteActivity, LoginActivity::class.java))
                        Toast.makeText(this@EstudianteActivity, message, Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("LogoutAPI", "Error Response: $errorBody")
                        Toast.makeText(
                            this@EstudianteActivity,
                            "Error al cerrar sesión. Intente nuevamente.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<Any>, t: Throwable) {
                    Log.e("LogoutAPI", "Network Error: ${t.message}")
                    Log.e("LogoutAPI", "Failed Request: ${call.request().url()}")
                    Toast.makeText(
                        this@EstudianteActivity,
                        "Error de conexión al cerrar sesión",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }

        // Setup card click listeners
        setupCardClickListeners()

        // Aplicar animaciones
        aplicarAnimaciones()
    }

    private fun setupCardClickListeners() {
        // Ver Monitores Card
        cardVerMonitores.setOnClickListener {
            loadFragment(VerMonitoresFragment.newInstance(), TAG_MONITORES)
        }

        // Historial Monitorias Card
        cardHistorialMonitorias.setOnClickListener {
            loadFragment(HistorialMonitoriasFragment.newInstance(), TAG_HISTORIAL_MONITORIAS)
        }

        // Historial Card
        cardHistorial.setOnClickListener {
            loadFragment(HistorialFragment.newInstance(), TAG_HISTORIAL)
        }

        // Tips Estudio Card
        cardTipsEstudio.setOnClickListener {
            loadFragment(TipsEstudioFragment.newInstance(), TAG_TIPS)
        }

        // PQRS Card
        cardPQRS.setOnClickListener {
            loadFragment(PQRSFragment.newInstance(), TAG_PQRS)
        }
    }

    private fun loadFragment(fragment: Fragment, tag: String) {
        // Show fragment container and hide grid layout
        fragmentContainer.visibility = View.VISIBLE
        gridLayout.visibility = View.GONE

        // Add back button to toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back) // Make sure you have this drawable

        // Update current fragment reference
        activeFragment = fragment
        currentFragmentTag = tag

        // Begin the transaction
        val transaction = supportFragmentManager.beginTransaction()

        // Use animation for transition
        transaction.setCustomAnimations(
            R.anim.slide_in_right,  // Enter animation
            R.anim.slide_out_left,  // Exit animation
            R.anim.slide_in_left,   // Pop enter animation
            R.anim.slide_out_right  // Pop exit animation
        )

        // Replace whatever is in the fragment container with this fragment
        transaction.replace(R.id.fragmentContainer, fragment, tag)

        // Add to back stack so user can navigate back
        transaction.addToBackStack(tag)

        // Commit the transaction
        transaction.commit()
    }

    private fun returnToMainGrid() {
        // Hide fragment container and show grid layout
        fragmentContainer.visibility = View.GONE
        gridLayout.visibility = View.VISIBLE

        // Reset toolbar icon to menu
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)

        // Clear the active fragment reference
        activeFragment = null
        currentFragmentTag = null

        // Clear back stack
        supportFragmentManager.popBackStack()
    }

    private fun aplicarAnimaciones() {
        val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        nombreUsuario.startAnimation(fadeInAnimation)

        val slideUpAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        cardVerMonitores.startAnimation(slideUpAnimation)
        cardHistorialMonitorias.startAnimation(slideUpAnimation)
        cardHistorial.startAnimation(slideUpAnimation)
        cardTipsEstudio.startAnimation(slideUpAnimation)
        cardPQRS.startAnimation(slideUpAnimation)
    }

    // Update these methods to work with fragments
    override fun onSupportNavigateUp(): Boolean {
        if (activeFragment != null) {
            returnToMainGrid()
            return true
        } else if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return true
    }

    override fun onBackPressed() {
        if (activeFragment != null) {
            returnToMainGrid()
        } else if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}