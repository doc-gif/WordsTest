package com.example.wordstest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wordstest.databinding.ActivityResultBinding
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where

class Result : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private lateinit var realm: Realm
    private lateinit var mAdapter: CustomAdapter
    private lateinit var mArray: ArrayList<RecResult>
    private var data: ArrayList<RecResult> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        realm = Realm.getDefaultInstance()

        val result = realm.where<QuestionInfo>().findAll()
        val dataResult = realm.where<Data>().findAll()
        for (i in 0 until result.size) {
            val imageView =
                if (result[i]!!.correct) R.drawable.ic_baseline_radio_button_unchecked_24
                else R.drawable.ic_baseline_close_24
            val question = dataResult[result[i]!!.id]!!.question
            val answer = dataResult[result[i]!!.id]!!.correct
            data.add(RecResult(imageView, question, answer))
        }
        mArray = data

        mAdapter = CustomAdapter(mArray)

        val recyclerView = binding.recyclerView

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = mAdapter

        binding.correct.text = realm.where<Settings>().findFirst()!!.howManyCorrect.toString()
        binding.total.text = realm.where<Settings>().findFirst()!!.howMany.toString()

        binding.check.setOnClickListener {
            AlertDialog.Builder(this)
                .setMessage("間違えた問題すべてにチェックしますか？")
                .setNegativeButton("キャンセル") { _, _ -> }
                .setPositiveButton("OK") { _, _ ->
                    checkAll()
                }
                .show()
        }
        binding.home.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun checkAll() {
        val result = realm.where<QuestionInfo>()
            .equalTo("correct", false)
            .sort("id")
            .findAll()
        realm.executeTransaction {
            for (i in 0 until result.size) {
                if (
                    realm.where<Check>()
                        .equalTo("kind", "")
                        .equalTo("id", result[i]!!.id + 1)
                        .findFirst()
                        == null
                ) {
                    val obj = realm.createObject<Check>()
                    obj.kind = ""
                    obj.id = result[i]!!.id + 1
                }
            }
        }
    }
}