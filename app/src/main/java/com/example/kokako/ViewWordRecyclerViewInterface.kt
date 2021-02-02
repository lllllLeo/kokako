package com.example.kokako

import android.view.View
import android.widget.Button
import com.example.kokako.model.CheckBoxData

interface ViewWordRecyclerViewInterface {
    fun onStarClicked(v: View, adapterPosition: Int)
    fun onPopupMenuWordClicked(v: View, myWordBtnViewOption: Button, adapterPosition: Int)
    fun onWordLongClicked(v: View, adapterPosition: Int)
    fun onCheckboxClicked(v: View, adapterPosition: Int, wordCheckBox: ArrayList<CheckBoxData>)
}
