package com.yeinerdpajaro.correntometro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.yeinerdpajaro.correntometro.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        mainBinding.btnBluetooth.setOnClickListener {
            intent = Intent(this,BluetoohActivity::class.java)
            startActivity(intent)
        }



    }
}