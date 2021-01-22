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

    init {
        Log.d("     TAG", "===== WordViewModel - init called")
        val db : WordDatabase = Room.databaseBuilder(application, WordDatabase::class.java, "word").build()
        wordDao = db.getWordDAO()
        if (!wordBookId.equals(-1)) {
            Log.d("     TAG", "===== WordViewModel - init - if wordBookId : $wordBookId")
            wordList = wordDao.getWordFromWordBook(arrayOf(wordBookId))
        }else{
            wordList = wordDao.getAll()
        }
//        var d = wordDao.getWordFromWordBook222(arrayOf(wordBookId)) as ArrayList<Word>
//        Log.d("     TAG", "===== WordViewModel - init d : $d")
//        wordArrayList = d
    }
    fun insertAllDatas(word : ArrayList<Word>) {
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
        return wordList
    }
    fun getWordFromWordBook222(wordBookIdForView: Long): ArrayList<Word> {
        Log.d("     TAG", "===== WordViewModel - getWordFromWordBook222 wordBookIdForView 값은 : $wordBookIdForView")
        wordArrayList = GetWordFromWordBookAsyncTask222().execute(wordBookIdForView).get()
        Log.d("     TAG", "===== WordViewModel - getWordFromWordBook222 wordList 값은 : ${wordArrayList}")
        return wordArrayList as ArrayList<Word>
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
            Log.d("     TAG", "===== WordViewModel - InsertAllWordAsyncTask - doInBackground -  word 값은 : ${words[0]}")
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

