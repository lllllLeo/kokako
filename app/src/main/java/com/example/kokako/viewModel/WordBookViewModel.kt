package com.example.kokako.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.os.AsyncTask
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.example.kokako.dao.WordBookDAO
import com.example.kokako.database.WordDatabase
import com.example.kokako.model.WordBook

class WordBookViewModel(application: Application) : AndroidViewModel(application) {
    var wordBookList: LiveData<List<WordBook>>
    private var wordBookDao: WordBookDAO
    var recentInsertedWordBookId : Long = 0
    get() = field
    private set(value) {
        field = value
    }

    init {
        val db: WordDatabase =
            Room.databaseBuilder(application, WordDatabase::class.java, "word").build()
        wordBookDao = db.getWordBookDAO()
        wordBookList = wordBookDao.getAll()
    }

    fun insert(wordBook: WordBook):Long? {
        return InsertWordBookAsyncTask().execute(wordBook).get()
    }

    fun delete(wordBook: WordBook) {
        DeleteWordBookAsyncTask().execute(wordBook)
    }

    fun update(wordBook: WordBook) {
        UpdateWordBookAsyncTask().execute(wordBook)
    }

    @SuppressLint("StaticFieldLeak")
    private inner class InsertWordBookAsyncTask() : AsyncTask<WordBook, Long, Long>() {
        override fun doInBackground(vararg wordBook: WordBook?): Long {
            Log.d("TAG","WordBookViewModel InsertWordBookAsyncTask - doInBackground() IN")
            Log.d("TAG","WordBookViewModel InsertWordBookAsyncTask - doInBackground() : "+wordBook[0].toString())
            recentInsertedWordBookId = wordBookDao.insert(wordBook[0]!!)
            Log.d("TAG",
                "WordBookViewModel InsertWordBookAsyncTask - doInBackground() recentInsertedWordBookId 값 : $recentInsertedWordBookId")
            Log.d("TAG","WordBookViewModel InsertWordBookAsyncTask - doInBackground() OUT")
            return recentInsertedWordBookId
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class DeleteWordBookAsyncTask(): AsyncTask<WordBook, Void, Void>() {
        override fun doInBackground(vararg wordBook: WordBook?): Void? {
            wordBookDao.delete(wordBook[0]!!)
            return null
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class UpdateWordBookAsyncTask(): AsyncTask<WordBook, Void, Void>() {
        override fun doInBackground(vararg wordBook: WordBook?): Void? {
            wordBookDao.update(wordBook[0]!!)
            return null
        }
    }

}
