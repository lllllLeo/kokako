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
    var wordList : LiveData<List<Word>>
    var wordArrayList : List<Word>? = null
    private var wordDao : WordDAO
    companion object {
        val                     TAG = "TAG WordViewModel"
    }
    init {
        Log.d("     TAG", "===== WordViewModel - init called")
        val db : WordDatabase = Room.databaseBuilder(application, WordDatabase::class.java, "word").build()
        wordDao = db.getWordDAO()
        if (!wordBookId.equals(-1)) {
            wordList = wordDao.getWordFromWordBook(arrayOf(wordBookId))
            Log.d("     TAG", "===== WordViewModel - init - if ### wordBookId : $wordBookId")
        }else{
            wordList = wordDao.getAll()
            Log.d("     TAG", "===== WordViewModel - init - else ### wordBookId : $wordList")
            Log.d("     TAG", "===== WordViewModel - init - else ### wordBookId : ${wordList.value}")
        }
    }
    fun insertAllDatas(word : ArrayList<Word>) {
        Log.d(TAG, "insertAllDatas: 여기")
        InsertAllWordAsyncTask().execute(word)
    }
    fun insert(word: Word) {
        InsertWordAsyncTask().execute(word)
    }
    fun delete(word: Word) {
        DeleteWordAsyncTask().execute(word)
    }
    fun deleteWordById(wordBookIdForView : Long) {
        Log.d("     TAG", "===== WordViewModel - deleteWordById called")
        DeleteAllWordAsyncTask().execute(wordBookIdForView)
    }
    fun getWordFromWordBook(wordBookIdForView: Long): LiveData<List<Word>> {
        wordList = GetWordFromWordBookAsyncTask().execute(wordBookIdForView).get()
        Log.d("     TAG", "===== WordViewModel - getWordFromWordBook wordList : $wordList")
        Log.d("     TAG", "===== WordViewModel - getWordFromWordBook wordList : ${wordList.value}")
        return wordList
    }
    fun getWordFromWordBook222(wordBookIdForView: Long): ArrayList<Word> {
        Log.d("     TAG", "===== WordViewModel - getWordFromWordBook222 wordBookIdForView 값은 : $wordBookIdForView")
        wordArrayList = GetWordFromWordBookAsyncTask222().execute(wordBookIdForView).get()
        Log.d("     TAG", "===== WordViewModel - getWordFromWordBook222 wordArrayList 값은 : $wordArrayList")
        return wordArrayList as ArrayList<Word>
    }

    fun getWordAscendingOrder(wordBookIdForView: Long): LiveData<List<Word>>{
        wordList = GetWordAscendingOrderAsyncTask().execute(wordBookIdForView).get()
        Log.d("     TAG", "===== WordViewModel - getWordAscendingOrder wordList 값은 : $wordList")
        return wordList
    }

    fun updateStarChecked(word: Word) {
        UpdateStarCheckedAsyncTask().execute(word)
    }

    fun update(word: Word) {
        UpdateWordMeanAsyncTask().execute(word)
    }

    @SuppressLint("StaticFieldLeak")
    private inner class UpdateWordMeanAsyncTask : AsyncTask<Word, Void, Void>(){
        override fun doInBackground(vararg word: Word?): Void? {
            Log.d("     TAG", "===== WordViewModel - UpdateStarCheckedAsyncTask - doInBackground called")
            wordDao.update(word[0]!!)
            Log.d("     TAG", "===== WordViewModel - UpdateStarCheckedAsyncTask - word[0]!! ${word[0]!!}")
            return null
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class UpdateStarCheckedAsyncTask : AsyncTask<Word, Void, Void>(){
        override fun doInBackground(vararg word: Word?): Void? {
            Log.d("     TAG", "===== WordViewModel - UpdateStarCheckedAsyncTask - doInBackground called")
            wordDao.update(word[0]!!)
            Log.d("     TAG", "===== WordViewModel - UpdateStarCheckedAsyncTask - word[0]!! ${word[0]!!}")
            return null
        }
    }

    // FIXME: 2021-01-25 오른차순 고치기
    @SuppressLint("StaticFieldLeak")
    private inner class GetWordAscendingOrderAsyncTask : AsyncTask<Long, Void, LiveData<List<Word>>>(){
        override fun doInBackground(vararg wordBookIdForView: Long?): LiveData<List<Word>> {
            Log.d("     TAG", "===== WordViewModel - GetWordAscendingOrderAsyncTask - doInBackground called")
            Log.d("     TAG", "===== WordViewModel - GetWordAscendingOrderAsyncTask - doInBackground out")
            var a = wordDao.getWordAscendingOrder(wordBookIdForView)
            Log.d("     TAG", "===== WordViewModel - GetWordAscendingOrderAsyncTask - doInBackground 값은 : ${a.value}")
            return a
        }

    }

    @SuppressLint("StaticFieldLeak")
    private inner class GetWordFromWordBookAsyncTask222 : AsyncTask<Long, Void, List<Word>>(){
        override fun doInBackground(vararg wordBookIdForView: Long?): List<Word> {
            Log.d("     TAG", "===== WordViewModel - GetWordFromWordBookAsyncTask - doInBackground called")
            Log.d("     TAG", "===== WordViewModel - GetWordFromWordBookAsyncTask - doInBackground 값은 : ${wordBookIdForView[0]}")
            Log.d("     TAG", "===== WordViewModel - GetWordFromWordBookAsyncTask - doInBackground out")
            return wordDao.getWordFromWordBook222(wordBookIdForView)
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
    private inner class GetWordFromWordBookAsyncTask : AsyncTask<Long, Void, LiveData<List<Word>>>(){
        override fun doInBackground(vararg wordBookIdForView: Long?): LiveData<List<Word>> {
            Log.d("     TAG", "===== WordViewModel - GetWordFromWordBookAsyncTask - doInBackground called")
            Log.d("     TAG", "===== WordViewModel - GetWordFromWordBookAsyncTask - doInBackground 값은 : ${wordBookIdForView[0]}")
            Log.d("     TAG", "===== WordViewModel - GetWordFromWordBookAsyncTask - doInBackground out")
            return wordDao.getWordFromWordBook(wordBookIdForView)
        }

    }

    @SuppressLint("StaticFieldLeak")
    private inner class DeleteWordAsyncTask : AsyncTask<Word, Void, Void>() {
        override fun doInBackground(vararg word: Word?): Void? {
            Log.d("     TAG", "===== WordViewModel - DeleteWordAsyncTask - doInBackground called")
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

