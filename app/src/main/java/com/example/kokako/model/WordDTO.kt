package com.example.kokako.model

import android.text.Editable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(primaryKeys = ["word", "mean"], tableName = "tb_wordBook")
data class WordDTO(
    var title: String? = null,
    var word: String? = null,
    var mean: String? = null,
    var wordCount: String? = null
)