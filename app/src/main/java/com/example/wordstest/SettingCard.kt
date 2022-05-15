package com.example.wordstest

import io.realm.RealmObject

open class SettingCard: RealmObject() {
    var howManyCorrect = 0
    var check = 0
    var csv = ""
}