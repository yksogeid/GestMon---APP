package com.yksogeid.gestmon

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.yksogeid.gestmon.services.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {
    private lateinit var spTipoDocumento: Spinner
    private lateinit var etNumeroDocumento: EditText
    private lateinit var etNombres: EditText
    private lateinit var etApellidos: EditText
    private lateinit var etEmail: EditText
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var tvLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize views
        initializeViews()
        
        // Set click listeners
        setupClickListeners()
    }

    private fun initializeViews() {
        spTipoDocumento = findViewById(R.id.spTipoDocumento)
        etNumeroDocumento = findViewById(R.id.etNumeroDocumento)
        etNombres = findViewById(R.id.etNombres)
        etApellidos = findViewById(R.id.etApellidos)
        etEmail = findViewById(R.id.etEmail)
        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnRegister = findViewById(R.id.btnRegister)
        tvLogin = findViewById(R.id.tvLogin)
        
        // Add text change listeners to update username in real-time
        etNombres.addTextChangedListener(usernameTextWatcher)
        etApellidos.addTextChangedListener(usernameTextWatcher)
    }
    
    // Add this text watcher to update username in real-time
    private val usernameTextWatcher = object : android.text.TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        
        override fun afterTextChanged(s: android.text.Editable?) {
            val nombres = etNombres.text.toString()
            val apellidos = etApellidos.text.toString()
            
            if (nombres.length >= 2 && apellidos.length >= 2) {
                val generatedUsername = generateUsername(nombres, apellidos)
                etUsername.setText(generatedUsername)
            }
        }
    }

    // Add at class level
    // Update the username generation function
    private fun generateUsername(nombres: String, apellidos: String): String {
        val firstLetter = nombres.trim().firstOrNull()?.lowercase() ?: ""
        val lastName = apellidos.trim().lowercase()
        val randomNumbers = (10..99).random()
        return "${firstLetter}_$lastName$randomNumbers"
    }

    private fun setupClickListeners() {
        btnRegister.setOnClickListener {
            if (validateFields()) {
                // Generate username before registration
                val generatedUsername = generateUsername(
                    etNombres.text.toString(),
                    etApellidos.text.toString()
                )
                etUsername.setText(generatedUsername)
                etUsername.isEnabled = false  // Disable username editing
                
                // Change button text and disable it
                btnRegister.isEnabled = false
                btnRegister.text = "Cargando..."
                
                registerUser()
            }
        }

        tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun registerUser() {
        val registerRequest = HashMap<String, Any>()
        registerRequest["tipoDocumento"] = spTipoDocumento.selectedItem.toString()
        registerRequest["numeroDocumento"] = etNumeroDocumento.text.toString()
        registerRequest["nombres"] = etNombres.text.toString()
        registerRequest["apellidos"] = etApellidos.text.toString()
        registerRequest["email"] = etEmail.text.toString()
        registerRequest["username"] = etUsername.text.toString()
        registerRequest["password"] = etPassword.text.toString()
        registerRequest["rol_id"] = 1

        // Log request data
        Log.d("RegisterAPI", "Request Data: $registerRequest")

        RetrofitClient.apiService.register(registerRequest).enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                // Reset button state
                runOnUiThread {
                    btnRegister.isEnabled = true
                    btnRegister.text = "Registrarse"
                }
                
                Log.d("RegisterAPI", "Response Code: ${response.code()}")
                Log.d("RegisterAPI", "Response Body: ${response.body()}")
                Log.d("RegisterAPI", "Request URL: ${call.request().toString()}")
                
                if (response.isSuccessful) {
                    Toast.makeText(this@RegisterActivity, "Registro exitoso", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                    finish()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("RegisterAPI", "Error Response: $errorBody")
                    val errorMessage = try {
                        JSONObject(errorBody ?: "").getString("message")
                    } catch (e: Exception) {
                        "Error en el registro"
                    }
                    Toast.makeText(this@RegisterActivity, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                // Reset button state
                runOnUiThread {
                    btnRegister.isEnabled = true
                    btnRegister.text = "Registrarse"
                }
                
                Log.e("RegisterAPI", "Network Error: ${t.message}")
                Log.e("RegisterAPI", "Failed Request: ${call.request().toString()}")
                Toast.makeText(
                    this@RegisterActivity,
                    "Error de conexión: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun validateFields(): Boolean {
        if (etNumeroDocumento.text.toString().length != 8) {
            Toast.makeText(this, "El número de documento debe tener 8 dígitos", Toast.LENGTH_SHORT).show()
            return false
        }
        if (etNombres.text.toString().isEmpty() || etApellidos.text.toString().isEmpty() ||
            etEmail.text.toString().isEmpty() || etUsername.text.toString().isEmpty() ||
            etPassword.text.toString().isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}
