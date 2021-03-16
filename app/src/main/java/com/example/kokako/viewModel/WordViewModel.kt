package com.example.kokako.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.os.AsyncTask
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.example.kokako.dao.WordDAO
import com.example.kokako.database.WordDatabase
import com.example.kokako.model.Word

//  Log.d("     TAG", "===== WordViewModel")
class WordViewModel(application: Application, wordBookId: Long) : AndroidViewModel(application) {
    var wordListLivedata : LiveData<List<Word>>
    var wordArrayList : List<Word>? = null
    var wordListForCSV: List<Array<String>>? = null
    private var wordDao : WordDAO
    companion object { const val TAG = "TAG WordViewModel" }
    init {
        Log.d("     TAG", "===== WordViewModel - init called")
        val db : WordDatabase = Room.databaseBuilder(application, WordDatabase::class.java, "word").build()
        wordDao = db.getWordDAO()
        if (!wordBookId.equals(-1)) {
            wordListLivedata = wordDao.getWordFromWordBook(arrayOf(wordBookId))
            Log.d("     TAG", "===== WordViewModel - init - if ### wordBookId : $wordBookId")
        }else{
            wordListLivedata = wordDao.getAll()
            Log.d("     TAG", "===== WordViewModel - init - else ### wordBookId : $wordListLivedata")
            Log.d("     TAG", "===== WordViewModel - init - else ### wordBookId : ${wordListLivedata.value}")
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
        Log.d("     TAG", "===== WordViewModel - deleteWordById called")
        DeleteAllWordAsyncTask().execute(wordBookIdForView)
    }
    fun getWordFromWordBook333(wordBookIdForView: Long): LiveData<List<Word>> {
        wordListLivedata = GetWordFromWordBook333AsyncTask().execute(wordBookIdForView).get()
        Log.d("     TAG", "===== WordViewModel - getWordFromWordBook wordList : $wordListLivedata")
        Log.d("     TAG", "===== WordViewModel - getWordFromWordBook wordList : ${wordListLivedata.value}")
        return wordListLivedata
    }
    fun getRecentOrder(wordBookIdForView: Long): ArrayList<Word> {
        Log.d("     TAG", "===== WordViewModel - getWordFromWordBook222 wordBookIdForView 값은 : $wordBookIdForView")
        wordArrayList = GetWordRecentOrderAsyncTask().execute(wordBookIdForView).get()
        Log.d("     TAG", "===== WordViewModel - getWordFromWordBook222 wordArrayList 값은 : $wordArrayList")
        return wordArrayList as ArrayList<Word>
    }



//    fun getWordForCSV(wordBookIdForView: Long): List<Array<String>>? {
//        Log.d("     TAG", "===== WordViewModel - getWordFromWordBook222 wordBookIdForView 값은 : $wordBookIdForView")
//        wordListForCSV = GetWordForCSVAsyncTask().execute(wordBookIdForView).get()
//        Log.d("     TAG", "===== WordViewModel - getWordFromWordBook222 wordArrayList 값은 : $wordArrayList")
//        return wordListForCSV
//    }
    fun getWordFavoriteOrder(wordBookIdForView: Long): ArrayList<Word>{
        wordArrayList = GetWordFavoriteOrderAsyncTask().execute(wordBookIdForView).get()
        Log.d("     TAG", "===== WordViewModel - getWordAscendingOrder wordList 값은 : $wordListLivedata")
        return wordArrayList as ArrayList<Word>
    }
    fun getWordAscendingOrder(wordBookIdForView: Long): ArrayList<Word>{
        wordArrayList = GetWordAscendingOrderAsyncTask().execute(wordBookIdForView).get()
        Log.d("     TAG", "===== WordViewModel - getWordAscendingOrder wordList 값은 : $wordListLivedata")
        return wordArrayList as ArrayList<Word>
    }
    fun getWordDescendingOrder(wordBookIdForView: Long): ArrayList<Word>{
        wordArrayList = GetWordDescendingOrderAsyncTask().execute(wordBookIdForView).get()
        Log.d("     TAG", "===== WordViewModel - getWordAscendingOrder wordList 값은 : $wordListLivedata")
        return wordArrayList as ArrayList<Word>
    }
    fun getMeanAscendingOrder(wordBookIdForView: Long): ArrayList<Word>{
        wordArrayList = GetMeanAscendingOrderAsyncTask().execute(wordBookIdForView).get()
        Log.d("     TAG", "===== WordViewModel - getWordAscendingOrder wordList 값은 : $wordListLivedata")
        return wordArrayList as ArrayList<Word>
    }
    fun getMeanDescendingOrder(wordBookIdForView: Long): ArrayList<Word>{
        wordArrayList = GetMeanDescendingOrderAsyncTask().execute(wordBookIdForView).get()
        Log.d("     TAG", "===== WordViewModel - getWordAscendingOrder wordList 값은 : $wordListLivedata")
        return wordArrayList as ArrayList<Word>
    }
    fun getRandomOrder(wordBookIdForView: Long): ArrayList<Word>{
        wordArrayList = GetRandomOrderAsyncTask().execute(wordBookIdForView).get()
        Log.d("     TAG", "===== WordViewModel - getWordAscendingOrder wordList 값은 : $wordListLivedata")
        return wordArrayList as ArrayList<Word>
    }






    fun getTestAllWord(wordBookIdForTest: Long): ArrayList<Word>{
        wordArrayList = GetTestAllWordAsyncTask().execute(wordBookIdForTest).get()
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



    fun getTestBookmarkAllWordRecentOrder(wordBookIdForTest: Long, testScope: String): ArrayList<Word>{
        val data = ArrayList<String>()
        data.add(wordBookIdForTest.toString())
        data.add(testScope)
        Log.d(TAG, "getTestAllWord: ${data[0]}")
        Log.d(TAG, "getTestAllWord: ${data[1]}")
        wordArrayList = GetTestBookmarkAllWordRecentOrderAsyncTask().execute(data).get()
        return wordArrayList as ArrayList<Word>
    }
    fun getTestBookmarkAllWordWordAscOrder(wordBookIdForTest: Long, testScope: String): ArrayList<Word> {
        val data = ArrayList<String>()
        data.add(wordBookIdForTest.toString())
        data.add(testScope)
        Log.d(TAG, "getTestAllWord: ${data[0]}")
        Log.d(TAG, "getTestAllWord: ${data[1]}")
        wordArrayList = GetTestBookmarkAllWordWordAscOrderAsyncTask().execute(data).get()
        return wordArrayList as ArrayList<Word>
    }
    fun getTestBookmarkAllWordRandomOrder(wordBookIdForTest: Long, testScope: String): ArrayList<Word> {
        val data = ArrayList<String>()
        data.add(wordBookIdForTest.toString())
        data.add(testScope)
        Log.d(TAG, "getTestAllWord: ${data[0]}")
        Log.d(TAG, "getTestAllWord: ${data[1]}")
        wordArrayList = GetTestBookmarkAllWordRandomOrderAsyncTask().execute(data).get()
        return wordArrayList as ArrayList<Word>
    }



    @SuppressLint("StaticFieldLeak")
    private inner class GetTestAllWordAsyncTask : AsyncTask<Long, Void, List<Word>>(){
        override fun doInBackground(vararg wordBookIdForTest: Long?): List<Word> {
            return wordDao.getTestAllWord(wordBookIdForTest[0]!!)
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
    private inner class GetTestBookmarkAllWordRecentOrderAsyncTask : AsyncTask<ArrayList<String>, Void, List<Word>>(){
        override fun doInBackground(vararg data: ArrayList<String>?): List<Word> {
            val wordBookId : Long = data[0]!![0].toLong()
            val testScope : Long = data[0]!![1].toLong()
            Log.d(TAG, "doInBackground: $wordBookId / $testScope")
            return wordDao.getBookmarkAllWordRecentOrder(wordBookId, testScope)
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
//            Log.d("     TAG", "===== WordViewModel - UpdateStarCheckedAsyncTask - doInBackground called")
            wordDao.update(word[0]!!)
//            Log.d("     TAG", "===== WordViewModel - UpdateStarCheckedAsyncTask - word[0]!! ${word[0]!!}")
            return null
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class UpdateFavoriteCheckedAsyncTask : AsyncTask<Word, Void, Void>(){
        override fun doInBackground(vararg word: Word?): Void? {
//            Log.d("     TAG", "===== WordViewModel - UpdateStarCheckedAsyncTask - doInBackground called")
            wordDao.update(word[0]!!)
//            Log.d("     TAG", "===== WordViewModel - UpdateStarCheckedAsyncTask - word[0]!! ${word[0]!!}")
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
    private inner class GetWordRecentOrderAsyncTask : AsyncTask<Long, Void, List<Word>>(){
        override fun doInBackground(vararg wordBookIdForView: Long?): List<Word> {
            return wordDao.getWordRecentOrder(wordBookIdForView)
        }
    }







//    @SuppressLint("StaticFieldLeak")
//    private inner class GetWordForCSVAsyncTask : AsyncTask<Long, Void, List<Array<String>>?>() {
//        override fun doInBackground(vararg wordBookIdForView: Long?): List<Array<String>>? {
//            return wordDao.getWordForCSV(wordBookIdForView)
//        }
//    }







    @SuppressLint("StaticFieldLeak")
    private inner class GetRandomOrderAsyncTask : AsyncTask<Long, Void, List<Word>>(){
        override fun doInBackground(vararg wordBookIdForView: Long?): List<Word> {
            return wordDao.getRandomOrder(wordBookIdForView)
        }
    }


    @SuppressLint("StaticFieldLeak")
    private inner class InsertAllWordAsyncTask : AsyncTask<ArrayList<Word>, Void, Void>(){
        override fun doInBackground(vararg words: ArrayList<Word>): Void? {
            Log.d("     TAG", "===== WordViewModel - InsertAllWordAsyncTask - doInBackground - called")
            var word : ArrayList<Word> = words[0]
            Log.d("     TAG", "===== WordViewModel - InsertAllWordAsyncTask - doInBackground -  word 값은 : ${word.toString()}")
            wordDao.insertAllDatas(word)
            Log.d("     TAG", "===== WordViewModel - InsertAllWordAsyncTask - doInBackground - out")
            return null
        }
    }
    @SuppressLint("StaticFieldLeak")
    private inner class GetWordFromWordBook333AsyncTask : AsyncTask<Long, Void, LiveData<List<Word>>>(){
        override fun doInBackground(vararg wordBookIdForView: Long?): LiveData<List<Word>> {
            Log.d("     TAG", "===== WordViewModel - GetWordFromWordBookAsyncTask - doInBackground called")
            Log.d("     TAG", "===== WordViewModel - GetWordFromWordBookAsyncTask - doInBackground 값은 : ${wordBookIdForView[0]}")
            Log.d("     TAG", "===== WordViewModel - GetWordFromWordBookAsyncTask - doInBackground out")
            return wordDao.getWordFromWordBook333(wordBookIdForView)
        }

    }

    @SuppressLint("StaticFieldLeak")
    private inner class DeleteWordAsyncTask : AsyncTask<Word, Void, Void>() {
        override fun doInBackground(vararg word: Word?): Void? {
            Log.d("     TAG", "===== WordViewModel - DeleteWordAsyncTask - doInBackground called")
            Log.d("     TAG", "===== WordViewModel - DeleteWordAsyncTask - doInBackground ${word[0]!!}")
            wordDao.delete(word[0]!!)
            Log.d("     TAG", "===== WordViewModel - DeleteWordAsyncTask - doInBackground out")
            return null
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class InsertWordAsyncTask : AsyncTask<Word, Void, Void>() {
        override fun doInBackground(vararg word: Word?): Void? {
            Log.d("     TAG", "===== WordViewModel - InsertWordAsyncTask - doInBackground called")
            Log.d("     TAG", "===== WordViewModel - InsertWordAsyncTask - doInBackground word[0] : ${word[0]}")
            wordDao.insert(word[0]!!)
            Log.d("     TAG", "===== WordViewModel - InsertWordAsyncTask - doInBackground out")
            return null
        }
    }
    @SuppressLint("StaticFieldLeak")
    private inner class DeleteAllWordAsyncTask(): AsyncTask<Long, Void, Void>() {
        override fun doInBackground(vararg wordBookIdForView: Long?): Void? {
            Log.d("     TAG", "===== WordViewModel - DeleteAllWordAsyncTask - doInBackground - called")
            wordDao.deleteWordById(wordBookIdForView)
            Log.d("     TAG", "===== WordViewModel - DeleteAllWordAsyncTask - doInBackground - out")
            return null
        }
    }
}

