package com.example.wordstest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.example.wordstest.databinding.ActivitySelectBinding
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where

class Select : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private lateinit var binding: ActivitySelectBinding
    private lateinit var realm: Realm
    private lateinit var spinner: Spinner

    private var howMany = 0
    private var csv = ""
    private var check = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        realm = Realm.getDefaultInstance()

        initializeSpinner()

        binding.checkGroup.check(R.id.none_check_set)

        binding.checkSet.setOnClickListener { check = 1 }
        binding.uncheckedSet.setOnClickListener { check = 2 }
        binding.noneCheckSet.setOnClickListener { check = 3 }
        binding.ten.setOnClickListener { howMany = 10 }
        binding.twenty.setOnClickListener { howMany = 20 }
        binding.thirty.setOnClickListener { howMany = 30 }
        binding.forty.setOnClickListener { howMany = 40 }
        binding.fifty.setOnClickListener { howMany = 50 }
        binding.all.setOnClickListener { howMany = 99 }
        binding.start.setOnClickListener {
            if (check == 1) {
                val result = realm.where<Check>()
                    .equalTo("kind", csv)
                    .findAll()
                if (result.isEmpty()) return@setOnClickListener
            }
            if (howMany != 0) {
                setting(howMany, check)
                val intent = Intent(this, TestScreen::class.java)
                startActivity(intent)
            }
        }
    }

    private fun setting(howMany: Int, check: Int) {
        realm.executeTransaction {
            val target = realm.where<Settings>().findAll()
            target.deleteAllFromRealm()
            val obj = realm.createObject<Settings>()
            obj.howMany = howMany
            obj.csv = csv
            obj.check = check
        }
    }

    private fun initializeSpinner() {
        spinner = binding.spinner
        ArrayAdapter.createFromResource(
            this,
            R.array.spinner_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
            spinner.onItemSelectedListener = this
        }
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        csv = parent.getItemAtPosition(pos).toString()
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Another interface callback
    }
}