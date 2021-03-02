package com.example.kokako

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.kokako.ViewWordActivity.Companion.checkboxList
import com.example.kokako.ViewWordActivity.Companion.visibleCheckboxList
import com.example.kokako.model.CheckBoxData
import com.example.kokako.model.VisibleCheckBoxData
import com.example.kokako.model.Word
import kotlinx.android.synthetic.main.rv_word_list.view.*
import kotlin.collections.ArrayList

class ViewWordViewHolder(itemView: View, viewWordRecyclerViewInterface: ViewWordRecyclerViewInterface) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {
    private var starButton = itemView.view_star
    private var wordTextView = itemView.view_word
    private var meanTextView = itemView.view_mean
    private var wordCheckBox = itemView.view_check
    private var wordMeanLayout = itemView.view_word_book_list
    private var listenButton = itemView.view_listen
    private var visibleCheckBox = itemView.visible_check
    private var after_visibilityOptions = 0
    private var current_visibilityOptions = 0
    private var _visibilityOptions = 0
    private var viewWordRecyclerViewInterface : ViewWordRecyclerViewInterface? = null
    companion object {
        private const val TAG = "TAG ViewWordViewHolder"
    }
    init {
        starButton.setOnClickListener(this)
        visibleCheckBox.setOnClickListener(this)
        wordMeanLayout.setOnLongClickListener(this)
        wordMeanLayout.setOnClickListener(this)
        wordCheckBox.setOnClickListener(this)
        listenButton.setOnClickListener(this)
        this.viewWordRecyclerViewInterface = viewWordRecyclerViewInterface
    }

    fun bind(wordDatas: ArrayList<Word>, position: Int, deleteMode: Int, visibilityOptions: Int, currentLongClickPosition: Int?) {
        _visibilityOptions = visibilityOptions
        wordTextView.text = wordDatas[position].word
        meanTextView.text = wordDatas[position].mean

        isDeleteMode(deleteMode, wordDatas, position)
        setInitializeUnCheckedList(position, wordDatas)     // 처음 들어올 때 체크박스 전부 false
        visibilityMode(visibilityOptions, position)         // 가리기 모드


        setCheckedByLongClicked(position, currentLongClickPosition, wordDatas)      // 롱클릭 true로 바꿔줌
        // TODO: 2021-02-08 별도 스레드
        isUnCheckedMode(deleteMode, currentLongClickPosition)                       // 선택 체크박스 취소
        isVisibilityUnCheckedMode(visibilityOptions)                                // 가리기모드 체크박스 취소

        wordCheckBox.isChecked = checkboxList[position].checked
        wordMeanLayout.isSelected = checkboxList[position].checked

        visibleCheckBox.isChecked = visibleCheckboxList[position].checked
        if (_visibilityOptions == 1) {
            if (visibleCheckboxList[position].checked) {
                wordTextView.visibility = View.VISIBLE
                meanTextView.visibility = View.VISIBLE
            } else {
                wordTextView.visibility = View.INVISIBLE
                meanTextView.visibility = View.VISIBLE
            }
        } else if (_visibilityOptions == 2){
            if (visibleCheckboxList[position].checked) {
                wordTextView.visibility = View.VISIBLE
                meanTextView.visibility = View.VISIBLE
            } else {
                wordTextView.visibility = View.VISIBLE
                meanTextView.visibility = View.INVISIBLE
            }
        }
    }

    // TODO: 2021-02-21 이거처럼 해야할듯 가리기버튼도
    private fun isDeleteMode(deleteMode: Int,wordDatas: ArrayList<Word>,position: Int,) {
        if (deleteMode == 1) {
            wordCheckBox.visibility = View.VISIBLE
            starButton.visibility = View.GONE
            listenButton.visibility = View.GONE
        } else {
            wordCheckBox.visibility = View.GONE
            starButton.visibility = View.VISIBLE
            listenButton.visibility = View.VISIBLE

            // FIXME: 2021-02-21 이거 빼기
            if (wordDatas[position].bookMarkCheck == 1) {
                starButton.setBackgroundResource(R.drawable.favorite_pressed_background)
            } else {
                starButton.setBackgroundResource(R.drawable.favorite_normal_background)
            }
        }
    }
    private fun setCheckedByLongClicked(position: Int,currentLongClickPosition: Int?,wordDatas: ArrayList<Word>,) {
        if (position == currentLongClickPosition) {
            checkboxList[currentLongClickPosition] = CheckBoxData(wordDatas[position].id, true)
    //            itemView.view_word_book_list.setBackgroundColor(ContextCompat.getColor(itemView.context,R.color.colorSelectItem))
        }
    }
    private fun setInitializeUnCheckedList(position: Int, wordDatas: ArrayList<Word>) {
        if (position >= checkboxList.size) {
            checkboxList.add(position, CheckBoxData(wordDatas[position].id, false))
            visibleCheckboxList.add(position, VisibleCheckBoxData(wordDatas[position].id, false))
        }
    }
    private fun visibilityMode(showAndHideNumber: Int, position: Int) {
        when (showAndHideNumber) {
            0 -> {
                visibleCheckBox.visibility = View.GONE
                wordTextView.visibility = View.VISIBLE
                meanTextView.visibility = View.VISIBLE
            }
            1 -> {
                visibleCheckBox.visibility = View.VISIBLE
                wordTextView.visibility = View.INVISIBLE
                meanTextView.visibility = View.VISIBLE

                if (visibleCheckboxList[position].checked) {
                    wordTextView.visibility = View.VISIBLE
                    meanTextView.visibility = View.VISIBLE
                } else {
                    wordTextView.visibility = View.INVISIBLE
                    meanTextView.visibility = View.VISIBLE
                }
            }
            2 -> {
                visibleCheckBox.visibility = View.VISIBLE
                wordTextView.visibility = View.VISIBLE
                meanTextView.visibility = View.INVISIBLE

                if (visibleCheckboxList[position].checked) {
                    wordTextView.visibility = View.VISIBLE
                    meanTextView.visibility = View.VISIBLE
                } else {
                    wordTextView.visibility = View.VISIBLE
                    meanTextView.visibility = View.INVISIBLE
                }

//                meanLayout.isSelected = visibleCheckboxList[position].checked
//                current_visibilityOptions = 2
            }
            // FIXME: 2021-02-24 랜덤하기
            3 -> {
                visibleCheckBox.visibility = View.VISIBLE
                val random = (1..10).shuffled().first()
                Log.d(TAG, "bind: random 은 $random")
                if (random % 2 == 0) {
                    wordTextView.visibility = View.VISIBLE
                    meanTextView.visibility = View.INVISIBLE
                } else {
                    wordTextView.visibility = View.INVISIBLE
                    meanTextView.visibility = View.VISIBLE
                }
            }
        }
//        isVisibilityUnCheckedMode(showAndHideNumber)

    }

    private fun isUnCheckedMode(num: Int, currentLongClickPosition: Int?) {
        if (num == 0 && currentLongClickPosition == -1) {
            for (i in 0 until checkboxList.size) {
                if (checkboxList[i].checked) {
                    checkboxList[i].checked = false
                    wordMeanLayout.isSelected = checkboxList[i].checked // view selected false로 만들어서 색깔을 화이트로
//                    itemView.view_word_book_list.setBackgroundColor(ContextCompat.getColor(itemView.context,R.color.colorWhite))
                }
            }
        }
    }
    private fun isVisibilityUnCheckedMode(showAndHideNumber: Int) {
        if (showAndHideNumber == 0) {
            for (i in 0 until visibleCheckboxList.size) {
                if (visibleCheckboxList[i].checked) {
                    visibleCheckboxList[i].checked = false
                }
            }
        }
    }



    override fun onClick(v: View?) {
        when(v!!.id) {
            R.id.visible_check -> {
//                if (visibleCheckList[adapterPosition].checked) {
//                    Log.d(TAG, "onClick: if문")
//                    wordTextView.visibility = View.VISIBLE
//                } else {
//                    Log.d(TAG, "onClick: else문")
//                    wordTextView.visibility = View.INVISIBLE
//                }
                this.viewWordRecyclerViewInterface?.onVisibilityCheckboxClicked(v, _visibilityOptions, wordTextView, meanTextView, adapterPosition)
            }
            R.id.view_star -> {
                this.viewWordRecyclerViewInterface?.onFavoriteButtonClicked(v, adapterPosition)
            }
            R.id.view_check -> {
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
}


/*
                wordOptionsButton.isClickable = false

                if(wordShowAndHideButton.isChecked) {
                    wordShowAndHideButton.setBackgroundResource(R.drawable.ic_baseline_visibility_24)
                    wordTextView.visibility = View.VISIBLE
                    meanTextView.visibility = View.VISIBLE
                } else {
                    wordShowAndHideButton.setBackgroundResource(R.drawable.ic_baseline_visibility_off_24)
                    wordTextView.visibility = View.INVISIBLE
                    meanTextView.visibility = View.VISIBLE
                }


*/

