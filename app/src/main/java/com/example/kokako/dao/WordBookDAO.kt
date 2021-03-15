package com.example.kokako.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.kokako.model.Word
import com.example.kokako.model.WordBook


/*
    val id: Long,
    var title : String?,
    var count : Int,
    var addTime : Long
    var order : Int
* */
@Dao
interface WordBookDAO {

//    @Insert
//    fun insert(wordBook: WordBook):Long

    @Query("INSERT INTO tb_word_book (title, count, itemOrder, language)VALUES (:title, :count, :itemOrder, :language)")
    fun insert(title: String?, count: Int, itemOrder: Int, language: Int):Long

    @Update
    fun update(wordBook: Array<out WordBook?>)

    @Delete
    fun delete(wordBook: WordBook)

    @Query("DELETE FROM tb_word_book WHERE id = :wordBookIdForView")
    fun deleteWordBookById(wordBookIdForView: Long)

    @Query("SELECT * FROM tb_word_book ORDER BY itemOrder ASC")
    fun getAll(): LiveData<List<WordBook>>

    @Query("SELECT * FROM tb_word_book ORDER BY itemOrder ASC")
    fun getRecentOrder(): List<WordBook>










    @Query("SELECT MAX(itemOrder) FROM tb_word_book")
    fun getMaxOrder(): Int

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateAll(wordBook: ArrayList<WordBook>)

    @Query("UPDATE tb_word_book SET count = (SELECT count(word) FROM tb_word WHERE wordBookId = :updateWordBookMain) WHERE id = :updateWordBookMain")
    fun updateWordBookCount(updateWordBookMain: Array<out Long?>)

    @Query("SELECT language FROM tb_word_book WHERE id = :wordBookIdForView")
    fun getLanguageCode(wordBookIdForView: Array<out Long?>): Int

}

