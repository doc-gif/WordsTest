package com.example.wordstest

import io.realm.RealmObject

open class Correct: RealmObject() {
    var id = 0
    var howManyCorrect = 0
}