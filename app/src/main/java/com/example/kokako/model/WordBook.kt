package com.example.kokako.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "tb_word_book")
data class WordBook(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    var title : String?,
    var count : Int,
    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    var addTime : Long,
    var itemOrder : Int,
    var language : Int
) : Comparable<WordBook> {
    override fun compareTo(other: WordBook): Int {  // 리스트 정렬을 위함
        return this.itemOrder.compareTo(other.itemOrder)
    }
}


/*
{
//    만약 DB로 해당 컬럼 값에 null이 insert 될 경우 기본값
    constructor() : this(0,"",0,0) //default값
}*/
