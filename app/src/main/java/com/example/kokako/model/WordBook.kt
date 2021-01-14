package com.example.kokako.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "tb_word_book")
data class WordBook(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="word_book_id")
    val id: Int,
    var title : String?,
    var count : Int,
    var addTime : Long  // 안드로이드에서는 Date로 변환
)
/*
{
//    만약 DB로 해당 컬럼 값에 null이 insert 될 경우 기본값
    constructor() : this(0,"",0,0) //default값
}*/
