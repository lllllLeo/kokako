package com.example.kokako.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(tableName = "tb_word",
    foreignKeys = [
        ForeignKey(entity = WordBook::class,
            parentColumns = ["id"],
            childColumns = ["wordBookId"],
            onDelete = CASCADE)
    ]
)
data class Word(
    @PrimaryKey(autoGenerate = true)
    var id: Long,
    var word: String,
    var mean: String,
    var wordBookId: Long
)

/*{
    constructor() : this(0,"Default word", "Default mean", 0)
}*/



//  primaryKeys = ["word", "mean"]
//  , foreignKeys = [ForeignKey(entity = WordBook::class, parentColumns = arrayOf("wordBookId"), childColumns = arrayOf("wordBookId_FK"), onDelete = CASCADE)]
