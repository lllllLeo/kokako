package com.example.kokako.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.kokako.dao.WordDAO
import com.example.kokako.model.Word

@Database(entities = [Word::class], version = 1)
abstract class WordDatabase : RoomDatabase() {
    abstract fun getWordDAO(): WordDAO


}
//    companion object {
//        var INSTANCE: WordDatabase? = null
//
//        fun getInstance(context: Context): WordDatabase? {
//            if (INSTANCE == null) {
//                synchronized(WordDatabase::class) {
//                    INSTANCE = Room.databaseBuilder(context.applicationContext,
//                        WordDatabase::class.java,
//                        "word.db")
//                        .fallbackToDestructiveMigration() // 버전 업그레이드 되었을때, 모든 데이터를 드랍하고 새로운 데이터로 시작.
//                        .build()
//                }
//            }
//            return INSTANCE
//        }
//
//    }
