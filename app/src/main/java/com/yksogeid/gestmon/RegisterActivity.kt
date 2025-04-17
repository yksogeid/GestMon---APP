package com.yksogeid.gestmon

import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.yksogeid.gestmon.services.RetrofitClient
import com.yksogeid.gestmon.services.CarreraResponse
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

    private lateinit var spCarrera: Spinner
    private var carreras: List<CarreraResponse> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize views first
        initializeViews()
        
        // Load carreras after views are initialized
        loadCarreras()
        
        // Setup click listeners
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
        spCarrera = findViewById(R.id.spCarrera)

        // Configure username field for numeric input with max length
        etUsername.inputType = android.text.InputType.TYPE_CLASS_NUMBER
        etUsername.filters = arrayOf(
            InputFilter.LengthFilter(12),
            InputFilter { source: CharSequence?, _: Int, _: Int, _: Spanned?, _: Int, _: Int ->
                source?.toString()?.replace(Regex("[^0-9]"), "") ?: ""
            }
        )
    }

    private fun setupClickListeners() {
        btnRegister.setOnClickListener {
            if (validateFields()) {
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

    private fun validateFields(): Boolean {
        if (etNumeroDocumento.text.toString().length != 8) {
            Toast.makeText(this, "El número de documento debe tener 8 dígitos", Toast.LENGTH_SHORT).show()
            return false
        }
        if (etUsername.text.toString().length != 12) {
            Toast.makeText(this, "El codigo de estudiante debe tener 12 dígitos", Toast.LENGTH_SHORT).show()
            return false
        }
        if (etNombres.text.toString().isEmpty() || etApellidos.text.toString().isEmpty() ||
            etEmail.text.toString().isEmpty() || etUsername.text.toString().isEmpty() ||
            etPassword.text.toString().isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            return false
        }
        // Add validation for username format
        if (!etUsername.text.toString().matches(Regex("^\\d{12}$"))) {
            Toast.makeText(this, "El usuario debe ser un número de 12 dígitos", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
    
    private fun loadCarreras() {
        RetrofitClient.apiService.getCarreras().enqueue(object : Callback<List<CarreraResponse>> {
            override fun onResponse(call: Call<List<CarreraResponse>>, response: Response<List<CarreraResponse>>) {
                if (response.isSuccessful) {
                    carreras = response.body() ?: emptyList()
                    val carrerasNames = carreras.map { it.nombre }
                    val adapter = ArrayAdapter(
                        this@RegisterActivity,
                        android.R.layout.simple_spinner_item,
                        carrerasNames
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spCarrera.adapter = adapter
                } else {
                    Toast.makeText(this@RegisterActivity, "Error al cargar carreras", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<CarreraResponse>>, t: Throwable) {
                Toast.makeText(this@RegisterActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // When registering, get the selected career ID
    private fun getSelectedCarreraId(): Int? {
        val position = spCarrera.selectedItemPosition
        return if (position != Spinner.INVALID_POSITION && carreras.isNotEmpty()) {
            carreras[position].idcarrera
        } else null
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
        registerRequest["carrera_id"] = getSelectedCarreraId() ?: 1  // Add career ID, default to 1 if none selected

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
}
