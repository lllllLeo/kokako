package com.example.kokako.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.kokako.model.Word

 /*
 WordDTO 데이터를 데이터베이스에 넣고 빼고 조회하는 Data Access Object
 UI를 위한 데이터를 준비하는 역할

 * */

@Dao
interface WordDAO {

    @Insert
    fun insert(word: Word)

    @Query("INSERT INTO tb_word (id, word, mean, wordBookId) VALUES (:id, :word, :mean, :wordBookId)")
    fun insert2(id: Long, word: String, mean: String, wordBookId: Long)

    @Update
    fun update(word: Word)

    @Delete
    fun delete(word: Word)

    @Query("DELETE FROM tb_word")
    fun deleteAll()
    @Query("SELECT * FROM tb_word")
    fun getAll(): LiveData<List<Word>>

}