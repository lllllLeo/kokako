package com.example.kokako

import android.util.Log
import androidx.recyclerview.widget.DiffUtil
import com.example.kokako.model.Word

//  Log.d("     TAG", "===== MyDiffCallback ")
object MyDiffCallback : DiffUtil.ItemCallback<Word>() {
    override fun areItemsTheSame(oldItem: Word, newItem: Word): Boolean {
          Log.d("     TAG", "===== MyDiffCallback areItemsTheSame called")
        return oldItem.word == newItem.word || newItem.mean == newItem.mean
    }
    override fun areContentsTheSame(oldItem: Word, newItem: Word): Boolean {
        Log.d("     TAG", "===== MyDiffCallback areContentsTheSame called")
        return oldItem == newItem
    }
}