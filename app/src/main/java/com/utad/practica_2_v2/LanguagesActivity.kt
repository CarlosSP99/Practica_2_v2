package com.utad.practica_2_v2

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.utad.practica_2_v2.databinding.ActivityLanguagesBinding
import com.utad.practica_2_v2.languages.Languages
import com.utad.practica_2_v2.languages.LanguagesAdapter
import com.utad.practica_2_v2.languages.LanguagesProvider
import io.paperdb.Paper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LanguagesActivity : AppCompatActivity() {
    private lateinit var adapter: LanguagesAdapter
    private var languagesList = mutableListOf<Languages>()
    private lateinit var binding: ActivityLanguagesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        binding = ActivityLanguagesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.rvLanguages.layoutManager = LinearLayoutManager(this)
        adapter = LanguagesAdapter(languagesList)

        binding.btnCrear.setOnClickListener {
            val intent = Intent(this, CreateLaguagesActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        readUsersInDB()
    }

    private fun readUsersInDB(){
        lifecycleScope.launch(Dispatchers.IO){
            val keyList: List<String> = Paper.book("Languages").allKeys
            val userList: MutableList<Languages> = mutableListOf()
            keyList.forEach { key ->
                val language: Languages? = Paper.book("Languages").read<Languages>(key)
                if (language!=null)
                    userList.add(language)
            }
            withContext(Dispatchers.Main){
                binding.rvLanguages.adapter = adapter
                adapter.submitList(userList)
            }
        }
    }
}