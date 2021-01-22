package com.example.kokako.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.example.kokako.dao.WordBookDAO
import com.example.kokako.database.WordDatabase
import com.example.kokako.model.WordBook

//  Log.d("     TAG", "===== WordBookViewModel")
class WordBookViewModel(application: Application) : AndroidViewModel(application) {
    var wordBookList: LiveData<List<WordBook>>
    private var wordBookDao: WordBookDAO
    var recentInsertedWordBookId : Long = 0

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

    fun deleteWordBookById(wordBookIdForView: Long) {
        DeleteWordBookByIdAsyncTask().execute(wordBookIdForView)
    }

    fun update(wordBook: WordBook) {
        UpdateWordBookAsyncTask().execute(wordBook)
    }

    @SuppressLint("StaticFieldLeak")
    private inner class InsertWordBookAsyncTask() : AsyncTask<WordBook, Long, Long>() {
        override fun doInBackground(vararg wordBook: WordBook?): Long? {
            recentInsertedWordBookId = wordBookDao.insert(wordBook[0]!!)
            return recentInsertedWordBookId
        }
    }
//    또 타입에러? 같은거 떴을때 그냥 deleteWordBookById안 쓰고 Delete다시 만드니까 됨
    @SuppressLint("StaticFieldLeak")
    private inner class DeleteWordBookAsyncTask(): AsyncTask<WordBook, Void, Void>() {
        override fun doInBackground(vararg wordBook: WordBook?): Void? {
            wordBookDao.delete(wordBook[0]!!)
            return null
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class DeleteWordBookByIdAsyncTask(): AsyncTask<Long, Void, Void>() {
        override fun doInBackground(vararg wordBookIdForView: Long?): Void? {
            wordBookDao.deleteWordBookById(wordBookIdForView[0]!!)
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
