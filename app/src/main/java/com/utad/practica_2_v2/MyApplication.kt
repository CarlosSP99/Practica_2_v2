package com.utad.practica_2_v2

import android.app.Application
import io.paperdb.Paper

class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        Paper.init(this)
    }
}