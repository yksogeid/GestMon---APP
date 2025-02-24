package com.yksogeid.gestmon

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.yksogeid.gestmon.services.ApiResponse
import com.yksogeid.gestmon.services.ApiService
import com.yksogeid.gestmon.services.RegisterRequest
import com.yksogeid.gestmon.services.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etApellido: EditText
    private lateinit var etCorreo: EditText
    private lateinit var etUsuario: EditText
    private lateinit var etClave: EditText
    private lateinit var btnRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etNombre = findViewById(R.id.etNombre)
        etApellido = findViewById(R.id.etApellido)
        etCorreo = findViewById(R.id.etCorreo)
        etUsuario = findViewById(R.id.etUsuario)
        etClave = findViewById(R.id.etClave)
        btnRegister = findViewById(R.id.btnRegister)

        btnRegister.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        val user = RegisterRequest(
            nombre = etNombre.text.toString(),
            apellido = etApellido.text.toString(),
            email = etCorreo.text.toString(),
            usuario = etUsuario.text.toString(),
            clave = etClave.text.toString(),
            rol = "Estudiante"
        )

        RetrofitClient.apiService.registerUser(user).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(applicationContext, "Registro exitoso", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(applicationContext, "Error en el registro", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(applicationContext, "Error de conexión", Toast.LENGTH_LONG).show()
            }
        })
    }
}
