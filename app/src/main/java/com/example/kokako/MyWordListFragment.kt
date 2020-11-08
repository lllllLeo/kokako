package com.example.kokako

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_my_word_list.*

class MyWordListFragment : Fragment() {
        private lateinit var mainViewModel : MainViewModel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?, ): View? {
        val view = inflater.inflate(R.layout.fragment_my_word_list, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.run {
            mainViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())
                .get(MainViewModel::class.java)
        }







        fab_add_note.setOnClickListener { view ->
            val select_languge = arrayOf("영어", "일본어", "중국어")
            val mBuilder = AlertDialog.Builder(view.context)
            var selectedRadioItem = 0
            mBuilder.setTitle("단어장 언어 선택")
                .setSingleChoiceItems(select_languge, selectedRadioItem,
                    DialogInterface.OnClickListener { dialog, which ->
                        selectedRadioItem = which
                    })
                .setNegativeButton("취소", null)
                .setPositiveButton("확인",
                    DialogInterface.OnClickListener { dialog, which ->
                        when(selectedRadioItem){
                            0 -> {
                                setFrag(0)
                            }
                            1 -> {
                                Toast.makeText(view.context,"일어", Toast.LENGTH_LONG).show()
                            }
                            2 -> {
                                Toast.makeText(view.context,"중국어", Toast.LENGTH_LONG).show()
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
}