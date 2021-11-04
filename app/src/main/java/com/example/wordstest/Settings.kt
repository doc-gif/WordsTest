package com.example.wordstest

import io.realm.RealmObject

open class Settings: RealmObject() {
    var howMany: Int = 0
    var howManyCorrect = 0
    var check = 0
    var csv = ""
}