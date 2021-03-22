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

    @Query("SELECT * FROM tb_word ORDER BY id DESC")
    fun getAll(): LiveData<List<Word>>

    @Query("SELECT * FROM tb_word WHERE wordBookId = :wordBookIdForView ORDER BY id DESC")
    fun getWordFromWordBook(wordBookIdForView: Array<out Long?>): LiveData<List<Word>>

    @Query("SELECT * FROM tb_word WHERE wordBookId = :wordBookIdForView ORDER BY word ASC")
    fun getWordFromWordBook333(wordBookIdForView: Array<out Long?>): LiveData<List<Word>>




// 모든 단어, word
    @Query("SELECT * FROM tb_word WHERE wordBookId = :wordBookId ORDER BY id DESC")
    fun getTestAllWordLatest(wordBookId: Long): List<Word>

    @Query("SELECT * FROM tb_word WHERE wordBookId = :wordBookId")
    fun getTestAllWordOldest(wordBookId: Long): List<Word>

    @Query("SELECT * FROM tb_word WHERE wordBookId = :wordBookId ORDER BY word")
    fun getTestAllWordWordAscOrder(wordBookId: Long): List<Word>

    @Query("SELECT * FROM tb_word WHERE wordBookId = :wordBookId ORDER BY RANDOM() LIMIT (SELECT count(word) FROM tb_word)")
    fun getTestAllWordRandomOrder(wordBookId: Long): List<Word>

// 북마크 안/한 단어, word
    @Query("SELECT * FROM tb_word WHERE wordBookId = :wordBookId AND bookMarkCheck = :testScope ORDER BY id DESC")
    fun getBookmarkAllWordLatestOrder(wordBookId: Long, testScope: Long): List<Word>

    @Query("SELECT * FROM tb_word WHERE wordBookId = :wordBookId AND bookMarkCheck = :testScope")
    fun getBookmarkAllWordOldestOrder(wordBookId: Long, testScope: Long): List<Word>

    @Query("SELECT * FROM tb_word WHERE wordBookId = :wordBookId AND bookMarkCheck = :testScope ORDER BY word")
    fun getBookmarkAllWordWordAscOrder(wordBookId: Long, testScope: Long): List<Word>

    @Query("SELECT * FROM tb_word WHERE wordBookId = :wordBookId AND bookMarkCheck = :testScope ORDER BY RANDOM() LIMIT (SELECT count(word) FROM tb_word)")
    fun getBookmarkAllWordRandomOrder(wordBookId: Long, testScope: Long): List<Word>

    /****/
// 모든 단어, mean
/*
    @Query("SELECT * FROM tb_word WHERE wordBookId = :wordBookId")
    fun getTestAllMean(wordBookId: Long): List<Word>

    @Query("SELECT * FROM tb_word WHERE wordBookId = :wordBookId ORDER BY mean")
    fun getTestAllMeanMeanAscOrder(wordBookId: Long): List<Word>

    @Query("SELECT * FROM tb_word WHERE wordBookId = :wordBookId ORDER BY RANDOM() LIMIT (SELECT count(mean) FROM tb_word)")
    fun getTestAllMeanRandomOrder(wordBookId: Long): List<Word>

// 북마크 안/한 단어, mean
    @Query("SELECT * FROM tb_word WHERE wordBookId = :wordBookId AND bookMarkCheck = :testScope")
    fun getBookmarkAllMeanRecentOrder(wordBookId: Long, testScope: Long): List<Word>

    @Query("SELECT * FROM tb_word WHERE wordBookId = :wordBookId AND bookMarkCheck = :testScope ORDER BY mean")
    fun getBookmarkAllMeanMeanAscOrder(wordBookId: Long, testScope: Long): List<Word>

    @Query("SELECT * FROM tb_word WHERE wordBookId = :wordBookId AND bookMarkCheck = :testScope ORDER BY RANDOM() LIMIT (SELECT count(mean) FROM tb_word)")
    fun getBookmarkAllMeanRandomOrder(wordBookId: Long, testScope: Long): List<Word>
*/






    @Query("SELECT * FROM tb_word WHERE wordBookId = :wordBookIdForView ORDER BY id DESC")
    fun getWordLatestOrder(wordBookIdForView: Array<out Long?>): List<Word>

    @Query("SELECT * FROM tb_word WHERE wordBookId = :wordBookIdForView ORDER BY id ASC")
    fun getWordOldestOrder(wordBookIdForView: Array<out Long?>): List<Word>

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