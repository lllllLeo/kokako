package com.example.kokako

import androidx.room.TypeConverter
import java.util.*

class TypeConverter {
//    Room DB에서 기본 자료형이 아닌 객체를 사용하기 위한 Annotation : @TypeConverter
    @TypeConverter
    fun fromTimestamp(value: Long?) : Date? = value?.let { Date(it) }
    @TypeConverter
    fun dateToTimestamp(date: Date?) : Long? = date?.time
}