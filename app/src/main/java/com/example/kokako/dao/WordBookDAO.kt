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

    @Query("SELECT * FROM tb_word_book")
    fun getAll(): LiveData<List<WordBook>>
}