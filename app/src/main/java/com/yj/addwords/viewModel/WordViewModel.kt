package com.yj.addwords.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.os.AsyncTask
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.yj.addwords.dao.WordDAO
import com.yj.addwords.database.WordDatabase
import com.yj.addwords.model.Word

//  Log.d("     TAG", "===== WordViewModel")
class WordViewModel(application: Application, wordBookId: Long) : AndroidViewModel(application) {
    var wordListLivedata : LiveData<List<Word>>
    var wordArrayList : List<Word>? = null
    private var wordDao : WordDAO
    companion object { const val TAG = "TAG WordViewModel" }
    init {
        Log.d("     TAG", "===== WordViewModel - init called")
        val db : WordDatabase = Room.databaseBuilder(application, WordDatabase::class.java, "word").build()
        wordDao = db.getWordDAO()
        if (!wordBookId.equals(-1)) { // getWordFromWordBook나 getall 똑같은듯
            wordListLivedata = wordDao.getWordFromWordBook(arrayOf(wordBookId))
            Log.d("     TAG", "===== WordViewModel - init - if ### wordBookId : $wordBookId")
        }else{
            wordListLivedata = wordDao.getAll()
        }
    }
    fun insertAllDatas(word : ArrayList<Word>) {
        InsertAllWordAsyncTask().execute(word)
    }
    fun insert(word: Word) {
        InsertWordAsyncTask().execute(word)
    }
    fun update(word: Word) {
        UpdateWordMeanAsyncTask().execute(word)
    }
    fun updateFavoriteChecked(word: Word) {
        UpdateFavoriteCheckedAsyncTask().execute(word)
    }
    fun delete(word: Word) {
        DeleteWordAsyncTask().execute(word)
    }
    fun deleteWordById(wordBookIdForView : Long) {
        DeleteAllWordAsyncTask().execute(wordBookIdForView)
    }
    fun getWordFromWordBook333(wordBookIdForView: Long): LiveData<List<Word>> {
        wordListLivedata = GetWordFromWordBook333AsyncTask().execute(wordBookIdForView).get()
        return wordListLivedata
    }
    fun getLatestOrder(wordBookIdForView: Long): ArrayList<Word> {
        wordArrayList = GetWordLatestOrderAsyncTask().execute(wordBookIdForView).get()
        return wordArrayList as ArrayList<Word>
    }
    fun getOldestOrder(wordBookIdForView: Long): ArrayList<Word> {
        wordArrayList = GetWordOldestOrderAsyncTask().execute(wordBookIdForView).get()
        return wordArrayList as ArrayList<Word>
    }




    fun getWordFavoriteOrder(wordBookIdForView: Long): ArrayList<Word>{
        wordArrayList = GetWordFavoriteOrderAsyncTask().execute(wordBookIdForView).get()
        return wordArrayList as ArrayList<Word>
    }
    fun getWordAscendingOrder(wordBookIdForView: Long): ArrayList<Word>{
        wordArrayList = GetWordAscendingOrderAsyncTask().execute(wordBookIdForView).get()
        return wordArrayList as ArrayList<Word>
    }
    fun getWordDescendingOrder(wordBookIdForView: Long): ArrayList<Word>{
        wordArrayList = GetWordDescendingOrderAsyncTask().execute(wordBookIdForView).get()
        return wordArrayList as ArrayList<Word>
    }
    fun getMeanAscendingOrder(wordBookIdForView: Long): ArrayList<Word>{
        wordArrayList = GetMeanAscendingOrderAsyncTask().execute(wordBookIdForView).get()
        return wordArrayList as ArrayList<Word>
    }
    fun getMeanDescendingOrder(wordBookIdForView: Long): ArrayList<Word>{
        wordArrayList = GetMeanDescendingOrderAsyncTask().execute(wordBookIdForView).get()
        return wordArrayList as ArrayList<Word>
    }
    fun getRandomOrder(wordBookIdForView: Long): ArrayList<Word>{
        wordArrayList = GetRandomOrderAsyncTask().execute(wordBookIdForView).get()
        return wordArrayList as ArrayList<Word>
    }






    fun getTestAllWordLatest(wordBookIdForTest: Long): ArrayList<Word>{
        wordArrayList = GetTestAllWordLatestAsyncTask().execute(wordBookIdForTest).get()
        return wordArrayList as ArrayList<Word>
    }
    fun getTestAllWordOldest(wordBookIdForTest: Long): ArrayList<Word>{
        wordArrayList = GetTestAllWordOldestAsyncTask().execute(wordBookIdForTest).get()
        return wordArrayList as ArrayList<Word>
    }
    fun getTestAllWordWordAscOrder(wordBookIdForTest: Long): ArrayList<Word> {
        wordArrayList = GetTestAllWordWordAscOrderAsyncTask().execute(wordBookIdForTest).get()
        return wordArrayList as ArrayList<Word>
    }

    fun getTestAllWordRandomOrder(wordBookIdForTest: Long): ArrayList<Word> {
        wordArrayList = GetTestAllWordRandomOrderAsyncTask().execute(wordBookIdForTest).get()
        return wordArrayList as ArrayList<Word>
    }



    fun getTestBookmarkAllWordLatestOrder(wordBookIdForTest: Long, testScope: String): ArrayList<Word>{
        val data = ArrayList<String>()
        data.add(wordBookIdForTest.toString())
        data.add(testScope)
        wordArrayList = GetTestBookmarkAllWordLatestOrderAsyncTask().execute(data).get()
        return wordArrayList as ArrayList<Word>
    }

    fun getTestBookmarkAllWordOldestOrder(wordBookIdForTest: Long, testScope: String): ArrayList<Word>{
        val data = ArrayList<String>()
        data.add(wordBookIdForTest.toString())
        data.add(testScope)
        wordArrayList = GetTestBookmarkAllWordOldestOrderAsyncTask().execute(data).get()
        return wordArrayList as ArrayList<Word>
    }

    fun getTestBookmarkAllWordWordAscOrder(wordBookIdForTest: Long, testScope: String): ArrayList<Word> {
        val data = ArrayList<String>()
        data.add(wordBookIdForTest.toString())
        data.add(testScope)
        wordArrayList = GetTestBookmarkAllWordWordAscOrderAsyncTask().execute(data).get()
        return wordArrayList as ArrayList<Word>
    }
    fun getTestBookmarkAllWordRandomOrder(wordBookIdForTest: Long, testScope: String): ArrayList<Word> {
        val data = ArrayList<String>()
        data.add(wordBookIdForTest.toString())
        data.add(testScope)
        wordArrayList = GetTestBookmarkAllWordRandomOrderAsyncTask().execute(data).get()
        return wordArrayList as ArrayList<Word>
    }


    @SuppressLint("StaticFieldLeak")
    private inner class GetTestAllWordLatestAsyncTask : AsyncTask<Long, Void, List<Word>>(){
        override fun doInBackground(vararg wordBookIdForTest: Long?): List<Word> {
            return wordDao.getTestAllWordLatest(wordBookIdForTest[0]!!)
        }
    }
    @SuppressLint("StaticFieldLeak")
    private inner class GetTestAllWordOldestAsyncTask : AsyncTask<Long, Void, List<Word>>(){
        override fun doInBackground(vararg wordBookIdForTest: Long?): List<Word> {
            return wordDao.getTestAllWordOldest(wordBookIdForTest[0]!!)
        }
    }
    @SuppressLint("StaticFieldLeak")
    private inner class GetTestAllWordWordAscOrderAsyncTask : AsyncTask<Long, Void, List<Word>>(){
        override fun doInBackground(vararg wordBookIdForTest: Long?): List<Word> {
            return wordDao.getTestAllWordWordAscOrder(wordBookIdForTest[0]!!)
        }
    }
    @SuppressLint("StaticFieldLeak")
    private inner class GetTestAllWordRandomOrderAsyncTask : AsyncTask<Long, Void, List<Word>>(){
        override fun doInBackground(vararg wordBookIdForTest: Long?): List<Word> {
            return wordDao.getTestAllWordRandomOrder(wordBookIdForTest[0]!!)
        }
    }
    @SuppressLint("StaticFieldLeak")
    private inner class GetTestBookmarkAllWordLatestOrderAsyncTask : AsyncTask<ArrayList<String>, Void, List<Word>>(){
        override fun doInBackground(vararg data: ArrayList<String>?): List<Word> {
            val wordBookId : Long = data[0]!![0].toLong()
            val testScope : Long = data[0]!![1].toLong()
            Log.d(TAG, "doInBackground: $wordBookId / $testScope")
            return wordDao.getBookmarkAllWordLatestOrder(wordBookId, testScope)
        }
    }
    @SuppressLint("StaticFieldLeak")
    private inner class GetTestBookmarkAllWordOldestOrderAsyncTask : AsyncTask<ArrayList<String>, Void, List<Word>>(){
        override fun doInBackground(vararg data: ArrayList<String>?): List<Word> {
            val wordBookId : Long = data[0]!![0].toLong()
            val testScope : Long = data[0]!![1].toLong()
            Log.d(TAG, "doInBackground: $wordBookId / $testScope")
            return wordDao.getBookmarkAllWordOldestOrder(wordBookId, testScope)
        }
    }
    @SuppressLint("StaticFieldLeak")
    private inner class GetTestBookmarkAllWordWordAscOrderAsyncTask : AsyncTask<ArrayList<String>, Void, List<Word>>(){
        override fun doInBackground(vararg data: ArrayList<String>?): List<Word> {
            val wordBookId : Long = data[0]!![0].toLong()
            val testScope : Long = data[0]!![1].toLong()
            Log.d(TAG, "doInBackground: $wordBookId / $testScope")
            return wordDao.getBookmarkAllWordWordAscOrder(wordBookId, testScope)
        }
    }
    @SuppressLint("StaticFieldLeak")
    private inner class GetTestBookmarkAllWordRandomOrderAsyncTask : AsyncTask<ArrayList<String>, Void, List<Word>>(){
        override fun doInBackground(vararg data: ArrayList<String>?): List<Word> {
            val wordBookId : Long = data[0]!![0].toLong()
            val testScope : Long = data[0]!![1].toLong()
            Log.d(TAG, "doInBackground: $wordBookId / $testScope")
            return wordDao.getBookmarkAllWordRandomOrder(wordBookId, testScope)
        }
    }






    @SuppressLint("StaticFieldLeak")
    private inner class UpdateWordMeanAsyncTask : AsyncTask<Word, Void, Void>(){
        override fun doInBackground(vararg word: Word?): Void? {
            wordDao.update(word[0]!!)
            return null
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class UpdateFavoriteCheckedAsyncTask : AsyncTask<Word, Void, Void>(){
        override fun doInBackground(vararg word: Word?): Void? {
            wordDao.update(word[0]!!)
            return null
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class GetWordFavoriteOrderAsyncTask : AsyncTask<Long, Void, List<Word>>(){
        override fun doInBackground(vararg wordBookIdForView: Long?): List<Word> {
            return wordDao.getWordFavoriteOrder(wordBookIdForView)
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class GetWordAscendingOrderAsyncTask : AsyncTask<Long, Void, List<Word>>(){
        override fun doInBackground(vararg wordBookIdForView: Long?): List<Word> {
            return wordDao.getWordAscendingOrder(wordBookIdForView)
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class GetWordDescendingOrderAsyncTask : AsyncTask<Long, Void, List<Word>>(){
        override fun doInBackground(vararg wordBookIdForView: Long?): List<Word> {
            return wordDao.getWordDescendingOrder(wordBookIdForView)
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class GetMeanAscendingOrderAsyncTask : AsyncTask<Long, Void, List<Word>>(){
        override fun doInBackground(vararg wordBookIdForView: Long?): List<Word> {
            return wordDao.getMeanAscendingOrder(wordBookIdForView)
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class GetMeanDescendingOrderAsyncTask : AsyncTask<Long, Void, List<Word>>(){
        override fun doInBackground(vararg wordBookIdForView: Long?): List<Word> {
            return wordDao.getMeanDescendingOrder(wordBookIdForView)
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class GetWordLatestOrderAsyncTask : AsyncTask<Long, Void, List<Word>>(){
        override fun doInBackground(vararg wordBookIdForView: Long?): List<Word> {
            return wordDao.getWordLatestOrder(wordBookIdForView)
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class GetWordOldestOrderAsyncTask : AsyncTask<Long, Void, List<Word>>(){
        override fun doInBackground(vararg wordBookIdForView: Long?): List<Word> {
            return wordDao.getWordOldestOrder(wordBookIdForView)
        }
    }






    @SuppressLint("StaticFieldLeak")
    private inner class GetRandomOrderAsyncTask : AsyncTask<Long, Void, List<Word>>(){
        override fun doInBackground(vararg wordBookIdForView: Long?): List<Word> {
            return wordDao.getRandomOrder(wordBookIdForView)
        }
    }


    @SuppressLint("StaticFieldLeak")
    private inner class InsertAllWordAsyncTask : AsyncTask<ArrayList<Word>, Void, Void>(){
        override fun doInBackground(vararg words: ArrayList<Word>): Void? {
            val word : ArrayList<Word> = words[0]
            wordDao.insertAllDatas(word)
            return null
        }
    }
    @SuppressLint("StaticFieldLeak")
    private inner class GetWordFromWordBook333AsyncTask : AsyncTask<Long, Void, LiveData<List<Word>>>(){
        override fun doInBackground(vararg wordBookIdForView: Long?): LiveData<List<Word>> {
            return wordDao.getWordFromWordBook333(wordBookIdForView)
        }

    }

    @SuppressLint("StaticFieldLeak")
    private inner class DeleteWordAsyncTask : AsyncTask<Word, Void, Void>() {
        override fun doInBackground(vararg word: Word?): Void? {
            wordDao.delete(word[0]!!)
            return null
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class InsertWordAsyncTask : AsyncTask<Word, Void, Void>() {
        override fun doInBackground(vararg word: Word?): Void? {
            wordDao.insert(word[0]!!)
            return null
        }
    }
    @SuppressLint("StaticFieldLeak")
    private inner class DeleteAllWordAsyncTask(): AsyncTask<Long, Void, Void>() {
        override fun doInBackground(vararg wordBookIdForView: Long?): Void? {
            wordDao.deleteWordById(wordBookIdForView)
            return null
        }
    }
}

