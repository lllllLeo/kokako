package com.example.kokako

import android.app.AlertDialog
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.kokako.model.WordDTO
import kotlinx.android.synthetic.main.list_item.view.*

class RecyclerAdapterWords(private val items: ArrayList<WordDTO>) : RecyclerView.Adapter<RecyclerAdapterWords.ViewHolder>() {

    override fun getItemCount(): Int = items.size

//    position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시
    override fun onBindViewHolder(holder: RecyclerAdapterWords.ViewHolder, position: Int) {
        val item = items[position]

//        var mBuilder = AlertDialog.Builder(@AddWordActivity)

        val listener = View.OnClickListener { it ->
            Toast.makeText(it.context, "Clicked: " + item.word, Toast.LENGTH_LONG).show()
        }

        /*val listener = View.OnClickListener { it ->
            var btn_rv_remove_word = View.OnClickListener { view ->
                items.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, items.size)
            }
        }
        btn_rv_remove_word.setOnClickListener(listener)*/
        holder.apply {
            bind(listener, item)
            itemView.tag = item
        }
    }

//    리사이클러뷰에 추가될 item 레이아웃을 연결한다.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int, ): RecyclerAdapterWords.ViewHolder {
        val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent,false)
        return RecyclerAdapterWords.ViewHolder(inflatedView)
    }
// 아이템 뷰를 저장하는 뷰홀더 클래스
    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private var view: View = v
        fun bind(listener: View.OnClickListener, item: WordDTO) {
//            view.rv_word.text = item.word
//            view.rv_mean.text = item.mean
            view.rv_word.setText(item.word)
            view.rv_mean.setText(item.mean)
//            Log.v("리사이클러뷰","ㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁ" + WordDTO.toString())
            view.setOnClickListener(listener)
        }
    }

}

