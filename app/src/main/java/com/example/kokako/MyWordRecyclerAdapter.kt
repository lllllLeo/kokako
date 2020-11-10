package com.example.kokako

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kokako.model.MyWordListDTO

// Adapter 캐리어 ViewHolder는 인터셉터
class MyWordRecyclerAdapter() : RecyclerView.Adapter<MyWordViewHolder>(){
    private var myWordListDTO = ArrayList<MyWordListDTO>()
    private var myWordListRecyclerViewInterface : MyWordListRecyclerViewInterface? = null

    override fun getItemCount(): Int { return myWordListDTO.size }

    init {
        this.myWordListRecyclerViewInterface = myWordListRecyclerViewInterface
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyWordViewHolder {
        return MyWordViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.rv_my_word_list, parent, false))
    }

//    하나하나 생성될 떄 마다 호출이되니까 포지션을 안다
    override fun onBindViewHolder(holder: MyWordViewHolder, position: Int) {
        holder.bind(this.myWordListDTO[position])
    }

//    내 단어장들 불러오는 메소드
    fun getMyList(wordListDTO: ArrayList<MyWordListDTO>) {
        this.myWordListDTO = wordListDTO
    }


}