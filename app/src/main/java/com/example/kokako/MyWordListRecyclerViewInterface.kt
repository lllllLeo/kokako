package com.example.kokako

import android.view.View
import android.widget.Button

//  커스텀 인터페이스
interface MyWordListRecyclerViewInterface {
    fun onRemoveClicked(v : View, position: Int)
    fun onViewClicked(v: View, adapterPosition: Int)
    fun onPopupMenuWordBookClicked(v: View, myWordBtnViewOption: Button, adapterPosition: Int)
}