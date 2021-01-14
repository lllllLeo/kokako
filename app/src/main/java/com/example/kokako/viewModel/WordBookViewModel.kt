package com.example.kokako.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.example.kokako.dao.WordBookDAO
import com.example.kokako.database.WordBookDatabase
import com.example.kokako.model.WordBook

class WordBookViewModel(application: Application) : AndroidViewModel(application) {
    var wordBookList: LiveData<List<WordBook>>
    private var wordBookDao: WordBookDAO

    init {
        val db: WordBookDatabase =
            Room.databaseBuilder(application, WordBookDatabase::class.java, "tb_word_book").build()
        wordBookDao = db.getWordBookDAO()
        wordBookList = wordBookDao.getAll()
    }

    fun insert(wordBook: WordBook) {
        InsertWordBookAsyncTask().execute(wordBook)
    }

    fun delete(wordBook: WordBook) {
        DeleteWordBookAsyncTask().execute(wordBook)
    }

    fun update(wordBook: WordBook) {
        UpdateWordBookAsyncTask().execute(wordBook)
    }

    @SuppressLint("StaticFieldLeak")
    private inner class InsertWordBookAsyncTask() : AsyncTask<WordBook, Void, Void>() {
        override fun doInBackground(vararg wordBook: WordBook?): Void? {
            wordBookDao.insert(wordBook[0]!!)
            return null
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
