package com.example.kokako

import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.kokako.model.WordDTO
import kotlinx.android.synthetic.main.list_item.view.*

class RecyclerAdapterWords(private val items: ArrayList<WordDTO>) : RecyclerView.Adapter<RecyclerAdapterWords.ViewHolder>() {

//    1
    override fun getItemCount(): Int = items.size
    //    2
//    리사이클러뷰에 추가될 item 레이아웃을 연결한다.
//    화면을 최초 로딩하여 만들어진 View가 없는 경우, xml파일을 inflate하여 ViewHolder를 생성한다
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int, ): RecyclerAdapterWords.ViewHolder {
        Log.v("","onCreateViewHolderonCreateViewHolderonCreateViewHolder")
        val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent,false)
        return RecyclerAdapterWords.ViewHolder(inflatedView)
    }
    //    3
//    position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시
//    위의 onCreateViewHolder에서 만든 view와 실제 입력되는 각각의 데이터를 연결한다.
    override fun onBindViewHolder(holder: RecyclerAdapterWords.ViewHolder, position: Int) {
        val item = items[position]
        Log.v("","onBindViewHolderonBindViewHolderonBindViewHolder")
//        var deleteIcon = view.findViewById<View>(R.id.rv_remove_word)
        val listener = View.OnClickListener { it ->

            holder.deleteIcon.setOnClickListener {
                val mBuilder = AlertDialog.Builder(it.context)
                mBuilder.setTitle("삭제")
                    .setMessage("단어 : " + item.word.toString() + "\n뜻 : " + item.word.toString() + "\n이 항목을 삭제하시겠습니까?")
                    .setPositiveButton("확인",
                        DialogInterface.OnClickListener { dialog, which ->
                            items.removeAt(position)
                            notifyItemRemoved(position)
                            notifyItemRangeChanged(position, items.size)
                        })
                    .setNegativeButton("취소",
                        DialogInterface.OnClickListener { dialog, which ->
                            dialog.cancel()
                        })
                mBuilder.show()
            }

/*            it.rv_remove_word.setOnClickListener {
                val mBuilder = AlertDialog.Builder(it.context)
                mBuilder.setTitle("삭제")
                    .setMessage("단어 : " + item.word.toString() + "\n뜻 : " + item.word.toString() + "\n이 항목을 삭제하시겠습니까?")
                    .setPositiveButton("확인",
                        DialogInterface.OnClickListener { dialog, which ->
                            items.removeAt(position)
                            notifyItemRemoved(position)
                            notifyItemRangeChanged(position, items.size)
                        })
                    .setNegativeButton("취소",
                        DialogInterface.OnClickListener { dialog, which ->
                            dialog.cancel()
                        })
                mBuilder.show()
            }*/



        }


        /*val listener = View.OnClickListener { it ->
            var btn_rv_remove_word = View.OnClickListener { view ->
                items.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, items.size)
            }
        }*/
//        rv_remove_word.setOnClickListener(listener)
        holder.apply {
            bind(listener, item)
            itemView.tag = item
        }
    }
//    4
// 아이템 뷰를 저장하는 뷰홀더 클래스
 class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private var view: View = v
        var deleteIcon = view.findViewById<View>(R.id.rv_remove_word)
    fun bind(listener: View.OnClickListener, item: WordDTO) {
            view.rv_word.setText(item.word)
            view.rv_mean.setText(item.mean)

            //            val listener = View.OnClickListener { it ->

           /* view.rv_remove_word.setOnClickListener { it
                val mBuilder = AlertDialog.Builder(it.context)
                mBuilder.setTitle("삭제")
//              .setMessage("해당 항목을 삭제하시겠습니까?")
                    .setMessage("단어 : "+view.rv_word.text.toString() + "\n뜻 : "+ view.rv_mean.text.toString() + "\n이 항목을 삭제하시겠습니까?")
                .setPositiveButton("확인",
                    DialogInterface.OnClickListener { dialog, which ->

                    })
                    .setNegativeButton("취소",
                    DialogInterface.OnClickListener { dialog, which ->
                        dialog.cancel()
                    })
                mBuilder.show()
            }*/

            view.setOnClickListener(listener)   // 원래있던거
            Log.v("","ViewHolderViewHolderViewHolderViewHolder")
        }
    }
 /*   class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    fun bind(listener: View.OnClickListener, items: ArrayList<WordDTO>, position: Int) {

        view.rv_word.setText(items[position].word)  // 원래있던거
        view.rv_mean.setText(items[position].mean)  // 원래있던거
        //            val listener = View.OnClickListener { it ->

        view.rv_remove_word.setOnClickListener { it
            val mBuilder = AlertDialog.Builder(it.context)
            mBuilder.setTitle("삭제")
//              .setMessage("해당 항목을 삭제하시겠습니까?")
                .setMessage("단어 : "+view.rv_word.text.toString() + "\n뜻 : "+ view.rv_mean.text.toString() + "\n이 항목을 삭제하시겠습니까?")
                .setPositiveButton("확인",
                    DialogInterface.OnClickListener { dialog, which ->
                        items.removeAt(position)
//                        notifyItemRemoved(position)
//                        notifyItemRangeChanged(position, items.size)
                    })
                .setNegativeButton("취소",
                    DialogInterface.OnClickListener { dialog, which ->
                        dialog.cancel()
                    })
            mBuilder.show()
        }

        view.setOnClickListener(listener) // 원래있던거
        Log.v("","ViewHolderViewHolderViewHolderViewHolder")
    }
    private var view: View = v
}*/

}

