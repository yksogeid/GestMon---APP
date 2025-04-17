package com.yksogeid.gestmon

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.yksogeid.gestmon.utils.SessionManager

class MonitorActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monitor)

        sessionManager = SessionManager(this)
        
        if (!sessionManager.isLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Initialize views
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        toolbar = findViewById(R.id.toolbar)

        // Setup header
        val headerView = navigationView.getHeaderView(0)
        val usuarioNombre = headerView.findViewById<TextView>(R.id.usuarioNombre)
        val rolUsuario = headerView.findViewById<TextView>(R.id.rolUsuario)

        usuarioNombre?.text = "${sessionManager.getNombre()} ${sessionManager.getApellido()}"
        rolUsuario?.text = sessionManager.getRol()

        // Setup toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)

        // Setup navigation
        setupNavigationView()
    }

    private fun setupNavigationView() {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_profile -> {
                    // Handle profile navigation
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
    }

    override fun onSupportNavigateUp(): Boolean {
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawer(navigationView)
        } else {
            drawerLayout.openDrawer(navigationView)
        }
        return true
    }
}