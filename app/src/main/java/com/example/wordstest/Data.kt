package com.example.wordstest

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Data : RealmObject() {
    var id: Int = 0
    var question: String = ""
    var annotation: String = ""
    var correct: String = ""
    var answer1: String = ""
    var answer2: String = ""
    var answer3: String = ""
}