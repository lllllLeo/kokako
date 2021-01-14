package com.example.kokako.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.kokako.model.WordBook

@Dao
interface WordBookDAO {

    @Insert
    fun insert(wordBook: WordBook)

    @Update
    fun update(wordBook: WordBook)

    @Delete
    fun delete(wordBook: WordBook)

    @Query("DELETE FROM tb_word_book WHERE word_book_id = :wordBookId")
    fun deleteWordBookById(wordBookId: Int)

    @Query("SELECT * FROM tb_word_book")
    fun getAll(): LiveData<List<WordBook>>
}