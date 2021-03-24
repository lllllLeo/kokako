package com.yj.addwords

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.yj.addwords.ViewWordActivity.Companion.checkboxList
import com.yj.addwords.ViewWordActivity.Companion.visibleCheckboxList
import com.yj.addwords.model.CheckBoxData
import com.yj.addwords.model.VisibleCheckBoxData
import com.yj.addwords.model.Word
import kotlinx.android.synthetic.main.rv_word_list.view.*

class ViewWordViewHolder(itemView: View, viewWordRecyclerViewInterface: ViewWordRecyclerViewInterface) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {
    private var starButton = itemView.view_star
    private var starButtonLayout = itemView.view_star_layout
    private var wordTextView = itemView.view_word
    private var meanTextView = itemView.view_mean
    private var wordCheckBox = itemView.view_check
    private var wordMeanLayout = itemView.view_word_book_list
    private var listenButton = itemView.view_listen_parent_layout
    private var visibleCheckBox = itemView.visible_check
    private var visibleCheckBoxLayout = itemView.visible_check_layout
    private var _visibilityOptions = 0
    private var viewWordRecyclerViewInterface : ViewWordRecyclerViewInterface? = null
    companion object {
        private const val TAG = "TAG ViewWordViewHolder"
    }
    init {
        starButton.setOnClickListener(this)
        starButtonLayout.setOnClickListener(this)
        visibleCheckBox.setOnClickListener(this)
        visibleCheckBoxLayout.setOnClickListener(this)
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
        setVisibilityMode(visibilityOptions, position)         // 가리기 모드

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
            // TODO: 2021-03-17 랜덤
        }
    }

    // TODO: 2021-02-21 이거처럼 해야할듯 가리기버튼도
    private fun isDeleteMode(deleteMode: Int,wordDatas: ArrayList<Word>,position: Int,) {
        if (deleteMode == 1) {
            wordCheckBox.visibility = View.VISIBLE
            starButtonLayout.visibility = View.GONE
            starButton.visibility = View.GONE
            listenButton.visibility = View.GONE
        } else {
            wordCheckBox.visibility = View.GONE
            starButtonLayout.visibility = View.VISIBLE
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
    private fun setVisibilityMode(showAndHideNumber: Int, position: Int) {
        when (showAndHideNumber) {
            0 -> {
                visibleCheckBoxLayout.visibility = View.GONE
                visibleCheckBox.visibility = View.GONE
                wordTextView.visibility = View.VISIBLE
                meanTextView.visibility = View.VISIBLE
            }
            1 -> {
                visibleCheckBoxLayout.visibility = View.VISIBLE
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
                visibleCheckBoxLayout.visibility = View.VISIBLE
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
            }
            // FIXME: 2021-02-24 랜덤하기
            /*3 -> {
                visibleCheckBoxLayout.visibility = View.VISIBLE
                visibleCheckBox.visibility = View.VISIBLE
                    // TODO: 2021-03-17 5:5비율로나오게
                val random = (1..10).shuffled().first()
//                Log.d(TAG, "visibilityMode: all unchecked random bind $position / $random")
//                if (random % 2 == 0) {
//                    wordTextView.visibility = View.VISIBLE
//                    meanTextView.visibility = View.INVISIBLE
//                } else {
//                    wordTextView.visibility = View.INVISIBLE
//                    meanTextView.visibility = View.VISIBLE
//                }


//                if (visibleCheckboxList[position].checked) {
//                    if (wordTextView.visibility == View.INVISIBLE) {
//                        wordTextView.visibility = View.VISIBLE
//                        isInvisibleItem = "word"
//                    } else if (meanTextView.visibility == View.INVISIBLE) {
//                        meanTextView.visibility = View.VISIBLE
//                        isInvisibleItem = "mean"
//                    }
//                } else {
//                    if (isInvisibleItem.equals("word")) {
//                        wordTextView.visibility = View.INVISIBLE
//                    } else {
//                        meanTextView.visibility = View.INVISIBLE
//                    }
//                }
            }*/
        }
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
                    visibleCheckBox.isSelected = visibleCheckboxList[i].checked
                }
            }
        }
    }
    override fun onClick(v: View?) {
        when(v!!.id) {
            R.id.visible_check_layout -> {
                this.viewWordRecyclerViewInterface?.onVisibilityCheckboxLayoutClicked(v, _visibilityOptions, wordTextView, meanTextView, adapterPosition)
            }
            R.id.visible_check -> {
                this.viewWordRecyclerViewInterface?.onVisibilityCheckboxClicked(v, _visibilityOptions, wordTextView, meanTextView, adapterPosition)
            }
            R.id.view_star -> {
                this.viewWordRecyclerViewInterface?.onFavoriteButtonClicked(v, adapterPosition)
            }
            R.id.view_star_layout -> {
                this.viewWordRecyclerViewInterface?.onFavoriteButtonLayoutClicked(v, adapterPosition)
            }
            R.id.view_check -> {
                this.viewWordRecyclerViewInterface?.onCheckboxClicked(v, wordMeanLayout, adapterPosition)
            }
            R.id.view_word_book_list -> {
                this.viewWordRecyclerViewInterface?.onViewClicked(v, wordMeanLayout, adapterPosition)
            }
            R.id.view_listen_parent_layout -> {
                this.viewWordRecyclerViewInterface?.ontextToSpeechSpeakButtonClicked(v, adapterPosition)
            }
        }
    }
    override fun onLongClick(v: View): Boolean {
        this.viewWordRecyclerViewInterface?.onWordLongClicked(v, adapterPosition)
        return true
    }
}

