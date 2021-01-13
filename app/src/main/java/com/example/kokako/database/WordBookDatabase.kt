package com.example.kokako.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.kokako.dao.WordBookDAO
import com.example.kokako.dao.WordDAO
import com.example.kokako.model.WordBook

@Database(entities = [WordBook::class], version = 1)
abstract class WordBookDatabase : RoomDatabase(){
    abstract fun getWordBookDAO(): WordBookDAO
}