package com.example.wordstest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.wordstest.databinding.ActivityMainBinding
import io.realm.Realm
import io.realm.kotlin.where

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        realm = Realm.getDefaultInstance()

        binding.button.setOnClickListener {
            val intent = Intent(this, Select::class.java)
            startActivity(intent)
        }

        binding.button2.setOnClickListener {
            val intent = Intent(this, SelectCardTest::class.java)
            startActivity(intent)
        }

        binding.checkReset.setOnClickListener {
            realm.executeTransaction {
                val target = realm.where<Check>().findAll()
                target.deleteAllFromRealm()
            }
        }
    }
}