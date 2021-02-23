package com.example.kokako

import android.view.View
import android.widget.Button
import android.widget.LinearLayout

interface ViewWordRecyclerViewInterface {
    fun onFavoriteButtonClicked(v: View, adapterPosition: Int)
    fun onPopupMenuWordClicked(v: View, myWordBtnViewOption: Button, adapterPosition: Int)
    fun onWordLongClicked(v: View, adapterPosition: Int)
    fun onCheckboxClicked(v: View, adapterPosition: Int)
    fun onViewClicked(v: View, adapterPosition: Int)
    fun onVisibilityCheckboxClicked(v: View, _visibilityOptions : Int, wordLayout: LinearLayout, meanLayout : LinearLayout, adapterPosition: Int)
}
