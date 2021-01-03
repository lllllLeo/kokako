package com.example.kokako.model

import androidx.room.Entity
import androidx.room.PrimaryKey

//@Entity(tableName = "tb_wordBook")
@Entity(primaryKeys = ["word", "mean"], tableName = "tb_wordBook")
data class Word(
//    var title: String? = null,
//    @PrimaryKey
    var word: String = "",
    var mean: String = ""
//    var wordCount: String? = null
)