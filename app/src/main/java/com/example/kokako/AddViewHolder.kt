package com.example.kokako

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.kokako.model.Word
import kotlinx.android.synthetic.main.rv_add_list_item.view.*

// 커스텀 뷰홀더 를 어댑터에 넣어줌
class AddViewHolder(itemView: View, recyclerViewInterface: AddRecyclerViewInterface) : RecyclerView.ViewHolder(itemView), View.OnClickListener{
    companion object {
        private const val TAG = "TAG"
    }
    private var wordEditText = itemView.rv_word
    private var meanEditText = itemView.rv_mean
    private var removeButton = itemView.rv_remove_word   //     X버튼 없앴으니 지우기
    private var addRecyclerViewInterface : AddRecyclerViewInterface? = null

    // 기본 생성자
    init {
        Log.d(TAG,"MyViewHolder - init() called")
        /*
        * itemView에 자기 자신 this는 View.OnClickListener.를 걸어줘야 onClick()이 발동이되고
        * */
// 클릭리스너 설정
//        itemView.setOnClickListener(this)
//        removeButton이 onClick된걸 리스너에 등록


        removeButton.setOnClickListener(this)
        // 인터페이스 연결
        this.addRecyclerViewInterface = recyclerViewInterface
    }

    //뷰와 데이터 묶기
    fun bind(word: ArrayList<Word>, position:Int){
        Log.d(" TAG ","MyViewHolder - bind() called")

        wordEditText.setText(word[position].word.toString())
        meanEditText.setText(word[position].mean.toString())
        wordEditText.id = (position * 2) + 1
        meanEditText.id = position * 2
    }

//    뷰홀더에서 아이템이 클릭된걸 암
//    리사이클러 인터페이스에 알려줌
//    이메소드가 발동이 되는걸 AddWordFragment 가 알도록 할거임
    /*
    * 이렇게해야 아래 onClick가 발동. 발동되면서
    * this.myRecyclerViewInterface?.onItemClicked()가 발동
    * myRecyclerViewInterface를 설정한 애들한테 소식을 알려줌->Add~Activity가 알게되고
    * onItemClicked() 를 실행하게 됨
    * */
    override fun onClick(v: View?) {
    // adapterPosition 뷰홀더에서 현재 어답터의 포지션을 알 수 있음
        this.addRecyclerViewInterface?.onRemoveClicked(v!!, adapterPosition)
    }
    /*
    * 아이템 클릭시
    * MyViewHolder onClick() called
    * AddWordActivity onItemClicked() called
    * */

}