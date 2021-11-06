package com.example.wordstest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wordstest.databinding.ActivityTestScreenBinding
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import org.apache.commons.csv.CSVFormat
import java.io.BufferedReader
import java.io.InputStreamReader

class TestScreen : AppCompatActivity() {
    private lateinit var binding: ActivityTestScreenBinding
    private lateinit var realm: Realm

    private var dataList: MutableList<Data> = mutableListOf()
    private var numList: MutableList<Int> = mutableListOf()
    private var ansIndex: MutableList<Int> = mutableListOf()
    private var current = 0
    private var total = 0
    private var ans1Index = 0
    private var ans2Index = 0
    private var ans3Index = 0
    private var ans4Index = 0
    private var correctAns = ""
    private var question = ""
    private var annotation = ""
    private var num = 0
    private var intentFlag = 0
    private var howManyCorrect = 0
    private var answered = false
    private var checked = false
    private var check = 0
    private var csv = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        realm = Realm.getDefaultInstance()

        realm.executeTransaction {
            val target = realm.where<QuestionInfo>().findAll()
            target.deleteAllFromRealm()
            val target2 = realm.where<Correct>().findAll()
            target2.deleteAllFromRealm()
        }

        createData()

        check = realm.where<Settings>().findFirst()!!.check

        dataList = realm.where<Data>().findAll()
        for (i in 0 until dataList.size) numList.add(i)
        adaptCheck()
        makeQuestion()

        current ++
        val howMany = realm.where<Settings>().findFirst()?.howMany!!
        total = when (howMany) {
            99 -> {
                realm.executeTransaction {
                    val target = realm.where<Settings>().findAll()
                    target.deleteAllFromRealm()
                    val obj = realm.createObject<Settings>()
                    obj.howMany = numList.size
                }
                numList.size
            }
            else -> if (howMany <= numList.size) howMany else {
                Toast.makeText(applicationContext, "問題が${howMany}問より少ないので${numList.size}問出題します。", Toast.LENGTH_SHORT).show()
                realm.executeTransaction {
                    val target = realm.where<Settings>().findAll()
                    target.deleteAllFromRealm()
                    val obj = realm.createObject<Settings>()
                    obj.howMany = numList.size
                }
                numList.size
            }
        }
        val checkR = realm.where<Check>()
            .equalTo("kind", csv)
            .equalTo("id", num + 1)
            .findFirst()
        checked = checkR != null

        val checkList = realm.where<Check>()
            .equalTo("kind", csv)
            .findAll()
        Log.e("tag", "$checkList")

        if (checked)
            binding.check.setImageResource(R.drawable.ic_baseline_check_circle_24)

        Log.e("total", "${realm.where<Settings>().findFirst()}")

        clearACS()

        binding.current.text = current.toString()
        binding.total.text = total.toString()

        binding.next.visibility = View.INVISIBLE

        binding.answer1.setOnClickListener { select(it as Button) }
        binding.answer2.setOnClickListener { select(it as Button) }
        binding.answer3.setOnClickListener { select(it as Button) }
        binding.answer4.setOnClickListener { select(it as Button) }
        binding.check.setOnClickListener {
            checked = !checked
            if (checked) {
                binding.check.setImageResource(R.drawable.ic_baseline_check_circle_24)
                Toast.makeText(applicationContext, "チェックしました。", Toast.LENGTH_SHORT).show()
            }
            else
                binding.check.setImageResource(R.drawable.ic_baseline_check_circle_outline_24)
        }
        binding.next.setOnClickListener { next() }
    }

    private fun createData() {
        csv = realm.where<Settings>().findFirst()?.csv.toString()
        val reader = BufferedReader(InputStreamReader(resources.assets.open(csv)))
        reader.use {
            val records = CSVFormat.EXCEL.parse(reader)
            realm.beginTransaction()
            val target = realm.where<Data>().findAll()
            target.deleteAllFromRealm()
            records.records.forEach { record ->
                val obj = realm.createObject<Data>()
                obj.id = record[0].toInt()
                obj.question = record[1]
                obj.annotation = record[2]
                obj.correct = record[3]
                obj.answer1 = record[4]
                obj.answer2 = record[5]
                obj.answer3 = record[6]
            }
            realm.commitTransaction()
        }
    }

    private fun makeQuestion() {
        for (i in 1..4) ansIndex.add(i)
        num = numList.random()
        question = dataList[num].question
        annotation = dataList[num].annotation
        ans1Index = chooseAnsIndex()
        ans2Index = chooseAnsIndex()
        ans3Index = chooseAnsIndex()
        ans4Index = chooseAnsIndex()
        binding.answer1.text = chooseAns(ans1Index)
        binding.answer2.text = chooseAns(ans2Index)
        binding.answer3.text = chooseAns(ans3Index)
        binding.answer4.text = chooseAns(ans4Index)
        binding.question.text = question
        binding.annotation.text = annotation
        correctAns = dataList[num].correct
    }

    private fun adaptCheck() {
        val checkList = realm.where<Check>()
            .equalTo("kind", csv)
            .findAll()
        if (checkList.isEmpty() || check == 3) return
        Log.e("adapt", "here")
        Log.e("adapt", "$checkList")
        if (check == 1) {
            numList.clear()
            for (i in 0 until checkList.size) {
                Log.e("adapt", "${dataList[checkList[i]!!.id - 1]}")
                numList.add(checkList[i]!!.id - 1)
            }
            Log.e("numL", "$numList")
        }
    }

    private fun chooseAnsIndex(): Int {
        val index = ansIndex.random()
        ansIndex.remove(index)
        return index
    }

    private fun chooseAns(index: Int): String {
        return when (index) {
            1 -> dataList[num].correct
            2 -> dataList[num].answer1
            3 -> dataList[num].answer2
            4 -> dataList[num].answer3
            else -> ""
        }
    }

    private fun setImg(img: Int) {
        binding.imageView.setImageResource(img)
    }

    private fun select(btn: Button) {
        if (!answered) {
            realm.executeTransaction {
                val obj = realm.createObject<QuestionInfo>()
                val result = realm.where<Correct>()
                    .equalTo("id", num)
                    .findFirst()
                obj.id = num
                if (btn.text == correctAns) {
                    setImg(R.drawable.ic_baseline_radio_button_unchecked_24)
                    howManyCorrect ++
                    obj.correct = true
                    if (result != null)
                        result.howManyCorrect = result.howManyCorrect + 1
                    else {
                        val new = realm.createObject<Correct>()
                        new.id = num
                        new.howManyCorrect = 1
                    }

                }
                else {
                    highlightAnswer(correctAns)
                    setImg(R.drawable.ic_baseline_close_24)
                    obj.correct = false
                    if (result != null) result.howManyCorrect = 0
                }
            }
            answered = true
            binding.next.visibility = View.VISIBLE
        }
    }

    private fun highlightAnswer(ans: String) {
        when (ans) {
            binding.answer1.text -> binding.acs1.setImageResource(R.drawable.ic_baseline_radio_button_unchecked_24)
            binding.answer2.text -> binding.acs2.setImageResource(R.drawable.ic_baseline_radio_button_unchecked_24)
            binding.answer3.text -> binding.acs3.setImageResource(R.drawable.ic_baseline_radio_button_unchecked_24)
            binding.answer4.text -> binding.acs4.setImageResource(R.drawable.ic_baseline_radio_button_unchecked_24)
        }
    }

    private fun clearACS() {
        binding.acs1.setImageResource(0)
        binding.acs2.setImageResource(0)
        binding.acs3.setImageResource(0)
        binding.acs4.setImageResource(0)
    }

    private fun check() {
        if (checked) {
            realm.executeTransaction {
                val target = realm.where<Check>()
                    .equalTo("kind", csv)
                    .equalTo("id", num + 1)
                    .findAll()
                target.deleteAllFromRealm()
                val obj = realm.createObject<Check>()
                obj.kind = csv
                obj.id = num + 1
            }
        } else {
            realm.executeTransaction {
                val target = realm.where<Check>()
                    .equalTo("kind", csv)
                    .equalTo("id", num + 1)
                    .findAll()
                target.deleteAllFromRealm()
            }
        }
    }

    private fun checkInitialize() {
        val checkResult = realm.where<Check>()
            .equalTo("kind", csv)
            .equalTo("id", num + 1)
            .findFirst()
        checked = checkResult != null

        binding.check.setImageResource(
            if (checked)
                R.drawable.ic_baseline_check_circle_24
            else
                R.drawable.ic_baseline_check_circle_outline_24
        )
    }

    private fun next() {
        if (intentFlag == 1) {
            check()
            realm.executeTransaction {
                val result = realm.where<Settings>().findFirst()
                result?.howManyCorrect = howManyCorrect
            }
            val intent = Intent(this, Result::class.java)
            startActivity(intent)
        } else {
            check()
            answered = false
            binding.next.visibility = View.INVISIBLE
            numList.remove(num)
            setImg(0)
            clearACS()
            current ++
            binding.current.text = current.toString()
            if (current == total) {
                binding.next.text = "終了"
                intentFlag = 1
            }
            makeQuestion()
            checkInitialize()
        }
    }
}