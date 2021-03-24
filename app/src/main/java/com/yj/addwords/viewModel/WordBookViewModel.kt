package com.yj.addwords.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.os.AsyncTask
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.yj.addwords.dao.WordBookDAO
import com.yj.addwords.database.WordDatabase
import com.yj.addwords.model.WordBook

//  Log.d("     TAG", "===== WordBookViewModel")
class WordBookViewModel(application: Application) : AndroidViewModel(application) {
    var wordBookListLivedata: LiveData<List<WordBook>>
    var wordBookArrayList: List<WordBook>? = null
    private var wordBookDao: WordBookDAO
    var recentInsertedWordBookId : Long = 0

    companion object {
        const val TAG = "TAG WordBookViewModel"
    }
    init {
        val db: WordDatabase =
            Room.databaseBuilder(application, WordDatabase::class.java, "word").build()
        wordBookDao = db.getWordBookDAO()
        wordBookListLivedata = wordBookDao.getAll()
    }

    fun getRecentOrder(): List<WordBook>? {
        wordBookArrayList = GetRecentOrderAsyncTask().execute().get()
        Log.d(TAG, "getRecentOrder: $wordBookArrayList")
        return wordBookArrayList
    }

    fun getWordbookNameAscendingOrder(): List<WordBook>? {
        wordBookArrayList = GetWordbookNameAscendingOrderAsyncTask().execute().get()
        Log.d(TAG, "getWordAscendingOrder: $wordBookArrayList")
        return wordBookArrayList
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
    fun getMaxOrder() : Int{
        return GetMaxOrderAsyncTask().execute().get()
    }

//    타이틀 업뎃
    fun update(wordBook: WordBook) {
        UpdateWordBookAsyncTask().execute(wordBook)
    }

    fun updateAll(wordbook : ArrayList<WordBook>) {
        Log.d(TAG, "updateAll: called")
        Log.d(TAG, "updateAll: wordbook $wordbook")
        UpdateAllAsyncTask().execute(wordbook)
    }

    fun updateWordBookCount(updateWordBookMain: Long) {
        UpdateWordBookCountAsyncTask().execute(updateWordBookMain)
    }

    fun getLanguageCode(wordBookIdForView: Long) : Int {
        return GetLanguageCodeAsyncTask().execute(wordBookIdForView).get()
    }

    @SuppressLint("StaticFieldLeak")
    private inner class GetLanguageCodeAsyncTask(): AsyncTask<Long, Void, Int>() {
        override fun doInBackground(vararg wordBookIdForView: Long?): Int {
            return wordBookDao.getLanguageCode(wordBookIdForView)
        }

    }

    @SuppressLint("StaticFieldLeak")
    private inner class GetRecentOrderAsyncTask(): AsyncTask<Void, Void, List<WordBook>>() {
        override fun doInBackground(vararg params: Void?): List<WordBook> {
            return wordBookDao.getRecentOrder()
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class GetWordbookNameAscendingOrderAsyncTask(): AsyncTask<Void, Void, List<WordBook>>() {
        override fun doInBackground(vararg params: Void?): List<WordBook> {
            return wordBookDao.getWordbookNameAscendingOrder()
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class GetMaxOrderAsyncTask() : AsyncTask<Void, Void, Int>() {
        override fun doInBackground(vararg params: Void?): Int {
            return wordBookDao.getMaxOrder()
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class UpdateAllAsyncTask() : AsyncTask<ArrayList<WordBook>, Void, Void>() {
        override fun doInBackground(vararg wordbook: ArrayList<WordBook>): Void? {
            Log.d("     TAG", "===== WordBookViewModel UpdateWordBookCountAsyncTask : ${wordbook[0]}")
            wordBookDao.updateAll(wordbook[0])
            return null
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class UpdateWordBookCountAsyncTask() : AsyncTask<Long, Void, Void>() {
        override fun doInBackground(vararg updateWordBookMain: Long?): Void? {
            Log.d("     TAG", "===== WordBookViewModel UpdateWordBookCountAsyncTask : $updateWordBookMain")
            wordBookDao.updateWordBookCount(updateWordBookMain)
            return null
        }
    }
//    타이틀
    @SuppressLint("StaticFieldLeak")
    private inner class UpdateWordBookAsyncTask(): AsyncTask<WordBook, Void, Void>() {
        override fun doInBackground(vararg wordBook: WordBook?): Void? {
            wordBookDao.update(wordBook)
            return null
        }
    }
    @SuppressLint("StaticFieldLeak")
    private inner class InsertWordBookAsyncTask() : AsyncTask<WordBook, Long, Long>() {
        override fun doInBackground(vararg wordBook: WordBook?): Long {
            recentInsertedWordBookId = wordBookDao.insert(wordBook[0]!!.title, wordBook[0]!!.count, wordBook[0]!!.itemOrder, wordBook[0]!!.language)
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


}
