package com.yksogeid.gestmon

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.yksogeid.gestmon.services.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.yksogeid.gestmon.services.LoginResponse
import com.yksogeid.gestmon.services.LoginRequest
import android.util.Log
import org.json.JSONObject


class LoginActivity : AppCompatActivity() {

    // Declaración de las vistas
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button
    private lateinit var recuperarClave: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicialización de las vistas
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnRegister = findViewById(R.id.btnRegister)
        recuperarClave = findViewById(R.id.tvForgotPassword)

        // Aplicar animaciones a los elementos
        applyAnimations()

        // Configurar el evento de clic en el botón de inicio de sesión
        btnLogin.setOnClickListener {
            // Animación de escala al hacer clic
            val scaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_up)
            btnLogin.startAnimation(scaleUp)

            // Validar los campos y procesar el inicio de sesión
            validateAndLogin()
        }
        // Configurar el evento de clic en el botón de inicio de sesión
        btnRegister.setOnClickListener {
            // Animación de escala al hacer clic
            val scaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_up)
            btnRegister.startAnimation(scaleUp)

            // Redirigir a RegisterActivity
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
        recuperarClave.setOnClickListener {
            recuperarClave.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_up))
            Toast.makeText(this, "Funcionalidad en desarrollo.", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Aplica animaciones a los elementos de la interfaz.
     */
    private fun applyAnimations() {
        val slideInBottom = AnimationUtils.loadAnimation(this, R.anim.slide_in_bottom)
        etEmail.startAnimation(slideInBottom)
        etPassword.startAnimation(slideInBottom)
        btnLogin.startAnimation(slideInBottom)
        btnRegister.startAnimation(slideInBottom)
    }

    /**
     * Valida los campos de entrada y procesa el inicio de sesión.
     */
    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return activeNetwork.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun validateAndLogin() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        Log.d("LOGIN", "Intentando iniciar sesión con email: $email")

        if (email.isEmpty() || password.isEmpty()) {
            Log.w("LOGIN", "Campos vacíos detectados")
            Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show()
            return
        }
        if (!isNetworkAvailable(this)) {
            Log.e("NETWORK", "Sin conexión a Internet")
            Toast.makeText(this, "No hay conexión a Internet. Verifique su conexión.", Toast.LENGTH_SHORT).show()
            return
        }
        // Test user validation
        if (email == "yksogeid" && password == "yksogeid") {
            Log.i("LOGIN_SUCCESS", "Usuario de prueba autenticado correctamente")
            Toast.makeText(this, "Inicio de sesión exitoso (Usuario de prueba).", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("nombre", "Test")
                putExtra("apellido", "User")
                putExtra("email", "yksogeid")
                putExtra("rol", "Estudiante")
            }
            startActivity(intent)
            finish()
            return
        }


        // Mostrar un indicador de carga (opcional)
        btnLogin.isEnabled = false
        btnLogin.text = "Cargando..."

        // Llamar a la API
        val call = RetrofitClient.apiService.login(LoginRequest(email, password))
        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                btnLogin.isEnabled = true
                btnLogin.text = "Iniciar sesión"

                Log.d("API_RESPONSE", "Código de respuesta: ${response.code()}")

                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    Log.d("API_RESPONSE", "Respuesta: $loginResponse")
                    if (loginResponse?.success == true) {
                        Log.i(
                            "LOGIN_SUCCESS",
                            "Usuario autenticado correctamente: ${loginResponse.user.nombre}"
                        )
                        Toast.makeText(
                            this@LoginActivity,
                            "Inicio de sesión exitoso.",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(this@LoginActivity, MainActivity::class.java).apply {
                            putExtra("nombre", loginResponse.user.nombre)
                            putExtra("apellido", loginResponse.user.apellido)
                            putExtra("email", loginResponse.user.email)
                            putExtra("rol", loginResponse.user.rol)
                        }
                        startActivity(intent)
                        finish()
                    } else {
                        Log.w(
                            "LOGIN_FAILURE",
                            "Error en la autenticación: ${loginResponse?.message}"
                        )
                        Toast.makeText(
                            this@LoginActivity,
                            loginResponse?.message ?: "Error desconocido.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    val errorBody = response.errorBody()?.use { it.string() } // Cierra el stream automáticamente
                    Log.e("API_ERROR", "Error en la respuesta: $errorBody")

                    val errorMessage = runCatching {
                        val jsonObject = JSONObject(errorBody ?: "")
                        jsonObject.optString("message", "Error en la autenticación.") // Obtiene el mensaje o usa el valor por defecto
                    }.getOrElse {
                        "Error en la autenticación." // En caso de excepción
                    }

                    AlertDialog.Builder(this@LoginActivity)                        .setTitle("Error")
                        .setMessage(errorMessage)
                        .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                        .show()
                }


            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                btnLogin.isEnabled = true
                btnLogin.text = "Iniciar sesión"
                Log.e("API_FAILURE", "Error en la llamada a la API: ${t.message}")
                Toast.makeText(this@LoginActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}