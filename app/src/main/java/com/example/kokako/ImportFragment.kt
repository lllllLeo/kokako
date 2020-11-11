package com.example.kokako

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.kokako.databinding.FragmentImportBinding

class ImportFragment : Fragment() {
    private var _binding : FragmentImportBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?, ): View? {
        _binding = FragmentImportBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var imText : String = "hi,안녕\nbye,잘가\nbeer,맥주"
        var importText = binding.importText.toString()

        binding.importPreview.setOnClickListener{
            var textSeparator : String = binding.textSeparator.text.toString()
            var previewText = imText.split(textSeparator,"\n")
//            Toast.makeText(context,""+previewText[0]+"|"+previewText[1], Toast.LENGTH_SHORT).show()

//       미리보기를 좀 다르게 어떻게 할까나 / 실시간, 똑같은 뷰,
/*            val mBuilder = AlertDialog.Builder(it.context)
            mBuilder.setMessage(previewText[0])*/
        }
    }
}