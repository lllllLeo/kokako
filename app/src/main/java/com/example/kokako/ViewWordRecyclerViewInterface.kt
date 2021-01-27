package com.example.kokako

import android.view.View
import android.widget.Button

interface ViewWordRecyclerViewInterface {
    fun onStarClicked(v: View, adapterPosition: Int)
    fun onPopupMenuWordClicked(v: View, myWordBtnViewOption: Button, adapterPosition: Int)
}
