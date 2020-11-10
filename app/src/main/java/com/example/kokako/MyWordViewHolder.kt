package com.example.kokako

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.kokako.model.MyWordListDTO
import kotlinx.android.synthetic.main.rv_my_word_list.view.*

class MyWordViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    private val myWordMainTitle = itemView.my_word_main_title
    private val myWordMainCount = itemView.my_word_main_count

    fun bind(myWordListDTO: MyWordListDTO){
        myWordMainTitle.text = myWordListDTO.title
        myWordMainCount.text = myWordListDTO.wordCount.toString()
    }

}
