package com.yj.addwords

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yj.addwords.databinding.FragmentMyWordListBinding
import com.yj.addwords.model.ItemWordDTO
import com.yj.addwords.model.WordBook
import kotlinx.android.synthetic.main.fragment_my_word_list.*


class MyWordListFragment : Fragment() {
    private lateinit var callback : OnBackPressedCallback
    private lateinit var myWordRecyclerAdapter: MyWordRecyclerAdapter
    private var _binding: FragmentMyWordListBinding? = null
    private val binding get() = _binding!!
    var myWordList = ArrayList<WordBook>()

    var itemWordDTO = ArrayList<WordBook>()
    private var recyclerview: RecyclerView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,): View? {
        _binding = FragmentMyWordListBinding.inflate(inflater, container, false)
        var view = binding.root
        return view
    }

//    프래그먼트 별 툴바 메뉴 추가
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.sub_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerview = binding.rvWordBook

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

        rv_word_book.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            recyclerview?.layoutManager
            adapter = MyWordRecyclerAdapter2(data)
//            var decorator = DividerItemDecoration(view.context,LinearLayoutManager.VERTICAL)
//            decorator.setDrawable(ContextCompat.getDrawable(context,R.drawable.divider_line)!!)
//            addItemDecoration(decorator)

        }


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
                                var intent = Intent(this.context, AddWordActivity::class.java)
                                startActivity(intent)
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
//                .setNeutralButton("취소") { dialog, which ->
//                    dialog.cancel()
//                }
            val mDialog = mBuilder.create()
            mDialog.show()

        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}