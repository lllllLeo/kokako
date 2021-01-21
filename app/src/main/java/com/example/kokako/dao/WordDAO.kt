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

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAllDatas(word: ArrayList<Word>)

    @Update
    fun update(word: Word)

    @Delete
    fun delete(word: Word)

    @Query("DELETE FROM tb_word")
    fun deleteAll()

    @Query("SELECT * FROM tb_word")
    fun getAll(): LiveData<List<Word>>

    @Query("SELECT * FROM tb_word WHERE wordBookId = :wordBookIdForView")
    fun getWordFromWordBook(wordBookIdForView: Array<out Long?>): LiveData<List<Word>>

    @Query("SELECT * FROM tb_word WHERE wordBookId = :wordBookIdForView")
    fun getWordFromWordBook222(wordBookIdForView: Array<out Long?>): List<Word>

}