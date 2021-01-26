package com.example.kokako.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.kokako.model.WordBook


/*
    val id: Long,
    var title : String?,
    var count : Int,
    var addTime : Long
* */
@Dao
interface WordBookDAO {

    @Insert
    fun insert(wordBook: WordBook):Long

    @Update
    fun update(wordBook: Array<out WordBook?>)

    @Delete
    fun delete(wordBook: WordBook)

    @Query("DELETE FROM tb_word_book WHERE id = :wordBookIdForView")
    fun deleteWordBookById(wordBookIdForView: Long)

    @Query("SELECT * FROM tb_word_book")
//    @Query("SELECT id, title, (SELECT count(word) FROM tb_word WHERE wordBookId = tb_word_book.id) AS count, addTime FROM tb_word_book")
    fun getAll(): LiveData<List<WordBook>>

//    @Update
//    fun updateWordBookName(wordBookDatas: Array<out WordBook?>)
    @Query("UPDATE tb_word_book SET count = (SELECT count(word) FROM tb_word WHERE wordBookId = :updateWordBookMain) WHERE id = :updateWordBookMain")
    fun updateWordBookCount(updateWordBookMain: Array<out Long?>)

}

