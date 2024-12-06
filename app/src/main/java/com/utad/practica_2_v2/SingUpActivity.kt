package com.utad.practica_2_v2

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.utad.practica_2_v2.databinding.ActivitySingUpBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SingUpActivity : AppCompatActivity() {
    var nombre: String = ""
    var password: String = ""
    var rPassword: String = ""

    val dataStore = DataStoreManager.getInstance(this)

    private lateinit var binding: ActivitySingUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        binding = ActivitySingUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.etUser.addTextChangedListener {
            checkData()
        }

        binding.etPass.addTextChangedListener {
            checkData()
        }

        binding.etRepetirPass.addTextChangedListener {
            checkData()
        }

        binding.btRegistro.setOnClickListener {
            saveNewUser { success ->
                if (success) {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this@SingUpActivity, "User already exist", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun saveNewUser(success: (Boolean) -> Unit) {
        lifecycleScope.launch(Dispatchers.IO) {
            // Obtener la lista de usuarios
            dataStore.loadUsersList().collectLatest { users ->
                // Comprobar si existe previamente
                val user = users.find { it.name == nombre }
                if (user == null) {
                    // Si no existe, añadirlo a la lista
                    val updateList = users.toMutableList().apply {
                        add(User(nombre, password))
                    }
                    dataStore.saveUsersList(updateList)
                    success(true)
                } else {
                    // Si existe, mostrar mensaje de error
                    success(false)
                }
            }
        }
    }

    private fun checkData() {
        nombre = binding.etUser.text.toString()
        password = binding.etPass.text.toString()
        rPassword = binding.etRepetirPass.text.toString()
        if (nombre.isEmpty() || password.isEmpty() || rPassword.isEmpty() || password != rPassword) {
            binding.btRegistro.isEnabled = false
        } else {
            binding.btRegistro.isEnabled = true
        }
    }
}