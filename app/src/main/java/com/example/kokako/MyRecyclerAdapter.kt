package com.example.kokako

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kokako.model.WordDTO

class MyRecyclerAdapter(myRecyclerViewInterface: MyRecyclerViewInterface): RecyclerView.Adapter<MyViewHolder>() {

    private var wordDtoData = ArrayList<WordDTO>()

    private var myRecyclerViewInterface : MyRecyclerViewInterface? = null

    // 생성자
    // 외부에서 들어온걸 여기에 장착
    init {
        this.myRecyclerViewInterface = myRecyclerViewInterface
    }

    // 뷰 홀더가 생성 되었을때
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        // 연결할 레이아웃 설정
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.rv_add_list_item, parent, false), this.myRecyclerViewInterface!!)
    }
    // 뷰와 뷰홀더가 묶였을때
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(this.wordDtoData,position)
        // 여기서 클릭리스너안함. MyViewHolder에 View.OnClickListener가 있으니까 발동된다
    }
    // 목록의 수
    override fun getItemCount(): Int { return wordDtoData.size }

    // 외부에서 데이터 넘기기, 단어, 뜻 추가시킴
    fun submitList(wordDto: ArrayList<WordDTO>){
        this.wordDtoData = wordDto
    }

    fun removeWord(wordDto: ArrayList<WordDTO>, position: Int){
        wordDto.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, wordDto.size)
    }


}