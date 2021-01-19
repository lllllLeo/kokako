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
    }

    fun insert(word: Word) {
        InsertWordAsyncTask().execute(word)
    }
    fun delete(word: Word) {
        DeleteWordAsyncTask().execute(word)
    }
    fun deleteAll() {
        DeleteAllWordAsyncTask().execute()
    }
    fun getWordFromWordBook(wordBookIdForView: Long): LiveData<List<Word>> {
        Log.d("     TAG", "===== WordViewModel - getWordFromWordBook wordBookIdForView 값은 : $wordBookIdForView")
        wordList = GetWordFromWordBookAsyncTask().execute(wordBookIdForView).get()
        Log.d("     TAG", "===== WordViewModel - getWordFromWordBook wordList 값은 : ${wordList.value}")
        return wordList
    }

    @SuppressLint("StaticFieldLeak")
    private inner class GetWordFromWordBookAsyncTask : AsyncTask<Long, Void, LiveData<List<Word>>>(){
        override fun doInBackground(vararg wordBookIdForView: Long?): LiveData<List<Word>> {
            Log.d("     TAG", "===== WordViewModel - GetWordFromWordBookAsyncTask - doInBackground called")
            Log.d("     TAG", "===== WordViewModel - GetWordFromWordBookAsyncTask - doInBackground 값은 : ${wordBookIdForView[0]}")
            return wordDao.getWordFromWordBook(wordBookIdForView)
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
            Log.d("     TAG", "===== WordViewModel - InsertWordAsyncTask - doInBackground called")
            Log.d("     TAG", "===== WordViewModel - InsertWordAsyncTask - doInBackground word[0] : ${word[0]}")
            wordDao.insert(word[0]!!)
            return null
        }
    }


    @SuppressLint("StaticFieldLeak")
    private inner class DeleteAllWordAsyncTask(): AsyncTask<Word, Void, Void>() {
        override fun doInBackground(vararg params: Word?): Void? {
            wordDao.deleteAll()
            return null
        }

    }

}