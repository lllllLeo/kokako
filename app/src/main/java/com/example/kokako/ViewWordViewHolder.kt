package com.example.kokako

import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.kokako.ViewWordActivity.Companion.checkboxList
import com.example.kokako.model.CheckBoxData
import com.example.kokako.model.Word
import kotlinx.android.synthetic.main.rv_view_list_item.view.*
import kotlin.collections.ArrayList
import kotlin.math.log

class ViewWordViewHolder(itemView: View, viewWordRecyclerViewInterface: ViewWordRecyclerViewInterface) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {
    private var starButton = itemView.view_my_star
    private var wordTextView = itemView.view_my_word
    private var meanTextView = itemView.view_my_mean
    private var myWordBtnViewOption = itemView.my_word_btn_options
    private var wordCheckBox = itemView.view_my_check
    private var wordList = itemView.view_word_book_list
    private var viewWordRecyclerViewInterface : ViewWordRecyclerViewInterface? = null

    init {
        starButton.setOnClickListener(this)
        myWordBtnViewOption.setOnClickListener(this)
        wordList.setOnLongClickListener(this)
        wordList.setOnClickListener(this)
        wordCheckBox.setOnClickListener(this)
        this.viewWordRecyclerViewInterface = viewWordRecyclerViewInterface
    }

    fun bind(wordDatas: ArrayList<Word>, position: Int, num: Int,showAndHideNumber: Int, currentLongClickPosition: Int?) {
        wordTextView.text = wordDatas[position].word
        meanTextView.text = wordDatas[position].mean
        if(num == 1) {
            wordCheckBox.visibility = View.VISIBLE
            starButton.visibility = View.GONE
            myWordBtnViewOption.visibility = View.GONE
        } else {
            wordCheckBox.visibility = View.GONE
            starButton.visibility = View.VISIBLE
            myWordBtnViewOption.visibility = View.VISIBLE

            if (wordDatas[position].bookMarkCheck == 1) {
                starButton.setBackgroundResource(R.drawable.star_visible)
            } else {
                starButton.setBackgroundResource(R.drawable.star_invisible)
            }
        }
            when (showAndHideNumber) {
                0 -> {
                    wordTextView.visibility = View.VISIBLE
                    meanTextView.visibility = View.VISIBLE
                }
                1 -> {
                    wordTextView.visibility = View.INVISIBLE
                    meanTextView.visibility = View.VISIBLE
                }
                2 -> {
                    wordTextView.visibility = View.VISIBLE
                    meanTextView.visibility = View.INVISIBLE
                }
            }

        if (position >= checkboxList.size) {
            checkboxList.add(position, CheckBoxData(wordDatas[position].id, false))
        }
        if (position == currentLongClickPosition) {
            checkboxList[currentLongClickPosition] = CheckBoxData(wordDatas[position].id, true)
        }
// TODO: 2021-02-08 별도 스레드
        if (num == 0 && currentLongClickPosition == -1) {
            for (i in 0 until checkboxList.size) {
                if (checkboxList[i].checked) {
//                    itemView.view_word_book_list.setBackgroundColor(ContextCompat.getColor(itemView.context,R.color.colorSelectItem))
                    checkboxList[i].checked = false
                }
            }
        }
        wordCheckBox.isChecked = checkboxList[position].checked
    }

    override fun onClick(v: View?) {
        when(v!!.id) {
            R.id.my_word_btn_options -> {
                this.viewWordRecyclerViewInterface?.onPopupMenuWordClicked(v, myWordBtnViewOption, adapterPosition)
            }
            R.id.view_my_star -> {
                this.viewWordRecyclerViewInterface?.onStarClicked(v, adapterPosition)
            }
            R.id.view_my_check -> {
                /*if (v.view_my_check.isChecked) {
                    itemView.view_word_book_list.setBackgroundColor(ContextCompat.getColor(itemView.context,
                        R.color.colorSelectItem))
                    Log.d(TAG, "onClick: view_my_check adapterPosition는 $adapterPosition")
                } else {
                    itemView.view_word_book_list.setBackgroundColor(ContextCompat.getColor(itemView.context,
                        R.color.colorWhite))
                }*/
                this.viewWordRecyclerViewInterface?.onCheckboxClicked(v, adapterPosition)
            }
            R.id.view_word_book_list -> {
                this.viewWordRecyclerViewInterface?.onViewClicked(v, adapterPosition)
            }
        }
    }

    override fun onLongClick(v: View): Boolean {
        this.viewWordRecyclerViewInterface?.onWordLongClicked(v, adapterPosition)
        return true
    }

    companion object {
        private const val TAG = "TAG ViewWordViewHolder"
    }
}
