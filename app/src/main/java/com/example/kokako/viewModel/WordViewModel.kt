package com.example.kokako.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.os.AsyncTask
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import com.example.kokako.dao.WordDAO
import com.example.kokako.database.WordDatabase
import com.example.kokako.model.Word

class WordViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private const val TAG = "TAG"
    }
    var wordList : LiveData<List<Word>>
    private var wordDao : WordDAO

    init {
        val db : WordDatabase = Room.databaseBuilder(application, WordDatabase::class.java, "tb_wordBook").build()
        wordDao = db.getWordDAO()
        wordList = wordDao.getAll()
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


    private inner class DeleteAllWordAsyncTask(): AsyncTask<Word, Void, Void>() {
        override fun doInBackground(vararg params: Word?): Void? {
            wordDao.deleteAll()
            return null
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
        override fun doInBackground(vararg words: Word?): Void? {
            wordDao.insert(words[0]!!)
            return null
        }

    }

}