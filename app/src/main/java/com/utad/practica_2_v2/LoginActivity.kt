package com.utad.practica_2_v2

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.lifecycleScope
import com.utad.practica_2_v2.databinding.ActivityLoginBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    var nombre: String = ""
    var password: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        binding=ActivityLoginBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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

        loadDataFromUser()

        binding.btnSingUp.setOnClickListener {
            val intent = Intent(this, SingUpActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogIn.setOnClickListener {
            checkCredentials(nombre, password)
        }
    }


    private fun checkCredentials(name:String, password:String){
        var userDataBase=""
        var passwordDataBase=""

        lifecycleScope.launch {
            getUserProfile().collect { userProfile ->
                withContext(Dispatchers.Main) {
                    userDataBase=userProfile.name
                    passwordDataBase=userProfile.password
                    if (name == userDataBase.toString() && password == passwordDataBase.toString()){
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                    }
                }
            }

        }
    }

    private fun checkCredentialsFromUser(user:String, password:String) {
        var userDataBase=""
        var passwordDataBase=""
         lifecycleScope.launch {
            getUserProfile().collect { userProfile ->
                withContext(Dispatchers.Main) {
                    binding.etUser.setText(userProfile.name)
                    binding.etPass.setText(userProfile.password)
                }
                userDataBase=userProfile.name
            }
            }

           lifecycleScope.launch {
                readFromDataStore("password").collect { password ->
                    passwordDataBase=password!!
                    if (user == userDataBase.toString() && password == passwordDataBase.toString()){
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                    }
                }

            }


    }

    private fun loadDataFromDataStore() {
        // Leer las preferencias desde DataStore
        lifecycleScope.launch {
            // Recolectar el flujo para la contraseña y actualizar el EditText
            readFromDataStore("password").collect { password ->
                binding.etPass.setText(password)
            }
        }
        lifecycleScope.launch {
            readFromDataStore("name").collect { username ->
                binding.etUser.setText(username)
            }
        }

    }

    private fun loadDataFromUser(){
        lifecycleScope.launch {
            getUserProfile().collect { userProfile ->
                withContext(Dispatchers.Main) {
                    binding.etUser.setText(userProfile.name)
                    binding.etPass.setText(userProfile.password)
                }
            }
        }
    }

    private fun checkData() {
        nombre = binding.etUser.text.toString()
        password = binding.etPass.text.toString()
        if (nombre.isEmpty() || password.isEmpty()) {
            binding.btnLogIn.isEnabled = false
        } else {
            binding.btnLogIn.isEnabled = true
        }
    }

    // Modificar la función para devolver Flow<String?>
    private fun readFromDataStore(key: String): Flow<String?> {
        val keyPreference = stringPreferencesKey(key)
        return applicationContext.dataStore.data
            .map { preferences ->
                preferences[keyPreference] ?: ""  // Si no hay valor, devuelve una cadena vacía
            }
    }

    private fun getUserProfile() = dataStore.data.map { preferences ->
        User(
            name = preferences[stringPreferencesKey("name")]?: "",
            password = preferences[stringPreferencesKey("password")] ?: ""
        )
    }
}