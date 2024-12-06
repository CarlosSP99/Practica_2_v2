package com.utad.practica_2_v2

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.utad.practica_2_v2.databinding.ActivityCreateLaguagesBinding
import com.utad.practica_2_v2.languages.Languages
import io.paperdb.Paper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreateLaguagesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateLaguagesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCreateLaguagesBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.etNameLanguague.addTextChangedListener {
            checkData()
        }

        binding.btnCrear.setOnClickListener {
            val languageName = binding.etNameLanguague.text.toString()
            val languague = Languages(languageName)
            saveData(languague)
            finish()
        }
    }

    private fun saveData(languague: Languages) {
        lifecycleScope.launch (Dispatchers.IO) {
            Paper.book("Languages").write(languague.name, languague)
        }
    }

    private fun checkData() {
        if(binding.etNameLanguague.text.toString().isNotEmpty()){
            binding.btnCrear.isEnabled = true
        } else {
            binding.btnCrear.isEnabled = false
        }
    }
}