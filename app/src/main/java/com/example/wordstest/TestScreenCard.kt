package com.example.wordstest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.wordstest.databinding.ActivityTestScreenCardBinding
import io.realm.Realm
import io.realm.kotlin.where

class TestScreenCard : AppCompatActivity() {
    private lateinit var binding: ActivityTestScreenCardBinding
    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestScreenCardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        realm = Realm.getDefaultInstance()

        realm.executeTransaction {
            val target = realm.where<QuestionInfoCard>().findAll()
            target.deleteAllFromRealm()
            val target2 = realm.where<CorrectCard>().findAll()
            target2.deleteAllFromRealm()
        }

        createData()
    }

    private fun createData() {

    }
}