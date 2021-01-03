package com.example.kokako.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.kokako.model.WordDTO


@Dao
interface WordDAO {
    @Query("SELECT * FROM tb_wordBook")
    fun getAll(): List<WordDTO>

    @Insert
    fun insertAll(vararg wordDAO: WordDAO)

    @Delete
    fun delete(wordDTO: WordDTO)
}