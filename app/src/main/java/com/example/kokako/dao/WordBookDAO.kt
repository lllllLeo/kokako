package com.example.kokako.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.kokako.model.WordBook

@Dao
interface WordBookDAO {

    @Insert
    fun insert(wordBook: WordBook):Long

    @Update
    fun update(wordBook: WordBook)

    @Delete
    fun delete(wordBook: WordBook)

    @Query("DELETE FROM tb_word_book WHERE id = :wordBookIdForView")
    fun deleteWordBookById(wordBookIdForView: Long)

//    @Query("SELECT * FROM tb_word_book")
    @Query("SELECT id, title, (SELECT count(word) FROM tb_word WHERE wordBookId = tb_word_book.id) AS count, addTime FROM tb_word_book")
    fun getAll(): LiveData<List<WordBook>>

}