package com.example.kokako.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(tableName = "tb_word",
    foreignKeys = [
        ForeignKey(entity = WordBook::class,
            parentColumns = arrayOf("word_book_id"),
            childColumns = arrayOf("word_book_id_fk"),
            onDelete = CASCADE)])
data class Word(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "word_id")
    var id: Int,
    var word: String,
    var mean: String,
    @ColumnInfo(name = "word_book_id_fk")
    var wordBookIdFK: Int
)

/*{
    constructor() : this(0,"Default word", "Default mean", 0)
}*/



//  primaryKeys = ["word", "mean"]
//  , foreignKeys = [ForeignKey(entity = WordBook::class, parentColumns = arrayOf("wordBookId"), childColumns = arrayOf("wordBookId_FK"), onDelete = CASCADE)]
