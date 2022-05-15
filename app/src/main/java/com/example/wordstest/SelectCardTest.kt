package com.example.wordstest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.example.wordstest.databinding.ActivitySelectCardTestBinding
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import java.lang.reflect.Array

class SelectCardTest : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private lateinit var binding: ActivitySelectCardTestBinding
    private lateinit var realm: Realm
    private lateinit var spinner: Spinner

    private var csv = ""
    private var check = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectCardTestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        realm = Realm.getDefaultInstance()

        initializeSpinner()
        initializer()

        binding.checkSetCard.setOnClickListener { check = 1 }
        binding.uncheckedSetCard.setOnClickListener { check = 2 }
        binding.noneCheckSetCard.setOnClickListener { check = 3 }

        binding.button3.setOnClickListener {
            if (check == 1) {
                val result = realm.where<CheckCard>()
                    .equalTo("kind", csv)
                    .findAll()
                if (result.isEmpty()) return@setOnClickListener
            }
            setting(csv, check)
            val intent = Intent(this, TestScreenCard::class.java)
            startActivity(intent)
        }
    }

    private fun initializer() {
        binding.checkGroup.check(0)
    }

    private fun initializeSpinner() {
        spinner = binding.spinner
        ArrayAdapter.createFromResource(
            this,
            R.array.spinner_card_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
            spinner.onItemSelectedListener = this
        }
    }

    private fun setting(csv: String, check: Int) {
        realm.executeTransaction {
            val target = realm.where<CheckCard>().findAll()
            target.deleteAllFromRealm()
            val obj = realm.createObject<SettingCard>()
            obj.csv = csv
            obj.check = check
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        csv = parent?.getItemAtPosition(pos).toString()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }
}