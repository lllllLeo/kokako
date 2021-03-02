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

    @Query("DELETE FROM tb_word WHERE wordBookId = :wordBookIdForView")
    fun deleteWordById(wordBookIdForView: Array<out Long?>)

    @Query("SELECT * FROM tb_word")
    fun getAll(): LiveData<List<Word>>

    @Query("SELECT * FROM tb_word WHERE wordBookId = :wordBookIdForView")
    fun getWordFromWordBook(wordBookIdForView: Array<out Long?>): LiveData<List<Word>>

    @Query("SELECT * FROM tb_word WHERE wordBookId = :wordBookIdForView ORDER BY word ASC")
    fun getWordFromWordBook333(wordBookIdForView: Array<out Long?>): LiveData<List<Word>>







    @Query("SELECT * FROM tb_word WHERE wordBookId = :wordBookIdForView ")
    fun getWordRecentOrder(wordBookIdForView: Array<out Long?>): List<Word>

//    @Query("SELECT word, mean, option FROM tb_word WHERE wordBookId = :wordBookIdForView ")
//    fun getWordForCSV(wordBookIdForView: Array<out Long?>): List<Word>

    @Query("SELECT * FROM tb_word WHERE wordBookId = :wordBookIdForView AND bookMarkCheck = 1")
    fun getWordFavoriteOrder(wordBookIdForView: Array<out Long?>): List<Word>

    @Query("SELECT * FROM tb_word WHERE wordBookId = :wordBookIdForView ORDER BY word ASC")
    fun getWordAscendingOrder(wordBookIdForView: Array<out Long?>): List<Word>

    @Query("SELECT * FROM tb_word WHERE wordBookId = :wordBookIdForView ORDER BY word DESC")
    fun getWordDescendingOrder(wordBookIdForView: Array<out Long?>): List<Word>

    @Query("SELECT * FROM tb_word WHERE wordBookId = :wordBookIdForView ORDER BY mean ASC")
    fun getMeanAscendingOrder(wordBookIdForView: Array<out Long?>): List<Word>

    @Query("SELECT * FROM tb_word WHERE wordBookId = :wordBookIdForView ORDER BY mean DESC")
    fun getMeanDescendingOrder(wordBookIdForView: Array<out Long?>): List<Word>

    @Query("SELECT * FROM tb_word WHERE wordBookId = :wordBookIdForView ORDER BY RANDOM() LIMIT (SELECT count(word) FROM tb_word)")
    fun getRandomOrder(wordBookIdForView: Array<out Long?>): List<Word>
}

/*
*     @Query("SELECT * FROM tb_word WHERE wordBookId = :wordBookIdForView ORDER BY word ASC")
    fun getWordAscendingOrder(wordBookIdForView: Array<out Long?>): List<Word>
* */