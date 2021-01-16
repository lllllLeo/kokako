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

class WordViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private const val TAG = "TAG"
    }
    var wordList : LiveData<List<Word>>
    private var wordDao : WordDAO

    init {
        val db : WordDatabase = Room.databaseBuilder(application, WordDatabase::class.java, "word").build()
        wordDao = db.getWordDAO()
        wordList = wordDao.getAll()
    }

    fun insert(word: Word) {
        Log.d("TAG","WordViewModel insert() IN")
        Log.d("TAG", "WordViewModel insert() word.wordBookId 값 : $word")
        InsertWordAsyncTask().execute(word)
        Log.d("TAG","WordViewModel insert() OUT")
    }
    fun delete(word: Word) {
        DeleteWordAsyncTask().execute(word)
    }
    fun deleteAll() {
        DeleteAllWordAsyncTask().execute()
    }


    @SuppressLint("StaticFieldLeak")
    private inner class DeleteWordAsyncTask : AsyncTask<Word, Void, Void>() {
        override fun doInBackground(vararg word: Word?): Void? {
            wordDao.delete(word[0]!!)
            return null
        }
    }

//  TODO 설마 비동기라서?
    @SuppressLint("StaticFieldLeak")
    private inner class InsertWordAsyncTask : AsyncTask<Word, Void, Void>() {
        override fun doInBackground(vararg word: Word?): Void? {
            Log.d("TAG","WordViewModel InsertWordAsyncTask - doInBackground() IN")
//            word[0]!!.id = 1
            Log.d("TAG ", "WordViewModel InsertWordAsyncTask - doInBackground() "+word[0].toString())
//            wordDao.insert2(word[0]!!.id, word[0]!!.word, word[0]!!.mean, word[0]!!.wordBookId)       // TODO  여기가 계속 터짐 FOREIGN KEY 에러뜸
            wordDao.insert(word[0]!!)       // TODO  여기가 계속 터짐 FOREIGN KEY 에러뜸
            Log.d("TAG","WordViewModel InsertWordAsyncTask - doInBackground() OUT")
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