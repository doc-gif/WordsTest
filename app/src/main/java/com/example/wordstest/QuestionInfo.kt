package com.example.wordstest

import io.realm.RealmObject

open class QuestionInfo: RealmObject() {
    var id = 0
    var correct = false
}