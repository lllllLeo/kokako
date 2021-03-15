package com.example.kokako

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kokako.model.Word
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.modal_bottom_sheet.*
import kotlinx.android.synthetic.main.modal_bottom_sheet.view.*

class BottomSheetDialog(wordForBottomSheet: Word, adapterPosition: Int) : BottomSheetDialogFragment() {
    lateinit var  bottomSheetInterface: BottomSheetInterface
    private var word : Word = wordForBottomSheet
    private var position : Int = adapterPosition
    private var isUpdated : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return inflater.inflate(R.layout.modal_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.bsd_cancel_btn.setOnClickListener{ dismiss() }

        view.bsd_btn_remove_text.setOnClickListener{
            if (view.bsd_input_word.isFocused) {
                view.bsd_input_word.text.clear()
            } else if (view.bsd_input_mean.isFocused) {
                view.bsd_input_mean.text.clear()
            }
        }
        view.bsd_btn_move_left.setOnClickListener{ view.bsd_input_word.requestFocus() }
        view.bsd_btn_move_right.setOnClickListener{
            view.bsd_input_mean.requestFocus()
            view.bsd_input_word.setSelection(word.mean!!.length)
        }

        view.bsd_input_word.setText(word.word)
        view.bsd_input_word.setSelection(word.word!!.length)
        view.bsd_input_mean.setText(word.mean)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            bottomSheetInterface = context as BottomSheetInterface
        } catch (e: ClassCastException) { }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bsd_btn_update_word.setOnClickListener {
            isUpdated = bottomSheetInterface.onUpdateButtonClicked(view!!, word, isUpdated)
            if(isUpdated) { dismiss() }
        }
        bsd_delete_btn.setOnClickListener{
            bottomSheetInterface.onDeleteButtonClicked(word, position)
            dismiss()
        }
    }


    interface BottomSheetInterface{
        fun onUpdateButtonClicked(view: View, wordList: Word, isUpdated: Boolean) : Boolean
        fun onDeleteButtonClicked(wordList: Word, position: Int)
    }
}