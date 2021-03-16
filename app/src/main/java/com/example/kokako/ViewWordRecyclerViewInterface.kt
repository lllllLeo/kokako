package com.example.kokako

import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

interface ViewWordRecyclerViewInterface {
    fun onFavoriteButtonClicked(v: View, adapterPosition: Int)
    fun onFavoriteButtonLayoutClicked(v: View, adapterPosition: Int)
//    fun onPopupMenuWordClicked(v: View, myWordBtnViewOption: Button, adapterPosition: Int)
    fun onWordLongClicked(v: View, adapterPosition: Int)
    fun onCheckboxClicked(v: View, wordMeanLayout: LinearLayout, adapterPosition: Int)
    fun onViewClicked(v: View, wordMeanLayout: LinearLayout, adapterPosition: Int)
    fun onVisibilityCheckboxClicked(v: View, _visibilityOptions : Int, wordTextView : TextView, meanTextView : TextView, adapterPosition: Int)
    fun onVisibilityCheckboxLayoutClicked(v: View, _visibilityOptions : Int, wordTextView : TextView, meanTextView : TextView, adapterPosition: Int)
    fun ontextToSpeechSpeakButtonClicked(v:View, adapterPosition: Int)
}
