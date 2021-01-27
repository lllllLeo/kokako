package com.example.kokako

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.kokako.model.WordBook
import kotlinx.android.synthetic.main.rv_my_word_list.view.*

class MyWordViewHolder(itemView: View, myWordListRecyclerViewInterface: MyWordListRecyclerViewInterface): RecyclerView.ViewHolder(itemView), View.OnLongClickListener, View.OnClickListener {

    private val myWordMainTitle = itemView.my_word_main_title
    private val myWordMainCount = itemView.my_word_main_count
    private val myWordBookList = itemView.my_word_book_list
    private val myWordBtnViewOption = itemView.my_word_btn_options
//    private val myWordBtnViewOption = itemView.my_word_text_view_options
    private var myWordListRecyclerViewInterface : MyWordListRecyclerViewInterface? = null

    init {
        myWordBookList.setOnLongClickListener(this)
        myWordBookList.setOnClickListener(this)
        myWordBtnViewOption.setOnClickListener(this)
        this.myWordListRecyclerViewInterface = myWordListRecyclerViewInterface
    }
    fun bind(wordBook: WordBook){
        myWordMainTitle.text = wordBook.title.toString()
        myWordMainCount.text = wordBook.count.toString()
    }

    override fun onLongClick(v: View?): Boolean {
        this.myWordListRecyclerViewInterface?.onRemoveClicked(v!!, adapterPosition)
        return true
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.my_word_book_list -> {
                this.myWordListRecyclerViewInterface?.onViewClicked(v, adapterPosition)
            }
            R.id.my_word_btn_options -> {
                this.myWordListRecyclerViewInterface?.onPopupMenuWordBookClicked(v, myWordBtnViewOption, adapterPosition)
            }
        }
    }

//    override fun onMenuItemClick(item: MenuItem?): Boolean {
//    this.myWordListRecyclerViewInterface?.onPopupMenuClicked(item!!, myWordBtnViewOption, adapterPosition)
//    }
}
