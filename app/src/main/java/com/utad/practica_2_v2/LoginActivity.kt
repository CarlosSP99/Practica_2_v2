package com.utad.practica_2_v2

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.lifecycleScope
import com.utad.practica_2_v2.databinding.ActivityLoginBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    var name: String = ""
    var password: String = ""


    val dataStore = DataStoreManager.getInstance(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        listeners()
        observers()
    }

    private fun listeners() {
        binding.etUser.addTextChangedListener {
            checkData()
        }

        binding.etPass.addTextChangedListener {
            checkData()
        }

        binding.btnSingUp.setOnClickListener {
            val intent = Intent(this, SingUpActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogIn.setOnClickListener {
            checkCredentials()
        }
    }

    private fun observers() {
        lifecycleScope.launch(Dispatchers.Main) {
            dataStore.loadData().collectLatest { userProfile ->
                binding.etUser.setText(userProfile.name)
                binding.etPass.setText(userProfile.password)
            }
        }

        lifecycleScope.launch(Dispatchers.Main) {
            dataStore.loadUsersList().collectLatest { users ->
                users.forEach {
                    println("User: ${it.name} - Password: ${it.password}")
                }
            }
        }
    }

    private fun checkCredentials() {
        lifecycleScope.launch(Dispatchers.Main) {
            dataStore.loadUsersList().collectLatest { users ->
                val user = users.find { it.name == name }
                if (user != null && user.password == password) {
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this@LoginActivity, "Invalid credentials", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun checkData() {
        this@LoginActivity.name = binding.etUser.text.toString()
        password = binding.etPass.text.toString()
        if (name.isEmpty() || password.isEmpty()) {
            binding.btnLogIn.isEnabled = false
        } else {
            binding.btnLogIn.isEnabled = true
        }
    }
}