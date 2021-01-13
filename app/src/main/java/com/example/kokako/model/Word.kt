package com.example.kokako.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(primaryKeys = ["word", "mean"], tableName = "tb_word")
data class Word(
//    @PrimaryKey
    var word: String = "",
    var mean: String = ""
)