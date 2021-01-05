package com.example.kokako.dao

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    @Update
    fun update(word: Word)

    @Delete
    fun delete(word: Word)

    @Query("SELECT * FROM tb_wordBook")
    fun getAll(): LiveData<List<Word>>

//    @Query("SELECT position FROM tb_wordBook")
//    fun getItem(word: Word, position: Int): LiveData<List<Word>>
}