package com.example.kokako.model

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize


@Entity(tableName = "tb_word",
    foreignKeys = [
        ForeignKey(entity = WordBook::class,
            parentColumns = ["id"],
            childColumns = ["wordBookId"],
            onDelete = CASCADE)
    ]
)
@Parcelize
data class Word(
    @PrimaryKey(autoGenerate = true)
    var id: Long,
    var word: String?,
    var mean: String?,
    var option: String?,
    var bookMarkCheck: Int,
    var testCheck : Int,
    var wordBookId: Long
) : Parcelable

/* {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readLong()) {
    }

    override fun describeContents(): Int {
        return 0
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeLong(id)
        dest?.writeString(word)
        dest?.writeString(mean)
        dest?.writeInt(bookMarkCheck)
        dest?.writeLong(wordBookId)
    }

    companion object CREATOR : Parcelable.Creator<Word> {
        override fun createFromParcel(parcel: Parcel): Word {
            return Word(parcel)
        }

        override fun newArray(size: Int): Array<Word?> {
            return arrayOfNulls(size)
        }
    }
}*/

/*{
    constructor() : this(0,"Default word", "Default mean", 0)
}*/



//  primaryKeys = ["word", "mean"]
//  , foreignKeys = [ForeignKey(entity = WordBook::class, parentColumns = arrayOf("wordBookId"), childColumns = arrayOf("wordBookId_FK"), onDelete = CASCADE)]
