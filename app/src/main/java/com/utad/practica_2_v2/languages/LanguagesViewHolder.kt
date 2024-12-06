package com.utad.practica_2_v2.languages

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.utad.practica_2_v2.databinding.ItemLanguageBinding

class LanguagesViewHolder(view: View): RecyclerView.ViewHolder(view) {

    val binding = ItemLanguageBinding.bind(view)

    fun render(languages: Languages){
        binding.tvName.text = languages.name
    }

}