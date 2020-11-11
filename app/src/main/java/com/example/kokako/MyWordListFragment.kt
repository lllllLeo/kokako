package com.example.kokako

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kokako.databinding.FragmentMyWordListBinding
import com.example.kokako.model.ItemWordDTO
import com.example.kokako.model.MyWordListDTO
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_my_word_list.*


class MyWordListFragment : Fragment() {
    private lateinit var myWordRecyclerAdapter: MyWordRecyclerAdapter
    private var _binding: FragmentMyWordListBinding? = null
    private val binding get() = _binding!!
    var myWordList = ArrayList<MyWordListDTO>()

    var itemWordDTO = ArrayList<MyWordListDTO>()
    private var recyclerview: RecyclerView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,): View? {
        _binding = FragmentMyWordListBinding.inflate(inflater, container, false)
        var view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
/*

        for (i in 1..20) {
            var myWordListDTO = MyWordListDTO(title = "유준 단어장 $i", wordCount = i)
            this.myWordList.add(myWordListDTO)
        }


        myWordRecyclerAdapter = MyWordRecyclerAdapter()
        myWordRecyclerAdapter.getMyList(this.myWordList)

        rv_my_item.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
//          위 인스턴스만들걸 넣어줌/ 커스텀햇기떄문에 장착됨
            adapter = myWordRecyclerAdapter
        }
*/
        recyclerview = binding.rvMyItem


        var data = ArrayList<ItemWordDTO>()
        data.add(ItemWordDTO(MyWordRecyclerAdapter2.HEADER, "N1"))
        data.add(ItemWordDTO(MyWordRecyclerAdapter2.CHILD, "단어"))
        data.add(ItemWordDTO(MyWordRecyclerAdapter2.CHILD, "문법"))
        data.add(ItemWordDTO(MyWordRecyclerAdapter2.CHILD, "회화표현"))
        data.add(ItemWordDTO(MyWordRecyclerAdapter2.HEADER, "영어"))
        data.add(ItemWordDTO(MyWordRecyclerAdapter2.CHILD, "토익 단어"))
        data.add(ItemWordDTO(MyWordRecyclerAdapter2.CHILD, "숙어"))
        data.add(ItemWordDTO(MyWordRecyclerAdapter2.CHILD, "헷갈리는 단어"))
        data.add(ItemWordDTO(MyWordRecyclerAdapter2.CHILD, "회화표현"))

        var places : ItemWordDTO = ItemWordDTO(MyWordRecyclerAdapter2.HEADER, "일본어")
        places.invisibleChildren = ArrayList<ItemWordDTO>() // 원래 <> 안에 없엇는데 내가 추가함
        places.invisibleChildren?.add(ItemWordDTO(MyWordRecyclerAdapter2.CHILD, "1~100"))
        places.invisibleChildren?.add(ItemWordDTO(MyWordRecyclerAdapter2.CHILD, "101~200"))
        places.invisibleChildren?.add(ItemWordDTO(MyWordRecyclerAdapter2.CHILD, "201~300"))
        places.invisibleChildren?.add(ItemWordDTO(MyWordRecyclerAdapter2.CHILD, "301~400"))

        data.add(places)

        rv_my_item.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            recyclerview?.layoutManager
            adapter = MyWordRecyclerAdapter2(data)
        }

      /*  rv_my_item.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = MyWordRecyclerAdapter2(data)
        }*/


        fab_add_note.setOnClickListener { view ->
            val selectLanguage = arrayOf("영어", "일본어", "중국어")
            val mBuilder = AlertDialog.Builder(view.context)
            var selectedRadioItem = 0
            mBuilder.setTitle("단어장 언어 선택")
                .setSingleChoiceItems(selectLanguage, selectedRadioItem,
                    DialogInterface.OnClickListener { dialog, which ->
                        selectedRadioItem = which
                    })
                .setNegativeButton("취소", null)
                .setPositiveButton("확인",
                    DialogInterface.OnClickListener { dialog, which ->
                        when (selectedRadioItem) {
                            0 -> {
                                setFrag(0)
                            }
                            1 -> {
                                Toast.makeText(view.context, "일어", Toast.LENGTH_LONG).show()
                            }
                            2 -> {
                                Toast.makeText(view.context, "중국어", Toast.LENGTH_LONG).show()
                            }

                        }
                        dialog.dismiss()
                    })
                .setNeutralButton("취소") { dialog, which ->
                    dialog.cancel()
                }
            val mDialog = mBuilder.create()
            mDialog.show()

        }
    }

    private fun setFrag(fragNum: Int) {
        val ft = (activity as MainActivity).supportFragmentManager.beginTransaction()
//        val ft = childFragmentManager.beginTransaction()
        when(fragNum) {
            0 -> {
                ft.replace(R.id.main_frame, AddWordFragment()).commit()
            }
            1 -> {

            }
            2 -> {

            }
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}